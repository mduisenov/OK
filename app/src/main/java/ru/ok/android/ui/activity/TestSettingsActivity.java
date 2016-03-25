package ru.ok.android.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.services.transport.AuthSessionDataStore;
import ru.ok.android.ui.custom.prefs.ConfirmPreference;
import ru.ok.android.ui.custom.prefs.ConfirmPreference.ConfirmPreferenceListener;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.ConfigurationPreferences.Type;
import ru.ok.android.utils.IntListPreference;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.config.PreferenceBuilder;
import ru.ok.android.utils.localization.base.LocalizedPreferencesActivity;

public class TestSettingsActivity extends LocalizedPreferencesActivity implements OnPreferenceChangeListener {
    private EditTextPreference address;
    private EditTextPreference appKey;
    private EditTextPreference appSecretKey;
    private EditTextPreference deepLinkInstallUrl;
    private IntListPreference deepLinkType;
    private EditTextPreference deepLinkUrl;
    private IntListPreference environment;
    private EditTextPreference locale;
    private EditTextPreference portalServer;
    private ConfirmPreference sessionPreference;
    private EditTextPreference webServer;
    private EditTextPreference wmfServer;
    private EditTextPreference xmppServer;

    /* renamed from: ru.ok.android.ui.activity.TestSettingsActivity.1 */
    class C05601 implements ConfirmPreferenceListener {
        C05601() {
        }

        public void onConfirmed() {
            Logger.m172d("Expiring API key...");
            GlobalBus.send(2131624041, new BusEvent());
        }
    }

    /* renamed from: ru.ok.android.ui.activity.TestSettingsActivity.2 */
    class C05612 implements OnPreferenceChangeListener {
        C05612() {
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            TestSettingsActivity.this.updateDeepLink(newValue);
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.activity.TestSettingsActivity.3 */
    class C05623 implements OnPreferenceChangeListener {
        C05623() {
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (TestSettingsActivity.this.deepLinkType.getIntValue() == 9) {
                PreferenceManager.getDefaultSharedPreferences(TestSettingsActivity.this).edit().putString("deep.link.custom.url", (String) newValue).apply();
                TestSettingsActivity.this.deepLinkUrl.setSummary(String.valueOf(newValue));
            }
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.activity.TestSettingsActivity.4 */
    class C05634 implements OnPreferenceChangeListener {
        C05634() {
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (TestSettingsActivity.this.deepLinkType.getIntValue() == 9) {
                PreferenceManager.getDefaultSharedPreferences(TestSettingsActivity.this).edit().putString("deep.link.custom.install.url", (String) newValue).apply();
                TestSettingsActivity.this.deepLinkInstallUrl.setSummary(String.valueOf(newValue));
            }
            return true;
        }
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        addPreferencesFromResource(2131034120);
        this.environment = (IntListPreference) findPreference(getString(2131166693));
        this.address = (EditTextPreference) findPreference(getString(2131166682));
        this.address.setOnPreferenceChangeListener(this);
        this.appKey = (EditTextPreference) findPreference(getString(2131166687));
        this.appKey.setOnPreferenceChangeListener(this);
        this.appSecretKey = (EditTextPreference) findPreference(getString(2131166689));
        this.appSecretKey.setOnPreferenceChangeListener(this);
        this.webServer = (EditTextPreference) findPreference(getString(2131166707));
        this.webServer.setOnPreferenceChangeListener(this);
        this.locale = (EditTextPreference) findPreference(getString(2131166695));
        this.locale.setOnPreferenceChangeListener(this);
        this.wmfServer = (EditTextPreference) findPreference(getString(2131166709));
        this.wmfServer.setOnPreferenceChangeListener(this);
        this.portalServer = (EditTextPreference) findPreference(getString(2131166705));
        this.portalServer.setOnPreferenceChangeListener(this);
        this.xmppServer = (EditTextPreference) findPreference(getString(2131166711));
        this.xmppServer.setOnPreferenceChangeListener(this);
        PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference(getString(2131166691));
        preferenceScreen.setOnPreferenceChangeListener(this);
        this.sessionPreference = (ConfirmPreference) findPreference(getString(2131166684));
        if (this.sessionPreference != null) {
            this.sessionPreference.setListener(new C05601());
        }
        try {
            preferenceScreen.setSummary(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (Exception e) {
        }
        this.environment.setOnPreferenceChangeListener(this);
        update(this);
        setProgressBarIndeterminateVisibility(false);
        Preference bannerExpirationPref = findPreference("banner_expiration_time");
        if (bannerExpirationPref != null) {
            bannerExpirationPref.setEnabled(false);
        }
        Preference bannerUpdatePref = findPreference("banner_update_interval");
        if (bannerUpdatePref != null) {
            bannerUpdatePref.setEnabled(false);
        }
        initDeepLinkPrefs();
    }

    private void initDeepLinkPrefs() {
        this.deepLinkType = (IntListPreference) findPreference("deep.link.type");
        this.deepLinkUrl = (EditTextPreference) findPreference("deep.link.url");
        this.deepLinkInstallUrl = (EditTextPreference) findPreference("deep.link.install.url");
        this.deepLinkType.setOnPreferenceChangeListener(new C05612());
        this.deepLinkUrl.setOnPreferenceChangeListener(new C05623());
        this.deepLinkInstallUrl.setOnPreferenceChangeListener(new C05634());
        updateDeepLink(null);
    }

    private void updateDeepLink(@Nullable Object newTypeValue) {
        boolean isCustom = true;
        Resources res = getResources();
        int type = this.deepLinkType.getIntValue();
        if (newTypeValue != null) {
            try {
                type = Integer.parseInt((String) newTypeValue);
            } catch (Exception e) {
                Logger.m180e(e, "Failed to parse int pref: %s", e);
            }
        }
        String[] summaryArray = res.getStringArray(2131558412);
        String typeSummary = (type < 0 || type >= summaryArray.length) ? "" : summaryArray[type];
        this.deepLinkType.setSummary(typeSummary);
        if (type != 9) {
            isCustom = false;
        }
        this.deepLinkUrl.setEnabled(isCustom);
        this.deepLinkInstallUrl.setEnabled(isCustom);
        String deepLinkUrlValue = "";
        String deepLinkInstallUrlValue = "";
        if (isCustom) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            deepLinkUrlValue = prefs.getString("deep.link.custom.url", deepLinkUrlValue);
            deepLinkInstallUrlValue = prefs.getString("deep.link.custom.install.url", deepLinkInstallUrlValue);
        } else if (type != 0) {
            String[] urlArray = res.getStringArray(2131558413);
            String[] installUrlArray = res.getStringArray(2131558411);
            if (type >= 0 && type < urlArray.length) {
                deepLinkUrlValue = urlArray[type];
            }
            if (type >= 0 && type < urlArray.length) {
                deepLinkInstallUrlValue = installUrlArray[type];
            }
        }
        this.deepLinkUrl.setText(deepLinkUrlValue);
        this.deepLinkUrl.setSummary(deepLinkUrlValue);
        this.deepLinkInstallUrl.setText(deepLinkInstallUrlValue);
        this.deepLinkInstallUrl.setSummary(deepLinkInstallUrlValue);
    }

    private void update(Context context) {
        ConfigurationPreferences cp = ConfigurationPreferences.getInstance();
        this.environment.setSummary(cp.getEnvironmentInfo());
        this.address.setSummary(cp.getApiAddress());
        this.appKey.setSummary(cp.getAppKey());
        this.appSecretKey.setSummary(cp.getAppSecretKey());
        this.webServer.setSummary(cp.getWebServer());
        this.locale.setSummary(cp.getLocalePackage());
        this.wmfServer.setSummary(cp.getWmfServer());
        this.portalServer.setSummary(cp.getPortalServer());
        this.xmppServer.setSummary(cp.getXmppServer());
        if (cp.getEnvironment() == Type.Custom) {
            this.environment.setValueIndex(0);
        } else if (cp.getEnvironment() == Type.Production) {
            this.environment.setValueIndex(1);
        } else if (cp.getEnvironment() == Type.Test) {
            this.environment.setValueIndex(2);
        } else {
            this.environment.setValueIndex(3);
        }
        this.address.setText(cp.getApiAddress());
        this.appKey.setText(cp.getAppKey());
        this.appSecretKey.setText(cp.getAppSecretKey());
        this.webServer.setText(cp.getWebServer());
        this.locale.setText(cp.getLocalePackage());
        this.wmfServer.setText(cp.getWmfServer());
        this.portalServer.setText(cp.getPortalServer());
        this.xmppServer.setText(cp.getXmppServer());
        this.sessionPreference.setSummary(AuthSessionDataStore.getDefault(context).getSessionKey());
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference != this.environment) {
            PreferenceBuilder builder = new PreferenceBuilder(ConfigurationPreferences.getInstance().getPreference());
            String newValueString = newValue.toString();
            if (preference == this.address) {
                builder.setApiAddress(newValueString);
            } else if (preference == this.appKey) {
                builder.setAppKey(newValueString);
            } else if (preference == this.appSecretKey) {
                builder.setAppSecretKey(newValueString);
            } else if (preference == this.webServer) {
                builder.setWebServer(newValueString);
            } else if (preference == this.locale) {
                builder.setLocalePackage(newValueString);
            } else if (preference == this.wmfServer) {
                builder.setWmfServer(newValueString);
            } else if (preference == this.portalServer) {
                builder.setPortalServer(newValueString);
            } else if (preference == this.xmppServer) {
                builder.setXmppServer(newValueString);
            }
            ConfigurationPreferences.getInstance().setPreference(builder.build());
        } else if ("0".equals(newValue)) {
            ConfigurationPreferences.getInstance().setEnvironmentType(Type.Custom);
        } else if ("1".equals(newValue)) {
            ConfigurationPreferences.getInstance().setEnvironmentType(Type.Production);
        } else if ("2".equals(newValue)) {
            ConfigurationPreferences.getInstance().setEnvironmentType(Type.Test);
        } else {
            ConfigurationPreferences.getInstance().setEnvironmentType(Type.Dev);
        }
        update(this);
        return false;
    }
}
