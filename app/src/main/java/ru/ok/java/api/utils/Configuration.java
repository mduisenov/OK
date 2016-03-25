package ru.ok.java.api.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public final class Configuration {
    private static String versionName;

    public static void init(Context context) {
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getVersion() {
        return versionName != null ? versionName : "";
    }
}
