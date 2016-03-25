package ru.ok.android.services.processors.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import org.json.JSONObject;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.utils.settings.ServicesSettingsHelper;

public class GifSettings {
    private static volatile GifSettingsHolder gifSettingsHolder;

    private static class GifSettingsHolder {
        final boolean isAutoPlay;
        final boolean isAutoPlayInConversation;
        final boolean isGifEnabled;
        final long lastUpdateTime;

        public GifSettingsHolder(boolean isAutoPlay, boolean isAutoPlayInConversation, boolean isGifEnabled, long lastUpdateTime) {
            this.isAutoPlay = isAutoPlay;
            this.isAutoPlayInConversation = isAutoPlayInConversation;
            this.isGifEnabled = isGifEnabled;
            this.lastUpdateTime = lastUpdateTime;
        }
    }

    static {
        gifSettingsHolder = fromSharedPreferences(ServicesSettingsHelper.getPreferences(OdnoklassnikiApplication.getContext()));
    }

    @NonNull
    public static String[] getFields() {
        return new String[]{"photo.gif.*"};
    }

    public static void parseAndSave(@NonNull JSONObject json) {
        if (json.has("photo.gif.autoplay")) {
            GifSettingsHolder settingsHolder = fromJson(json);
            toSharedPreferences(settingsHolder, ServicesSettingsHelper.getPreferences(OdnoklassnikiApplication.getContext()));
            gifSettingsHolder = settingsHolder;
        }
    }

    @NonNull
    private static GifSettingsHolder fromJson(@NonNull JSONObject json) {
        return new GifSettingsHolder(json.optBoolean("photo.gif.autoplay", false), json.optBoolean("photo.gif.autoplay.conversation", false), json.optBoolean("photo.gif.enabled", false), System.currentTimeMillis());
    }

    private static void toSharedPreferences(@NonNull GifSettingsHolder settingsHolder, @NonNull SharedPreferences prefs) {
        Editor editor = prefs.edit();
        editor.putBoolean("photo.gif.autoplay", settingsHolder.isAutoPlay);
        editor.putBoolean("photo.gif.autoplay.conversation", settingsHolder.isAutoPlayInConversation);
        editor.putBoolean("photo.gif.enabled", settingsHolder.isGifEnabled);
        editor.putLong("photo.gif.last_update_time", settingsHolder.lastUpdateTime);
        editor.apply();
    }

    @NonNull
    private static GifSettingsHolder fromSharedPreferences(@NonNull SharedPreferences prefs) {
        return new GifSettingsHolder(prefs.getBoolean("photo.gif.autoplay", false), prefs.getBoolean("photo.gif.autoplay.conversation", false), prefs.getBoolean("photo.gif.enabled", false), prefs.getLong("photo.gif.last_update_time", 0));
    }

    public static boolean isReadyToUpdateGifSettings() {
        return System.currentTimeMillis() - gifSettingsHolder.lastUpdateTime > 21600000;
    }

    public static boolean isAutoPlay() {
        Context appContext = OdnoklassnikiApplication.getContext();
        if (isGifEnabled() && PreferenceManager.getDefaultSharedPreferences(appContext).getBoolean(appContext.getString(2131165904), true)) {
            return true;
        }
        return false;
    }

    public static boolean isAutoPlayInConversation() {
        return isAutoPlay();
    }

    public static boolean isGifEnabled() {
        return gifSettingsHolder.isGifEnabled;
    }
}
