package ru.ok.android.utils.settings;

import android.content.Context;
import android.content.SharedPreferences;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.java.api.response.ServicesSettings;

public final class ServicesSettingsHelper {
    public static ServicesSettings getServicesSettings() {
        SharedPreferences preferences = getPreferences();
        return new ServicesSettings(preferences.getInt("multi.chat.max.participants.count", 0), preferences.getInt("multi.chat.max.text.length", 0), preferences.getInt("multi.chat.max.theme.length", 0), preferences.getInt("upload.photo.max.width", 0), preferences.getInt("upload.photo.max.height", 0), preferences.getInt("upload.photo.max.quality", 0), preferences.getInt("audio.attach.recording.max.duration", 0), preferences.getInt("video.attach.recording.max.duration", 0), preferences.getLong("message.edit.timeout", 0));
    }

    public static boolean isTimeToRegularUpdate() {
        long updateTime = getPreferences().getLong("lastUpdateTime", 0);
        if (updateTime != 0 && System.currentTimeMillis() - updateTime < 86400000) {
            return false;
        }
        return true;
    }

    private static SharedPreferences getPreferences() {
        return getPreferences(OdnoklassnikiApplication.getContext());
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("ServicesSettings", 0);
    }
}
