package ru.ok.android.ui.fragments.messages.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.AvatarImageView;
import ru.ok.android.ui.fragments.messages.adapter.MessageDataView.MessageDateViewProvider;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.ui.utils.RowPosition;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.Utils;
import ru.ok.model.messages.MessageBase;
import ru.ok.model.messages.MessageBase.RepliedTo;
import ru.ok.model.messages.MessageComment;

public final class CommentDataView extends MessageDataView {
    private final int authorBottomMargin;
    private final View authorWithReply;
    private final AvatarImageView repliedAvatar;
    private final View repliedLayout;

    public CommentDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.date.setTextColor(context.getResources().getColor(2131493015));
        this.authorBottomMargin = (int) Utils.dipToPixels(8.0f);
        this.repliedAvatar = (AvatarImageView) findViewById(2131624699);
        this.repliedLayout = findViewById(2131624697);
        this.authorWithReply = findViewById(2131624695);
        this.padding4 = 0;
        this.padding8 = 0;
    }

    public void setMessage(OfflineMessage<? extends MessageBase> offlineMessage, RowPosition rowPosition) {
        super.setMessage(offlineMessage, rowPosition);
        String messageType = ((MessageComment) offlineMessage.message).messageType;
        if (messageType != null && messageType.equals("REMOVED_MESSAGE")) {
            this.messageText.setText(getContext().getString(2131165605));
            this.like.setVisibility(4);
            this.reply.setVisibility(4);
        }
    }

    public void setProvider(MessageDateViewProvider provider) {
        super.setProvider(provider);
        this.repliedLayout.setOnClickListener(provider.getRepliedToClickListener());
    }

    protected int getAuthorTextColor(boolean isMy) {
        return 2131492915;
    }

    protected void layoutIsNewView() {
    }

    protected boolean isTopicLickAttachesSupported() {
        return true;
    }

    protected int getLayoutId() {
        return 2130903129;
    }

    protected String getDateString(OfflineMessage message) {
        return DateFormatter.formatTodayTimeOrOlderDate(getContext(), message.message.date);
    }

    protected void invalidateRowForStatus(OfflineMessage<? extends MessageBase> offlineMessage) {
    }

    protected void updateReplyToBlock(OfflineMessage<? extends MessageBase> offlineMessage) {
        super.updateReplyToBlock(offlineMessage);
        RepliedTo repliedToInfo = offlineMessage.message.repliedToInfo;
        if (!this.expandRepliedToMessage || repliedToInfo == null || TextUtils.isEmpty(this.provider.getAuthorName(repliedToInfo.authorType, repliedToInfo.authorId))) {
            this.repliedAvatar.setVisibility(8);
            this.repliedTo.setVisibility(8);
        } else {
            String avatar = this.provider.getAuthorAvatar(repliedToInfo.authorType, repliedToInfo.authorId);
            this.repliedLayout.setTag(offlineMessage);
            if (avatar != null) {
                ImageViewManager.getInstance().displayImage(avatar, this.repliedAvatar, true, null);
            } else if ("GROUP".equals(repliedToInfo.authorType)) {
                this.repliedAvatar.setImageResource(2130837663);
            } else if (TextUtils.isEmpty(repliedToInfo.authorType)) {
                this.repliedAvatar.setImageResource(2130838321);
            }
            this.repliedAvatar.setVisibility(0);
            this.repliedTo.setVisibility(0);
        }
        this.repliedTo.setText("");
    }

    protected void updateIsNewMargin() {
    }

    public void setAttachments(OfflineMessage<? extends MessageBase> message) {
        super.setAttachments(message);
        if (this.messageText.getVisibility() == 8) {
            ((LayoutParams) this.authorWithReply.getLayoutParams()).bottomMargin = this.authorBottomMargin;
            return;
        }
        ((LayoutParams) this.authorWithReply.getLayoutParams()).bottomMargin = 0;
    }

    protected int getTextTopPadding(MessageBase message) {
        return this.padding4;
    }

    protected int getAuthorVisibility(int avatarVisibility, boolean isMy) {
        return 0;
    }
}
