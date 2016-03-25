package ru.ok.android.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.localization.base.LocalizedFragment;

public final class DeleteMediaTopicDialog extends LocalizedFragment {

    /* renamed from: ru.ok.android.ui.dialogs.DeleteMediaTopicDialog.1 */
    class C07681 implements OnClickListener {
        C07681() {
        }

        public void onClick(DialogInterface dialog, int which) {
            DeleteMediaTopicDialog.this.getTargetFragment().onActivityResult(DeleteMediaTopicDialog.this.getTargetRequestCode(), 0, null);
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.DeleteMediaTopicDialog.2 */
    class C07692 implements OnClickListener {
        C07692() {
        }

        public void onClick(DialogInterface dialog, int which) {
            Intent data = new Intent();
            data.putExtras(DeleteMediaTopicDialog.this.getArguments());
            DeleteMediaTopicDialog.this.getTargetFragment().onActivityResult(DeleteMediaTopicDialog.this.getTargetRequestCode(), -1, data);
        }
    }

    public static DeleteMediaTopicDialog newInstance(long feedId, String mediaTopicDeleteId) {
        Bundle args = new Bundle();
        args.putLong("MEDIATOPIC_ID", feedId);
        args.putString("MEDIATOPIC_DELETE_ID", mediaTopicDeleteId);
        DeleteMediaTopicDialog result = new DeleteMediaTopicDialog();
        result.setArguments(args);
        return result;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        builder.setPositiveButton(getStringLocalized(2131165671), new C07692()).setNegativeButton(getStringLocalized(2131165476), new C07681()).setTitle(LocalizationManager.getString(getContext(), 2131165858)).setView(inflateViewLocalized(getLayoutId(), null, false));
        return builder.create();
    }

    protected int getLayoutId() {
        return 2130903157;
    }

    protected boolean isActionBarAffecting() {
        return false;
    }
}
