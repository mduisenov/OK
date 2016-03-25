package ru.ok.android.services.processors.settings;

import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import org.json.JSONObject;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.utils.settings.ServicesSettingsHelper;

public class PhotoCollageSettings {
    private static Boolean isCollageEnabled;

    static {
        isCollageEnabled = null;
    }

    public static synchronized boolean isPhotoCollageEnabled() {
        boolean booleanValue;
        synchronized (PhotoCollageSettings.class) {
            if (isCollageEnabled == null) {
                isCollageEnabled = Boolean.valueOf(ServicesSettingsHelper.getPreferences(OdnoklassnikiApplication.getContext()).getBoolean("stream.photo.collage.enabled", true));
            }
            booleanValue = isCollageEnabled.booleanValue();
        }
        return booleanValue;
    }

    private static synchronized void setPhotoCollageEnabled(boolean isCollageEnabled) {
        synchronized (PhotoCollageSettings.class) {
            Editor editor = ServicesSettingsHelper.getPreferences(OdnoklassnikiApplication.getContext()).edit();
            editor.putBoolean("stream.photo.collage.enabled", isCollageEnabled);
            editor.putLong("stream.photo.collage.enabled_last_update_time", System.currentTimeMillis());
            editor.apply();
            isCollageEnabled = Boolean.valueOf(isCollageEnabled);
        }
    }

    public static synchronized boolean isReadyToUpdateCollageEnabled() {
        boolean z;
        synchronized (PhotoCollageSettings.class) {
            z = System.currentTimeMillis() - ServicesSettingsHelper.getPreferences(OdnoklassnikiApplication.getContext()).getLong("stream.photo.collage.enabled_last_update_time", 0) > Long.parseLong(PreferenceManager.getDefaultSharedPreferences(OdnoklassnikiApplication.getContext()).getString("stream_photo_collage_update_interval", "21600")) * 1000;
        }
        return z;
    }

    public static void parseAndSave(@NonNull JSONObject photoCollageObject) {
        if (photoCollageObject.has("stream.photo.collage.enabled")) {
            setPhotoCollageEnabled(photoCollageObject.optBoolean("stream.photo.collage.enabled", true));
        }
    }

    @NonNull
    public static String[] getFields() {
        return new String[]{"stream.photo.collage.enabled"};
    }
}
