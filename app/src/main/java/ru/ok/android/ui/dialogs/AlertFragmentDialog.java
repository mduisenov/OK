package ru.ok.android.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import com.google.android.gms.plus.PlusShare;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.utils.localization.LocalizationManager;

public class AlertFragmentDialog extends DialogFragment {
    private OnAlertDismissListener onDismissListener;

    public interface OnAlertDismissListener {
        void onAlertDismiss(int i);
    }

    public static AlertFragmentDialog newInstance(String title, String message, int requestCode) {
        AlertFragmentDialog result = new AlertFragmentDialog();
        result.setArguments(createArgs(title, message, requestCode));
        return result;
    }

    protected static Bundle createArgs(String title, String message, int requestCode) {
        Bundle args = new Bundle();
        args.putString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, title);
        args.putString(Message.ELEMENT, message);
        args.putInt("req_code", requestCode);
        return args;
    }

    private String getTitle() {
        return getArguments().getString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE);
    }

    private String getMessage() {
        return getArguments().getString(Message.ELEMENT);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return buildDialog().create();
    }

    protected Builder buildDialog() {
        return new Builder(getActivity()).setTitle(getTitle()).setMessage(getMessage()).setPositiveButton(LocalizationManager.getString(getActivity(), 2131165595), null);
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDialogDismissed();
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        onDialogDismissed();
    }

    private void onDialogDismissed() {
        if (this.onDismissListener != null) {
            this.onDismissListener.onAlertDismiss(getArguments().getInt("req_code"));
            return;
        }
        Fragment fragment = getTargetFragment();
        if (fragment instanceof OnAlertDismissListener) {
            ((OnAlertDismissListener) fragment).onAlertDismiss(getArguments().getInt("req_code", getTargetRequestCode()));
            return;
        }
        Activity activity = getActivity();
        if (activity instanceof OnAlertDismissListener) {
            ((OnAlertDismissListener) activity).onAlertDismiss(getArguments().getInt("req_code"));
        }
    }
}
