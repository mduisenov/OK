package ru.ok.android.services.messages;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Pair;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.model.cache.ram.ConversationsCache;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.ConversationProto.Conversation.Builder;
import ru.ok.android.services.processors.messaging.ConversationsProcessor;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NotificationsUtils;
import ru.ok.android.utils.controls.events.EventsManager;

final class MarkAsReadHelper {
    static void actionMarkAsRead(Context context, Intent intent) {
        boolean fromNotification = intent.getBooleanExtra("from-notification", false);
        if (fromNotification) {
            StatisticManager.getInstance().addStatisticEvent("message-notification-mark-as-read", new Pair[0]);
        }
        String conversationId = intent.getStringExtra("conversation_id");
        String messageId = intent.getStringExtra("message_id");
        Logger.m173d("conversationId: %s, messageId: %s", conversationId, messageId);
        if (!TextUtils.isEmpty(conversationId) && !TextUtils.isEmpty(messageId)) {
            if (fromNotification) {
                NotificationsUtils.hideNotificationForConversation(context, conversationId);
            }
            Conversation conversation = ConversationsCache.getInstance().getConversation(conversationId);
            if (conversation == null) {
                Logger.m185w("Conversation is not in cache: %s", conversationId);
                return;
            }
            long messageTime = MessagesCache.getInstance().getMessageTime(conversationId, messageId);
            if (messageTime > conversation.getLastViewTime()) {
                Builder lastViewTime = conversation.toBuilder().setLastViewTime(messageTime);
                if (messageId == null) {
                    messageId = "";
                }
                ConversationsCache.getInstance().updateConversation(lastViewTime.setLastViewedMessageId(messageId).setNewMessagesCount(0).build());
                context.getContentResolver().notifyChange(OdklProvider.conversationUri(conversationId), null);
                int count = 0;
                for (Conversation c : ConversationsCache.getInstance().getAllConversations()) {
                    if (c.getNewMessagesCount() > 0) {
                        count++;
                    }
                }
                EventsManager.getInstance().updateConversationsCounter(count);
                ConversationsProcessor.markAsReadSingleConversation(conversationId);
            }
        }
    }
}
