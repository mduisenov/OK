package ru.ok.android.offline;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.utils.Logger;

public final class NotificationUtils {
    private static final String LOG_TAG;

    static {
        LOG_TAG = NotificationUtils.class.getSimpleName();
    }

    public static void sendDiscussionCommentFailedBroadcast(Context context, String discussionId, String discussionType, String comment) {
        Logger.m173d(LOG_TAG, "sendDiscussionCommentFailedBroadcast: " + discussionId + ", " + discussionType + " - " + comment);
        Intent intent = new Intent("ru.ok.android.action.NOTIFY");
        intent.putExtra("dsc_id", discussionId + ":" + discussionType);
        intent.putExtra(Message.ELEMENT, context.getResources().getString(2131165602, new Object[]{comment}));
        intent.putExtra("general_error", true);
        context.sendOrderedBroadcast(intent, null);
    }

    public static void sendDiscussionCommentFailedServerBroadcast(Context context, String discussionId, String discussionType, String errorMessage, String comment) {
        Logger.m173d(LOG_TAG, "sendDiscussionCommentFailedServerBroadcast: " + discussionId + ", " + discussionType + " - " + comment);
        Intent intent = new Intent("ru.ok.android.action.NOTIFY");
        intent.putExtra("dsc_id", discussionId + ":" + discussionType);
        intent.putExtra("server_error", true);
        intent.putExtra(Message.ELEMENT, context.getResources().getString(2131165604, new Object[]{errorMessage, comment}));
        context.sendOrderedBroadcast(intent, null);
    }

    public static void sendMessageFailedBroadcast(Context context, String conversationId, String message) {
        Logger.m173d(LOG_TAG, "sendMessageFailedBroadcast: " + conversationId + ", " + message);
        Intent intent = new Intent("ru.ok.android.action.NOTIFY");
        intent.putExtra("conversation_id", conversationId);
        String str = Message.ELEMENT;
        Resources resources = context.getResources();
        Object[] objArr = new Object[1];
        if (message == null) {
            message = "";
        }
        objArr[0] = message;
        intent.putExtra(str, resources.getString(2131166200, objArr));
        intent.putExtra("general_error", true);
        context.sendOrderedBroadcast(intent, null);
    }

    public static void sendMessageFailedServerBroadcast(Context context, String conversationId, String errorMessage, String message) {
        Logger.m173d(LOG_TAG, "sendMessageFailedServerBroadcast: receiver: " + conversationId + ", message: " + message + ", error: " + errorMessage);
        Intent intent = new Intent("ru.ok.android.action.NOTIFY");
        intent.putExtra("conversation_id", conversationId);
        intent.putExtra("server_error", true);
        intent.putExtra(Message.ELEMENT, context.getResources().getString(2131166202, new Object[]{errorMessage, message}));
        context.sendOrderedBroadcast(intent, null);
    }
}
