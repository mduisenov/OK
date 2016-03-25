package ru.ok.android.utils.localization;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.localization.visitor.ActivityVisitableHolder;
import ru.ok.android.utils.localization.visitor.BaseVisitableHolder;
import ru.ok.android.utils.localization.visitor.FragmentVisitableHolder;
import ru.ok.android.utils.localization.visitor.MenuContextVisitableHolder;
import ru.ok.android.utils.localization.visitor.MenuSherlockVisitableHolder;
import ru.ok.android.utils.localization.visitor.PreferenceActivityVisitableHolder;
import ru.ok.android.utils.localization.visitor.UpdateLocalizationViewVisitor;
import ru.ok.android.utils.localization.visitor.ViewVisitableHolder;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.request.TranslationsMarkerRequest;
import ru.ok.java.api.response.translations.TranslationsResponse;
import ru.ok.java.api.wmf.json.JsonTranslationsParser;

public final class LocalizationManager {
    private final Context _context;
    private final LocalizationRepository _repository;
    private Map<BaseVisitableHolder<?>, Integer> _views;

    /* renamed from: ru.ok.android.utils.localization.LocalizationManager.1 */
    class C14681 implements Runnable {
        C14681() {
        }

        public void run() {
            LocalizationManager.this.notifyLocaleChanged();
        }
    }

    class LocalizationLoaderAsyncTask extends AsyncTask<String, Void, Boolean> {
        LocalizationLoaderAsyncTask() {
        }

        protected Boolean doInBackground(String... args) {
            String locale = args[0];
            try {
                String currentModified = Settings.getLocaleModified(LocalizationManager.this._context);
                String lastPackage = Settings.getLocaleLastPackage(LocalizationManager.this._context);
                String currentPackage = ConfigurationPreferences.getInstance().getLocalePackage();
                TranslationsResponse translations = new JsonTranslationsParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new TranslationsMarkerRequest(currentPackage, "*", locale, currentModified)).getResultAsObject()).parse();
                if (!isLocaleEquals(locale)) {
                    return Boolean.valueOf(false);
                }
                Settings.setLocaleLastUpdate(LocalizationManager.this._context, (int) (System.currentTimeMillis() / 1000));
                if (!TextUtils.isEmpty(currentModified) && TextUtils.equals(currentModified, translations.modified) && TextUtils.equals(lastPackage, currentPackage)) {
                    return Boolean.valueOf(false);
                }
                TranslationData currentTranslationData = LocalizationStorage.loadLocalizationFile(LocalizationManager.this._context, locale);
                if (currentTranslationData == null) {
                    currentTranslationData = new TranslationData(translations.modified);
                }
                currentTranslationData.inject(translations);
                boolean result = LocalizationStorage.saveLocalizationFile(LocalizationManager.this._context, locale, currentTranslationData);
                if (!isLocaleEquals(locale)) {
                    return Boolean.valueOf(false);
                }
                if (result) {
                    Settings.setLocaleModifiedAndPackage(LocalizationManager.this._context, translations.modified, currentModified);
                }
                return Boolean.valueOf(result);
            } catch (Throwable e) {
                Logger.m178e(e);
                return Boolean.valueOf(false);
            }
        }

        private boolean isLocaleEquals(String locale) {
            return locale.equals(Settings.getCurrentLocale(LocalizationManager.this._context));
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result.booleanValue()) {
                LocalizationManager.this.notifyLocaleChanged();
            }
        }
    }

    public Context getContext() {
        return this._context;
    }

    public static LocalizationManager from(Context context) {
        Context appContext = context != null ? context.getApplicationContext() : OdnoklassnikiApplication.getContext();
        if (appContext instanceof OdnoklassnikiApplication) {
            return ((OdnoklassnikiApplication) appContext).getLocalizationManager();
        }
        return null;
    }

    public LocalizationManager(Context context) {
        this._views = new HashMap();
        this._context = context;
        this._repository = new LocalizationRepository(this._context);
        if (StringUtils.isEmpty(Settings.getCurrentLocale(getContext()))) {
            resetLocale();
        }
    }

    public static View inflate(Context context, int layoutId, ViewGroup parent, boolean attachToParent) {
        View result = LayoutInflater.from(context).inflate(layoutId, parent, attachToParent);
        from(context).registerView(result, layoutId);
        return result;
    }

    public static void inflate(Context context, MenuInflater inflater, int menuId, Menu menu) {
        inflater.inflate(menuId, menu);
        from(context).registerMenu(menu, menuId);
    }

    public static void inflate(Context context, MenuInflater inflater, int menuId, ContextMenu menu) {
        inflater.inflate(menuId, menu);
        from(context).registerMenu(menu, menuId);
    }

    public void registerView(View view, int layoutId) {
        addHolder(new ViewVisitableHolder(view), layoutId);
    }

    public void registerFragment(Fragment fragment, int layoutId) {
        addHolder(new FragmentVisitableHolder(fragment), layoutId);
    }

    public void registerActivity(Activity activity, int layoutId) {
        addHolder(new ActivityVisitableHolder(activity), layoutId);
    }

    public void registerPreferenceActivity(PreferenceActivity activity, int resourceId) {
        addHolder(new PreferenceActivityVisitableHolder(activity), resourceId);
    }

    public void registerMenu(Menu menu, int resourceId) {
        addHolder(new MenuSherlockVisitableHolder(this._context, menu), resourceId);
    }

    private void registerMenu(ContextMenu menu, int resourceId) {
        addHolder(new MenuContextVisitableHolder(this._context, menu), resourceId);
    }

    private void addHolder(BaseVisitableHolder holder, int resourceId) {
        holder.visit(new UpdateLocalizationViewVisitor(), resourceId);
        this._views.put(holder, Integer.valueOf(resourceId));
        clearViewsMap();
    }

    private void clearViewsMap() {
        Set<BaseVisitableHolder<?>> deadViews = null;
        for (BaseVisitableHolder<?> view : this._views.keySet()) {
            if (view.getView() == null) {
                if (deadViews == null) {
                    deadViews = new HashSet();
                }
                deadViews.add(view);
            }
        }
        if (deadViews != null) {
            for (BaseVisitableHolder<?> view2 : deadViews) {
                this._views.remove(view2);
            }
        }
    }

    public String getString(int resourceId) {
        return this._repository.getString(resourceId);
    }

    public String getString(int resourceId, Object... args) {
        return this._repository.getString(resourceId, args);
    }

    public String[] getStringArray(int resourceId) {
        return this._repository.getStringArray(resourceId);
    }

    public static String getString(Context context, int resourceId) {
        LocalizationManager localizationManager = from(context);
        if (localizationManager != null) {
            return localizationManager.getString(resourceId);
        }
        return "";
    }

    public static String getString(Context context, int resourceId, Object... args) {
        return from(context).getString(resourceId, args);
    }

    public static String[] getStringArray(Context context, int resourceId) {
        LocalizationManager localizationManager = from(context);
        if (localizationManager != null) {
            return localizationManager.getStringArray(resourceId);
        }
        return new String[0];
    }

    public void setLocaleTo(String newLocale) {
        boolean localeChanged;
        String locale = Settings.getCurrentLocale(this._context);
        if (locale.equals(newLocale)) {
            localeChanged = false;
        } else {
            localeChanged = true;
        }
        if (localeChanged) {
            Settings.setCurrentLocale(this._context, newLocale);
            Settings.setLocaleModifiedAndPackage(this._context, null, null);
            Settings.setLocaleLastUpdate(this._context, 0);
            LocalizationStorage.removeLocaleFile(this._context, locale);
            ThreadUtil.executeOnMain(new C14681());
        }
        if (localeChanged || isTimeToUpdateLocale()) {
            updateLocaleAsync();
        }
    }

    private void notifyLocaleChanged() {
        this._repository.clear();
        UpdateLocalizationViewVisitor visitor = new UpdateLocalizationViewVisitor();
        for (Entry<BaseVisitableHolder<?>, Integer> entry : this._views.entrySet()) {
            ((BaseVisitableHolder) entry.getKey()).visit(visitor, ((Integer) entry.getValue()).intValue());
        }
        LocalBroadcastManager.getInstance(this._context).sendBroadcast(new Intent("ru.ok.android.utils.localization.LOCALE_CHANGED"));
    }

    public void updateLocaleIfNeeded() {
        if (isTimeToUpdateLocale()) {
            updateLocaleAsync();
        }
    }

    private boolean isTimeToUpdateLocale() {
        return (System.currentTimeMillis() / 1000) - ((long) Settings.getLocaleLastUpdate(this._context)) > ((long) 86400);
    }

    private void updateLocaleAsync() {
        new LocalizationLoaderAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{Settings.getCurrentLocale(this._context)});
    }

    public void resetLocale() {
        setLocaleTo(Locale.getDefault().getLanguage());
    }
}
