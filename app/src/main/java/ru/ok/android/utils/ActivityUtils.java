package ru.ok.android.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public final class ActivityUtils {
    public static Bundle getMeta(Context context, ComponentName componentName) {
        try {
            ActivityInfo ai = context.getPackageManager().getActivityInfo(componentName, 129);
            if (!(ai == null || ai.metaData == null)) {
                return ai.metaData;
            }
        } catch (Throwable e) {
            Logger.m177e("Failed to get meta from component %s: %s", componentName, e);
            Logger.m178e(e);
        }
        return new Bundle();
    }
}
