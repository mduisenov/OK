package ru.ok.android.utils.bus;

import android.os.Bundle;
import java.util.ArrayList;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.java.api.request.paging.PagingAnchor;
import ru.ok.model.Discussion;
import ru.ok.model.messages.MessageAuthor;
import ru.ok.model.messages.MessageBase.RepliedTo;
import ru.ok.model.messages.MessageComment;

public final class BusDiscussionsHelper {
    public static void loadFirstCommentsPortion(Discussion discussion, PagingAnchor anchor) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("DISCUSSION", discussion);
        bundle.putString("ANCHOR", anchor.name());
        GlobalBus.send(2131623967, new BusEvent(bundle));
    }

    public static void loadPreviousCommentsPortion(Discussion discussion, String anchor) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("DISCUSSION", discussion);
        bundle.putString("ANCHOR", anchor);
        GlobalBus.send(2131623969, new BusEvent(bundle));
    }

    public static void loadNextCommentsPortion(Discussion discussion, String anchor, boolean isNew) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("DISCUSSION", discussion);
        bundle.putString("ANCHOR", anchor);
        bundle.putBoolean("IS_NEW", isNew);
        GlobalBus.send(2131623968, new BusEvent(bundle));
    }

    public static void addComment(Discussion discussion, String text, RepliedTo repliedTo, MessageAuthor messageAuthor) {
        addComment(discussion, text, repliedTo, messageAuthor, null);
    }

    public static void addComment(Discussion discussion, String text, RepliedTo repliedTo, MessageAuthor messageAuthor, String serverId) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("DISCUSSION", discussion);
        bundle.putString("TEXT", text);
        bundle.putString("SERVER_ID", serverId);
        bundle.putParcelable("AUTHOR", messageAuthor);
        bundle.putParcelable("REPLY_TO", repliedTo);
        GlobalBus.send(2131623963, new BusEvent(bundle));
    }

    public static void deleteComments(Discussion discussion, ArrayList<OfflineMessage<MessageComment>> messages, boolean block) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("DISCUSSION", discussion);
        bundle.putParcelableArrayList("MESSAGES", messages);
        bundle.putBoolean("BLOCK", block);
        GlobalBus.send(2131623964, new BusEvent(bundle));
    }

    public static void spamComments(Discussion discussion, ArrayList<OfflineMessage<MessageComment>> messages) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("DISCUSSION", discussion);
        bundle.putParcelableArrayList("MESSAGES", messages);
        GlobalBus.send(2131623972, new BusEvent(bundle));
    }

    public static void likeComment(Discussion discussionInfo, MessageComment comment, String logContext) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("DISCUSSION", discussionInfo);
        bundle.putString("MESSAGE_ID", comment.id);
        bundle.putParcelable("LIKE_INFO", comment.likeInfo);
        bundle.putString("LOG_CONTEXT", logContext);
        GlobalBus.send(2131623966, new BusEvent(bundle));
    }

    public static void loadOneComment(Discussion discussion, String messageId, String reasonMessageId) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("DISCUSSION", discussion);
        bundle.putString("MESSAGE_ID", messageId);
        bundle.putString("REASON_MESSAGE_ID", reasonMessageId);
        GlobalBus.send(2131623975, new BusEvent(bundle));
    }

    public static void editComment(Discussion discussion, OfflineMessage<MessageComment> comment, String newText) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("DISCUSSION", discussion);
        bundle.putParcelable("COMMENT", comment);
        bundle.putString("TEXT", newText);
        GlobalBus.send(2131623973, new BusEvent(bundle));
    }

    public static void undoCommentEdit(OfflineMessage<MessageComment> comment) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("COMMENT", comment);
        GlobalBus.send(2131623974, new BusEvent(bundle));
    }
}
