package ru.ok.android.services.processors.offline.discussions;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.db.access.DiscussionsStorageFacade;
import ru.ok.android.offline.NotificationUtils;
import ru.ok.android.services.processors.offline.OfflineShowNotificationBaseProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;

public final class DiscussionShowNotificationProcessor extends OfflineShowNotificationBaseProcessor {
    private static final String COMMAND_NAME;

    static {
        COMMAND_NAME = DiscussionShowNotificationProcessor.class.getName();
    }

    public static void fillIntent(Intent intent, String discussionId, String discussionType, int commentId) {
        intent.putExtra("COMMAND_NAME", COMMAND_NAME);
        intent.putExtra("DISCUSSION_ID", discussionId);
        intent.putExtra("DISCUSSION_TYPE", discussionType);
        intent.putExtra("COMMENT_ID", commentId);
    }

    public DiscussionShowNotificationProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    protected Cursor queryItem(Context context, Intent data) {
        return DiscussionsStorageFacade.queryComment(context, data.getIntExtra("COMMENT_ID", 0));
    }

    protected void sendSendingFailedBroadcast(Context context, Intent data, Cursor cursor) {
        NotificationUtils.sendDiscussionCommentFailedBroadcast(context, data.getStringExtra("DISCUSSION_ID"), data.getStringExtra("DISCUSSION_TYPE"), cursor.getString(cursor.getColumnIndex(Message.ELEMENT)));
    }
}
