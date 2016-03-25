package ru.ok.android.ui.stream.view;

import android.content.Context;
import java.util.Arrays;
import java.util.List;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.ui.quickactions.BaseQuickAction.OnActionItemClickListener;
import ru.ok.android.ui.quickactions.QuickAction;
import ru.ok.android.utils.Logger;

public class OptionsPopupWindow extends AbstractOptionsPopupWindow implements OnActionItemClickListener {
    protected ActionItem deleteAction;
    private OptionsPopupListener listener;
    protected ActionItem markAsSpamAction;

    public interface OptionsPopupListener {
        void onDeleteClicked();

        void onMarkAsSpamClicked();
    }

    public void onItemClick(QuickAction source, int pos, int actionId) {
        switch (actionId) {
            case RECEIVED_VALUE:
                Logger.m172d("Mark as spam feed clicked: %d");
                if (this.listener != null) {
                    this.listener.onMarkAsSpamClicked();
                    break;
                }
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                Logger.m172d("Delete feed clicked: %d");
                if (this.listener != null) {
                    this.listener.onDeleteClicked();
                    break;
                }
                break;
        }
        dismiss();
    }

    public OptionsPopupWindow(Context context) {
        super(context);
    }

    protected List<ActionItem> getActionItems() {
        this.markAsSpamAction = new ActionItem(0, 2131165855, 2130838053);
        this.deleteAction = new ActionItem(1, 2131165864, 2130838045);
        return Arrays.asList(new ActionItem[]{this.markAsSpamAction, this.deleteAction});
    }

    public void setListener(OptionsPopupListener adapterListener) {
        this.listener = adapterListener;
    }

    public void setOptionVisible(boolean deleteVisible, boolean markAsSpamVisible) {
        setActionItemVisibility(this.deleteAction, deleteVisible);
        setActionItemVisibility(this.markAsSpamAction, markAsSpamVisible);
    }
}
