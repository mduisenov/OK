package ru.ok.android.services.processors.update;

import android.content.Context;
import android.content.SharedPreferences;
import ru.ok.android.utils.Logger;

public final class CheckUpdatePreferences {
    public static AvailableUpdateInfo getAvailableUpdateInfo(Context context) {
        return AvailableUpdateInfo.fromPreferences(getPreferences(context));
    }

    static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("check_update", 0);
    }

    static long getLastCheckDate(Context context) {
        long j = 0;
        try {
            j = getPreferences(context).getLong("last.check.date", 0);
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to read last check date from prefs: " + e);
        }
        return j;
    }

    static void touchLastCheckDate(Context context) {
        getPreferences(context).edit().putLong("last.check.date", System.currentTimeMillis()).apply();
    }
}
