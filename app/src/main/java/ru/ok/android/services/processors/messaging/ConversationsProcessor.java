package ru.ok.android.services.processors.messaging;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.DatabaseExecutor;
import ru.ok.android.db.DatabaseExecutor.DatabaseOperation;
import ru.ok.android.db.access.DBStatementsFactory;
import ru.ok.android.db.access.QueriesConversations.LastUpdate;
import ru.ok.android.db.access.fillers.UserInfoValuesFiller;
import ru.ok.android.model.cache.ram.ConversationsCache;
import ru.ok.android.model.cache.ram.UsersCache;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.ConversationProto.Participant;
import ru.ok.android.proto.ProtoProxy;
import ru.ok.android.services.processors.friends.GetFriendsProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.conversations.JsonConversationsDiffParser;
import ru.ok.java.api.json.conversations.JsonConversationsParser;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.messaging.ConversationsListDiffRequest;
import ru.ok.java.api.request.messaging.ConversationsListRequest;
import ru.ok.java.api.request.messaging.MessagesMarkAsReadRequest;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.java.api.response.messages.ConversationsListDiffResponse;
import ru.ok.java.api.response.messages.ConversationsResponse;
import ru.ok.model.ConversationParticipant;
import ru.ok.model.UserInfo;

public final class ConversationsProcessor {

    /* renamed from: ru.ok.android.services.processors.messaging.ConversationsProcessor.1 */
    static class C04721 implements Runnable {
        final /* synthetic */ Context val$context;
        final /* synthetic */ ConversationsListDiffResponse val$diffResponse;

        C04721(Context context, ConversationsListDiffResponse conversationsListDiffResponse) {
            this.val$context = context;
            this.val$diffResponse = conversationsListDiffResponse;
        }

        public void run() {
            Settings.storeLongValue(this.val$context, "conversations_last_update", this.val$diffResponse.lastUpdateTime);
        }
    }

    /* renamed from: ru.ok.android.services.processors.messaging.ConversationsProcessor.2 */
    static class C04732 implements DatabaseOperation {
        C04732() {
        }

        public void performOperation(SQLiteDatabase db) {
            long time = System.currentTimeMillis();
            SQLiteStatement statement = DBStatementsFactory.getStatement(db, LastUpdate.QUERY_CONVERSATION);
            statement.bindLong(1, time);
            statement.execute();
        }
    }

    public static void loadAllConversations(boolean setOnline) throws Exception {
        Context context = OdnoklassnikiApplication.getContext();
        if (OdnoklassnikiApplication.getDatabase(context) != null) {
            boolean fetchAllConversations;
            long t = System.currentTimeMillis();
            markAsReadConversations(setOnline);
            ConversationsListDiffResponse diffResponse = fetchConversationsDiff(setOnline);
            if (diffResponse == null || "HAS_MORE".equals(diffResponse.status) || "DISABLED".equals(diffResponse.status) || "ERROR".equals(diffResponse.status) || ConversationsCache.getInstance().getConversationsCount() <= 0) {
                fetchAllConversations = true;
            } else {
                fetchAllConversations = false;
            }
            Runnable competeCallback = (diffResponse == null || diffResponse.lastUpdateTime <= 0) ? null : new C04721(context, diffResponse);
            if (fetchAllConversations) {
                fetchAndWriteAllConversations(setOnline, competeCallback);
            } else if ("DIFF".equals(diffResponse.status)) {
                applyDiffResponse(diffResponse, setOnline, competeCallback);
            } else if ("FULL".equals(diffResponse.status)) {
                applyFullResponse(diffResponse, setOnline, competeCallback);
            } else {
                Logger.m185w("Strange status: %s", diffResponse.status);
            }
            updateConversationLastUpdate();
            Logger.m173d("Update conversations: %02f seconds", Float.valueOf(((float) (System.currentTimeMillis() - t)) / 1000.0f));
        }
    }

    private static void updateConversationLastUpdate() {
        DatabaseExecutor.getInstance().addOperation(new C04732());
    }

    private static boolean updateUsers(@NonNull Collection<String> userIds, boolean setOnline) {
        if (userIds.isEmpty()) {
            return true;
        }
        AtomicReference<List<String>> absentUsers = new AtomicReference();
        List<UserInfo> dbUsers = UsersCache.getInstance().getUsers(userIds, absentUsers);
        List<String> dbUserIds = new ArrayList();
        for (UserInfo user : dbUsers) {
            dbUserIds.add(user.uid);
        }
        try {
            List<String> absentUserIds = (List) absentUsers.get();
            if (!(absentUserIds == null || absentUserIds.isEmpty())) {
                Logger.m173d("No users in DB: %d", Integer.valueOf(absentUserIds.size()));
                UsersCache.getInstance().updateUsers(GetFriendsProcessor.requestUsersInfos(absentUserIds, UserInfoValuesFiller.CONVERSATIONS_LIST, setOnline));
            }
            try {
                if (!dbUserIds.isEmpty()) {
                    Logger.m173d("Update online for: %d", Integer.valueOf(dbUserIds.size()));
                    UsersCache.getInstance().updateUsersOnline(GetFriendsProcessor.requestUsersInfos(dbUserIds, UserInfoValuesFiller.ONLINE_ONLY, setOnline));
                }
                return true;
            } catch (Throwable e) {
                Logger.m179e(e, "Failed to fetch online states");
                return true;
            }
        } catch (Throwable e2) {
            Logger.m179e(e2, "Failed to fetch absent users");
            return false;
        }
    }

    private static void applyDiffResponse(ConversationsListDiffResponse response, boolean setOnline, @Nullable Runnable completeCallback) {
        Logger.m173d("Apply diff. Updated %d, removed: %d", Integer.valueOf(response.conversations.size()), Integer.valueOf(response.removedIds.size()));
        Set<String> userIds = collectUsers(response.conversations);
        userIds.addAll(collectUsersCache(ConversationsCache.getInstance().getAllConversations()));
        if (updateUsers(userIds, setOnline)) {
            ConversationsCache.getInstance().applyDiff(ProtoProxy.api2Proto(response.conversations), response.removedIds, true, completeCallback);
        }
    }

    private static void applyFullResponse(ConversationsListDiffResponse response, boolean setOnline, @Nullable Runnable completeCallback) {
        Logger.m173d("Apply full. Updated %d, removed: %d", Integer.valueOf(response.conversations.size()), Integer.valueOf(response.removedIds.size()));
        if (updateUsers(collectUsers(response.conversations), setOnline)) {
            ConversationsCache.getInstance().rewriteConversations(ProtoProxy.api2Proto(response.conversations), completeCallback);
        }
    }

    public static void markAsReadSingleConversation(String conversationId) {
        markAsReadByList(true, Arrays.asList(new String[]{conversationId}));
    }

    private static synchronized void markAsReadConversations(boolean setOnline) throws BaseApiException {
        synchronized (ConversationsProcessor.class) {
            markAsReadByList(setOnline, ConversationsCache.getInstance().getAllConversationsIds());
        }
    }

    private static void markAsReadByList(boolean setOnline, List<String> conversationIds) {
        List<Conversation> conversations = null;
        for (String conversationId : conversationIds) {
            Conversation conversation = ConversationsCache.getInstance().getConversation(conversationId);
            if (!(conversation == null || TextUtils.isEmpty(conversation.getLastViewedMessageId()))) {
                if (conversations == null) {
                    conversations = new ArrayList();
                }
                conversations.add(conversation);
                Logger.m173d("Mark as read conversation: %s with message %s", conversationId, conversation.getLastViewedMessageId());
            }
        }
        if (conversations != null && !conversations.isEmpty()) {
            List<Pair<BatchRequests, ArrayList<Conversation>>> requests = new ArrayList();
            Pair<BatchRequests, ArrayList<Conversation>> currentRequests = null;
            int count = 0;
            for (Conversation c : conversations) {
                if (currentRequests == null) {
                    currentRequests = new Pair(new BatchRequests(), new ArrayList());
                    requests.add(currentRequests);
                }
                ((BatchRequests) currentRequests.first).addRequest(new MessagesMarkAsReadRequest(c.getLastViewedMessageId()));
                count++;
                if (count >= 10) {
                    count = 0;
                    currentRequests = null;
                }
            }
            for (Pair<BatchRequests, ArrayList<Conversation>> request : requests) {
                boolean eraseInDb = true;
                try {
                    JsonHttpResult response = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new BatchRequest((BatchRequests) request.first, setOnline));
                    Logger.m172d("Conversations marked as read successfully on server");
                } catch (Throwable e) {
                    Logger.m178e(e);
                    eraseInDb = false;
                } catch (Throwable e2) {
                    Logger.m178e(e2);
                }
                if (eraseInDb) {
                    Logger.m172d("Erase last viewed message ids");
                    Iterator i$ = ((ArrayList) request.second).iterator();
                    while (i$.hasNext()) {
                        ConversationsCache.getInstance().updateConversation(((Conversation) i$.next()).toBuilder().setLastViewedMessageId("").build());
                    }
                }
            }
        }
    }

    private static ConversationsListDiffResponse fetchConversationsDiff(boolean setOnline) {
        try {
            return JsonConversationsDiffParser.parse(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new ConversationsListDiffRequest(Settings.getLongValue(OdnoklassnikiApplication.getContext(), "conversations_last_update", 0), "conversation.*", setOnline)));
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to parse diff response");
            return null;
        }
    }

    private static void fetchAndWriteAllConversations(boolean setOnline, Runnable completeCallback) throws BaseApiException {
        Logger.m172d("Load whole conversations list...");
        List conversations = fetchAllConversations(setOnline);
        if (updateUsers(collectUsers(conversations), setOnline)) {
            ConversationsCache.getInstance().rewriteConversations(ProtoProxy.api2Proto(conversations), completeCallback);
        }
    }

    @NonNull
    private static Set<String> collectUsers(List<ru.ok.model.Conversation> conversations) {
        Set<String> userIds = new HashSet();
        for (ru.ok.model.Conversation conversation : conversations) {
            Iterator i$ = conversation.participants.iterator();
            while (i$.hasNext()) {
                userIds.add(((ConversationParticipant) i$.next()).id);
            }
        }
        return userIds;
    }

    @NonNull
    private static Set<String> collectUsersCache(List<Conversation> conversations) {
        Set<String> userIds = new HashSet();
        for (Conversation conversation : conversations) {
            for (Participant p : conversation.getParticipantsList()) {
                userIds.add(p.getId());
            }
        }
        return userIds;
    }

    @NonNull
    private static List<ru.ok.model.Conversation> fetchAllConversations(boolean setOnline) throws BaseApiException {
        String anchor = "";
        List<ru.ok.model.Conversation> conversations = new ArrayList();
        ConversationsResponse response;
        do {
            response = (ConversationsResponse) new JsonConversationsParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new ConversationsListRequest(anchor, PagingDirection.BACKWARD, 50, setOnline))).parse();
            conversations.addAll(response.conversations);
            anchor = response.anchor;
        } while (response.hasMore);
        return conversations;
    }

    @Subscribe(on = 2131623944, to = 2131623959)
    public void markAsRead(BusEvent event) {
        try {
            markAsReadConversations(false);
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }
}
