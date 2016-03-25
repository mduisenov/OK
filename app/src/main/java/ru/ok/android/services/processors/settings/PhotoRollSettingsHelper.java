package ru.ok.android.services.processors.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import org.json.JSONObject;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.utils.settings.ServicesSettingsHelper;

public class PhotoRollSettingsHelper {
    private static volatile SettingsHolder settingsHolder;

    public static class SettingsHolder {
        public final boolean enabled;
        public final int maxPhotoCountToShow;
        public final long recentPhotoIntervalMs;
        public final long restIntervalMs;
        private final long settingsLastUpdateTime;
        private final long settingsRefreshIntervalMs;
        public final String style;

        private SettingsHolder(boolean enabled, @NonNull String style, int maxPhotoCountToShow, long restIntervalMs, long recentPhotoIntervalMs, long settingsRefreshIntervalMs, long settingsLastUpdateTime) {
            this.enabled = enabled;
            this.style = style;
            this.maxPhotoCountToShow = maxPhotoCountToShow;
            this.restIntervalMs = restIntervalMs;
            this.recentPhotoIntervalMs = recentPhotoIntervalMs;
            this.settingsRefreshIntervalMs = settingsRefreshIntervalMs;
            this.settingsLastUpdateTime = settingsLastUpdateTime;
        }
    }

    static {
        settingsHolder = fromSharedPreferences(getServiceSettings());
    }

    @NonNull
    public static String[] getFields() {
        return new String[]{"stream.photo_roll.*"};
    }

    public static void parseAndSave(@NonNull JSONObject json) {
        if (json.has("stream.photo_roll.enabled")) {
            SettingsHolder settingsHolder = fromJson(json);
            toSharedPreferences(settingsHolder, getServiceSettings());
            settingsHolder = settingsHolder;
            GlobalBus.send(2131624094, new BusEvent());
        }
    }

    @NonNull
    private static SettingsHolder fromJson(@NonNull JSONObject json) {
        return new SettingsHolder(json.optString("stream.photo_roll.style", "white"), json.optInt("stream.photo_roll.max_photo_count_to_show", 20), json.optLong("stream.photo_roll.rest_interval", 21600000), json.optLong("stream.photo_roll.recent_photo_interval", 172800000), json.optLong("stream.photo_roll.settings_refresh_interval", 21600000), System.currentTimeMillis(), null);
    }

    private static void toSharedPreferences(@NonNull SettingsHolder settingsHolder, @NonNull SharedPreferences prefs) {
        Editor editor = prefs.edit();
        editor.putBoolean("stream.photo_roll.enabled", settingsHolder.enabled).putString("stream.photo_roll.style", settingsHolder.style).putInt("stream.photo_roll.max_photo_count_to_show", settingsHolder.maxPhotoCountToShow).putLong("stream.photo_roll.rest_interval", settingsHolder.restIntervalMs).putLong("stream.photo_roll.recent_photo_interval", settingsHolder.recentPhotoIntervalMs).putLong("stream.photo_roll.settings_refresh_interval", settingsHolder.settingsRefreshIntervalMs).putLong("stream.photo_roll.settings_last_update_time", settingsHolder.settingsLastUpdateTime);
        resetPreferencesIfDisabled(editor, settingsHolder.enabled);
        editor.apply();
    }

    private static void resetPreferencesIfDisabled(@NonNull Editor editor, boolean nowEnabled) {
        if (!nowEnabled) {
            editor.remove("stream.photo_roll.last_closed_time").remove("stream.photo_roll.last_activated_time").remove("stream.photo_roll.first_photo_added_date");
        }
    }

    @NonNull
    private static SettingsHolder fromSharedPreferences(@NonNull SharedPreferences prefs) {
        return new SettingsHolder(prefs.getString("stream.photo_roll.style", "white"), prefs.getInt("stream.photo_roll.max_photo_count_to_show", 20), prefs.getLong("stream.photo_roll.rest_interval", 21600000), prefs.getLong("stream.photo_roll.recent_photo_interval", 172800000), prefs.getLong("stream.photo_roll.settings_refresh_interval", 21600000), prefs.getLong("stream.photo_roll.settings_last_update_time", 0), null);
    }

    public static boolean isReadyToUpdateSettings() {
        SettingsHolder holder = settingsHolder;
        return System.currentTimeMillis() - holder.settingsLastUpdateTime > holder.settingsRefreshIntervalMs;
    }

    public static long getLastClosedTime() {
        return getServiceSettings().getLong("stream.photo_roll.last_closed_time", 0);
    }

    public static void setLastClosedTime(long lastClosedTime) {
        getServiceSettings().edit().putLong("stream.photo_roll.last_closed_time", lastClosedTime).apply();
    }

    public static long getLastActivatedTime() {
        return getServiceSettings().getLong("stream.photo_roll.last_activated_time", 0);
    }

    public static void setLastActivatedTime(long lastActivatedTime) {
        getServiceSettings().edit().putLong("stream.photo_roll.last_activated_time", lastActivatedTime).apply();
    }

    public static long getEarliestPhotoAddedDate() {
        return getServiceSettings().getLong("stream.photo_roll.first_photo_added_date", 0);
    }

    public static void setEarliestPhotoAddedDate(long photoAddedDate) {
        getServiceSettings().edit().putLong("stream.photo_roll.first_photo_added_date", photoAddedDate).apply();
    }

    public static void setUploadAttempt() {
        getServiceSettings().edit().putBoolean("stream.photo_roll.upload_attempt", true).apply();
    }

    public static void clearUploadAttempt() {
        getServiceSettings().edit().remove("stream.photo_roll.upload_attempt").apply();
    }

    public static boolean hasUploadAttempt() {
        return getServiceSettings().contains("stream.photo_roll.upload_attempt");
    }

    public static void setFirstShowAfterClose() {
        getServiceSettings().edit().putBoolean("stream.photo_roll.first_show_after_close", true).apply();
    }

    public static void clearFirstShowAfterClose() {
        getServiceSettings().edit().remove("stream.photo_roll.first_show_after_close").apply();
    }

    public static boolean hasFirstShowAfterClose() {
        return getServiceSettings().contains("stream.photo_roll.first_show_after_close");
    }

    public static SettingsHolder getSettings() {
        return settingsHolder;
    }

    private static SharedPreferences getServiceSettings() {
        return ServicesSettingsHelper.getPreferences(OdnoklassnikiApplication.getContext());
    }

    public static void resetSettingsOnLogout() {
        SettingsHolder settingsHolder = new SettingsHolder("white", 20, 21600000, 172800000, 21600000, 0, null);
        toSharedPreferences(settingsHolder, getServiceSettings());
        settingsHolder = settingsHolder;
    }
}
