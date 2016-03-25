package ru.ok.android.ui.nativeRegistration;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.fragments.registr.NotLoggedInWebFragment.Page;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.NotLoggedInWebActivity;
import ru.ok.android.utils.localization.LocalizationManager;

public class NeedHelpDialog extends DialogFragment {

    /* renamed from: ru.ok.android.ui.nativeRegistration.NeedHelpDialog.1 */
    class C10981 implements OnClickListener {
        final /* synthetic */ Page val$feedBackPage;

        C10981(Page page) {
            this.val$feedBackPage = page;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            Intent intent = new Intent(NeedHelpDialog.this.getActivity(), NotLoggedInWebActivity.class);
            intent.addFlags(65536);
            switch (i) {
                case RECEIVED_VALUE:
                    intent.putExtra("page", Page.Recovery);
                    break;
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    intent.putExtra("page", this.val$feedBackPage);
                    break;
            }
            NeedHelpDialog.this.startActivity(intent);
            NeedHelpDialog.this.dismiss();
        }
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] buttons = new String[2];
        buttons[0] = LocalizationManager.getString(getContext(), Page.Recovery.titleResId);
        Page feedBackPage = HelpSettingsHandler.isFeedbackEnabled(getContext()) ? Page.FeedBack : Page.Faq;
        buttons[1] = LocalizationManager.getString(getContext(), feedBackPage.titleResId);
        return new Builder(getContext()).setNegativeButton(LocalizationManager.getString(getContext(), 2131165476), null).setTitle(LocalizationManager.getString(getContext(), 2131166249)).setItems(buttons, new C10981(feedBackPage)).create();
    }
}
