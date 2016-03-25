package ru.ok.android.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.utils.localization.LocalizationManager;

public class PasswordDialog extends DialogFragment {
    private EditText editTextView;

    public interface OnLogoutAllClickListener {
        void onLogoutAllClick(String str);
    }

    /* renamed from: ru.ok.android.ui.dialogs.PasswordDialog.1 */
    class C07721 implements OnClickListener {
        C07721() {
        }

        public void onClick(DialogInterface dialog, int which) {
            Activity activity = PasswordDialog.this.getActivity();
            if (activity != null && (activity instanceof OnLogoutAllClickListener)) {
                ((OnLogoutAllClickListener) PasswordDialog.this.getActivity()).onLogoutAllClick(PasswordDialog.this.editTextView.getText().toString());
            }
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.PasswordDialog.2 */
    class C07732 implements TextWatcher {
        final /* synthetic */ AlertDialog val$dialog;

        C07732(AlertDialog alertDialog) {
            this.val$dialog = alertDialog;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Button button = this.val$dialog.getButton(-1);
            if (button == null) {
                return;
            }
            if (s.length() > 0) {
                button.setEnabled(true);
            } else {
                button.setEnabled(false);
            }
        }

        public void afterTextChanged(Editable s) {
        }
    }

    public static PasswordDialog newInstance() {
        return new PasswordDialog();
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LocalizationManager.inflate(getActivity(), 2130903158, null, false);
        this.editTextView = (EditText) rootView.findViewById(2131624773);
        this.editTextView.setHint(LocalizationManager.getString(getActivity(), 2131166328));
        Builder builder = new Builder(getActivity());
        builder.setView(rootView);
        builder.setTitle(LocalizationManager.getString(getActivity(), 2131165788));
        builder.setPositiveButton(LocalizationManager.getString(getActivity(), 2131166310), new C07721());
        builder.setNegativeButton(LocalizationManager.getString(getActivity(), 2131165476), null);
        AlertDialog dialog = builder.create();
        this.editTextView.addTextChangedListener(new C07732(dialog));
        Button button = dialog.getButton(-1);
        if (button != null) {
            button.setEnabled(false);
        }
        return dialog;
    }
}
