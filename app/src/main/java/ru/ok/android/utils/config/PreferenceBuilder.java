package ru.ok.android.utils.config;

import ru.ok.java.api.utils.Constants.Api;

public class PreferenceBuilder {
    private String apiAddress;
    private String appKey;
    private String appSecretKey;
    private String localePackage;
    private String portalServer;
    private String webServer;
    private String wmfServer;
    private String xmppServer;

    public PreferenceBuilder() {
        this.apiAddress = "https://api.odnoklassniki.ru";
        this.appKey = "CBAFJIICABABABABA";
        this.appSecretKey = Api.m193k();
        this.webServer = "http://m.odnoklassniki.ru/";
        this.localePackage = "ru.ok.app.android.0";
        this.wmfServer = "http://wmf1.odnoklassniki.ru";
        this.portalServer = "https://ok.ru/";
        this.xmppServer = "xmpp.odnoklassniki.ru";
    }

    public PreferenceBuilder(Preference preference) {
        this.apiAddress = "https://api.odnoklassniki.ru";
        this.appKey = "CBAFJIICABABABABA";
        this.appSecretKey = Api.m193k();
        this.webServer = "http://m.odnoklassniki.ru/";
        this.localePackage = "ru.ok.app.android.0";
        this.wmfServer = "http://wmf1.odnoklassniki.ru";
        this.portalServer = "https://ok.ru/";
        this.xmppServer = "xmpp.odnoklassniki.ru";
        from(preference);
    }

    public PreferenceBuilder setApiAddress(String apiAddress) {
        this.apiAddress = apiAddress;
        return this;
    }

    public PreferenceBuilder setAppKey(String appKey) {
        this.appKey = appKey;
        return this;
    }

    public PreferenceBuilder setAppSecretKey(String appSecretKey) {
        this.appSecretKey = appSecretKey;
        return this;
    }

    public PreferenceBuilder setWebServer(String webServer) {
        this.webServer = webServer;
        return this;
    }

    public PreferenceBuilder setLocalePackage(String localePackage) {
        this.localePackage = localePackage;
        return this;
    }

    public PreferenceBuilder setWmfServer(String wmfServer) {
        this.wmfServer = wmfServer;
        return this;
    }

    public PreferenceBuilder setPortalServer(String portalServer) {
        this.portalServer = portalServer;
        return this;
    }

    public PreferenceBuilder setXmppServer(String xmppServer) {
        this.xmppServer = xmppServer;
        return this;
    }

    public void from(Preference preference) {
        this.apiAddress = preference.getApiAddress();
        this.appKey = preference.getAppKey();
        this.appSecretKey = preference.getAppSecretKey();
        this.webServer = preference.getWebServer();
        this.localePackage = preference.getLocalePackage();
        this.wmfServer = preference.getWmfServer();
        this.portalServer = preference.getPortalServer();
        this.xmppServer = preference.getXmppServer();
    }

    public Preference build() {
        return new Preference(this.apiAddress, this.appKey, this.appSecretKey, this.webServer, this.localePackage, this.wmfServer, this.portalServer, this.xmppServer);
    }
}
