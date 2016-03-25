package ru.ok.android.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.localization.base.LocalizedFragment;
import ru.ok.model.stream.Feed;

public final class DeleteFeedDialog extends LocalizedFragment {
    private CheckBox unsubscribe;

    /* renamed from: ru.ok.android.ui.dialogs.DeleteFeedDialog.1 */
    class C07661 implements OnClickListener {
        C07661() {
        }

        public void onClick(DialogInterface dialog, int which) {
            DeleteFeedDialog.this.getTargetFragment().onActivityResult(DeleteFeedDialog.this.getTargetRequestCode(), 0, null);
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.DeleteFeedDialog.2 */
    class C07672 implements OnClickListener {
        C07672() {
        }

        public void onClick(DialogInterface dialog, int which) {
            Intent data = new Intent();
            data.putExtras(DeleteFeedDialog.this.getArguments());
            data.putExtra("IS_UNSUBSCRIBE", DeleteFeedDialog.this.unsubscribe.isChecked());
            DeleteFeedDialog.this.getTargetFragment().onActivityResult(DeleteFeedDialog.this.getTargetRequestCode(), -1, data);
        }
    }

    public static DeleteFeedDialog newInstance(int feedPosition, Feed feed, ArrayList<String> friendIds, ArrayList<String> groupIds, int itemAdapterPosition) {
        Bundle args = new Bundle();
        args.putLong("FEED_ID", feed.getId());
        args.putInt("FEED_POSITION", feedPosition);
        args.putString("FEED_STAT_INFO", feed.getFeedStatInfo());
        args.putString("DELETE_ID", feed.getDeleteId());
        args.putStringArrayList("FRIEND_IDS", friendIds);
        args.putStringArrayList("GROUP_IDS", groupIds);
        args.putInt("ITEM_ADAPTER_POSITION", itemAdapterPosition);
        DeleteFeedDialog result = new DeleteFeedDialog();
        result.setArguments(args);
        return result;
    }

    private List<String> getFriendIds() {
        return getArguments().getStringArrayList("FRIEND_IDS");
    }

    private List<String> getGroupIds() {
        return getArguments().getStringArrayList("GROUP_IDS");
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        boolean isIdsPresents;
        int i;
        int i2 = 0;
        Builder builder = new Builder(getActivity());
        View view = inflateViewLocalized(getLayoutId(), null, false);
        this.unsubscribe = (CheckBox) view.findViewById(2131624772);
        View message = view.findViewById(2131624538);
        if ((getFriendIds() == null || getFriendIds().isEmpty()) && (getGroupIds() == null || getGroupIds().isEmpty())) {
            isIdsPresents = false;
        } else {
            isIdsPresents = true;
        }
        CheckBox checkBox = this.unsubscribe;
        if (isIdsPresents) {
            i = 0;
        } else {
            i = 8;
        }
        checkBox.setVisibility(i);
        if (isIdsPresents) {
            i2 = 8;
        }
        message.setVisibility(i2);
        if (isIdsPresents) {
            i = 2131165971;
        } else {
            i = 2131165671;
        }
        Builder negativeButton = builder.setPositiveButton(getStringLocalized(i), new C07672()).setNegativeButton(getStringLocalized(2131165476), new C07661());
        Context context = getContext();
        if (isIdsPresents) {
            i = 2131165865;
        } else {
            i = 2131165858;
        }
        negativeButton.setTitle(LocalizationManager.getString(context, i)).setView(view);
        return builder.create();
    }

    protected int getLayoutId() {
        return 2130903156;
    }

    protected boolean isActionBarAffecting() {
        return false;
    }
}
