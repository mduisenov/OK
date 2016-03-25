package ru.ok.android.ui.presents.helpers;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.json.JSONObject;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.utils.settings.ServicesSettingsHelper;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.model.settings.PresentsSettings;

public class PresentSettingsHelper {
    public static void saveSettings(@Nullable JSONObject json) throws BaseApiException {
        if (json != null) {
            PresentsSettings.fromJson(json).toSharedPreferences(ServicesSettingsHelper.getPreferences(OdnoklassnikiApplication.getContext()));
        }
    }

    @NonNull
    public static PresentsSettings getSettings() {
        return PresentsSettings.fromSharedPreferences(ServicesSettingsHelper.getPreferences(OdnoklassnikiApplication.getContext()));
    }

    public static boolean isAnimatedPresentsEnabled() {
        Context context = OdnoklassnikiApplication.getContext();
        boolean userChoose = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(2131165390), true);
        boolean pmsChoose = getSettings().animatedPresentsEnabled;
        if (userChoose && pmsChoose) {
            return true;
        }
        return false;
    }
}
