package ru.ok.android.ui.dialogs.actions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import ru.ok.android.model.cache.ram.UsersCache;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.ConversationProto.Conversation.Type;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.fragments.messages.helpers.ConversationParticipantsUtils;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.ui.quickactions.BaseQuickAction.OnActionItemClickListener;
import ru.ok.android.ui.quickactions.QuickAction;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.model.UserInfo;

public class ConversationDoActionBox implements OnActionItemClickListener {
    private final View anchor;
    private String callUserId;
    private final Conversation conversation;
    private ConversationSelectListener conversationSelectListener;
    private QuickAction quickAction;

    public interface ConversationSelectListener {
        void onCallSelect(String str, View view);

        void onDeleteConversationSelect(Conversation conversation, View view);

        void onLeaveSelected(Conversation conversation, View view);

        void onShortcutSelected(@NonNull Conversation conversation, @NonNull View view);
    }

    public ConversationDoActionBox(Context context, Conversation conversation, View anchor) {
        this.conversation = conversation;
        this.anchor = anchor;
        this.quickAction = new QuickAction(context);
        this.quickAction.setOnActionItemClickListener(this);
        ActionItem item = null;
        if (conversation.getCapabilities().getCanDelete()) {
            item = new ActionItem(0, 2131165672, 2130838574);
        } else if (conversation.getType() == Type.CHAT) {
            item = new ActionItem(1, 2131166035, 2130838574);
        }
        if (item != null) {
            this.quickAction.addActionItem(item);
        }
        if (conversation.getType() == Type.PRIVATE) {
            this.callUserId = ConversationParticipantsUtils.findNonCurrentUserIdProto(conversation.getParticipantsList());
            if (this.callUserId != null) {
                UserInfo callUser = UsersCache.getInstance().getUser(this.callUserId);
                if (callUser != null && callUser.availableCall) {
                    this.quickAction.addActionItem(new ActionItem(2, 2131165468, 2130838571));
                }
            }
        }
        if (!DeviceUtils.isSonyDevice()) {
            this.quickAction.addActionItem(new ActionItem(3, 2131165636, 2130838210));
        }
    }

    public void setConversationSelectListener(ConversationSelectListener conversationSelectListener) {
        this.conversationSelectListener = conversationSelectListener;
    }

    public void onItemClick(QuickAction source, int pos, int actionId) {
        if (this.conversationSelectListener != null) {
            switch (actionId) {
                case RECEIVED_VALUE:
                    this.conversationSelectListener.onDeleteConversationSelect(this.conversation, this.anchor);
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    this.conversationSelectListener.onLeaveSelected(this.conversation, this.anchor);
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    this.conversationSelectListener.onCallSelect(this.callUserId, this.anchor);
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    this.conversationSelectListener.onShortcutSelected(this.conversation, this.anchor);
                default:
            }
        }
    }

    public void show() {
        this.quickAction.show(this.anchor);
    }
}
