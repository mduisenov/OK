package ru.ok.android.playservices;

import android.content.Context;
import android.text.TextUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.settings.Settings;

public final class GCMUtils {
    public static String getRegistrationId(Context context) {
        migrateGcmRegistration(context);
        String gcmRegKey = Settings.getStrValueInvariable(context, "gcm_registation_key", null);
        if (TextUtils.isEmpty(gcmRegKey)) {
            return null;
        }
        if (Settings.getIntValueInvariable(context, "gcm_registered_version", 0) != Utils.getVersionCode(context)) {
            return null;
        }
        return gcmRegKey;
    }

    private static void migrateGcmRegistration(Context context) {
        String gcmRegKey = Settings.getStrValue(context, "gcm_registation_key");
        if (!TextUtils.isEmpty(gcmRegKey)) {
            Logger.m173d("Migrate GCM settings: '%s', %d", gcmRegKey, Integer.valueOf(Settings.getIntValue(context, "gcm_registered_version", 0)));
            Settings.getEditorInvariable(context).putString("gcm_registation_key", gcmRegKey).putInt("gcm_registered_version", version).apply();
            Settings.clearSettingByKey(context, "gcm_registered_version");
            Settings.clearSettingByKey(context, "gcm_registation_key");
        }
    }

    public static void storeRegistrationId(Context context, String registrationId) {
        Settings.getEditorInvariable(context).putString("gcm_registation_key", registrationId).putInt("gcm_registered_version", Utils.getVersionCode(context)).apply();
    }

    public static void clearRegistrationId(Context context) {
        Settings.getEditorInvariable(context).remove("gcm_registation_key").remove("gcm_registered_version").apply();
    }
}
