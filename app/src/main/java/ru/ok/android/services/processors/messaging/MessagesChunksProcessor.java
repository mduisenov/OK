package ru.ok.android.services.processors.messaging;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.provider.OdklContract.Users;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.model.cache.ram.ConversationsCache;
import ru.ok.android.model.cache.ram.MessageModel;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.model.cache.ram.UsersCache;
import ru.ok.android.proto.ConversationProto;
import ru.ok.android.proto.ProtoProxy;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.bus.BusMessagingHelper;
import ru.ok.java.api.json.messages.JsonMessagesBatchParser;
import ru.ok.java.api.json.messages.JsonMessagesUpdatesParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.batch.SupplierRequest;
import ru.ok.java.api.request.messaging.AttachmentRequest;
import ru.ok.java.api.request.messaging.ChatSingleRequest;
import ru.ok.java.api.request.messaging.MessagesListRequest;
import ru.ok.java.api.request.messaging.MessagesUpdatesRequest;
import ru.ok.java.api.request.paging.PagingAnchor;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.java.api.request.param.RequestJSONParam;
import ru.ok.java.api.request.users.UserInfoRequest;
import ru.ok.java.api.request.users.UserInfoRequest.FIELDS;
import ru.ok.java.api.response.messages.MessagesBatchResponse;
import ru.ok.java.api.response.messages.MessagesUpdatesResponse;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.Conversation;
import ru.ok.model.ConversationParticipant;
import ru.ok.model.ConversationParticipantCapabilities;
import ru.ok.model.UserInfo;
import ru.ok.model.messages.MessageConversation;

public final class MessagesChunksProcessor {

    /* renamed from: ru.ok.android.services.processors.messaging.MessagesChunksProcessor.1 */
    class C04741 implements Runnable {
        final /* synthetic */ BusEvent val$event;
        final /* synthetic */ String val$finalConversationId;
        final /* synthetic */ String val$userId;

        C04741(String str, String str2, BusEvent busEvent) {
            this.val$finalConversationId = str;
            this.val$userId = str2;
            this.val$event = busEvent;
        }

        public void run() {
            try {
                Logger.m173d("conversation '%s' has no messages, perform request to server", this.val$finalConversationId);
                MessagesBatchResponse<MessageConversation, Conversation> batchResponse = MessagesChunksProcessor.performMessagesChunkRequest(this.val$finalConversationId, this.val$userId, true, true, MessagesChunksProcessor.createLoadFirstRequestParams());
                if (TextUtils.isEmpty(this.val$finalConversationId)) {
                    ((Conversation) batchResponse.generalInfoAfter).addParticipant(new ConversationParticipant(this.val$userId, 0, new ConversationParticipantCapabilities(true)));
                    ((Conversation) batchResponse.generalInfoAfter).addParticipant(new ConversationParticipant(OdnoklassnikiApplication.getCurrentUser().uid, 0, new ConversationParticipantCapabilities(true)));
                }
                Bundle outputBundle = MessagesChunksProcessor.processMessagesChunkResponse(batchResponse, 0, PagingDirection.BACKWARD);
                outputBundle.putBoolean("HAS_MORE_PREVIOUS", batchResponse.messages.hasMore);
                GlobalBus.send(2131624205, new BusEvent(this.val$event.bundleInput, outputBundle, -1));
            } catch (Throwable e) {
                Logger.m178e(e);
                GlobalBus.send(2131624205, new BusEvent(this.val$event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
            }
        }
    }

    /* renamed from: ru.ok.android.services.processors.messaging.MessagesChunksProcessor.2 */
    class C04752 implements Runnable {
        final /* synthetic */ String val$conversationId;
        final /* synthetic */ BusEvent val$event;
        final /* synthetic */ long val$time;

        C04752(String str, long j, BusEvent busEvent) {
            this.val$conversationId = str;
            this.val$time = j;
            this.val$event = busEvent;
        }

        public void run() {
            try {
                MessagesBatchResponse<MessageConversation, Conversation> batchResponse = MessagesChunksProcessor.performMessagesChunkRequest(this.val$conversationId, null, true, true, MessagesChunksProcessor.createLoadPreviousRequestParams(this.val$conversationId));
                Bundle outputBundle = MessagesChunksProcessor.processMessagesChunkResponse(batchResponse, this.val$time, PagingDirection.BACKWARD);
                outputBundle.putBoolean("HAS_MORE_PREVIOUS", batchResponse.messages.hasMore);
                GlobalBus.send(2131624208, new BusEvent(this.val$event.bundleInput, outputBundle, -1));
            } catch (Throwable e) {
                Logger.m178e(e);
                GlobalBus.send(2131624208, new BusEvent(this.val$event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
            }
        }
    }

    public static class MessagesRequestParams {
        public final String anchor;
        public final long date;
        public final PagingDirection direction;

        public MessagesRequestParams(PagingDirection direction, String anchor, long date) {
            this.direction = direction;
            this.anchor = anchor;
            this.date = date;
        }
    }

    private static MessageConversation findLastUnreadServerMessage(List<OfflineMessage<MessageConversation>> messages, long lastViewTime, String currentUserId) {
        ListIterator<OfflineMessage<MessageConversation>> it = messages.listIterator(messages.size());
        while (it.hasPrevious()) {
            OfflineMessage<MessageConversation> message = (OfflineMessage) it.previous();
            if (((MessageConversation) message.message).date <= lastViewTime) {
                return null;
            }
            if (((MessageConversation) message.message).hasServerId() && !TextUtils.equals(currentUserId, ((MessageConversation) message.message).authorId)) {
                return (MessageConversation) message.message;
            }
        }
        return null;
    }

    @Subscribe(on = 2131623945, to = 2131624031)
    public void loadFirstPortion(BusEvent event) {
        try {
            String conversationId = event.bundleInput.getString("CONVERSATION_ID");
            String userId = event.bundleInput.getString("USER_IDS");
            Logger.m173d("conversationId: '%s', userId: '%s'", conversationId, userId);
            Context context = OdnoklassnikiApplication.getContext();
            if (OdnoklassnikiApplication.getDatabase(context) != null) {
                if (TextUtils.isEmpty(conversationId)) {
                    conversationId = ConversationsCache.getInstance().findPrivateByUserId(userId);
                    Logger.m173d("conversationId: '%s' found by userId: '%s'", conversationId, userId);
                }
                if (!TextUtils.isEmpty(conversationId)) {
                    ConversationProto.Conversation conversation = ConversationsCache.getInstance().getConversation(conversationId);
                    if (conversation != null) {
                        Logger.m173d("conversation '%s' found", conversationId);
                        ArrayList<OfflineMessage<MessageConversation>> messages = ProtoProxy.proto2Api(MessagesCache.getInstance().getMessagesBefore(conversationId, 0, 50));
                        if (!messages.isEmpty()) {
                            Logger.m173d("conversation '%s' has messages: %d", conversationId, Integer.valueOf(messages.size()));
                            Bundle output = createOutputBundle(ProtoProxy.proto2Api(conversation), messages, queryUsers(messages));
                            output.putBoolean("HAS_MORE_PREVIOUS", messages.size() >= 50);
                            MessageConversation lastMessage = findLastUnreadServerMessage(messages, conversation.getLastViewTime(), OdnoklassnikiApplication.getCurrentUser().getId());
                            if (lastMessage != null) {
                                r15 = new Object[2];
                                r15[0] = lastMessage.id;
                                r15[1] = conversationId;
                                Logger.m173d("Set last viewed message id: %s for conversation: %s", r15);
                                if (ConversationsCache.getInstance().updateConversationLastViewMessage(conversationId, lastMessage)) {
                                    context.getContentResolver().notifyChange(OdklProvider.conversationUri(conversationId), null);
                                }
                            }
                            GlobalBus.send(2131624205, new BusEvent(event.bundleInput, output, -1));
                            return;
                        }
                    }
                }
                GlobalBus.post(new C04741(conversationId, userId, event), 2131623944);
            }
        } catch (Throwable e) {
            Logger.m178e(e);
            GlobalBus.send(2131624205, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623945, to = 2131624034)
    public void loadPreviousMessages(BusEvent event) {
        try {
            String conversationId = event.bundleInput.getString("CONVERSATION_ID");
            long time = event.bundleInput.getLong("TIME");
            Logger.m173d("conversationId: %s, message time: %d", conversationId, Long.valueOf(time));
            if (time != 0) {
                ArrayList<OfflineMessage<MessageConversation>> messages = ProtoProxy.proto2Api(MessagesCache.getInstance().getMessagesBefore(conversationId, time, 50));
                Logger.m173d("Cached messages fetched: %d", Integer.valueOf(messages.size()));
                boolean messageEmpty = messages.isEmpty();
                if (!messageEmpty) {
                    Bundle output = createOutputBundle(ProtoProxy.proto2Api(ConversationsCache.getInstance().getConversation(conversationId)), messages, !messageEmpty ? queryUsers(messages) : null);
                    output.putBoolean("HAS_MORE_PREVIOUS", messages.size() > 0);
                    GlobalBus.send(2131624208, new BusEvent(event.bundleInput, output, -1));
                    if (!messages.isEmpty()) {
                        BusMessagingHelper.updateMessages(conversationId, OfflineMessage.findFirstWithServerMessage(messages), OfflineMessage.findLastWithServerMessage(messages));
                        return;
                    }
                    return;
                }
            }
            GlobalBus.post(new C04752(conversationId, time, event), 2131623944);
        } catch (Throwable e) {
            Logger.m178e(e);
            GlobalBus.send(2131624208, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131624032)
    public void loadNextMessages(BusEvent event) {
        try {
            performLoadNextMessages(event.bundleInput);
        } catch (Throwable e) {
            Logger.m178e(e);
            GlobalBus.send(2131624206, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    public static Bundle performLoadNextMessages(Bundle inputBundle) throws Exception {
        boolean z = true;
        String conversationId = inputBundle.getString("CONVERSATION_ID");
        boolean markAsRead = inputBundle.getBoolean("MARK_AS_READ", true);
        boolean setOnline = inputBundle.getBoolean("SET_ONLINE", true);
        Logger.m173d("conversationId: %s, markAsRead: %s", conversationId, Boolean.valueOf(markAsRead));
        MessagesRequestParams requestParams = createLoadNextRequestParams(conversationId);
        MessagesBatchResponse<MessageConversation, Conversation> batchResponse = performMessagesChunkRequest(conversationId, null, markAsRead, setOnline, requestParams);
        ConversationsCache.getInstance().eraseConversationLastViewedMessageId(conversationId);
        Bundle result = processMessagesChunkResponse(batchResponse, requestParams.date, requestParams.direction);
        String str = "HAS_MORE_NEXT";
        if (!batchResponse.messages.hasMore || batchResponse.messages.isFirst) {
            z = false;
        }
        result.putBoolean(str, z);
        GlobalBus.send(2131624206, new BusEvent(inputBundle, result, -1));
        return result;
    }

    private static MessagesBatchResponse<MessageConversation, Conversation> performMessagesChunkRequest(String conversationId, String userId, boolean markAsRead, boolean setOnline, MessagesRequestParams requestParams) throws Exception {
        return (MessagesBatchResponse) new JsonMessagesBatchParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(createMessagesChunkRequest(conversationId, userId, markAsRead, setOnline, requestParams))).parse();
    }

    private static MessagesRequestParams createLoadFirstRequestParams() {
        return new MessagesRequestParams(PagingDirection.BACKWARD, null, 0);
    }

    private static MessagesRequestParams createLoadPreviousRequestParams(String conversationId) {
        String anchor = null;
        Pair<String, Long> pair = MessagesCache.getInstance().getMessageWithMinDate(conversationId);
        if (!(pair == null || pair.first == null)) {
            anchor = pair.first;
        }
        return new MessagesRequestParams(PagingDirection.BACKWARD, anchor != null ? PagingAnchor.buildAnchor(anchor) : PagingAnchor.LAST.name(), pair != null ? ((Long) pair.second).longValue() : 0);
    }

    public static MessagesRequestParams createLoadNextRequestParams(String conversationId) {
        Pair<String, Long> pair = MessagesCache.getInstance().getMessageWithMaxDate(conversationId);
        if (pair == null || pair.first == null) {
            return new MessagesRequestParams(PagingDirection.BACKWARD, PagingAnchor.LAST.name(), 0);
        }
        return new MessagesRequestParams(PagingDirection.FORWARD, PagingAnchor.buildAnchor((String) pair.first), ((Long) pair.second).longValue());
    }

    public static Bundle processMessagesChunkResponse(MessagesBatchResponse<MessageConversation, Conversation> batchResponse, long messageTime, PagingDirection direction) throws Exception {
        UsersCache.getInstance().updateUsers4Message(batchResponse.users);
        ConversationsCache.getInstance().updateConversation(ProtoProxy.api2Proto((Conversation) batchResponse.generalInfoAfter));
        List<MessageModel> protoMessages = ProtoProxy.api2ProtoM(batchResponse.messages.list);
        String conversationId = ((Conversation) batchResponse.generalInfoAfter).id;
        Bundle outputBundle = createOutputBundle(combineGeneralInfos(batchResponse), ProtoProxy.proto2Api(MessagesCache.getInstance().addMessages(conversationId, protoMessages, messageTime, direction)), batchResponse.users);
        ContentResolver cr = OdnoklassnikiApplication.getContext().getContentResolver();
        cr.notifyChange(OdklProvider.conversationUri(conversationId), null);
        cr.notifyChange(Users.getContentUri(), null);
        return outputBundle;
    }

    private static Conversation combineGeneralInfos(MessagesBatchResponse<MessageConversation, Conversation> response) {
        Conversation a = response.generalInfoAfter;
        Conversation b = (Conversation) response.generalInfoBefore;
        return new Conversation(a.id, a.topic, a.type, a.ownerId, b.lastMsgTime, b.lastViewTime, a.newMessagesCount, a.lastMessage, a.lastAuthorId, a.participants, a.capabilities);
    }

    private static Bundle createOutputBundle(Conversation conversation, ArrayList<OfflineMessage<MessageConversation>> messages, ArrayList<UserInfo> users) {
        Bundle output = new Bundle();
        output.putParcelableArrayList("MESSAGES", messages);
        output.putParcelable("GENERAL_INFO", conversation);
        output.putParcelableArrayList("USERS", users);
        return output;
    }

    static BaseRequest createMessagesChunkRequest(String conversationId, String userId, boolean markAsRead, boolean setOnline, MessagesRequestParams params) {
        MessagesListRequest messagesRequest = new MessagesListRequest(conversationId, userId, params.anchor, params.direction, 50, markAsRead);
        String fields = new RequestFieldsBuilder().addField(FIELDS.FIRST_NAME).addField(FIELDS.LAST_NAME).addField(FIELDS.NAME).addField(FIELDS.GENDER).addField(DeviceUtils.getUserAvatarPicFieldName()).addField(FIELDS.ONLINE).addField(FIELDS.LAST_ONLINE).addField(FIELDS.CAN_VIDEO_CALL).addField(FIELDS.CAN_VIDEO_MAIL).build();
        BaseRequest userInfoRequest = new UserInfoRequest(new RequestJSONParam(new SupplierRequest(messagesRequest.getUserIdsSupplier())), fields, false);
        BaseRequest userInfoRequestParticipants = new UserInfoRequest(new RequestJSONParam(new SupplierRequest("messagesV2.get.user_ids")), fields, false);
        AttachmentRequest attachmentRequest = new AttachmentRequest(new RequestJSONParam(new SupplierRequest(MessagesListRequest.getAttachmentSupplier())));
        ChatSingleRequest conversationInfoRequest = new ChatSingleRequest(conversationId, userId);
        return new BatchRequest(new BatchRequests().addRequest(conversationInfoRequest).addRequest(messagesRequest).addRequest(userInfoRequest).addRequest(userInfoRequestParticipants).addRequest(conversationInfoRequest).addRequest(attachmentRequest), setOnline);
    }

    private static ArrayList<UserInfo> queryUsers(List<OfflineMessage<MessageConversation>> messages) {
        Set<String> userIds = new HashSet();
        for (OfflineMessage<MessageConversation> message : messages) {
            userIds.add(((MessageConversation) message.message).authorId);
            if (((MessageConversation) message.message).repliedToInfo != null) {
                userIds.add(((MessageConversation) message.message).repliedToInfo.authorId);
            }
        }
        return UsersCache.getInstance().getUsers(userIds, null);
    }

    @Subscribe(on = 2131623944, to = 2131624030)
    public void getMessagesUpdates(BusEvent event) {
        try {
            String conversationId = event.bundleInput.getString("CONVERSATION_ID");
            OfflineMessage<MessageConversation> startMessage = (OfflineMessage) event.bundleInput.getParcelable("START_MESSAGE_ID");
            OfflineMessage<MessageConversation> endMessage = (OfflineMessage) event.bundleInput.getParcelable("END_MESSAGE_ID");
            if (startMessage == null || endMessage == null) {
                Logger.m185w("Invalid messages: %s, %s", startMessage, endMessage);
                return;
            }
            long startDate = ((MessageConversation) startMessage.message).date;
            long endDate = ((MessageConversation) endMessage.message).date;
            Logger.m173d("conversationId: %s, lastUpdateTime: %d, startMessage: %s, endMessage: %s", conversationId, Long.valueOf(MessagesCache.getInstance().getMinLastUpdateTime(conversationId, startDate, endDate)), startMessage, endMessage);
            MessagesUpdatesResponse updates = new JsonMessagesUpdatesParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new MessagesUpdatesRequest(conversationId, lastUpdateTime, ((MessageConversation) startMessage.message).id, ((MessageConversation) endMessage.message).id, MessagesListRequest.FIELDS.ALL.getName())).getResultAsObject()).parse();
            if ("OK".equalsIgnoreCase(updates.status)) {
                if (!updates.removedIds.isEmpty()) {
                    MessagesCache.getInstance().removeMessages(conversationId, updates.removedIds);
                    Bundle removedBundle = new Bundle();
                    Bundle bundle = removedBundle;
                    bundle.putStringArrayList("MESSAGE_SERVER_IDS", updates.removedIds);
                    Bundle inputBundle = new Bundle();
                    inputBundle.putString("CONVERSATION_ID", conversationId);
                    GlobalBus.send(2131624200, new BusEvent(inputBundle, removedBundle, -1));
                }
                MessagesCache.getInstance().updateMessages(conversationId, ProtoProxy.api2ProtoM(updates.edited));
                if (updates.lastUpdateTime > 0) {
                    String str = conversationId;
                    long j = startDate;
                    long j2 = endDate;
                    MessagesCache.getInstance().updateLastUpdateTime(str, j, j2, updates.lastUpdateTime);
                    return;
                }
                return;
            }
            Logger.m185w("Messages updates method status no ok: '%s'", updates.status);
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }
}
