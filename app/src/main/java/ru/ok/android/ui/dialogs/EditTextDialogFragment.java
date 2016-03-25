package ru.ok.android.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.view.View;
import android.widget.EditText;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.utils.localization.LocalizationManager;

public class EditTextDialogFragment extends DialogFragment {
    private EditText editTextView;

    /* renamed from: ru.ok.android.ui.dialogs.EditTextDialogFragment.1 */
    class C07701 implements OnClickListener {
        C07701() {
        }

        public void onClick(DialogInterface dialog, int which) {
            if (EditTextDialogFragment.this.getActivity() != null) {
                ((EditTextDialogListener) EditTextDialogFragment.this.getActivity()).onSubmitEditText(EditTextDialogFragment.this.editTextView.getText().toString());
            }
        }
    }

    public static class Builder {
        private Bundle args;

        public Builder() {
            this.args = new Bundle();
        }

        public Builder setDefaultText(String text) {
            this.args.putString("deftext", text);
            return this;
        }

        public Builder setHintText(String text) {
            this.args.putString("hinttext", text);
            return this;
        }

        public Builder setTitle(String title) {
            this.args.putString("titletext", title);
            return this;
        }

        public Builder setPositiveButtonText(String text) {
            this.args.putString("postext", text);
            return this;
        }

        public EditTextDialogFragment build() {
            return EditTextDialogFragment.newInstance(this.args);
        }

        public EditTextDialogFragment show(FragmentManager manager, String tag) {
            EditTextDialogFragment fragment = build();
            fragment.show(manager, tag);
            return fragment;
        }
    }

    public interface EditTextDialogListener {
        void onSubmitEditText(String str);
    }

    public static EditTextDialogFragment newInstance(Bundle args) {
        EditTextDialogFragment fragment = new EditTextDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LocalizationManager.inflate(getActivity(), 2130903154, null, false);
        this.editTextView = (EditText) rootView.findViewById(C0263R.id.text);
        this.editTextView.setText(getArguments().getString("deftext"));
        this.editTextView.setHint(getArguments().getString("hinttext"));
        com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getActivity());
        builder.setView(rootView);
        builder.setTitle(getArguments().getString("titletext"));
        builder.setPositiveButton(getArguments().containsKey("postext") ? getArguments().getString("postext") : LocalizationManager.getString(getActivity(), 2131166310), new C07701());
        builder.setNegativeButton(getArguments().containsKey("negtext") ? getArguments().getString("negtext") : LocalizationManager.getString(getActivity(), 2131165476), null);
        if (getArguments().containsKey("maxlngth")) {
            this.editTextView.setFilters(new InputFilter[]{new LengthFilter(getArguments().getInt("maxlngth"))});
        }
        return builder.create();
    }
}
