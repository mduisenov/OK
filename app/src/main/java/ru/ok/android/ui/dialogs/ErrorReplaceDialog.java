package ru.ok.android.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;

public class ErrorReplaceDialog {
    protected AlertDialog dialog;
    private OnClickListener firstButtonListener;
    private boolean isDismissIfOnClick;
    private OnClickListener replaceButtonListener;

    public class FirstButtonClickListener implements OnClickListener {
        public void onClick(DialogInterface dialogInterface, int i) {
            if (ErrorReplaceDialog.this.firstButtonListener != null) {
                ErrorReplaceDialog.this.firstButtonListener.onClick(dialogInterface, i);
            }
        }
    }

    public class ReplaceButtonClickListener implements OnClickListener {
        public void onClick(DialogInterface dialogInterface, int i) {
            if (ErrorReplaceDialog.this.replaceButtonListener != null) {
                ErrorReplaceDialog.this.replaceButtonListener.onClick(dialogInterface, i);
            }
        }
    }

    public ErrorReplaceDialog(Context context, String message, String firstButtonText, String replaceButtonText) {
        this.isDismissIfOnClick = true;
        init(context, message, firstButtonText, replaceButtonText);
    }

    public ErrorReplaceDialog(Context context, String title, String message, String firstButtonText, String replaceButtonText, boolean cancelable, boolean useNegativeButton) {
        this.isDismissIfOnClick = true;
        init(context, title, message, firstButtonText, replaceButtonText, cancelable, useNegativeButton);
    }

    protected void init(Context context, String message, String firstButtonText, String replaceButtonText) {
        init(context, null, message, firstButtonText, replaceButtonText, false, true);
    }

    protected void init(Context context, String title, String message, String firstButtonText, String replaceButtonText, boolean cancelable, boolean useNegativeButton) {
        Builder builder = new Builder(context).setCancelable(cancelable).setMessage((CharSequence) message).setPositiveButton((CharSequence) replaceButtonText, new ReplaceButtonClickListener()).setTitle((CharSequence) title);
        if (useNegativeButton) {
            builder.setNegativeButton((CharSequence) firstButtonText, new FirstButtonClickListener());
        }
        this.dialog = builder.create();
    }

    public void show() {
        this.dialog.show();
    }

    public void setOnReplaceButtonClickListener(OnClickListener listener) {
        this.replaceButtonListener = listener;
    }
}
