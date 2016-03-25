package ru.ok.android.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import com.google.android.gms.plus.PlusShare;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.utils.localization.LocalizationManager;

public class ConfirmationDialog extends DialogFragment implements OnClickListener {
    private OnConfirmationDialogListener listener;

    public interface OnConfirmationDialogListener {
        void onConfirmationDialogDismissed(int i);

        void onConfirmationDialogResult(boolean z, int i);
    }

    public static class Builder {
        private final Bundle args;

        public Builder() {
            this.args = new Bundle();
        }

        public Builder withTitle(String title) {
            this.args.putString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, title);
            return this;
        }

        public Builder withTitle(int titleResId) {
            this.args.putInt("title_res_id", titleResId);
            return this;
        }

        public Builder withMessage(String message) {
            this.args.putString(Message.ELEMENT, message);
            return this;
        }

        public Builder withMessage(int messageResId) {
            this.args.putInt("message_res_id", messageResId);
            return this;
        }

        public Builder withPositiveText(String positiveText) {
            this.args.putString("positive", positiveText);
            return this;
        }

        public Builder withPositiveText(int positiveTextResId) {
            this.args.putInt("positive_res_id", positiveTextResId);
            return this;
        }

        public Builder withNegativeText(String negativeText) {
            this.args.putString("negative", negativeText);
            return this;
        }

        public Builder withNegativeText(int negativeTextResId) {
            this.args.putInt("negative_res_id", negativeTextResId);
            return this;
        }

        public Builder withRequestCode(int requestCode) {
            this.args.putInt("request_code", requestCode);
            return this;
        }

        public Builder withCancelable(boolean isCancelable) {
            this.args.putBoolean("cancelable", isCancelable);
            return this;
        }

        public ConfirmationDialog build() {
            ConfirmationDialog fragment = new ConfirmationDialog();
            fragment.setArguments(this.args);
            return fragment;
        }

        public Bundle buildArgs() {
            return this.args;
        }
    }

    public static ConfirmationDialog newInstance(int titleResId, int messageResId, int positiveTextResId, int negativeTextResId, int requestCode) {
        return new Builder().withTitle(titleResId).withMessage(messageResId).withPositiveText(positiveTextResId).withNegativeText(negativeTextResId).withRequestCode(requestCode).build();
    }

    public static ConfirmationDialog newInstance(int titleResId, String message, int positiveTextResId, int negativeTextResId, int requestCode) {
        return new Builder().withTitle(titleResId).withMessage(message).withPositiveText(positiveTextResId).withNegativeText(negativeTextResId).withRequestCode(requestCode).build();
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(getArguments().getBoolean("cancelable", true));
        return buildDialog().create();
    }

    protected com.afollestad.materialdialogs.AlertDialogWrapper.Builder buildDialog() {
        Context context = getActivity();
        LocalizationManager localizationManager = LocalizationManager.from(context);
        Bundle args = getArguments();
        CharSequence title = args.getString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE);
        CharSequence message = args.getString(Message.ELEMENT);
        CharSequence positiveText = args.getString("positive");
        CharSequence negativeText = args.getString("negative");
        if (TextUtils.isEmpty(title)) {
            int titleResId = args.getInt("title_res_id");
            if (titleResId != 0) {
                title = localizationManager.getString(titleResId);
            }
        }
        if (TextUtils.isEmpty(message)) {
            int messageResId = args.getInt("message_res_id");
            if (messageResId != 0) {
                message = localizationManager.getString(messageResId);
            }
        }
        if (TextUtils.isEmpty(positiveText)) {
            int positiveTextResId = args.getInt("positive_res_id");
            if (positiveTextResId != 0) {
                positiveText = localizationManager.getString(positiveTextResId);
            } else {
                positiveText = localizationManager.getString(2131166310);
            }
        }
        if (TextUtils.isEmpty(negativeText)) {
            int negativeTextResId = args.getInt("negative_res_id");
            if (negativeTextResId != 0) {
                negativeText = localizationManager.getString(negativeTextResId);
            } else {
                negativeText = localizationManager.getString(2131165476);
            }
        }
        return new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context).setTitle(title).setMessage(message).setPositiveButton(positiveText, (OnClickListener) this).setNegativeButton(negativeText, (OnClickListener) this);
    }

    public void onClick(DialogInterface dialog, int which) {
        boolean isPositive;
        int i = 0;
        if (which == -1) {
            isPositive = true;
        } else {
            isPositive = false;
        }
        if (this.listener != null) {
            this.listener.onConfirmationDialogResult(isPositive, getArguments().getInt("request_code"));
            return;
        }
        Fragment fragment = getTargetFragment();
        if (fragment == null || !(fragment instanceof OnConfirmationDialogListener)) {
            Activity activity = getActivity();
            if (activity != null && (activity instanceof OnConfirmationDialogListener)) {
                ((OnConfirmationDialogListener) activity).onConfirmationDialogResult(isPositive, getArguments().getInt("request_code"));
                return;
            } else if (fragment != null) {
                Intent intent = new Intent();
                intent.putExtras(getArguments());
                int targetRequestCode = getTargetRequestCode();
                if (isPositive) {
                    i = -1;
                }
                fragment.onActivityResult(targetRequestCode, i, intent);
                return;
            } else {
                return;
            }
        }
        ((OnConfirmationDialogListener) fragment).onConfirmationDialogResult(isPositive, getArguments().getInt("request_code", getTargetRequestCode()));
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        onDismissed();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDismissed();
    }

    private void onDismissed() {
        if (this.listener != null) {
            this.listener.onConfirmationDialogDismissed(getArguments().getInt("request_code"));
            return;
        }
        Fragment fragment = getTargetFragment();
        if (fragment == null || !(fragment instanceof OnConfirmationDialogListener)) {
            Activity activity = getActivity();
            if (activity != null && (activity instanceof OnConfirmationDialogListener)) {
                ((OnConfirmationDialogListener) activity).onConfirmationDialogDismissed(getArguments().getInt("request_code"));
                return;
            }
            return;
        }
        ((OnConfirmationDialogListener) fragment).onConfirmationDialogDismissed(getArguments().getInt("request_code", getTargetRequestCode()));
    }
}
