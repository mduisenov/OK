package ru.ok.android.services.processors.messaging;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import org.json.JSONObject;
import ru.ok.android.services.processors.settings.StartSettingsGetProcessor.SettingHandler;
import ru.ok.android.utils.settings.ServicesSettingsHelper;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.utils.JsonUtil;

public class MessagesSettingsHandler implements SettingHandler {
    private final Context context;

    public MessagesSettingsHandler(Context context) {
        this.context = context;
    }

    public String getSettingsKey() {
        return "multi.chat.max.participants.count,multi.chat.max.text.length,multi.chat.max.theme.length,message.edit.timeout";
    }

    public boolean isSettingsTimeRequestValid() {
        return System.currentTimeMillis() - getLastUpdateSettingTimeMs() >= 86400000;
    }

    private long getLastUpdateSettingTimeMs() {
        return Settings.getLongValueInvariable(this.context, "messages-settings-last-update-time-ms", 0);
    }

    public void handleResult(JSONObject json) {
        Editor editor = ServicesSettingsHelper.getPreferences(this.context).edit();
        applyKeyInt(json, editor, "multi.chat.max.participants.count");
        applyKeyInt(json, editor, "multi.chat.max.text.length");
        applyKeyInt(json, editor, "multi.chat.max.theme.length");
        applyKeyLong(json, editor, "message.edit.timeout");
        editor.apply();
        Settings.storeLongValueInvariable(this.context, "messages-settings-last-update-time-ms", System.currentTimeMillis());
    }

    private static void applyKeyLong(JSONObject json, Editor editor, String key) {
        if (json.has(key)) {
            editor.putLong(key, JsonUtil.getLongSafely(json, key));
        }
    }

    private static void applyKeyInt(JSONObject json, Editor editor, String key) {
        if (json.has(key)) {
            editor.putInt(key, JsonUtil.getIntSafely(json, key));
        }
    }
}
