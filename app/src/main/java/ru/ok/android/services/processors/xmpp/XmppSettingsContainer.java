package ru.ok.android.services.processors.xmpp;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import ru.ok.android.utils.Logger;

public class XmppSettingsContainer implements Parcelable {
    public static final Creator<XmppSettingsContainer> CREATOR;
    public final int delayBetweenComposingAndComposing;
    public final int delayBetweenComposingAndPaused;
    public final int delayBetweenResetAndPaused;
    public final boolean isPushFeatureComposingEnabled;
    public final boolean isPushFeatureMessageReadEnabled;
    public final boolean isPushFeatureNewMessageEnabled;
    public final boolean isXmppEnabled;

    /* renamed from: ru.ok.android.services.processors.xmpp.XmppSettingsContainer.1 */
    static class C05161 implements Creator<XmppSettingsContainer> {
        C05161() {
        }

        public XmppSettingsContainer createFromParcel(Parcel source) {
            return new XmppSettingsContainer(source);
        }

        public XmppSettingsContainer[] newArray(int size) {
            return new XmppSettingsContainer[size];
        }
    }

    private XmppSettingsContainer(boolean isXmppEnabled, boolean isPushFeatureComposingEnabled, int delayBetweenComposingAndComposing, int delayBetweenComposingAndPaused, int delayBetweenResetAndPaused, boolean isPushFeatureNewMessageEnabled, boolean isPushFeatureMessageReadEnabled) {
        this.isXmppEnabled = isXmppEnabled;
        this.isPushFeatureComposingEnabled = isPushFeatureComposingEnabled;
        this.delayBetweenComposingAndComposing = delayBetweenComposingAndComposing;
        this.delayBetweenComposingAndPaused = delayBetweenComposingAndPaused;
        this.delayBetweenResetAndPaused = delayBetweenResetAndPaused;
        this.isPushFeatureNewMessageEnabled = isPushFeatureNewMessageEnabled;
        this.isPushFeatureMessageReadEnabled = isPushFeatureMessageReadEnabled;
    }

    void toSharedPreferences(Editor editor) {
        editor.putBoolean("xmpp.isEnabled", this.isXmppEnabled);
        editor.putInt("xmpp.delay.composing.composing", this.delayBetweenComposingAndComposing);
        editor.putInt("xmpp.delay.composing.paused", this.delayBetweenComposingAndPaused);
        editor.putInt("xmpp.delay.delay.reset.paused", this.delayBetweenResetAndPaused);
        editor.putBoolean("xmpp.push.composing", this.isPushFeatureComposingEnabled);
        editor.putBoolean("xmpp.push.newmessage", this.isPushFeatureNewMessageEnabled);
        editor.putBoolean("xmpp.push.messageread", this.isPushFeatureMessageReadEnabled);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        byte b;
        byte b2 = (byte) 1;
        dest.writeByte(this.isXmppEnabled ? (byte) 1 : (byte) 0);
        dest.writeInt(this.delayBetweenComposingAndComposing);
        dest.writeInt(this.delayBetweenComposingAndPaused);
        dest.writeInt(this.delayBetweenResetAndPaused);
        if (this.isPushFeatureComposingEnabled) {
            b = (byte) 1;
        } else {
            b = (byte) 0;
        }
        dest.writeByte(b);
        if (this.isPushFeatureNewMessageEnabled) {
            b = (byte) 1;
        } else {
            b = (byte) 0;
        }
        dest.writeByte(b);
        if (!this.isPushFeatureMessageReadEnabled) {
            b2 = (byte) 0;
        }
        dest.writeByte(b2);
    }

    XmppSettingsContainer(Parcel src) {
        boolean z;
        boolean z2 = true;
        this.isXmppEnabled = src.readByte() != null;
        this.delayBetweenComposingAndComposing = src.readInt();
        this.delayBetweenComposingAndPaused = src.readInt();
        this.delayBetweenResetAndPaused = src.readInt();
        if (src.readByte() != null) {
            z = true;
        } else {
            z = false;
        }
        this.isPushFeatureComposingEnabled = z;
        if (src.readByte() != null) {
            z = true;
        } else {
            z = false;
        }
        this.isPushFeatureNewMessageEnabled = z;
        if (src.readByte() == null) {
            z2 = false;
        }
        this.isPushFeatureMessageReadEnabled = z2;
    }

    public String toString() {
        return "XmppSettingsParcel[isXmppEnabled=" + this.isXmppEnabled + " delayBetweenComposingAndComposing=" + this.delayBetweenComposingAndComposing + " delayBetweenComposingAndPaused=" + this.delayBetweenComposingAndPaused + " delayBetweenResetAndPaused=" + this.delayBetweenResetAndPaused + " isPushFeatureComposingEnabled=" + this.isPushFeatureComposingEnabled + " isPushFeatureNewMessageEnabled=" + this.isPushFeatureNewMessageEnabled + " isPushFeatureMessageReadEnabled=" + this.isPushFeatureMessageReadEnabled + "]";
    }

    static XmppSettingsContainer fromPreferences(SharedPreferences prefs) {
        try {
            return new XmppSettingsContainer(prefs.getBoolean("xmpp.isEnabled", false), prefs.getBoolean("xmpp.push.composing", false), prefs.getInt("xmpp.delay.composing.composing", 10000), prefs.getInt("xmpp.delay.composing.paused", 5000), prefs.getInt("xmpp.delay.delay.reset.paused", 3000), prefs.getBoolean("xmpp.push.newmessage", false), prefs.getBoolean("xmpp.push.messageread", false));
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to read XmppSettingsParcel from prefs: " + e);
            return new XmppSettingsContainer(false, false, 10000, 5000, 3000, false, false);
        }
    }

    static XmppSettingsContainer create(boolean isXmppEnabled, boolean isPushFeatureComposingEnabled, int delayBetweenComposingAndComposing, int delayBetweenComposingAndPaused, int delayBetweenResetAndPaused, boolean isPushFeatureNewMessageEnabled, boolean isPushFeatureMessageReadEnabled) {
        return new XmppSettingsContainer(isXmppEnabled, isPushFeatureComposingEnabled, delayBetweenComposingAndComposing, delayBetweenComposingAndPaused, delayBetweenResetAndPaused, isPushFeatureNewMessageEnabled, isPushFeatureMessageReadEnabled);
    }

    static {
        CREATOR = new C05161();
    }
}
