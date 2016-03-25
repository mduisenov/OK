package ru.ok.android.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.localization.base.LocalizedFragment;

public final class SetToStatusMediaTopicDialog extends LocalizedFragment {

    /* renamed from: ru.ok.android.ui.dialogs.SetToStatusMediaTopicDialog.1 */
    class C07841 implements OnClickListener {
        C07841() {
        }

        public void onClick(DialogInterface dialog, int which) {
            SetToStatusMediaTopicDialog.this.getTargetFragment().onActivityResult(SetToStatusMediaTopicDialog.this.getTargetRequestCode(), 0, null);
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.SetToStatusMediaTopicDialog.2 */
    class C07852 implements OnClickListener {
        C07852() {
        }

        public void onClick(DialogInterface dialog, int which) {
            Intent data = new Intent();
            data.putExtras(SetToStatusMediaTopicDialog.this.getArguments());
            SetToStatusMediaTopicDialog.this.getTargetFragment().onActivityResult(SetToStatusMediaTopicDialog.this.getTargetRequestCode(), -1, data);
        }
    }

    public static SetToStatusMediaTopicDialog newInstance(long mediaTopicId) {
        Bundle args = new Bundle();
        args.putLong("MEDIATOPIC_ID", mediaTopicId);
        SetToStatusMediaTopicDialog result = new SetToStatusMediaTopicDialog();
        result.setArguments(args);
        return result;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        builder.setPositiveButton(getStringLocalized(2131166541), new C07852()).setNegativeButton(getStringLocalized(2131165476), new C07841()).setTitle(LocalizationManager.getString(getContext(), 2131166177));
        return builder.create();
    }

    protected int getLayoutId() {
        return 0;
    }

    protected boolean isActionBarAffecting() {
        return false;
    }
}
