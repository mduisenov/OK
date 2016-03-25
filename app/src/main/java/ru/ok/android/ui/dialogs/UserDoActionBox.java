package ru.ok.android.ui.dialogs;

import android.content.Context;
import android.view.View;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.ui.quickactions.BaseQuickAction.OnActionItemClickListener;
import ru.ok.android.ui.quickactions.QuickAction;
import ru.ok.android.utils.Utils;
import ru.ok.model.UserInfo;

public class UserDoActionBox extends UsersDoBase implements OnActionItemClickListener {
    private ActionItem callItem;
    private ActionItem goToMainItem;
    protected QuickAction quickAction;

    public UserDoActionBox(Context context, UserInfo user, View anchor) {
        super(context, user, anchor);
        this.quickAction = new QuickAction(context);
        this.quickAction.setOnActionItemClickListener(this);
        this.goToMainItem = new ActionItem(0, 2131166404, 2130838581);
        this.quickAction.addActionItem(this.goToMainItem);
        if (Utils.userCanCall(user)) {
            this.callItem = new ActionItem(2, 2131165468, 2130838571);
            this.quickAction.addActionItem(this.callItem);
        }
    }

    public void onItemClick(QuickAction source, int pos, int actionId) {
        switch (actionId) {
            case RECEIVED_VALUE:
                if (this.onGoToMainPageSelectListener != null) {
                    this.onGoToMainPageSelectListener.onGoToMainPageSelect(this.user, this.anchor);
                }
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (this.onCallUserSelectListener != null) {
                    this.onCallUserSelectListener.onCallUserSelect(this.user, this.anchor);
                }
            default:
        }
    }

    public void show() {
        this.quickAction.show(this.anchor);
    }
}
