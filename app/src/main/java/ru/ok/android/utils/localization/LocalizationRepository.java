package ru.ok.android.utils.localization;

import android.content.Context;
import android.text.TextUtils;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.settings.Settings;

public class LocalizationRepository {
    private final Context _context;
    private AtomicReference<TranslationData> valuesRef;

    public LocalizationRepository(Context context) {
        this._context = context;
        int versionCode = Utils.getVersionCode(this._context);
        int savedVersionCode = Settings.getIntValue(this._context, "localization_repository:version_code", -1);
        if (savedVersionCode != versionCode) {
            if (savedVersionCode != -1) {
                LocalizationStorage.removeLocaleFile(this._context, Settings.getCurrentLocale(this._context));
            }
            Settings.storeIntValue(this._context, "localization_repository:version_code", versionCode);
            Settings.setLocaleLastUpdate(this._context, 0);
            Settings.setLocaleModifiedAndPackage(this._context, null, null);
        }
    }

    private TranslationData getValues() {
        if (this.valuesRef == null) {
            this.valuesRef = new AtomicReference(LocalizationStorage.loadLocalizationFile(this._context, Settings.getCurrentLocale(this._context)));
        }
        return (TranslationData) this.valuesRef.get();
    }

    private static String resourceId2Name(Context context, int resourceId) {
        return context.getResources().getResourceEntryName(resourceId).toLowerCase();
    }

    String getString(int resourceId) {
        TranslationData values = getValues();
        if (values != null) {
            String resourceName = resourceId2Name(this._context, resourceId);
            if (values.getStringTranslations().containsKey(resourceName)) {
                return (String) values.getStringTranslations().get(resourceName);
            }
        }
        return this._context.getResources().getString(resourceId);
    }

    public String getString(int resourceId, Object... args) {
        String string = getString(resourceId);
        if (TextUtils.isEmpty(string)) {
            return string;
        }
        try {
            return String.format(string, args);
        } catch (Throwable e) {
            Logger.m178e(e);
            StatisticManager.getInstance().reportError("Localization", String.format("String format failed for language: '%s', resource name: '%s'", new Object[]{Settings.getCurrentLocale(this._context), resourceId2Name(this._context, resourceId)}), e);
            return this._context.getResources().getString(resourceId, args);
        }
    }

    String[] getStringArray(int resourceId) {
        TranslationData values = getValues();
        if (values != null) {
            String resourceName = resourceId2Name(this._context, resourceId);
            Map<String, String[]> translations = values.getArrayTranslations();
            if (translations.containsKey(resourceName)) {
                return (String[]) translations.get(resourceName);
            }
        }
        return this._context.getResources().getStringArray(resourceId);
    }

    public void clear() {
        this.valuesRef = null;
    }
}
