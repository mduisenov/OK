package ru.ok.android.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.utils.localization.LocalizationManager;

public class ConfirmLogOutDialog {
    private AlertDialog dialog;
    private OnConfirmLogOutListener listener;

    private class NegativeOnClickListener implements OnClickListener {
        private NegativeOnClickListener() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (ConfirmLogOutDialog.this.listener != null) {
                ConfirmLogOutDialog.this.listener.onLogOutNoConfirm();
            }
            ConfirmLogOutDialog.this.dialog.dismiss();
        }
    }

    public interface OnConfirmLogOutListener {
        void onLogOutConfirm();

        void onLogOutNoConfirm();
    }

    private class PositiveOnClickListener implements OnClickListener {
        private PositiveOnClickListener() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (ConfirmLogOutDialog.this.listener != null) {
                ConfirmLogOutDialog.this.listener.onLogOutConfirm();
            }
        }
    }

    public ConfirmLogOutDialog(Context context) {
        this.dialog = new Builder(context).setTitle(LocalizationManager.getString(context, 2131165848)).setMessage(LocalizationManager.getString(context, 2131165849)).setPositiveButton(LocalizationManager.getString(context, 2131165850), new PositiveOnClickListener()).setNegativeButton(LocalizationManager.getString(context, 2131165476), new NegativeOnClickListener()).create();
    }

    public void setOnConfirmLogOutListener(OnConfirmLogOutListener listener) {
        this.listener = listener;
    }

    public Dialog getDialog() {
        return this.dialog;
    }
}
