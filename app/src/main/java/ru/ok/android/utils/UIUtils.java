package ru.ok.android.utils;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class UIUtils {
    @SuppressLint({"NewApi"})
    public static void removeOnGlobalLayoutListener(View view, OnGlobalLayoutListener listener) {
        if (DeviceUtils.hasSdk(16)) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        } else {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
    }
}
