package ru.ok.android.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import ru.ok.android.ui.dialogs.AlertFragmentDialog.OnAlertDismissListener;

public class ProgressDialogFragment extends DialogFragment {
    private OnAlertDismissListener listener;

    public static ProgressDialogFragment createInstance(CharSequence message, boolean cancelable) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putCharSequence(NotificationCompat.CATEGORY_MESSAGE, message);
        fragment.setArguments(args);
        fragment.setCancelable(cancelable);
        return fragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getArguments().getCharSequence(NotificationCompat.CATEGORY_MESSAGE));
        return dialog;
    }

    public void onCancel(DialogInterface dialog) {
        if (this.listener != null) {
            this.listener.onAlertDismiss(getTargetRequestCode());
            return;
        }
        Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof OnAlertDismissListener) {
            ((OnAlertDismissListener) targetFragment).onAlertDismiss(getTargetRequestCode());
            return;
        }
        Activity targetActivity = getActivity();
        if (targetActivity instanceof OnAlertDismissListener) {
            ((OnAlertDismissListener) targetActivity).onAlertDismiss(getTargetRequestCode());
        }
    }
}
