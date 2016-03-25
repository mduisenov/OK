package ru.ok.android.services.processors.video.check;

import android.content.Context;
import org.json.JSONObject;
import ru.ok.android.services.processors.settings.StartSettingsGetProcessor.SettingHandler;
import ru.ok.android.utils.Logger;

public class VideoSettingsGetProcessor implements SettingHandler {
    private final Context context;

    public VideoSettingsGetProcessor(Context context) {
        this.context = context;
    }

    public String getSettingsKey() {
        return "video.useExo,video.prerollsPerDay";
    }

    public boolean isSettingsTimeRequestValid() {
        return System.currentTimeMillis() - VideoPreferences.getLastUpdateVideoSettingsDate(this.context) >= 14400000;
    }

    public void handleResult(JSONObject json) {
        saveSettings(parseJson(json));
        VideoPreferences.setLastUpdateVideoSettingsDate(this.context);
        Logger.m172d("Video settings ok save: " + json);
    }

    private static boolean parseJson(JSONObject json) {
        if (json.has("video.useExo")) {
            return json.optBoolean("video.useExo");
        }
        return true;
    }

    private void saveSettings(boolean useExo) {
        VideoPreferences.saveUseExo(this.context, useExo);
    }
}
