package ru.ok.android.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RadioGroup;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.json.users.ComplaintType;

public class ComplaintGroupDialog extends DialogFragment implements OnClickListener {
    private RadioGroup radioGroup;

    public interface OnSelectComplaintGroupDataListener {
        void onSelectComplaintGroupData(ComplaintType complaintType);
    }

    public static ComplaintGroupDialog newInstance() {
        return new ComplaintGroupDialog();
    }

    private int getLayoutId() {
        return 2130903132;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return buildDialog().create();
    }

    protected Builder buildDialog() {
        Context context = getActivity();
        return new Builder(context).setTitle(LocalizationManager.getString(context, 2131165627)).setView(createView()).setNegativeButton(LocalizationManager.getString(context, 2131165476), null).setPositiveButton(LocalizationManager.getString(context, 2131165623), (OnClickListener) this);
    }

    private View createView() {
        View mainView = LocalizationManager.inflate(getActivity(), getLayoutId(), null, false);
        this.radioGroup = (RadioGroup) mainView.findViewById(2131624720);
        return mainView;
    }

    public void onClick(DialogInterface dialog, int which) {
        ComplaintType returnType = ComplaintType.ADVERTISING;
        switch (this.radioGroup.getCheckedRadioButtonId()) {
            case 2131624721:
                returnType = ComplaintType.PORNO;
                break;
            case 2131624722:
                returnType = ComplaintType.ADVERTISING;
                break;
            case 2131624723:
                returnType = ComplaintType.EXTREME;
                break;
        }
        onNotifyResult(returnType);
    }

    private void onNotifyResult(ComplaintType type) {
        Fragment fragment = getTargetFragment();
        if (fragment == null || !(fragment instanceof OnSelectComplaintGroupDataListener)) {
            Activity activity = getActivity();
            if (activity != null && (activity instanceof OnSelectComplaintGroupDataListener)) {
                ((OnSelectComplaintGroupDataListener) activity).onSelectComplaintGroupData(type);
                return;
            }
            return;
        }
        ((OnSelectComplaintGroupDataListener) fragment).onSelectComplaintGroupData(type);
    }
}
