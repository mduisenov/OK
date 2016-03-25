package ru.ok.android.services.processors.stickers;

import android.content.Context;
import org.json.JSONObject;
import ru.ok.android.services.processors.settings.StartSettingsGetProcessor.SettingHandler;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.utils.JsonUtil;

public final class StickersSettingsHandler implements SettingHandler {
    private final Context context;

    public StickersSettingsHandler(Context context) {
        this.context = context;
    }

    public String getSettingsKey() {
        return "sticker.enabled";
    }

    public boolean isSettingsTimeRequestValid() {
        return System.currentTimeMillis() - getLastUpdateSettingTimeMs() >= 86400000;
    }

    public void handleResult(JSONObject json) {
        Logger.m173d("Enabled: %s", Boolean.valueOf(JsonUtil.getBooleanSafely(json, "sticker.enabled")));
        Settings.getEditorInvariable(this.context).putLong("last-pref-update-ms", System.currentTimeMillis()).putBoolean("stickers-enabled", enabled).apply();
    }

    public long getLastUpdateSettingTimeMs() {
        return Settings.getLongValueInvariable(this.context, "last-pref-update-ms", 0);
    }

    public static boolean isStickersEnabled(Context context) {
        return Settings.getBoolValueInvariable(context, "stickers-enabled", false);
    }
}
