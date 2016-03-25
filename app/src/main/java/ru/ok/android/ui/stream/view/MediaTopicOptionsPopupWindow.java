package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.text.TextUtils;
import java.util.Arrays;
import java.util.List;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.groups.data.MediaTopicFeed;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.ui.quickactions.QuickAction;
import ru.ok.android.ui.stream.view.FeedOptionsPopupWindow.FeedOptionsPopupListener;
import ru.ok.model.stream.Feed;

public final class MediaTopicOptionsPopupWindow extends FeedOptionsPopupWindow {
    private final MediaTopicFeedOptionsPopupListener adapterListener;
    protected ActionItem pinAction;
    private boolean pinEnabled;
    protected ActionItem setToStatusAction;
    private boolean setToStatusEnabled;
    protected ActionItem unpinAction;

    public interface MediaTopicFeedOptionsPopupListener extends FeedOptionsPopupListener {
        void onPinClicked(int i, Feed feed);

        void onSetToStatusClicked(int i, Feed feed);

        void onUnPinClicked(int i, Feed feed);
    }

    public MediaTopicOptionsPopupWindow(Context context, MediaTopicFeedOptionsPopupListener listener) {
        super(context, null);
        this.adapterListener = listener;
    }

    protected List<ActionItem> getActionItems() {
        this.setToStatusAction = new ActionItem(2, 2131166175, 2130838207);
        this.pinAction = new ActionItem(3, 2131166355, 2130838209);
        this.unpinAction = new ActionItem(4, 2131166748, 2130838209);
        this.markAsSpamAction = new ActionItem(0, 2131165855, 2130838053);
        this.deleteAction = new ActionItem(1, 2131165671, 2130838045);
        return Arrays.asList(new ActionItem[]{this.setToStatusAction, this.pinAction, this.unpinAction, this.markAsSpamAction, this.deleteAction});
    }

    public static boolean isOptionsButtonVisible(Feed feed, boolean topicToStatusEnabled, boolean topicPinEnabled) {
        return topicToStatusEnabled || topicPinEnabled || !TextUtils.isEmpty(feed.getSpamId()) || !TextUtils.isEmpty(feed.getDeleteId());
    }

    public void setFeed(int position, Feed feed, int itemAdapterPosition) {
        boolean z = true;
        super.setFeed(position, feed, itemAdapterPosition);
        ActionItem actionItem = this.setToStatusAction;
        boolean z2 = this.setToStatusEnabled && canSetToStatus(feed);
        setActionItemVisibility(actionItem, z2);
        actionItem = this.pinAction;
        if (!this.pinEnabled || feed.isPinned()) {
            z2 = false;
        } else {
            z2 = true;
        }
        setActionItemVisibility(actionItem, z2);
        actionItem = this.unpinAction;
        if (this.pinEnabled && feed.isPinned()) {
            z2 = true;
        } else {
            z2 = false;
        }
        setActionItemVisibility(actionItem, z2);
        actionItem = this.markAsSpamAction;
        if (TextUtils.isEmpty(feed.getSpamId())) {
            z2 = false;
        } else {
            z2 = true;
        }
        setActionItemVisibility(actionItem, z2);
        ActionItem actionItem2 = this.deleteAction;
        if (TextUtils.isEmpty(feed.getDeleteId())) {
            z = false;
        }
        setActionItemVisibility(actionItem2, z);
    }

    private boolean canSetToStatus(Feed feed) {
        return (feed instanceof MediaTopicFeed) && ((MediaTopicFeed) feed).isCanSetToStatus();
    }

    public void onItemClick(QuickAction source, int position, int actionId) {
        switch (actionId) {
            case RECEIVED_VALUE:
                this.adapterListener.onMarkAsSpamClicked(this.feedPosition, this.feed, this.itemAdapterPosition);
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.adapterListener.onDeleteClicked(position, this.feed, this.itemAdapterPosition);
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                this.adapterListener.onSetToStatusClicked(this.feedPosition, this.feed);
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                this.adapterListener.onPinClicked(this.feedPosition, this.feed);
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                this.adapterListener.onUnPinClicked(this.feedPosition, this.feed);
                break;
        }
        dismiss();
    }

    public void setSetToStatusEnabled(boolean topicToStatusEnabled) {
        this.setToStatusEnabled = topicToStatusEnabled;
    }

    public void setPinEnabled(boolean pinEnabled) {
        this.pinEnabled = pinEnabled;
    }
}
