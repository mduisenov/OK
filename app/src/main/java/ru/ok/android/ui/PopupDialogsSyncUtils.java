package ru.ok.android.ui;

import android.content.Context;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.settings.Settings;

public final class PopupDialogsSyncUtils {
    private static int showRequestSinceOnCreate;

    public static synchronized boolean atomicCheckAndShow(Context context, Runnable show) {
        boolean z = false;
        synchronized (PopupDialogsSyncUtils.class) {
            if (showRequestSinceOnCreate <= 0) {
                showRequestSinceOnCreate++;
                long now = System.currentTimeMillis();
                long lastDisplayedPopupTs = Settings.getLongValue(context, "last_displayed_popup", 0);
                if (lastDisplayedPopupTs == 0 || now - lastDisplayedPopupTs >= 60000) {
                    Settings.storeLongValue(context, "last_displayed_popup", now);
                    ThreadUtil.executeOnMain(show);
                    z = true;
                }
            }
        }
        return z;
    }

    static {
        showRequestSinceOnCreate = 0;
    }

    public static void onOdklActivityCreate() {
        showRequestSinceOnCreate = 0;
    }
}
