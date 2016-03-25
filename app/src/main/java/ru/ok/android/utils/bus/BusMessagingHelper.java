package ru.ok.android.utils.bus;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.services.processors.messaging.MessagesAddProcessor;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.model.messages.Attachment;
import ru.ok.model.messages.MessageAuthor;
import ru.ok.model.messages.MessageBase.RepliedTo;
import ru.ok.model.messages.MessageConversation;

public final class BusMessagingHelper {

    /* renamed from: ru.ok.android.utils.bus.BusMessagingHelper.1 */
    static class C14341 implements Runnable {
        final /* synthetic */ Bundle val$bundle;

        C14341(Bundle bundle) {
            this.val$bundle = bundle;
        }

        public void run() {
            MessagesAddProcessor.addMessage(new BusEvent(this.val$bundle));
        }
    }

    /* renamed from: ru.ok.android.utils.bus.BusMessagingHelper.2 */
    static class C14352 implements Runnable {
        final /* synthetic */ Bundle val$bundle;

        C14352(Bundle bundle) {
            this.val$bundle = bundle;
        }

        public void run() {
            MessagesAddProcessor.addMessage(new BusEvent(this.val$bundle));
        }
    }

    public static void addParticipants(String conversationId, ArrayList<String> userIds) {
        Bundle bundle = new Bundle();
        bundle.putString("CONVERSATION_ID", conversationId);
        bundle.putStringArrayList("USER_IDS", userIds);
        GlobalBus.send(2131623950, new BusEvent(bundle));
    }

    public static void createChat(ArrayList<String> userIds) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("USER_IDS", userIds);
        GlobalBus.send(2131623951, new BusEvent(bundle));
    }

    public static void setTopic(String conversationId, String topic) {
        Bundle bundle = new Bundle();
        bundle.putString("CONVERSATION_ID", conversationId);
        bundle.putString("TOPIC", topic);
        GlobalBus.send(2131623954, new BusEvent(bundle));
    }

    public static void leaveChat(String conversationId) {
        Bundle bundle = new Bundle();
        bundle.putString("CONVERSATION_ID", conversationId);
        GlobalBus.send(2131623953, new BusEvent(bundle));
    }

    public static void kickUser(String conversationId, String userId) {
        Bundle bundle = new Bundle();
        bundle.putString("CONVERSATION_ID", conversationId);
        bundle.putString("USER_IDS", userId);
        GlobalBus.send(2131623952, new BusEvent(bundle));
    }

    public static void deleteConversation(String conversationId) {
        Bundle bundle = new Bundle();
        bundle.putString("CONVERSATION_ID", conversationId);
        GlobalBus.send(2131623958, new BusEvent(bundle));
    }

    public static void updateConversation(String conversationId) {
        Bundle bundle = new Bundle();
        bundle.putString("CONVERSATION_ID", conversationId);
        GlobalBus.send(2131623960, new BusEvent(bundle));
    }

    public static void loadFirstPortion(String conversationId, String userId) {
        Bundle bundle = new Bundle();
        bundle.putString("CONVERSATION_ID", conversationId);
        bundle.putString("USER_IDS", userId);
        GlobalBus.send(2131624031, new BusEvent(bundle));
    }

    public static void loadPreviousPortion(String conversationId, long messageDate) {
        Bundle bundle = new Bundle();
        bundle.putString("CONVERSATION_ID", conversationId);
        bundle.putLong("TIME", messageDate);
        GlobalBus.send(2131624034, new BusEvent(bundle));
    }

    public static void loadNextPortion(String conversationId, boolean markAsRead, boolean isNew, boolean setOnline) {
        GlobalBus.send(2131624032, new BusEvent(loadNextMessagesBundle(conversationId, markAsRead, isNew, setOnline)));
    }

    public static Bundle loadNextMessagesBundle(String conversationId, boolean markAsRead, boolean isNew, boolean setOnline) {
        Bundle bundle = new Bundle();
        bundle.putString("CONVERSATION_ID", conversationId);
        bundle.putBoolean("IS_NEW", isNew);
        bundle.putBoolean("MARK_AS_READ", markAsRead);
        bundle.putBoolean("SET_ONLINE", setOnline);
        return bundle;
    }

    public static void addMessage(String conversationId, String text, @Nullable RepliedTo repliedTo, @NonNull MessageAuthor messageAuthor) {
        Bundle bundle = new Bundle();
        bundle.putString("CONVERSATION_ID", conversationId);
        bundle.putString("TEXT", text);
        bundle.putParcelable("AUTHOR", messageAuthor);
        bundle.putParcelable("REPLY_TO", repliedTo);
        ThreadUtil.execute(new C14341(bundle));
    }

    public static void addMessage(String conversationId, List<Attachment> attachments, RepliedTo repliedTo, MessageAuthor messageAuthor) {
        Bundle bundle = new Bundle();
        bundle.putString("CONVERSATION_ID", conversationId);
        bundle.putParcelableArray("ATTACHMENTS", (Parcelable[]) attachments.toArray(new Attachment[attachments.size()]));
        bundle.putParcelable("AUTHOR", messageAuthor);
        bundle.putParcelable("REPLY_TO", repliedTo);
        ThreadUtil.execute(new C14352(bundle));
    }

    public static void loadOneMessage(String conversationId, String messageId, String reasonMessageId) {
        Bundle bundle = new Bundle();
        bundle.putString("CONVERSATION_ID", conversationId);
        bundle.putString("MESSAGE_ID", messageId);
        bundle.putString("REASON_MESSAGE_ID", reasonMessageId);
        GlobalBus.send(2131624033, new BusEvent(bundle));
    }

    public static void deleteMessages(String conversationId, ArrayList<OfflineMessage<MessageConversation>> messages) {
        Bundle bundle = new Bundle();
        bundle.putString("CONVERSATION_ID", conversationId);
        bundle.putParcelableArrayList("MESSAGES", messages);
        GlobalBus.send(2131624025, new BusEvent(bundle));
    }

    public static void spamMessages(String conversationId, ArrayList<OfflineMessage<MessageConversation>> messages) {
        Bundle bundle = new Bundle();
        bundle.putString("CONVERSATION_ID", conversationId);
        bundle.putParcelableArrayList("MESSAGES", messages);
        GlobalBus.send(2131624036, new BusEvent(bundle));
    }

    public static void updateMessages(String conversationId, OfflineMessage<MessageConversation> startMessage, OfflineMessage<MessageConversation> endMessage) {
        Bundle bundle = new Bundle();
        bundle.putString("CONVERSATION_ID", conversationId);
        bundle.putParcelable("START_MESSAGE_ID", startMessage);
        bundle.putParcelable("END_MESSAGE_ID", endMessage);
        GlobalBus.send(2131624030, new BusEvent(bundle));
    }

    public static void sendReadConversations() {
        GlobalBus.send(2131623959, new BusEvent());
    }

    public static void messageUpdated(int databaseId) {
        Bundle params = new Bundle();
        params.putInt("database_id", databaseId);
        GlobalBus.send(2131624213, new BusEvent(null, params));
    }
}
