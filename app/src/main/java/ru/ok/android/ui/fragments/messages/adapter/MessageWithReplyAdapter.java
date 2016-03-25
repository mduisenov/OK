package ru.ok.android.ui.fragments.messages.adapter;

import android.content.Context;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.MessagesAdapterListener;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.ui.fragments.messages.loaders.data.RepliedToInfo;
import ru.ok.android.ui.fragments.messages.loaders.data.RepliedToInfo.Status;
import ru.ok.model.messages.MessageBase;

public abstract class MessageWithReplyAdapter<M extends MessageBase, G extends Parcelable> extends MessagesBaseAdapter<M, G> {
    public MessageWithReplyAdapter(Context context, String userUid, MessagesAdapterListener listener) {
        super(context, userUid, listener);
    }

    public int getCount() {
        int count = super.getCount();
        if (count == 0) {
            return 0;
        }
        List<OfflineMessage<M>> messages = getData().messages;
        int i = 0;
        while (i < messages.size()) {
            OfflineMessage<M> message = (OfflineMessage) messages.get(i);
            if (i <= 0 || !isPreviousMessageReply(message, (OfflineMessage) messages.get(i - 1))) {
                count += getMessageRepliesChainCount(message);
            }
            i++;
        }
        return count;
    }

    public OfflineMessage<M> getItem(int position) {
        int i = 0;
        List<OfflineMessage<M>> messages = getData().messages;
        OfflineMessage<M> result = null;
        int mi = 0;
        while (mi < messages.size()) {
            OfflineMessage<M> message = (OfflineMessage) messages.get(mi);
            boolean lookInChain = mi <= 0 || !isPreviousMessageReply(message, (OfflineMessage) messages.get(mi - 1));
            if (lookInChain && message.repliedToInfo != null && message.repliedToInfo.status == Status.EXPANDED) {
                boolean foundInChain = false;
                List<RepliedToInfo> repliesChain = getMessageRepliesChain(message);
                for (int j = repliesChain.size() - 1; j >= 0; j--) {
                    result = ((RepliedToInfo) repliesChain.get(j)).offlineMessage;
                    if (result == null) {
                        result = message;
                    }
                    if (i == position) {
                        foundInChain = true;
                        break;
                    }
                    i++;
                }
                if (foundInChain) {
                    break;
                }
            }
            result = message;
            if (i == position) {
                break;
            }
            i++;
            mi++;
        }
        return result;
    }

    public long getItemId(int position) {
        if (getData() == null) {
            return (long) position;
        }
        if (position >= getCount() - 1) {
            return MessagesBaseAdapter.identifyMessageId(position, getItem(position));
        }
        OfflineMessage<M> curItem = getItem(position);
        OfflineMessage<M> nextItem = getItem(position + 1);
        if (isNextMessageReplies(curItem, nextItem)) {
            return MessagesBaseAdapter.identifyMessageId(position, curItem) + MessagesBaseAdapter.identifyMessageId(position + 1, nextItem);
        }
        return MessagesBaseAdapter.identifyMessageId(position, curItem);
    }

    public int getMessagePosition(OfflineMessage<M> message) {
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i) == message) {
                return i;
            }
        }
        return -1;
    }

    protected boolean isReplied(int position) {
        if (position >= getCount() - 1 || !isNextMessageReplies(getItem(position), getItem(position + 1))) {
            return false;
        }
        return true;
    }

    private int getMessageRepliesChainCount(OfflineMessage<M> message) {
        int count = 0;
        RepliedToInfo replyInfo = message.repliedToInfo;
        while (replyInfo != null && replyInfo.status == Status.EXPANDED) {
            count++;
            replyInfo = replyInfo.offlineMessage != null ? replyInfo.offlineMessage.repliedToInfo : null;
        }
        return count;
    }

    private List<RepliedToInfo> getMessageRepliesChain(OfflineMessage<M> message) {
        List<RepliedToInfo> result = new ArrayList();
        RepliedToInfo replyInfo = message.repliedToInfo;
        while (replyInfo != null && replyInfo.status == Status.EXPANDED) {
            result.add(replyInfo);
            replyInfo = replyInfo.offlineMessage != null ? replyInfo.offlineMessage.repliedToInfo : null;
        }
        return result;
    }

    private boolean isPreviousMessageReply(OfflineMessage<M> message, OfflineMessage<M> prevoiusMessage) {
        if (message.repliedToInfo == null || message.repliedToInfo.status != Status.EXPANDED || message.repliedToInfo.offlineMessage == null || !message.repliedToInfo.offlineMessage.message.id.equals(prevoiusMessage.message.id)) {
            return false;
        }
        return true;
    }

    private boolean isNextMessageReplies(OfflineMessage<M> message, OfflineMessage<M> nextMessage) {
        return isPreviousMessageReply(nextMessage, message);
    }
}
