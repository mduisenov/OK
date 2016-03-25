package ru.ok.android.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.utils.localization.LocalizationManager;

public abstract class YesNoQuestionDialogFragment extends DialogFragment implements OnClickListener {
    abstract String getQuestion();

    abstract String getTitle();

    abstract void onNotifyYesResult();

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return buildDialog().create();
    }

    protected Builder buildDialog() {
        return new Builder(getActivity()).setTitle(getTitle()).setMessage(getQuestion()).setPositiveButton(LocalizationManager.getString(getActivity(), 2131166881), (OnClickListener) this).setNegativeButton(LocalizationManager.getString(getActivity(), 2131166257), null);
    }

    public void onClick(DialogInterface dialog, int which) {
        onNotifyYesResult();
    }
}
