package ru.ok.android.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.utils.localization.LocalizationManager;

public final class DeleteConversationDialog extends DialogFragment {

    /* renamed from: ru.ok.android.ui.dialogs.DeleteConversationDialog.1 */
    class C07641 implements OnClickListener {
        C07641() {
        }

        public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent();
            intent.putExtra("EXTRA_CONVERSATION_ID", DeleteConversationDialog.this.getConversationId());
            DeleteConversationDialog.this.getTargetFragment().onActivityResult(DeleteConversationDialog.this.getTargetRequestCode(), -1, intent);
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.DeleteConversationDialog.2 */
    class C07652 implements OnClickListener {
        C07652() {
        }

        public void onClick(DialogInterface dialog, int which) {
            DeleteConversationDialog.this.getTargetFragment().onActivityResult(DeleteConversationDialog.this.getTargetRequestCode(), 0, null);
            dialog.cancel();
        }
    }

    public static DeleteConversationDialog newInstance(String conversationId, CharSequence userName) {
        Bundle args = new Bundle();
        args.putString("EXTRA_CONVERSATION_ID", conversationId);
        args.putCharSequence("EXTRA_USER_NAME", userName);
        DeleteConversationDialog result = new DeleteConversationDialog();
        result.setArguments(args);
        return result;
    }

    private CharSequence getUserName() {
        return getArguments().getCharSequence("EXTRA_USER_NAME");
    }

    private String getConversationId() {
        return getArguments().getString("EXTRA_CONVERSATION_ID");
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        View layout = LocalizationManager.inflate(context, 2130903142, null, false);
        ((TextView) layout.findViewById(2131624758)).setText(LocalizationManager.getString(context, 2131165684, getUserName()));
        Builder builder = new Builder(context);
        builder.setTitle(LocalizationManager.getString(context, 2131165677));
        builder.setPositiveButton(LocalizationManager.getString(context, 2131165673), new C07641());
        builder.setNegativeButton(LocalizationManager.getString(context, 2131165477), new C07652()).setCancelable(false);
        builder.setView(layout);
        return builder.create();
    }
}
