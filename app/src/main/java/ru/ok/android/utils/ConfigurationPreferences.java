package ru.ok.android.utils;

import android.content.Context;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Base64;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.config.Preference;
import ru.ok.android.utils.config.PreferenceBuilder;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.utils.Constants.Api;

public final class ConfigurationPreferences {
    private static final Preference DEV;
    private static final String KEY_CUSTOM_SAVE;
    private static final Preference PRODUCTION;
    private static final Preference TEST;
    private static volatile ConfigurationPreferences instance;
    private volatile Preference currentPreference;
    private volatile Preference customPreference;

    /* renamed from: ru.ok.android.utils.ConfigurationPreferences.1 */
    static /* synthetic */ class C14181 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$utils$ConfigurationPreferences$Type;

        static {
            $SwitchMap$ru$ok$android$utils$ConfigurationPreferences$Type = new int[Type.values().length];
            try {
                $SwitchMap$ru$ok$android$utils$ConfigurationPreferences$Type[Type.Custom.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$utils$ConfigurationPreferences$Type[Type.Production.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$utils$ConfigurationPreferences$Type[Type.Test.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$android$utils$ConfigurationPreferences$Type[Type.Dev.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public enum Type {
        Custom,
        Production,
        Test,
        Dev
    }

    static {
        KEY_CUSTOM_SAVE = ConfigurationPreferences.class.getSimpleName() + "_custom_pref_keys_3";
        PRODUCTION = new PreferenceBuilder().build();
        DEV = new PreferenceBuilder().build();
        TEST = new PreferenceBuilder().setApiAddress("https://apitest.odnoklassniki.ru").setAppKey("CBAFJIICABABABABA").setAppSecretKey(Api.m193k()).setWebServer("http://mtest.odnoklassniki.ru/").setLocalePackage("ru.ok.app.android.test").setWmfServer("http://5.61.16.164").setPortalServer("http://test.ok.ru/").setXmppServer("217.20.149.140").build();
    }

    public static ConfigurationPreferences getInstance() {
        if (instance == null) {
            synchronized (ConfigurationPreferences.class) {
                if (instance == null) {
                    instance = new ConfigurationPreferences(OdnoklassnikiApplication.getContext());
                }
            }
        }
        return instance;
    }

    private ConfigurationPreferences(Context context) {
        Parcel parcel;
        try {
            String string = Settings.getStrValueInvariable(context, KEY_CUSTOM_SAVE, "");
            if (!TextUtils.isEmpty(string)) {
                byte[] bs = Base64.decode(string, 0);
                parcel = Parcel.obtain();
                parcel.unmarshall(bs, 0, bs.length);
                parcel.setDataPosition(0);
                this.currentPreference = new Preference(parcel);
                this.customPreference = new Preference(parcel);
                parcel.recycle();
            }
        } catch (Throwable e) {
            Logger.m178e(e);
        } catch (Throwable th) {
            parcel.recycle();
        }
        if (this.customPreference == null) {
            this.customPreference = new PreferenceBuilder(PRODUCTION).build();
        }
        if (this.currentPreference == null || this.currentPreference.equals(this.customPreference)) {
            this.currentPreference = this.customPreference;
        }
    }

    public String getEnvironmentInfo() {
        return getEnvironment().toString();
    }

    public void setPreference(Preference preference) {
        this.currentPreference = preference;
        this.customPreference = preference;
        save();
    }

    public Preference getPreference() {
        return this.currentPreference;
    }

    public void setEnvironmentType(Type environmentType) {
        switch (C14181.$SwitchMap$ru$ok$android$utils$ConfigurationPreferences$Type[environmentType.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.currentPreference = this.customPreference;
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                this.customPreference = this.currentPreference;
                this.currentPreference = PRODUCTION;
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                this.customPreference = this.currentPreference;
                this.currentPreference = TEST;
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                this.customPreference = this.currentPreference;
                this.currentPreference = DEV;
                break;
        }
        save();
    }

    public String getApiAddress() {
        return this.currentPreference.getApiAddress();
    }

    public String getAppKey() {
        return this.currentPreference.getAppKey();
    }

    public String getAppSecretKey() {
        return this.currentPreference.getAppSecretKey();
    }

    public String getWebServer() {
        return this.currentPreference.getWebServer();
    }

    public String getLocalePackage() {
        return this.currentPreference.getLocalePackage();
    }

    public String getWmfServer() {
        return this.currentPreference.getWmfServer();
    }

    public String getPortalServer() {
        return this.currentPreference.getPortalServer();
    }

    public String getXmppServer() {
        return this.currentPreference.getXmppServer();
    }

    public Type getEnvironment() {
        if (this.currentPreference == this.customPreference) {
            return Type.Custom;
        }
        if (this.currentPreference.equals(PRODUCTION)) {
            return Type.Production;
        }
        if (this.currentPreference.equals(TEST)) {
            return Type.Test;
        }
        if (this.currentPreference.equals(DEV)) {
            return Type.Dev;
        }
        return Type.Custom;
    }

    private void save() {
        Parcel parcel;
        try {
            Context context = OdnoklassnikiApplication.getContext();
            parcel = Parcel.obtain();
            this.currentPreference.writeToParcel(parcel, 0);
            this.customPreference.writeToParcel(parcel, 0);
            Settings.storeStrValueInvariable(context, KEY_CUSTOM_SAVE, Base64.encodeToString(parcel.marshall(), 0));
            parcel.recycle();
        } catch (Throwable e) {
            Logger.m178e(e);
        } catch (Throwable th) {
            parcel.recycle();
        }
    }
}
