package ru.ok.android.services.processors.settings;

import android.content.Context;
import org.json.JSONObject;
import ru.ok.android.services.processors.settings.StartSettingsGetProcessor.SettingHandler;
import ru.ok.android.utils.settings.Settings;

public class PhotoRollSettingsHandler implements SettingHandler {
    private final Context context;

    public PhotoRollSettingsHandler(Context context) {
        this.context = context.getApplicationContext();
    }

    public String getSettingsKey() {
        return "stream.photo_roll.*";
    }

    public boolean isSettingsTimeRequestValid() {
        return Settings.hasLoginData(this.context) && PhotoRollSettingsHelper.isReadyToUpdateSettings();
    }

    public void handleResult(JSONObject json) {
        PhotoRollSettingsHelper.parseAndSave(json);
    }
}
