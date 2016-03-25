package ru.ok.android.ui.stream.data;

import android.content.Context;
import org.json.JSONObject;
import ru.ok.android.services.processors.settings.StartSettingsGetProcessor.SettingHandler;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.utils.JsonUtil;

public class StreamPmsSettingsHandler implements SettingHandler {
    private final Context context;

    public StreamPmsSettingsHandler(Context context) {
        this.context = context;
    }

    public String getSettingsKey() {
        return "stream.*";
    }

    public boolean isSettingsTimeRequestValid() {
        return true;
    }

    public void handleResult(JSONObject json) {
        StreamSettingsHelper settingsHelper = new StreamSettingsHelper(this.context, StreamContext.stream());
        if (json == null) {
            settingsHelper.clearForceRefreshInterval();
            settingsHelper.clearPositionTtl();
            return;
        }
        long forceRefreshInterval = -1;
        long positionTtl = -1;
        if (json.has("stream.force.refresh.interval")) {
            forceRefreshInterval = JsonUtil.getLongSafely(json, "stream.force.refresh.interval", -1);
            Logger.m173d("stream.force.refresh.interval=%d", Long.valueOf(forceRefreshInterval));
        }
        if (forceRefreshInterval == -1) {
            settingsHelper.clearForceRefreshInterval();
        } else {
            settingsHelper.setForceRefreshInterval(forceRefreshInterval);
        }
        if (json.has("stream.position.ttl")) {
            positionTtl = JsonUtil.getLongSafely(json, "stream.position.ttl", -1);
            Logger.m173d("stream.position.ttl=%d", Long.valueOf(positionTtl));
        }
        if (positionTtl == -1) {
            settingsHelper.clearPositionTtl();
        } else {
            settingsHelper.setPositionTtl(positionTtl);
        }
    }
}
