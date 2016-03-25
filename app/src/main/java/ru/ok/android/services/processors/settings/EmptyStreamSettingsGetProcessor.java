package ru.ok.android.services.processors.settings;

import android.content.Context;
import org.json.JSONObject;
import ru.ok.android.services.processors.settings.StartSettingsGetProcessor.SettingHandler;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.utils.JsonUtil;

public class EmptyStreamSettingsGetProcessor implements SettingHandler {
    public static String KEY_SHOW_PYMK;
    public static String KEY_SHOW_VK_BUTTON;
    public static String KEY_STREAM_SETTINGS;
    private Context context;

    static {
        KEY_STREAM_SETTINGS = "empty.stream.*";
        KEY_SHOW_VK_BUTTON = "empty.stream.show.vk";
        KEY_SHOW_PYMK = "empty.stream.show.pymk";
    }

    public EmptyStreamSettingsGetProcessor(Context context) {
        this.context = context;
    }

    public String getSettingsKey() {
        return KEY_STREAM_SETTINGS;
    }

    public boolean isSettingsTimeRequestValid() {
        return true;
    }

    public static boolean isShowPymk(Context context) {
        return Settings.getBoolValueInvariable(context, KEY_SHOW_PYMK, false);
    }

    public void handleResult(JSONObject json) {
        Settings.getEditorInvariable(this.context).putBoolean(KEY_SHOW_PYMK, JsonUtil.getBooleanSafely(json, KEY_SHOW_PYMK)).apply();
        Settings.getEditorInvariable(this.context).putBoolean(KEY_SHOW_VK_BUTTON, JsonUtil.getBooleanSafely(json, KEY_SHOW_VK_BUTTON)).apply();
    }
}
