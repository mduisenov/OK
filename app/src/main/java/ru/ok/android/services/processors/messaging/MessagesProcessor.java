package ru.ok.android.services.processors.messaging;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.AnyRes;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONException;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.model.cache.ram.ConversationsCache;
import ru.ok.android.model.cache.ram.MessageModel;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.proto.ProtoProxy;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.JsonBooleanParser;
import ru.ok.java.api.json.JsonIdParser;
import ru.ok.java.api.json.conversations.JsonChatAddParticipantsBatchParser;
import ru.ok.java.api.json.conversations.JsonConversationKickUserParser;
import ru.ok.java.api.json.conversations.JsonConversationParser;
import ru.ok.java.api.json.messages.JsonMessageParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.messaging.ChatAddParticipantsRequest;
import ru.ok.java.api.request.messaging.ChatCreateRequest;
import ru.ok.java.api.request.messaging.ChatDeleteRequest;
import ru.ok.java.api.request.messaging.ChatKickUserRequest;
import ru.ok.java.api.request.messaging.ChatLeaveRequest;
import ru.ok.java.api.request.messaging.ChatSetTopicRequest;
import ru.ok.java.api.request.messaging.ChatSingleRequest;
import ru.ok.java.api.request.messaging.MessageLoadOneRequest;
import ru.ok.java.api.request.messaging.MessagesDeleteRequest;
import ru.ok.java.api.request.messaging.MessagesMarkAsSpamRequest;
import ru.ok.java.api.request.param.BaseStringParam;
import ru.ok.java.api.response.messages.ChatAddParticipantsResponse;
import ru.ok.model.Conversation;
import ru.ok.model.Conversation.Type;
import ru.ok.model.ConversationCapabilities;
import ru.ok.model.ConversationParticipant;
import ru.ok.model.ConversationParticipantCapabilities;
import ru.ok.model.messages.MessageBase;
import ru.ok.model.messages.MessageConversation;
import ru.ok.model.messages.MessageConversation.MessageConversationBuilder;

public final class MessagesProcessor {

    public interface DeleteCallback {
        BaseRequest createDeleteRequest(Bundle bundle, Set<String> set);

        void deleteDatabaseMessages(Collection<Integer> collection);

        void onPostDelete(Bundle bundle) throws Exception;

        void unscheduleUndeliveredNotification(Bundle bundle, Integer num);
    }

    private static abstract class DeleteCallbackAdapter implements DeleteCallback {
        private final String conversationId;

        protected DeleteCallbackAdapter(String conversationId) {
            this.conversationId = conversationId;
        }

        public void deleteDatabaseMessages(Collection<Integer> ids) {
            MessagesCache.getInstance().removeMessagesDatabaseIds(this.conversationId, ids);
        }

        public void unscheduleUndeliveredNotification(Bundle data, Integer id) {
        }

        public void onPostDelete(Bundle data) throws Exception {
            ConversationsCache.getInstance().updateConversation(ProtoProxy.api2Proto(new JsonConversationParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new ChatSingleRequest(data.getString("CONVERSATION_ID"), null)).getResultAsObject().getJSONObject("conversation")).parse()));
        }
    }

    /* renamed from: ru.ok.android.services.processors.messaging.MessagesProcessor.1 */
    class C04761 extends DeleteCallbackAdapter {
        final /* synthetic */ String val$conversationId;

        C04761(String x0, String str) {
            this.val$conversationId = str;
            super(x0);
        }

        public BaseRequest createDeleteRequest(Bundle data, Set<String> serverIds) {
            return new MessagesDeleteRequest(this.val$conversationId, serverIds);
        }
    }

    /* renamed from: ru.ok.android.services.processors.messaging.MessagesProcessor.2 */
    class C04772 extends DeleteCallbackAdapter {
        final /* synthetic */ String val$conversationId;

        C04772(String x0, String str) {
            this.val$conversationId = str;
            super(x0);
        }

        public BaseRequest createDeleteRequest(Bundle data, Set<String> serverIds) {
            return new MessagesMarkAsSpamRequest(this.val$conversationId, serverIds);
        }
    }

    @Subscribe(on = 2131623944, to = 2131623950)
    public void addParticipants(BusEvent event) {
        try {
            String conversationId = event.bundleInput.getString("CONVERSATION_ID");
            ChatAddParticipantsRequest addRequest = new ChatAddParticipantsRequest(conversationId, event.bundleInput.getStringArrayList("USER_IDS"));
            ChatAddParticipantsResponse response = new JsonChatAddParticipantsBatchParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new BatchRequest(new BatchRequests().addRequest(addRequest).addRequest(new ChatSingleRequest(conversationId, null))))).parse();
            ConversationsCache.getInstance().updateConversation(ProtoProxy.api2Proto(response.conversation));
            Bundle output = new Bundle();
            output.putStringArrayList("BLOCKED_USER_IDS", response.blockedUsers);
            GlobalBus.send(2131624130, new BusEvent(event.bundleInput, output, -1));
        } catch (Exception ex) {
            GlobalBus.send(2131624130, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(ex), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623951)
    public void createChat(BusEvent event) {
        try {
            ArrayList<String> userIds = event.bundleInput.getStringArrayList("USER_IDS");
            String chatId = (String) new JsonIdParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new ChatCreateRequest(userIds))).parse();
            ArrayList<ConversationParticipant> participants = new ArrayList();
            Iterator i$ = userIds.iterator();
            while (i$.hasNext()) {
                String str = (String) i$.next();
                participants.add(new ConversationParticipant(userId, 0, new ConversationParticipantCapabilities(false)));
            }
            long lastMsgTimeNow = System.currentTimeMillis();
            long lastMsgViewTimeNow = lastMsgTimeNow;
            ConversationsCache.getInstance().updateConversation(ProtoProxy.api2Proto(new Conversation(chatId, null, Type.CHAT, OdnoklassnikiApplication.getCurrentUser().uid, lastMsgTimeNow, lastMsgViewTimeNow, 0, null, null, participants, new ConversationCapabilities(true, true, false, false, false))));
            OdnoklassnikiApplication.getContext().getContentResolver().notifyChange(OdklProvider.conversationsUri(), null);
            Bundle output = new Bundle();
            output.putString("CONVERSATION_ID", chatId);
            GlobalBus.send(2131624131, new BusEvent(event.bundleInput, output, -1));
        } catch (Exception e) {
            GlobalBus.send(2131624131, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623954)
    public void setTopic(BusEvent event) {
        try {
            int i;
            String conversationId = event.bundleInput.getString("CONVERSATION_ID");
            String topic = event.bundleInput.getString("TOPIC");
            boolean result = ((Boolean) new JsonBooleanParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new ChatSetTopicRequest(conversationId, topic))).parse()).booleanValue();
            if (result && ConversationsCache.getInstance().updateConversationTopic(conversationId, topic)) {
                OdnoklassnikiApplication.getContext().getContentResolver().notifyChange(OdklProvider.conversationUri(conversationId), null);
            }
            Bundle bundle = event.bundleInput;
            if (result) {
                i = -1;
            } else {
                i = -2;
            }
            GlobalBus.send(2131624134, new BusEvent(bundle, null, i));
        } catch (Throwable e) {
            Logger.m178e(e);
            GlobalBus.send(2131624134, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623953)
    public void leaveChat(BusEvent event) {
        String conversationId = event.bundleInput.getString("CONVERSATION_ID");
        try {
            int i;
            boolean result = ((Boolean) new JsonBooleanParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new ChatLeaveRequest(conversationId))).parse()).booleanValue();
            if (result) {
                ConversationsCache.getInstance().removeConversation(conversationId);
            }
            Bundle bundle = event.bundleInput;
            if (result) {
                i = -1;
            } else {
                i = -2;
            }
            GlobalBus.send(2131624133, new BusEvent(bundle, null, i));
        } catch (Exception e) {
            GlobalBus.send(2131624133, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623952)
    public void kickUser(BusEvent event) {
        String conversationId = event.bundleInput.getString("CONVERSATION_ID");
        String userId = event.bundleInput.getString("USER_IDS");
        try {
            if (new JsonConversationKickUserParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new ChatKickUserRequest(conversationId, userId)).getResultAsObject()).parse().success) {
                ConversationsCache.getInstance().removeParticipant(conversationId, userId);
                GlobalBus.send(2131624132, new BusEvent(event.bundleInput, null, -1));
                return;
            }
            GlobalBus.send(2131624132, new BusEvent(event.bundleInput, null, -2));
        } catch (Exception e) {
            GlobalBus.send(2131624132, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623958)
    public void deleteConversation(BusEvent event) {
        String conversationId = event.bundleInput.getString("CONVERSATION_ID");
        try {
            if (((Boolean) new JsonBooleanParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new ChatDeleteRequest(conversationId))).parse()).booleanValue()) {
                ConversationsCache.getInstance().removeConversation(conversationId);
                GlobalBus.send(2131624138, new BusEvent(event.bundleInput, null, -1));
                return;
            }
            GlobalBus.send(2131624138, new BusEvent(event.bundleInput, null, -2));
        } catch (Exception e) {
            GlobalBus.send(2131624138, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623960)
    public void updateConversation(BusEvent event) {
        try {
            ConversationsCache.getInstance().updateConversation(ProtoProxy.api2Proto(new JsonConversationParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new ChatSingleRequest(event.bundleInput.getString("CONVERSATION_ID"), null)).getResultAsObject().getJSONObject("conversation")).parse()));
            GlobalBus.send(2131624139, new BusEvent(event.bundleInput, null, -1));
        } catch (Exception e) {
            GlobalBus.send(2131624139, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131624033)
    public void loadOneMessage(BusEvent event) {
        Exception e;
        String conversationId = event.bundleInput.getString("CONVERSATION_ID");
        String messageId = event.bundleInput.getString("MESSAGE_ID");
        SQLiteDatabase db = OdnoklassnikiApplication.getDatabase(OdnoklassnikiApplication.getContext());
        Bundle output = new Bundle();
        if (db != null) {
            MessageModel message = MessagesCache.getInstance().getMessageByServerId(conversationId, messageId);
            if (message != null) {
                output.putParcelable("MESSAGE", ProtoProxy.proto2Api(message));
                GlobalBus.send(2131624207, new BusEvent(event.bundleInput, output, -1));
                return;
            }
        }
        try {
            JsonHttpResult response = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new MessageLoadOneRequest(conversationId, new BaseStringParam(messageId), "message.*"));
            MessageConversationBuilder msgBuilder = new MessageConversationBuilder();
            JsonMessageParser.parse(response.getResultAsObject().getJSONObject(Message.ELEMENT), msgBuilder);
            msgBuilder.setConversationId(conversationId);
            MessageConversation result = msgBuilder.build();
            if (TextUtils.isEmpty(result.id)) {
                GlobalBus.send(2131624207, new BusEvent(event.bundleInput, null, -2));
                return;
            }
            output.putParcelable("MESSAGE", new OfflineMessage(result, null));
            GlobalBus.send(2131624207, new BusEvent(event.bundleInput, output, -1));
        } catch (BaseApiException e2) {
            e = e2;
            GlobalBus.send(2131624207, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        } catch (JSONException e3) {
            e = e3;
            GlobalBus.send(2131624207, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131624025)
    public void deleteMessages(BusEvent event) {
        String conversationId = event.bundleInput.getString("CONVERSATION_ID");
        deleteGeneral(2131624200, event, new C04761(conversationId, conversationId));
    }

    @Subscribe(on = 2131623944, to = 2131624036)
    public void spamMessages(BusEvent event) {
        String conversationId = event.bundleInput.getString("CONVERSATION_ID");
        deleteGeneral(2131624210, event, new C04772(conversationId, conversationId));
    }

    public static <M extends MessageBase> void deleteGeneral(@AnyRes int kind, BusEvent event, DeleteCallback deleteCallback) {
        Bundle bundleInput = event.bundleInput;
        ArrayList<OfflineMessage<M>> messages = bundleInput.getParcelableArrayList("MESSAGES");
        int resultCode = -1;
        Bundle output = new Bundle();
        Set<Integer> offlineOnlyMessagesIds = new HashSet();
        List<OfflineMessage<M>> offlineOnlyMessages = new ArrayList();
        Set<String> serverIds = new HashSet();
        List<OfflineMessage<M>> serverMessages = new ArrayList();
        Iterator i$ = messages.iterator();
        while (i$.hasNext()) {
            OfflineMessage<M> message = (OfflineMessage) i$.next();
            if (message.message.hasServerId()) {
                serverIds.add(message.message.id);
                serverMessages.add(message);
            } else if (message.offlineData != null) {
                offlineOnlyMessagesIds.add(Integer.valueOf(message.offlineData.databaseId));
                offlineOnlyMessages.add(message);
            }
        }
        deleteCallback.deleteDatabaseMessages(offlineOnlyMessagesIds);
        ArrayList<OfflineMessage<M>> deletedMessages = new ArrayList();
        deletedMessages.addAll(offlineOnlyMessages);
        for (Integer id : offlineOnlyMessagesIds) {
            deleteCallback.unscheduleUndeliveredNotification(bundleInput, id);
        }
        try {
            if (serverIds.isEmpty()) {
                resultCode = -1;
            } else {
                BaseRequest request = deleteCallback.createDeleteRequest(bundleInput, serverIds);
                if (Boolean.FALSE != ((Boolean) new JsonBooleanParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(request)).parse())) {
                    List<Integer> databaseIdsOfServerMessages = new ArrayList();
                    i$ = messages.iterator();
                    while (i$.hasNext()) {
                        message = (OfflineMessage) i$.next();
                        if (!(TextUtils.isEmpty(message.message.id) || message.offlineData == null)) {
                            databaseIdsOfServerMessages.add(Integer.valueOf(message.offlineData.databaseId));
                        }
                    }
                    deleteCallback.deleteDatabaseMessages(databaseIdsOfServerMessages);
                    deletedMessages.addAll(serverMessages);
                    deleteCallback.onPostDelete(bundleInput);
                }
            }
        } catch (Throwable e) {
            resultCode = -2;
            Logger.m178e(e);
        }
        output.putParcelableArrayList("MESSAGES", deletedMessages);
        GlobalBus.send(kind, new BusEvent(bundleInput, output, resultCode));
    }
}
