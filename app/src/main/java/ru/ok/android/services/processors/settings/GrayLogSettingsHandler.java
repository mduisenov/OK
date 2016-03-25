package ru.ok.android.services.processors.settings;

import android.content.Context;
import org.json.JSONObject;
import ru.ok.android.graylog.GrayLog;
import ru.ok.android.services.processors.settings.StartSettingsGetProcessor.SettingHandler;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.utils.JsonUtil;

public class GrayLogSettingsHandler implements SettingHandler {
    public GrayLogSettingsHandler(Context context) {
    }

    public String getSettingsKey() {
        return "client.graylog.enabled";
    }

    public boolean isSettingsTimeRequestValid() {
        return true;
    }

    public void handleResult(JSONObject json) {
        Logger.m173d("Graylog enabled? %s", Boolean.valueOf(JsonUtil.getBooleanSafely(json, "client.graylog.enabled")));
        GrayLog.setEnabled(enabled);
    }
}
