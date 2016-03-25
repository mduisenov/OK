package ru.ok.android.services.processors.xmpp;

import android.content.Context;
import android.content.SharedPreferences;

public final class XmppSettingsPreferences {
    public static XmppSettingsContainer getXmppSettingsContainer(Context context) {
        return XmppSettingsContainer.fromPreferences(getPreferences(context));
    }

    static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("ServicesSettings", 0);
    }

    static long getLastCheckDate(Context context) {
        return getPreferences(context).getLong("xmpp.last.check.time", 0);
    }

    static void touchLastCheckDate(Context context) {
        setLastCheckTime(context, System.currentTimeMillis());
    }

    public static void resetLastCheckDate(Context context) {
        setLastCheckTime(context, 0);
    }

    private static void setLastCheckTime(Context context, long last_check_time_ms) {
        getPreferences(context).edit().putLong("xmpp.last.check.time", last_check_time_ms).apply();
    }
}
