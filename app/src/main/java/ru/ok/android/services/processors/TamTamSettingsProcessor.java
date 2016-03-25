package ru.ok.android.services.processors;

import android.content.Context;
import org.json.JSONObject;
import ru.ok.android.services.processors.settings.StartSettingsGetProcessor.SettingHandler;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.utils.JsonUtil;

public final class TamTamSettingsProcessor implements SettingHandler {
    private final Context context;

    public TamTamSettingsProcessor(Context context) {
        this.context = context;
    }

    public String getSettingsKey() {
        return "tamtam.settings.authority";
    }

    public boolean isSettingsTimeRequestValid() {
        return true;
    }

    public void handleResult(JSONObject json) {
        Logger.m173d("TamTam package: %s", JsonUtil.getStringSafely(json, "tamtam.settings.authority"));
        Settings.storeStrValueInvariable(this.context, "tamtam.settings.authority", packageName);
    }
}
