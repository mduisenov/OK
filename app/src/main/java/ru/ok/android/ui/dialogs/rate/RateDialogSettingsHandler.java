package ru.ok.android.ui.dialogs.rate;

import android.content.Context;
import org.json.JSONObject;
import ru.ok.android.services.processors.settings.StartSettingsGetProcessor.SettingHandler;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.utils.JsonUtil;

public class RateDialogSettingsHandler implements SettingHandler {
    private final Context context;

    public RateDialogSettingsHandler(Context context) {
        this.context = context;
    }

    public String getSettingsKey() {
        return "rate.dialog.*";
    }

    public boolean isSettingsTimeRequestValid() {
        return true;
    }

    public void handleResult(JSONObject json) {
        Logger.m173d("%s=%s", "rate.dialog.feature", JsonUtil.optStringOrNull(json, "rate.dialog.feature"));
        Settings.storeStrValueInvariable(this.context, "rate.dialog.feature", feature);
        Logger.m173d("minLaunches=%d", Integer.valueOf(parseInt(json, "rate.dialog.interval.launches", Integer.MAX_VALUE)));
        Settings.storeIntValueInvariable(this.context, "rate.dialog.interval.launches", minLaunches);
        Logger.m173d("minTimeSec=%d", Integer.valueOf(parseInt(json, "rate.dialog.interval.time", Integer.MAX_VALUE)));
        Settings.storeIntValueInvariable(this.context, "rate.dialog.interval.time", minTimeSec);
    }

    private static int parseInt(JSONObject json, String key, int defaultValue) {
        int value = defaultValue;
        if (json != null) {
            try {
                value = Integer.parseInt(JsonUtil.optStringOrNull(json, key));
            } catch (NumberFormatException e) {
                Logger.m185w("Failed to parse PMS int value: %s=%s", key, s);
            }
        }
        return value;
    }
}
