package ru.ok.android.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.utils.localization.LocalizationManager;

public class ConfirmClearCacheDialog {
    private AlertDialog dialog;
    private OnConfirmClearCacheListener listener;

    private class NegativeOnClickListener implements OnClickListener {
        private NegativeOnClickListener() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (ConfirmClearCacheDialog.this.listener != null) {
                ConfirmClearCacheDialog.this.listener.onClearCacheNoConfirm();
            }
            ConfirmClearCacheDialog.this.dialog.dismiss();
        }
    }

    public interface OnConfirmClearCacheListener {
        void onClearCacheConfirm();

        void onClearCacheNoConfirm();
    }

    private class PositiveOnClickListener implements OnClickListener {
        private PositiveOnClickListener() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (ConfirmClearCacheDialog.this.listener != null) {
                ConfirmClearCacheDialog.this.listener.onClearCacheConfirm();
            }
        }
    }

    public ConfirmClearCacheDialog(Context context) {
        this.dialog = new Builder(context).setTitle(LocalizationManager.getString(context, 2131165592)).setMessage(LocalizationManager.getString(context, 2131165591)).setPositiveButton(LocalizationManager.getString(context, 2131165454), new PositiveOnClickListener()).setNegativeButton(LocalizationManager.getString(context, 2131165476), new NegativeOnClickListener()).create();
    }

    public void setOnConfirmListener(OnConfirmClearCacheListener listener) {
        this.listener = listener;
    }

    public Dialog getDialog() {
        return this.dialog;
    }
}
