package ru.ok.android.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.utils.localization.LocalizationManager;

public class WhatNewDialog implements OnClickListener {
    private AlertDialog dialog;
    private boolean isDismissIfOnClick;

    public WhatNewDialog(Context context) {
        this.isDismissIfOnClick = true;
        init(context);
    }

    public void init(Context context) {
        this.dialog = new Builder(context).setMessage(LocalizationManager.getString(context, 2131166870)).setTitle(LocalizationManager.getString(context, 2131166869)).setPositiveButton(LocalizationManager.getString(context, 2131165595), (OnClickListener) this).create();
    }

    public void show() {
        this.dialog.show();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.dialog.dismiss();
    }
}
