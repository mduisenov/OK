package ru.ok.android.ui.fragments.messages.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter$ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.ViewHolder;
import ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.MessagesAdapterListener;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.ui.quickactions.QuickActionList;
import ru.ok.android.ui.utils.RowPosition;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.Utils;
import ru.ok.java.api.response.discussion.info.DiscussionInfoResponse;
import ru.ok.model.messages.MessageComment;

public final class MessagesDiscussionAdapter extends MessageWithReplyAdapter<MessageComment, DiscussionInfoResponse> {
    private CommentActionsBuilder commentActionsBuilder;
    private OnClickListener onOptionsClickListener;
    private OfflineMessage<MessageComment> replyingMessage;

    public interface CommentActionsBuilder {
        void buildActions(QuickActionList quickActionList, OfflineMessage<MessageComment> offlineMessage);
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.MessagesDiscussionAdapter.1 */
    class C08751 implements OnClickListener {
        C08751() {
        }

        public void onClick(View v) {
            if (MessagesDiscussionAdapter.this.commentActionsBuilder != null) {
                OfflineMessage<MessageComment> offlineMessage = (OfflineMessage) v.getTag();
                QuickActionList action = new QuickActionList(v.getContext());
                MessagesDiscussionAdapter.this.commentActionsBuilder.buildActions(action, offlineMessage);
                if (DeviceUtils.getType(MessagesDiscussionAdapter.this.getContext()) == DeviceLayoutType.LARGE) {
                    action.show(v, (int) Utils.dipToPixels(-50.0f));
                } else {
                    action.show(v);
                }
            }
        }
    }

    private class CommentViewHolder extends ViewHolder {
        final View optionsView;
        final View separator;
        final View tailView;

        public CommentViewHolder(View view) {
            super(view);
            this.optionsView = view.findViewById(2131624715);
            this.optionsView.setOnClickListener(MessagesDiscussionAdapter.this.onOptionsClickListener);
            this.separator = view.findViewById(2131624718);
            this.tailView = view.findViewById(2131624719);
        }
    }

    public MessagesDiscussionAdapter(Context context, String userUid, MessagesAdapterListener listener) {
        super(context, userUid, listener);
        this.onOptionsClickListener = new C08751();
        this.paddingBeforeMessageSeparator = 0;
        this.paddingBeforeDateSeparator = 0;
    }

    public void setCommentActionsBuilder(CommentActionsBuilder commentActionsBuilder) {
        this.commentActionsBuilder = commentActionsBuilder;
    }

    protected boolean isCommentingAllowed() {
        if (getData() == null || getData().generalInfo == null) {
            return false;
        }
        DiscussionInfoResponse discussionInfo = getData().generalInfo;
        if (discussionInfo == null || discussionInfo.generalInfo == null || !discussionInfo.generalInfo.permissions.commentAllowed) {
            return false;
        }
        return true;
    }

    protected View newView(Context context, ViewGroup parent, int position) {
        View result = LayoutInflater.from(context).inflate(2130903130, parent, false);
        result.setTag(new CommentViewHolder(result));
        return result;
    }

    protected void bindView(ViewHolder holder, int position, OfflineMessage<MessageComment> message) {
        int i;
        boolean isReplyToPrevious = true;
        super.bindView(holder, position, message);
        CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
        commentViewHolder.optionsView.setTag(message);
        View view = commentViewHolder.separator;
        if (position == 0) {
            i = 4;
        } else {
            i = 0;
        }
        view.setVisibility(i);
        if (this.replyingMessage == null || this.replyingMessage == message) {
            setViewEnabled(commentViewHolder, true);
        } else {
            setViewEnabled(commentViewHolder, false);
        }
        boolean isReplied = isReplied(position);
        if (position == 0 || !isReplied(position - 1)) {
            isReplyToPrevious = false;
        }
        if (isReplied) {
            commentViewHolder.row.setBackgroundResource(2131492984);
        } else {
            commentViewHolder.row.setBackgroundResource(2131492983);
        }
        if (isReplied || isReplyToPrevious) {
            alignSeparatorToLeft(commentViewHolder.separator);
        } else {
            alignSeparatorToAvatar(commentViewHolder.separator);
        }
        if (isReplyToPrevious) {
            commentViewHolder.tailView.setVisibility(0);
        } else {
            commentViewHolder.tailView.setVisibility(4);
        }
    }

    private void alignSeparatorToLeft(View separator) {
        ((LayoutParams) separator.getLayoutParams()).addRule(1, -1);
    }

    private void alignSeparatorToAvatar(View separator) {
        ((LayoutParams) separator.getLayoutParams()).addRule(1, 2131624657);
    }

    private void setViewEnabled(CommentViewHolder holder, boolean enabled) {
        if (holder.row.isEnabled() != enabled) {
            holder.row.setAlpha(enabled ? 1.0f : 0.3f);
            holder.avatar.setEnabled(enabled);
            holder.row.setEnabled(enabled);
            holder.messageDataView.setEnabled(enabled);
            holder.optionsView.setEnabled(enabled);
            holder.messageDataView.setEnabled(enabled);
        }
    }

    protected void updateMessageViewBackground(ViewHolder viewHolder, OfflineMessage<MessageComment> offlineMessage, RowPosition rowPosition) {
    }

    protected String getGroupAvatar() {
        return ((DiscussionInfoResponse) getData().generalInfo).generalInfo.group.avatar;
    }

    protected String getGroupId() {
        return ((DiscussionInfoResponse) getData().generalInfo).generalInfo.group.id;
    }

    public String getGroupName() {
        return ((DiscussionInfoResponse) getData().generalInfo).generalInfo.group.name;
    }

    public boolean isWantToShowNames() {
        return true;
    }

    public boolean isUnlikeAllowed() {
        return true;
    }

    public boolean isLikeAllowed() {
        return true;
    }

    public void setReplyingMessage(OfflineMessage<MessageComment> replyingMessage) {
        this.replyingMessage = replyingMessage;
    }

    public OfflineMessage<MessageComment> getReplyingMessage() {
        return this.replyingMessage;
    }
}
