package ru.ok.model.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import org.json.JSONObject;

public class PresentsSettings extends PMSSettings {
    public static final Creator<PresentsSettings> CREATOR;
    public boolean animatedPresentsEnabled;
    public boolean nativeMySentAndReceivedEnabled;
    public boolean nativeSendEnabled;
    public boolean streamHolidaysEnabled;

    /* renamed from: ru.ok.model.settings.PresentsSettings.1 */
    static class C15931 implements Creator<PresentsSettings> {
        C15931() {
        }

        public PresentsSettings createFromParcel(Parcel source) {
            return new PresentsSettings(source);
        }

        public PresentsSettings[] newArray(int size) {
            return new PresentsSettings[size];
        }
    }

    public PresentsSettings() {
        this.nativeSendEnabled = false;
        this.streamHolidaysEnabled = false;
        this.nativeMySentAndReceivedEnabled = false;
        this.animatedPresentsEnabled = false;
    }

    public PresentsSettings(@NonNull Parcel parcel) {
        boolean z;
        boolean z2 = true;
        this.nativeSendEnabled = false;
        this.streamHolidaysEnabled = false;
        this.nativeMySentAndReceivedEnabled = false;
        this.animatedPresentsEnabled = false;
        if (parcel.readInt() == 1) {
            z = true;
        } else {
            z = false;
        }
        this.nativeSendEnabled = z;
        if (parcel.readInt() == 1) {
            z = true;
        } else {
            z = false;
        }
        this.streamHolidaysEnabled = z;
        if (parcel.readInt() == 1) {
            z = true;
        } else {
            z = false;
        }
        this.nativeMySentAndReceivedEnabled = z;
        if (parcel.readInt() != 1) {
            z2 = false;
        }
        this.animatedPresentsEnabled = z2;
    }

    @NonNull
    public static PresentsSettings fromJson(@NonNull JSONObject json) {
        PresentsSettings settings = new PresentsSettings();
        settings.nativeSendEnabled = json.optBoolean("presents.nativeSendEnabled", false);
        settings.streamHolidaysEnabled = json.optBoolean("presents.streamHolidaysEnabled", false);
        settings.nativeMySentAndReceivedEnabled = json.optBoolean("presents.nativeMySentAndReceivedEnabled", false);
        settings.animatedPresentsEnabled = json.optBoolean("presents.animatedPresentsEnabled", false);
        return settings;
    }

    @NonNull
    public static PresentsSettings fromSharedPreferences(SharedPreferences prefs) {
        PresentsSettings settings = new PresentsSettings();
        Editor[] cleanupEditor = new Editor[1];
        settings.nativeSendEnabled = PMSSettings.readBooleanPref(prefs, "presents.nativeSendEnabled", false, cleanupEditor);
        settings.streamHolidaysEnabled = PMSSettings.readBooleanPref(prefs, "presents.streamHolidaysEnabled", false, cleanupEditor);
        settings.nativeMySentAndReceivedEnabled = PMSSettings.readBooleanPref(prefs, "presents.nativeMySentAndReceivedEnabled", false, cleanupEditor);
        settings.animatedPresentsEnabled = PMSSettings.readBooleanPref(prefs, "presents.animatedPresentsEnabled", false, cleanupEditor);
        if (cleanupEditor[0] != null) {
            cleanupEditor[0].apply();
        }
        return settings;
    }

    public void toSharedPreferences(SharedPreferences prefs) {
        Editor editor = prefs.edit();
        editor.putBoolean("presents.nativeSendEnabled", this.nativeSendEnabled);
        editor.putBoolean("presents.streamHolidaysEnabled", this.streamHolidaysEnabled);
        editor.putBoolean("presents.nativeMySentAndReceivedEnabled", this.nativeMySentAndReceivedEnabled);
        editor.putBoolean("presents.animatedPresentsEnabled", this.animatedPresentsEnabled);
        editor.apply();
    }

    public String toString() {
        return "PresentsSettings[nativeSendEnabled=" + this.nativeSendEnabled + "streamHolidaysEnabled=" + this.streamHolidaysEnabled + "nativeMySentAndReceivedEnabled=" + this.nativeMySentAndReceivedEnabled + "animatedPresentsEnabled=" + this.animatedPresentsEnabled + "]";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        int i;
        int i2 = 1;
        if (this.nativeSendEnabled) {
            i = 1;
        } else {
            i = 0;
        }
        parcel.writeInt(i);
        if (this.streamHolidaysEnabled) {
            i = 1;
        } else {
            i = 0;
        }
        parcel.writeInt(i);
        if (this.nativeMySentAndReceivedEnabled) {
            i = 1;
        } else {
            i = 0;
        }
        parcel.writeInt(i);
        if (!this.animatedPresentsEnabled) {
            i2 = 0;
        }
        parcel.writeInt(i2);
    }

    static {
        CREATOR = new C15931();
    }
}
