package ru.ok.android.services.processors.video.check;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import ru.ok.android.utils.Logger;

public final class VideoPreferences {
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("video_use_exo", 0);
    }

    public static boolean isUseExoPlayer(Context context) {
        boolean z = true;
        try {
            z = getPreferences(context).getBoolean("use.exo", true);
        } catch (Throwable e) {
            Logger.m179e(e, "Failed get use exoplayer from prefs: " + e);
        }
        return z;
    }

    public static void setLastUpdateVideoSettingsDate(Context context) {
        getPreferences(context).edit().putLong("last.video.time", System.currentTimeMillis()).apply();
    }

    public static long getLastUpdateVideoSettingsDate(Context context) {
        long j = 0;
        try {
            j = getPreferences(context).getLong("last.video.time", 0);
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to read last check date from prefs: " + e);
        }
        return j;
    }

    public static void saveUseExo(Context context, boolean useExo) {
        Editor editor = getPreferences(context).edit();
        editor.putBoolean("use.exo", useExo);
        editor.apply();
    }
}
