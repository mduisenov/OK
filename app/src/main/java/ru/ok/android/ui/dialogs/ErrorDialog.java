package ru.ok.android.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.utils.localization.LocalizationManager;

public class ErrorDialog implements OnClickListener {
    private AlertDialog dialog;
    private boolean isDismissIfOnClick;
    private OnClickButtonListener listener;

    public interface OnClickButtonListener {
        void OnClick(boolean z);
    }

    public ErrorDialog(Context context, int messageId, int buttonTextId) {
        this.isDismissIfOnClick = true;
        init(context, messageId, buttonTextId);
    }

    public void init(Context context, int messageId, int buttonTextId) {
        this.dialog = new Builder(context).setMessage(LocalizationManager.getString(context, messageId)).setPositiveButton(LocalizationManager.getString(context, buttonTextId), (OnClickListener) this).create();
    }

    public void show() {
        this.dialog.show();
    }

    public Dialog getDialog() {
        return this.dialog;
    }

    public void setOnClickButtonListener(OnClickButtonListener listener) {
        this.listener = listener;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (this.isDismissIfOnClick) {
            notifyOnClickListener(true);
            this.dialog.dismiss();
            return;
        }
        notifyOnClickListener(false);
    }

    private void notifyOnClickListener(boolean dismiss) {
        if (this.listener != null) {
            this.listener.OnClick(dismiss);
        }
    }
}
