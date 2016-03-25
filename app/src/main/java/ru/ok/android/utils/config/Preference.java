package ru.ok.android.utils.config;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import ru.ok.java.api.utils.ObjectUtils;

public class Preference implements Parcelable {
    public static final Creator<Preference> CREATOR;
    private final String apiAddress;
    private final String appKey;
    private final String appSecretKey;
    private final String localePackage;
    private final String portalServer;
    private final String webServer;
    private final String wmfServer;
    private final String xmppServer;

    /* renamed from: ru.ok.android.utils.config.Preference.1 */
    static class C14381 implements Creator<Preference> {
        C14381() {
        }

        public Preference createFromParcel(Parcel in) {
            return new Preference(in);
        }

        public Preference[] newArray(int size) {
            return new Preference[size];
        }
    }

    public Preference(String apiAddress, String appKey, String appSecretKey, String webServer, String localePackage, String wmfServer, String portalServer, String xmppServer) {
        this.apiAddress = apiAddress;
        this.appKey = appKey;
        this.appSecretKey = appSecretKey;
        this.webServer = webServer;
        this.localePackage = localePackage;
        this.wmfServer = wmfServer;
        this.portalServer = portalServer;
        this.xmppServer = xmppServer;
    }

    public Preference(Parcel in) {
        this.apiAddress = in.readString();
        this.appKey = in.readString();
        this.appSecretKey = in.readString();
        this.webServer = in.readString();
        this.localePackage = in.readString();
        this.wmfServer = in.readString();
        this.portalServer = in.dataAvail() > 0 ? in.readString() : "";
        this.xmppServer = in.dataAvail() > 0 ? in.readString() : "";
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.apiAddress);
        dest.writeString(this.appKey);
        dest.writeString(this.appSecretKey);
        dest.writeString(this.webServer);
        dest.writeString(this.localePackage);
        dest.writeString(this.wmfServer);
        dest.writeString(this.portalServer);
        dest.writeString(this.xmppServer);
    }

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new C14381();
    }

    public String getApiAddress() {
        return this.apiAddress;
    }

    public String getAppKey() {
        return this.appKey;
    }

    public String getAppSecretKey() {
        return this.appSecretKey;
    }

    public String getWebServer() {
        return this.webServer;
    }

    public String getLocalePackage() {
        return this.localePackage;
    }

    public String getWmfServer() {
        return this.wmfServer;
    }

    public String getPortalServer() {
        return this.portalServer;
    }

    public String getXmppServer() {
        return this.xmppServer;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Preference)) {
            return false;
        }
        Preference that = (Preference) o;
        if (ObjectUtils.equals(that.getApiAddress(), this.apiAddress) && ObjectUtils.equals(that.getAppKey(), this.appKey) && ObjectUtils.equals(that.getAppSecretKey(), this.appSecretKey) && ObjectUtils.equals(that.getLocalePackage(), this.localePackage) && ObjectUtils.equals(that.getWebServer(), this.webServer) && ObjectUtils.equals(that.getWmfServer(), this.wmfServer) && ObjectUtils.equals(that.getPortalServer(), this.portalServer) && ObjectUtils.equals(that.getXmppServer(), this.xmppServer)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result;
        int hashCode;
        int i = 0;
        if (this.apiAddress != null) {
            result = this.apiAddress.hashCode();
        } else {
            result = 0;
        }
        int i2 = result * 31;
        if (this.appKey != null) {
            hashCode = this.appKey.hashCode();
        } else {
            hashCode = 0;
        }
        i2 = (i2 + hashCode) * 31;
        if (this.appSecretKey != null) {
            hashCode = this.appSecretKey.hashCode();
        } else {
            hashCode = 0;
        }
        i2 = (i2 + hashCode) * 31;
        if (this.webServer != null) {
            hashCode = this.webServer.hashCode();
        } else {
            hashCode = 0;
        }
        i2 = (i2 + hashCode) * 31;
        if (this.localePackage != null) {
            hashCode = this.localePackage.hashCode();
        } else {
            hashCode = 0;
        }
        i2 = (i2 + hashCode) * 31;
        if (this.wmfServer != null) {
            hashCode = this.wmfServer.hashCode();
        } else {
            hashCode = 0;
        }
        i2 = (i2 + hashCode) * 31;
        if (this.portalServer != null) {
            hashCode = this.portalServer.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode = (i2 + hashCode) * 31;
        if (this.xmppServer != null) {
            i = this.xmppServer.hashCode();
        }
        return hashCode + i;
    }
}
