package ru.ok.android.utils;

import android.content.Context;
import android.text.TextUtils;
import ru.ok.android.utils.settings.Settings;

public final class ReferrerStorage {
    public static String getReferrer(Context context) {
        String referrer = Settings.getStrValueInvariable(context, "referrer", "");
        if (TextUtils.isEmpty(referrer)) {
            return maybeInitReferrerFromVendor(context);
        }
        return referrer;
    }

    private static String maybeInitReferrerFromVendor(Context context) {
        String referrer = "";
        if (TextUtils.isEmpty("") || Settings.getBoolValueInvariable(context, "referrer_is_reset", false)) {
            return referrer;
        }
        referrer = "";
        Settings.storeStrValueInvariable(context, "referrer", referrer);
        return referrer;
    }

    public static void setReferrer(Context context, String referrer) {
        Settings.storeStrValueInvariable(context, "referrer", referrer);
        Settings.clearSettingInvariableByKey(context, "referrer_is_reset");
    }

    public static void clear(Context context) {
        Settings.clearSettingInvariableByKey(context, "referrer");
        Settings.storeBoolValueInvariable(context, "referrer_is_reset", true);
    }
}
