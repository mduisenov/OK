package ru.ok.android.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.utils.bus.BusUsersHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.json.users.ComplaintType;

public class ComplaintUserDialog extends DialogFragment implements OnClickListener {
    private CheckBox checkBoxBlackList;
    private RadioGroup radioGroup;

    public static ComplaintUserDialog newInstance(String userId) {
        ComplaintUserDialog result = new ComplaintUserDialog();
        Bundle args = new Bundle();
        args.putString("user_id", userId);
        result.setArguments(args);
        return result;
    }

    private int getLayoutId() {
        return 2130903133;
    }

    private String getUserId() {
        return getArguments().getString("user_id");
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return buildDialog().create();
    }

    protected Builder buildDialog() {
        return new Builder(getActivity()).setTitle(2131165630).setView(createView()).setNegativeButton(2131165476, null).setPositiveButton(2131165623, (OnClickListener) this);
    }

    private View createView() {
        View mainView = LocalizationManager.inflate(getActivity(), getLayoutId(), null, false);
        this.radioGroup = (RadioGroup) mainView.findViewById(2131624720);
        this.checkBoxBlackList = (CheckBox) mainView.findViewById(2131624725);
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
            case 2131624724:
                returnType = ComplaintType.FAKEPROFILE;
                break;
        }
        onNotifyResult(returnType, this.checkBoxBlackList.isChecked());
    }

    private void onNotifyResult(ComplaintType type, boolean isAddToBlackList) {
        BusUsersHelper.complaintToUser(getUserId(), type, isAddToBlackList);
    }
}
