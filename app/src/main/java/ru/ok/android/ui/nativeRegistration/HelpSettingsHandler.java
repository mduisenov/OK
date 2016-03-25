package ru.ok.android.ui.nativeRegistration;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import org.json.JSONObject;
import ru.ok.android.services.processors.settings.StartSettingsGetProcessor.SettingHandler;
import ru.ok.android.utils.settings.Settings;

public class HelpSettingsHandler implements SettingHandler {
    private final Context context;

    public HelpSettingsHandler(Context context) {
        this.context = context.getApplicationContext();
    }

    public String getSettingsKey() {
        return "help.*";
    }

    public boolean isSettingsTimeRequestValid() {
        return true;
    }

    public void handleResult(JSONObject json) {
        Editor editor = Settings.getEditorInvariable(this.context);
        if (json.has("help.feedback.enabled")) {
            editor.putBoolean("help.feedback.enabled", json.optBoolean("help.feedback.enabled", true));
        } else {
            editor.remove("help.feedback.enabled");
        }
        editor.apply();
    }

    public static boolean isFeedbackEnabled(Context context) {
        return Settings.getBoolValueInvariable(context, "help.feedback.enabled", true);
    }
}
