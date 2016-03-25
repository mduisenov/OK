package ru.ok.android.ui.fragments.messages.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ru.ok.android.bus.Bus;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.model.cache.ram.MessageModel;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.proto.MessagesProto.Message.Status;
import ru.ok.android.proto.ProtoProxy;
import ru.ok.android.services.messages.MessagesService;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.bus.BusMessagingHelper;
import ru.ok.model.Conversation;
import ru.ok.model.messages.Attachment;
import ru.ok.model.messages.MessageAuthor;
import ru.ok.model.messages.MessageBase.MessageBaseBuilder;
import ru.ok.model.messages.MessageBase.RepliedTo;
import ru.ok.model.messages.MessageConversation;
import ru.ok.model.messages.MessageConversation.MessageConversationBuilder;

public final class MessagesConversationLoader extends MessagesBaseLoader<MessageConversation, Conversation> {
    private String conversationId;
    private final String userId;

    /* renamed from: ru.ok.android.ui.fragments.messages.loaders.MessagesConversationLoader.1 */
    class C08821 implements Runnable {
        final /* synthetic */ int val$databaseId;

        C08821(int i) {
            this.val$databaseId = i;
        }

        public void run() {
            MessagesCache.getInstance().updateStatusAndDate(this.val$databaseId, Status.WAITING, System.currentTimeMillis());
            MessagesService.sendActionSendAll(MessagesConversationLoader.this.getContext());
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.loaders.MessagesConversationLoader.2 */
    class C08832 implements Runnable {
        final /* synthetic */ int val$databaseId;

        C08832(int i) {
            this.val$databaseId = i;
        }

        public void run() {
            MessagesCache.getInstance().undoMessageEdit(this.val$databaseId);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.loaders.MessagesConversationLoader.3 */
    class C08843 implements Runnable {
        final /* synthetic */ Context val$context;
        final /* synthetic */ int val$databaseId;
        final /* synthetic */ String val$newText;

        C08843(int i, String str, Context context) {
            this.val$databaseId = i;
            this.val$newText = str;
            this.val$context = context;
        }

        public void run() {
            MessagesCache.getInstance().editMessage(this.val$databaseId, this.val$newText, System.currentTimeMillis());
            MessagesService.sendActionSendAll(this.val$context);
        }
    }

    private class UpdateSingleMessageAsyncTask extends AsyncTask<Void, Void, OfflineMessage<MessageConversation>> {
        final int databaseId;

        private UpdateSingleMessageAsyncTask(int databaseId) {
            this.databaseId = databaseId;
        }

        protected OfflineMessage<MessageConversation> doInBackground(Void... params) {
            MessageModel message = MessagesCache.getInstance().getMessage(this.databaseId);
            if (message == null) {
                return null;
            }
            return ProtoProxy.proto2Api(message);
        }

        protected void onPostExecute(OfflineMessage<MessageConversation> updatedMessage) {
            if (!MessagesConversationLoader.this.isReset()) {
                if (updatedMessage == null) {
                    for (OfflineMessage<MessageConversation> m : MessagesConversationLoader.this.messages) {
                        if (m.offlineData.databaseId == this.databaseId) {
                            MessagesConversationLoader.this.messages.remove(m);
                            MessagesConversationLoader.this.recreateAndDeliverResult(true);
                            return;
                        }
                    }
                } else if (MessagesConversationLoader.this.animationList == null || !MessagesConversationLoader.this.animationList.isAnimating()) {
                    MessagesConversationLoader.this.updateSingleMessage(updatedMessage, false);
                } else {
                    Logger.m173d("List is animating: %s", updatedMessage);
                    MessagesConversationLoader.this.eventsQueue.offer(new UpdateMessageRunnable(updatedMessage));
                }
            }
        }
    }

    public MessagesConversationLoader(Context context, String conversationId, String userId) {
        super(context, 2131166194);
        this.conversationId = conversationId;
        this.userId = userId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    protected void loadFirst() {
        BusMessagingHelper.loadFirstPortion(this.conversationId, this.userId);
    }

    public void loadPrevious() {
        BusMessagingHelper.loadPreviousPortion(this.conversationId, findFirstMessageDate());
    }

    private long findFirstMessageDate() {
        if (0 >= this.messages.size()) {
            return this.messages.isEmpty() ? 0 : ((MessageConversation) ((OfflineMessage) this.messages.get(0)).message).date;
        } else {
            MessageConversation message = ((OfflineMessage) this.messages.get(0)).message;
            if (message.hasServerId()) {
            }
            return message.date;
        }
    }

    public void loadNew(boolean markAsRead) {
        OfflineMessage<MessageConversation> startMessage = OfflineMessage.findFirstWithServerMessage(this.messages);
        OfflineMessage<MessageConversation> endMessage = OfflineMessage.findLastWithServerMessage(this.messages);
        if (!(startMessage == null || endMessage == null)) {
            BusMessagingHelper.updateMessages(this.conversationId, startMessage, endMessage);
        }
        BusMessagingHelper.loadNextPortion(this.conversationId, markAsRead, true, true);
    }

    public void loadNext() {
        BusMessagingHelper.loadNextPortion(this.conversationId, true, false, true);
    }

    public void likeMessage(MessageConversation message) {
    }

    public void addMessage(String text, RepliedTo repliedTo, MessageAuthor messageAuthor) {
        BusMessagingHelper.addMessage(this.conversationId, text, repliedTo, messageAuthor);
    }

    public void addMessage(List<Attachment> attachments, RepliedTo repliedTo, MessageAuthor messageAuthor) {
        BusMessagingHelper.addMessage(this.conversationId, (List) attachments, repliedTo, messageAuthor);
    }

    protected void loadOneMessage(String messageId, String reasonMessageId) {
        BusMessagingHelper.loadOneMessage(this.conversationId, messageId, reasonMessageId);
    }

    public void deleteMessages(ArrayList<OfflineMessage<MessageConversation>> messages, boolean block) {
        BusMessagingHelper.deleteMessages(this.conversationId, messages);
    }

    public void spamMessages(ArrayList<OfflineMessage<MessageConversation>> messages) {
        BusMessagingHelper.spamMessages(this.conversationId, messages);
    }

    public void resendMessage(OfflineMessage<MessageConversation> message) {
        ThreadUtil.execute(new C08821(message.offlineData.databaseId));
    }

    public void undoMessageEdit(OfflineMessage<MessageConversation> message) {
        ThreadUtil.execute(new C08832(message.offlineData.databaseId));
    }

    protected boolean isForCurrentLoader(BusEvent event) {
        return TextUtils.equals(event.bundleInput.getString("CONVERSATION_ID"), this.conversationId);
    }

    protected Uri getUriForMessage(OfflineMessage<MessageConversation> offlineMessage) {
        return null;
    }

    public long extractInitialAccessDate(Conversation generalInfo) {
        return generalInfo.lastViewTime;
    }

    protected OfflineMessage<MessageConversation> convertCursor2OfflineMessage(Cursor cursor) {
        return null;
    }

    protected MessageBaseBuilder<MessageConversation> createMessageBuilder() {
        return new MessageConversationBuilder();
    }

    protected void fillBuilder(MessageConversation message, MessageBaseBuilder<MessageConversation> builder) {
        MessageConversationBuilder conversationBuilder = (MessageConversationBuilder) builder;
        conversationBuilder.setType(message.type);
        conversationBuilder.setConversationId(message.conversationId);
    }

    protected int getFirstPortionEventKind() {
        return 2131624205;
    }

    protected int getPreviousPortionEventKind() {
        return 2131624208;
    }

    protected int getNextPortionEventKind() {
        return 2131624206;
    }

    protected int getMessageAddEventKind() {
        return 2131624197;
    }

    protected int getMessageDeleteEventKind() {
        return 2131624200;
    }

    protected int getMessageSpamEventKind() {
        return 2131624210;
    }

    protected int getMessageLikeEventKind() {
        return 0;
    }

    protected int getMessageLoadOneEventKind() {
        return 2131624207;
    }

    protected int getSingleMessageLoadErrorId() {
        return 2131166198;
    }

    protected void registerBus(@NonNull Bus bus) {
        super.registerBus(bus);
        bus.subscribe(2131624213, this, 2131623946);
    }

    protected void unregisterBus(@NonNull Bus bus) {
        super.unregisterBus(bus);
        bus.unsubscribe(2131624213, this);
    }

    public void consume(@AnyRes int kind, @NonNull BusEvent event) {
        if (kind == 2131624213) {
            int databaseId = event.bundleOutput.getInt("database_id", 0);
            if (databaseId != 0) {
                for (OfflineMessage<MessageConversation> message : this.messages) {
                    if (message.offlineData.databaseId == databaseId) {
                        Logger.m173d("Message changed: %d", Integer.valueOf(databaseId));
                        new UpdateSingleMessageAsyncTask(databaseId, null).execute(new Void[]{(Void) null});
                        return;
                    }
                }
                return;
            }
            return;
        }
        super.consume(kind, event);
    }

    protected void preProcessAddMessageBundle(Bundle bundle) {
        if (bundle.getParcelableArrayList("MESSAGES") == null) {
            MessageModel m = MessagesCache.getInstance().getMessage(bundle.getInt("MESSAGE_ID"));
            if (m != null) {
                bundle.putParcelableArrayList("MESSAGES", new ArrayList(Collections.singletonList(ProtoProxy.proto2Api(m))));
            }
        }
    }

    public void editMessage(OfflineMessage<MessageConversation> message, String newText) {
        ThreadUtil.execute(new C08843(message.offlineData.databaseId, newText, getContext()));
    }
}
