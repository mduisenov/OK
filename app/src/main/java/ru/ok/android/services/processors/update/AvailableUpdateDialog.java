package ru.ok.android.services.processors.update;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.util.Pair;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;

class AvailableUpdateDialog implements OnCancelListener, OnClickListener {
    private final String appStoreUrl;
    private final Context context;
    private AlertDialog dialog;
    private final boolean isCancellable;
    private final String message;

    AvailableUpdateDialog(Context context, String message, String appStoreUrl, boolean isCancellable) {
        this.context = context;
        this.message = message;
        this.appStoreUrl = appStoreUrl;
        this.isCancellable = isCancellable;
    }

    void show() {
        this.dialog = new Builder(this.context).setMessage(this.message).setTitle(LocalizationManager.getString(this.context, 2131166754)).setPositiveButton(LocalizationManager.getString(this.context, 2131166753), (OnClickListener) this).setCancelable(this.isCancellable).setOnCancelListener(this).create();
        this.dialog.show();
        StatisticManager.getInstance().addStatisticEvent("update_dialog_show", new Pair[0]);
    }

    public void onClick(DialogInterface dialog, int which) {
        if (which == -1) {
            openAppStore();
            StatisticManager.getInstance().addStatisticEvent("update_dialog_ok", new Pair[0]);
        }
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void onCancel(DialogInterface dialog) {
        StatisticManager.getInstance().addStatisticEvent("update_dialog_cancel", new Pair[0]);
    }

    private void openAppStore() {
        try {
            this.context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(this.appStoreUrl)));
        } catch (Throwable e) {
            Logger.m186w(e, "Failed to open app store: " + e);
        }
    }
}
