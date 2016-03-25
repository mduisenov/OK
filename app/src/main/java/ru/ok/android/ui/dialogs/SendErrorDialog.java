package ru.ok.android.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import com.google.android.gms.plus.PlusShare;
import java.lang.ref.WeakReference;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.utils.localization.LocalizationManager;

public final class SendErrorDialog extends DialogFragment {
    private WeakReference<SendErrorDialogListener> listener;

    /* renamed from: ru.ok.android.ui.dialogs.SendErrorDialog.1 */
    class C07811 implements OnClickListener {
        C07811() {
        }

        public void onClick(DialogInterface dialog, int which) {
            if (SendErrorDialog.this.listener != null) {
                SendErrorDialogListener ref = (SendErrorDialogListener) SendErrorDialog.this.listener.get();
                if (ref != null) {
                    ref.onResendClicked();
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.SendErrorDialog.2 */
    class C07822 implements OnClickListener {
        C07822() {
        }

        public void onClick(DialogInterface dialog, int which) {
            if (SendErrorDialog.this.listener != null) {
                SendErrorDialogListener ref = (SendErrorDialogListener) SendErrorDialog.this.listener.get();
                if (ref != null) {
                    ref.onPayStickersClicked();
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.SendErrorDialog.3 */
    class C07833 implements OnClickListener {
        C07833() {
        }

        public void onClick(DialogInterface dialog, int which) {
            if (SendErrorDialog.this.listener != null) {
                SendErrorDialogListener ref = (SendErrorDialogListener) SendErrorDialog.this.listener.get();
                if (ref != null) {
                    ref.onUndoEditClicked();
                }
            }
        }
    }

    public interface SendErrorDialogListener {
        void onPayStickersClicked();

        void onResendClicked();

        void onUndoEditClicked();
    }

    public static SendErrorDialog newInstance(String title, String message, boolean showResend, boolean showUndoEdit, boolean showPayStickers) {
        Bundle args = new Bundle();
        args.putString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, title);
        args.putString(Message.ELEMENT, message);
        args.putBoolean("resend_show", showResend);
        args.putBoolean("undo_edit", showUndoEdit);
        args.putBoolean("pay-stickers", showPayStickers);
        SendErrorDialog result = new SendErrorDialog();
        result.setArguments(args);
        return result;
    }

    private String getTitle() {
        return getArguments().getString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE);
    }

    private String getMessage() {
        return getArguments().getString(Message.ELEMENT);
    }

    private boolean isShowResend() {
        return getArguments().getBoolean("resend_show", false);
    }

    private boolean isShowUndoEdit() {
        return getArguments().getBoolean("undo_edit", false);
    }

    private boolean isShowPayStickers() {
        return getArguments().getBoolean("pay-stickers", false);
    }

    public void setResendListener(SendErrorDialogListener listener) {
        this.listener = new WeakReference(listener);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CharSequence undoTitle;
        Builder builder = new Builder(getActivity()).setTitle(getTitle()).setMessage(getMessage()).setNegativeButton(LocalizationManager.from(getActivity()).getString(2131165595), null);
        if (isShowResend()) {
            builder.setPositiveButton(LocalizationManager.from(getActivity()).getString(2131166463), new C07811());
        }
        if (isShowPayStickers()) {
            undoTitle = LocalizationManager.from(getActivity()).getString(2131166335);
            OnClickListener payListener = new C07822();
            if (isShowResend()) {
                builder.setNeutralButton(undoTitle, payListener);
            } else {
                builder.setPositiveButton(undoTitle, payListener);
            }
        }
        if (isShowUndoEdit()) {
            undoTitle = LocalizationManager.from(getActivity()).getString(2131166745);
            OnClickListener undoListener = new C07833();
            if (isShowResend()) {
                builder.setNeutralButton(undoTitle, undoListener);
            } else {
                builder.setPositiveButton(undoTitle, undoListener);
            }
        }
        return builder.create();
    }
}
