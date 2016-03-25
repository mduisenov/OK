package ru.ok.android.services.processors.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Utils;

public class AvailableUpdateInfo implements Parcelable {
    public static final Creator<AvailableUpdateInfo> CREATOR;
    public final String appStoreUrl;
    public final boolean forceUpdate;
    public final String fromFingerprint;
    public final int fromVersionCode;
    public final boolean isUpdateAvailable;
    public final String message;
    public final int remindIntervalSec;
    public final int toVersionCode;

    /* renamed from: ru.ok.android.services.processors.update.AvailableUpdateInfo.1 */
    static class C04971 implements Creator<AvailableUpdateInfo> {
        C04971() {
        }

        public AvailableUpdateInfo createFromParcel(Parcel source) {
            return new AvailableUpdateInfo(source);
        }

        public AvailableUpdateInfo[] newArray(int size) {
            return new AvailableUpdateInfo[size];
        }
    }

    private AvailableUpdateInfo(boolean isUpdateAvailable, int fromVersionCode, int toVersionCode, String fromBuildId, String message, String appStoreUrl, int remindIntervalSec, boolean forceUpdate) {
        this.isUpdateAvailable = isUpdateAvailable;
        this.fromVersionCode = fromVersionCode;
        this.toVersionCode = toVersionCode;
        this.fromFingerprint = fromBuildId;
        this.message = message;
        this.appStoreUrl = appStoreUrl;
        this.remindIntervalSec = remindIntervalSec;
        this.forceUpdate = forceUpdate;
    }

    public boolean isApplicable(Context context) {
        if (Utils.getVersionCode(context) != this.fromVersionCode) {
            return false;
        }
        return TextUtils.equals(Build.FINGERPRINT, this.fromFingerprint);
    }

    void toSharedPreferences(Editor editor) {
        editor.putBoolean("available.update.is.available", this.isUpdateAvailable);
        if (this.isUpdateAvailable) {
            editor.putInt("available.update.from.version.code", this.fromVersionCode);
            editor.putInt("available.update.to.version.code", this.toVersionCode);
            updateString(editor, "available.update.from.fingerprint", this.fromFingerprint);
            updateString(editor, "available.update.message", this.message);
            updateString(editor, "available.update.app.store.url", this.appStoreUrl);
            if (this.remindIntervalSec == -1) {
                editor.remove("available.update.remind.interval.sec");
            } else {
                editor.putInt("available.update.remind.interval.sec", this.remindIntervalSec);
            }
            editor.putBoolean("available.update.force", this.forceUpdate);
            return;
        }
        editor.remove("available.update.from.version.code");
        editor.remove("available.update.to.version.code");
        editor.remove("available.update.from.fingerprint");
        editor.remove("available.update.message");
        editor.remove("available.update.app.store.url");
        editor.remove("available.update.remind.interval.sec");
        editor.remove("available.update.force");
    }

    void updateString(Editor editor, String key, String value) {
        if (value == null) {
            editor.remove(key);
        } else {
            editor.putString(key, value);
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        byte b = (byte) 1;
        dest.writeByte(this.isUpdateAvailable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.fromVersionCode);
        dest.writeInt(this.toVersionCode);
        dest.writeString(this.fromFingerprint);
        dest.writeString(this.message);
        dest.writeString(this.appStoreUrl);
        dest.writeInt(this.remindIntervalSec);
        if (!this.forceUpdate) {
            b = (byte) 0;
        }
        dest.writeByte(b);
    }

    AvailableUpdateInfo(Parcel src) {
        boolean z = true;
        this.isUpdateAvailable = src.readByte() != null;
        this.fromVersionCode = src.readInt();
        this.toVersionCode = src.readInt();
        this.fromFingerprint = src.readString();
        this.message = src.readString();
        this.appStoreUrl = src.readString();
        this.remindIntervalSec = src.readInt();
        if (src.readByte() == null) {
            z = false;
        }
        this.forceUpdate = z;
    }

    public String toString() {
        return "AvailableUpdateInfo[isAvailable=" + this.isUpdateAvailable + " fromVersionCode=" + this.fromVersionCode + " toVersionCode=" + this.toVersionCode + " fromFingerprint=" + this.fromFingerprint + " message=" + this.message + " appStoreUrl=" + this.appStoreUrl + " remindIntervalSec=" + this.remindIntervalSec + " forceUpdate=" + this.forceUpdate + "]";
    }

    static AvailableUpdateInfo fromPreferences(SharedPreferences prefs) {
        try {
            if (prefs.getBoolean("available.update.is.available", false)) {
                int fromVersion = prefs.getInt("available.update.from.version.code", -1);
                int toVersion = prefs.getInt("available.update.to.version.code", -1);
                String fromFingerprint = prefs.getString("available.update.from.fingerprint", null);
                String message = prefs.getString("available.update.message", null);
                String appStoreUrl = prefs.getString("available.update.app.store.url", null);
                int remindIntervalSec = prefs.getInt("available.update.remind.interval.sec", -1);
                boolean forceUpdate = prefs.getBoolean("available.update.force", false);
                if (fromVersion > 0 && toVersion > 0 && fromFingerprint != null) {
                    return new AvailableUpdateInfo(true, fromVersion, toVersion, fromFingerprint, message, appStoreUrl, remindIntervalSec, forceUpdate);
                }
            }
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to read available update info from prefs: " + e);
        }
        return notAvailable();
    }

    static AvailableUpdateInfo notAvailable() {
        return new AvailableUpdateInfo(false, 0, 0, null, null, null, -1, false);
    }

    static AvailableUpdateInfo available(Context context, int toVersionCode, String message, String appStoreUrl, int remindIntervalSec, boolean forceUpdate) {
        return new AvailableUpdateInfo(true, Utils.getVersionCode(context), toVersionCode, Build.FINGERPRINT, message, appStoreUrl, remindIntervalSec, forceUpdate);
    }

    static {
        CREATOR = new C04971();
    }
}
