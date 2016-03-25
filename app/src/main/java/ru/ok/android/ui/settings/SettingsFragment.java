package ru.ok.android.ui.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.preference.TwoStatePreference;
import android.support.annotation.NonNull;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Pair;
import com.facebook.drawee.backends.pipeline.Fresco;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import ru.ok.android.C0206R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.app.helper.AccountsHelper;
import ru.ok.android.model.cache.music.async.MusicAsyncFileCache;
import ru.ok.android.services.processors.settings.GifSettings;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.custom.prefs.CustomRingtonePreference;
import ru.ok.android.ui.dialogs.ConfirmClearCacheDialog;
import ru.ok.android.ui.dialogs.ConfirmClearCacheDialog.OnConfirmClearCacheListener;
import ru.ok.android.ui.presents.helpers.PresentSettingsHelper;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.IntListPreference;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.PermissionUtils;
import ru.ok.android.utils.controls.authorization.AuthorizationControl;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.Settings;
import ru.ok.model.UserInfo;

public class SettingsFragment extends PreferenceFragment {
    private ArrayList<OnActivityResultListener> activityResultListeners;
    private boolean contactsSyncInitialValue;
    private boolean contactsSyncLastValue;
    private LocalizationManager localizationManager;
    int mAppWidgetId;
    private int nextRequestCode;
    private SharedPreferences settings;
    private SharedPreferences settingsInvariable;
    private String testServersStr;

    /* renamed from: ru.ok.android.ui.settings.SettingsFragment.10 */
    class AnonymousClass10 implements OnPreferenceClickListener {
        final /* synthetic */ Preference val$clearCachePreference;

        /* renamed from: ru.ok.android.ui.settings.SettingsFragment.10.1 */
        class C12011 implements OnConfirmClearCacheListener {
            final /* synthetic */ ConfirmClearCacheDialog val$confirmClearCacheDialog;

            C12011(ConfirmClearCacheDialog confirmClearCacheDialog) {
                this.val$confirmClearCacheDialog = confirmClearCacheDialog;
            }

            public void onClearCacheConfirm() {
                SettingsFragment.this.clearCache(AnonymousClass10.this.val$clearCachePreference);
            }

            public void onClearCacheNoConfirm() {
                this.val$confirmClearCacheDialog.getDialog().hide();
            }
        }

        AnonymousClass10(Preference preference) {
            this.val$clearCachePreference = preference;
        }

        public boolean onPreferenceClick(Preference preference) {
            ConfirmClearCacheDialog confirmClearCacheDialog = new ConfirmClearCacheDialog(SettingsFragment.this.getActivity());
            confirmClearCacheDialog.setOnConfirmListener(new C12011(confirmClearCacheDialog));
            confirmClearCacheDialog.getDialog().show();
            return false;
        }
    }

    /* renamed from: ru.ok.android.ui.settings.SettingsFragment.1 */
    class C12021 implements OnPreferenceClickListener {
        C12021() {
        }

        public boolean onPreferenceClick(Preference preference) {
            NavigationHelper.showWebSettings(SettingsFragment.this.getActivity());
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.settings.SettingsFragment.2 */
    class C12032 implements OnPreferenceChangeListener {
        C12032() {
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String key = preference.getKey();
            try {
                SettingsFragment.this.settings.edit().putBoolean(key, ((Boolean) newValue).booleanValue()).apply();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /* renamed from: ru.ok.android.ui.settings.SettingsFragment.3 */
    class C12043 implements OnPreferenceChangeListener {
        C12043() {
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            SettingsFragment.this.updateBoolSettingInvariable(preference, (Boolean) newValue);
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.settings.SettingsFragment.4 */
    class C12054 implements OnPreferenceChangeListener {
        C12054() {
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Settings.storeStrValueInvariable(SettingsFragment.this.getActivity(), SettingsFragment.this.getActivity().getString(2131166288), (String) newValue);
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.settings.SettingsFragment.5 */
    class C12065 implements OnPreferenceChangeListener {
        C12065() {
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean z;
            boolean z2 = false;
            Boolean boolValue = (Boolean) newValue;
            SettingsFragment.this.enablePreference(2131166288, !boolValue.booleanValue());
            SettingsFragment settingsFragment = SettingsFragment.this;
            if (boolValue.booleanValue()) {
                z = false;
            } else {
                z = true;
            }
            settingsFragment.enablePreference(2131166295, z);
            settingsFragment = SettingsFragment.this;
            if (boolValue.booleanValue()) {
                z = false;
            } else {
                z = true;
            }
            settingsFragment.enablePreference(2131166305, z);
            SettingsFragment settingsFragment2 = SettingsFragment.this;
            if (!boolValue.booleanValue()) {
                z2 = true;
            }
            settingsFragment2.enablePreference(2131166289, z2);
            SettingsFragment.this.updateBoolSettingInvariable(preference, boolValue);
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.settings.SettingsFragment.6 */
    class C12076 implements OnPreferenceChangeListener {
        C12076() {
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            SettingsFragment.this.setCacheMusicPreferenceSummary(preference, Integer.valueOf((String) newValue).intValue());
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.settings.SettingsFragment.7 */
    class C12087 implements OnPreferenceChangeListener {
        C12087() {
        }

        @TargetApi(23)
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (newValue.equals(Boolean.TRUE)) {
                if (PermissionUtils.checkSelfPermission(SettingsFragment.this.getActivity(), "android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS") != 0) {
                    SettingsFragment.this.requestPermissions(new String[]{"android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS"}, C0206R.styleable.Theme_spinnerStyle);
                    return false;
                }
            }
            SettingsFragment.this.contactsSyncLastValue = ((Boolean) newValue).booleanValue();
            SettingsFragment.this.updateBoolSettingInvariable(preference, Boolean.valueOf(SettingsFragment.this.contactsSyncLastValue));
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.settings.SettingsFragment.8 */
    class C12098 implements OnPreferenceChangeListener {
        C12098() {
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            SettingsFragment.this.setDiscussionsBubblePreferenceSummary(preference, Integer.valueOf((String) newValue).intValue());
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.settings.SettingsFragment.9 */
    class C12109 implements OnPreferenceClickListener {
        C12109() {
        }

        public boolean onPreferenceClick(Preference preference) {
            StatisticManager.getInstance().addStatisticEvent("settings-logout", new Pair[0]);
            AuthorizationControl.getInstance().showLogoutDialog(SettingsFragment.this.getActivity());
            return false;
        }
    }

    public SettingsFragment() {
        this.mAppWidgetId = 0;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(2131034117);
        this.testServersStr = getResources().getString(2131166675);
        this.settings = getActivity().getSharedPreferences("PrefsFile1", 0);
        this.settingsInvariable = getActivity().getSharedPreferences("PrefsFileSavedAfterLogout", 0);
        if (!getActivity().isFinishing()) {
            bindViews();
        }
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            this.mAppWidgetId = extras.getInt("appWidgetId", 0);
        }
        if (this.mAppWidgetId != 0) {
            Intent resultValue = new Intent();
            resultValue.putExtra("appWidgetId", this.mAppWidgetId);
            getActivity().setResult(-1, resultValue);
        }
    }

    private void bindViews() {
        initGifAutoplayPreference();
        initProfilePreference();
        initNotificationPreference();
        initTestServersPreference();
        initDiscussionsBubblePreference();
        initAppVersionPreference();
        initClearCachePreference();
        initMusicPreference();
        initContactsPreference();
        initLogoutPreference();
        initAnimatedPresentsPreference();
        updateLocalization();
    }

    public void onStop() {
        super.onStop();
        if (this.contactsSyncInitialValue != this.contactsSyncLastValue) {
            Logger.m173d("User has changed contacts sync settings to %s", Boolean.valueOf(this.contactsSyncLastValue));
            AccountsHelper.applyNewSyncSettings(getActivity(), this.contactsSyncLastValue);
            this.contactsSyncInitialValue = this.contactsSyncLastValue;
        }
    }

    private void updateLocalization() {
        if (this.localizationManager == null) {
            this.localizationManager = LocalizationManager.from(getActivity());
        }
        updatePrefTitleSummary(2131166409, 2131166410, 2131166390);
        updatePrefTitleSummary(2131166630, 2131166632, 2131166631);
        updatePrefTitleSummary(2131165904, 2131165905, 0);
        updatePrefTitleSummary(2131166294, 2131166299, 2131166298);
        updatePrefTitleSummary(2131166285, 2131166286, 0);
        updatePrefTitleSummary(2131166288, 2131166302, 0);
        updatePrefTitleSummary(2131166305, 2131166306, 0);
        updatePrefTitleSummary(2131166295, 2131166297, 2131166296);
        updatePrefTitleSummary(2131166289, 2131166290, 0);
        updatePrefTitleSummary(2131165494, 2131166385, 0);
        updatePrefTitleSummary(2131165457, 2131165458, 0);
        updatePrefTitleSummary(2131165593, 2131165592, 0);
        updatePrefTitleSummary(2131166226, 2131166228, 2131166227);
        updatePrefTitleSummary(2131165493, 2131166384, 0);
        updatePrefTitleSummary(2131165402, 2131165404, 0);
        updatePrefTitleSummary(2131166058, 2131165850, 0);
        updatePrefTitleSummary(2131166672, 2131166674, 2131166673);
        updatePrefTitleSummary(2131165390, 2131165391, 0);
        IntListPreference cacheMusicPref = (IntListPreference) findPreference(getString(2131165457));
        cacheMusicPref.setEntries(this.localizationManager.getStringArray(2131558400));
        cacheMusicPref.setDialogTitle(this.localizationManager.getString(2131165458));
    }

    private void updatePrefTitleSummary(int prefKey, int prefTitleResId, int prefSummaryResId) {
        if (prefTitleResId != 0 || prefSummaryResId != 0) {
            Preference pref = findPreference(getString(prefKey));
            if (pref != null) {
                if (prefTitleResId != 0) {
                    pref.setTitle(this.localizationManager.getString(prefTitleResId));
                }
                if (prefSummaryResId != 0) {
                    pref.setSummary(this.localizationManager.getString(prefSummaryResId));
                }
            }
        }
    }

    private Preference initProfilePreference() {
        Preference profilePreference = findPreference(getString(2131166409));
        if (profilePreference != null) {
            getPreferenceScreen().removePreference(profilePreference);
            profilePreference.setOnPreferenceClickListener(new C12021());
        }
        return profilePreference;
    }

    private void initGifAutoplayPreference() {
        Preference gifAutoplayPreference = findPreference(getString(2131165904));
        if (gifAutoplayPreference != null && !GifSettings.isGifEnabled()) {
            getPreferenceScreen().removePreference(gifAutoplayPreference);
        }
    }

    private void initAnimatedPresentsPreference() {
        Preference animatedPresentsPreference = findPreference(getString(2131165390));
        if (animatedPresentsPreference != null && !PresentSettingsHelper.getSettings().animatedPresentsEnabled) {
            getPreferenceScreen().removePreference(animatedPresentsPreference);
        }
    }

    private CheckBoxPreference initTestServersPreference() {
        CheckBoxPreference testServersPreference = (CheckBoxPreference) findPreference(this.testServersStr);
        if (testServersPreference != null) {
            OnPreferenceChangeListener checkBoxListener = new C12032();
            if (testServersPreference != null) {
                testServersPreference.setOnPreferenceChangeListener(checkBoxListener);
            }
        }
        return testServersPreference;
    }

    private void initNotificationPreference() {
        boolean subSettingsEnabled = false;
        Preference screen = findPreference(getString(2131166294));
        if (screen != null) {
            screen.setEnabled(true);
            OnPreferenceChangeListener updatePreferenceListener = new C12043();
            TwoStatePreference disableNotifications = (TwoStatePreference) findPreference(getString(2131166285));
            bindCheckboxInvariablePreferenceWithListener(2131166285, null, false, true);
            if (!disableNotifications.isChecked()) {
                subSettingsEnabled = true;
            }
            bindCheckboxInvariablePreferenceWithListener(2131166295, updatePreferenceListener, true, subSettingsEnabled);
            bindCheckboxInvariablePreferenceWithListener(2131166305, updatePreferenceListener, true, subSettingsEnabled);
            bindCheckboxInvariablePreferenceWithListener(2131166289, updatePreferenceListener, true, subSettingsEnabled);
            CustomRingtonePreference ringtonePreference = (CustomRingtonePreference) findPreference(getString(2131166288));
            ringtonePreference.setOwningFragment(this);
            ringtonePreference.setEnabled(subSettingsEnabled);
            ringtonePreference.setOnPreferenceChangeListener(new C12054());
            disableNotifications.setOnPreferenceChangeListener(new C12065());
        }
    }

    private void bindCheckboxInvariablePreferenceWithListener(int preferenceKey, OnPreferenceChangeListener listener, boolean defaultValue, boolean enabled) {
        String key = getString(preferenceKey);
        TwoStatePreference preference = (TwoStatePreference) findPreference(key);
        preference.setChecked(this.settingsInvariable.getBoolean(key, defaultValue));
        preference.setOnPreferenceChangeListener(listener);
        preference.setEnabled(enabled);
    }

    private void enablePreference(int preferenceKey, boolean enabled) {
        findPreference(getString(preferenceKey)).setEnabled(enabled);
    }

    private void updateBoolSettingInvariable(Preference preference, Boolean boolValue) {
        this.settingsInvariable.edit().putBoolean(preference.getKey(), boolValue.booleanValue()).apply();
    }

    private void initMusicPreference() {
        Preference cachePreference = findPreference(getString(2131165457));
        if (cachePreference != null) {
            setCacheMusicPreferenceSummary(cachePreference, PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(getString(2131165457), 0));
            cachePreference.setOnPreferenceChangeListener(new C12076());
        }
    }

    private void initContactsPreference() {
        String key = getString(2131166672);
        if (PermissionUtils.checkSelfPermission(getActivity(), "android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS") != 0) {
            Settings.clearSettingInvariableByKey(getActivity(), key);
        }
        TwoStatePreference preference = (TwoStatePreference) findPreference(key);
        boolean z = this.settingsInvariable.getBoolean(key, false);
        this.contactsSyncInitialValue = z;
        this.contactsSyncLastValue = z;
        preference.setChecked(z);
        preference.setOnPreferenceChangeListener(new C12087());
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case C0206R.styleable.Theme_spinnerStyle /*106*/:
                TwoStatePreference preference = (TwoStatePreference) findPreference(getString(2131166672));
                this.contactsSyncLastValue = PermissionUtils.getGrantResult(grantResults) == 0;
                updateBoolSettingInvariable(preference, Boolean.valueOf(this.contactsSyncLastValue));
                preference.setChecked(this.contactsSyncLastValue);
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private Preference initDiscussionsBubblePreference() {
        Preference mDiscussionsBubblePreference = findPreference(getString(2131165714));
        if (mDiscussionsBubblePreference != null) {
            setDiscussionsBubblePreferenceSummary(mDiscussionsBubblePreference, PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(getString(2131165714), 0));
            mDiscussionsBubblePreference.setOnPreferenceChangeListener(new C12098());
        }
        return mDiscussionsBubblePreference;
    }

    private Preference initAppVersionPreference() {
        Preference appVersion = findPreference(getString(2131165402));
        appVersion.setSummary(getAppVersionString());
        return appVersion;
    }

    private Preference initLogoutPreference() {
        Preference logout = findPreference(getString(2131166058));
        UserInfo currentUser = OdnoklassnikiApplication.getCurrentUser();
        Context context = getActivity();
        logout.setSummary(LocalizationManager.getString(context, 2131166887, currentUser.getAnyName()));
        if (Settings.hasLoginData(context)) {
            logout.setEnabled(true);
            logout.setOnPreferenceClickListener(new C12109());
        } else {
            logout.setEnabled(false);
        }
        return logout;
    }

    private Preference initClearCachePreference() {
        Preference clearCachePreference = findPreference(getString(2131165593));
        if (clearCachePreference != null) {
            long musicCacheSize = MusicAsyncFileCache.getInstance().getSize();
            if (musicCacheSize == -1) {
                musicCacheSize = 0;
            }
            long imageCacheSize = Fresco.getImagePipelineFactory().getMainDiskStorageCache().getSize() + Fresco.getImagePipelineFactory().getSmallImageDiskStorageCache().getSize();
            if (imageCacheSize == -1) {
                imageCacheSize = 0;
            }
            long size = musicCacheSize + imageCacheSize;
            Context context = getActivity();
            File tempDir = FileUtils.getCacheDir(context, "temp");
            if (tempDir.exists()) {
                size += FileUtils.folderSize(tempDir);
            }
            if (size > 0) {
                clearCachePreference.setSummary(LocalizationManager.getString(context, 2131165459, Long.valueOf((size / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID)));
                clearCachePreference.setEnabled(true);
            } else {
                clearCachePreference.setSummary("");
                clearCachePreference.setEnabled(false);
            }
            clearCachePreference.setOnPreferenceClickListener(new AnonymousClass10(clearCachePreference));
        }
        return clearCachePreference;
    }

    private void clearCache(Preference clearCachePreference) {
        clearTemp();
        Fresco.getImagePipeline().clearCaches();
        clearCachePreference.setSummary("");
        clearCachePreference.setEnabled(false);
    }

    private boolean clearTemp() {
        File tempDir = FileUtils.getCacheDir(getActivity(), "temp");
        if (tempDir.exists()) {
            return FileUtils.deleteFolder(tempDir);
        }
        return true;
    }

    protected final void setCacheMusicPreferenceSummary(Preference preference, int value) {
        preference.setSummary(LocalizationManager.getStringArray(getActivity(), 2131558400)[value]);
    }

    protected final void setDiscussionsBubblePreferenceSummary(Preference preference, int value) {
        preference.setSummary(getResources().getStringArray(2131558415)[value]);
    }

    private String getAppVersionString() {
        String versionName = "";
        try {
            return getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return versionName;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        synchronized (this) {
            if (this.activityResultListeners != null) {
                Iterator i$ = this.activityResultListeners.iterator();
                while (i$.hasNext()) {
                    if (((OnActivityResultListener) i$.next()).onActivityResult(requestCode, resultCode, data)) {
                        break;
                    }
                }
            }
        }
    }

    public void registerOnActivityResultListener(OnActivityResultListener listener) {
        synchronized (this) {
            if (this.activityResultListeners == null) {
                this.activityResultListeners = new ArrayList();
            }
            if (!this.activityResultListeners.contains(listener)) {
                this.activityResultListeners.add(listener);
            }
        }
    }

    public int getNextRequestCode() {
        int i;
        synchronized (this) {
            i = this.nextRequestCode;
            this.nextRequestCode = i + 1;
        }
        return i;
    }
}
