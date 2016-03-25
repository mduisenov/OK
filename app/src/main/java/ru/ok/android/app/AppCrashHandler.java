package ru.ok.android.app;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import ru.ok.android.ui.dialogs.rate.RateDialog;
import ru.ok.android.utils.ChainedUncaughtExceptionHandler;

public class AppCrashHandler extends ChainedUncaughtExceptionHandler {
    private Context context;
    private NotificationManager mNotificationManager;

    public AppCrashHandler(Context context) {
        this.context = context;
        this.mNotificationManager = (NotificationManager) context.getSystemService("notification");
    }

    protected void handleUncaughtException(Thread thread, Throwable ex) {
        try {
            RateDialog.clearCounter(this.context, "");
            this.mNotificationManager.cancelAll();
        } catch (Exception e) {
            Log.e("AppCrashHandler", "Notification manager has failed you ;(");
        }
    }
}
