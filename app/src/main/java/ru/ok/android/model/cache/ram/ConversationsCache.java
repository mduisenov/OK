package ru.ok.android.model.cache.ram;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.DatabaseExecutor;
import ru.ok.android.db.DatabaseExecutor.DatabaseOperation;
import ru.ok.android.db.access.DBStatementsFactory;
import ru.ok.android.db.access.QueriesConversations;
import ru.ok.android.db.access.QueriesConversations.Delete;
import ru.ok.android.db.access.QueriesConversations.Insert;
import ru.ok.android.db.access.QueriesConversations.Single;
import ru.ok.android.db.access.QueriesConversations.Update;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.ConversationProto.Conversation.Builder;
import ru.ok.android.proto.ConversationProto.Conversation.Type;
import ru.ok.android.proto.ConversationProto.Participant;
import ru.ok.android.proto.ConversationProto.Participant.OnlineType;
import ru.ok.android.proto.ProtoProxy;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.utils.Logger;
import ru.ok.model.UserInfo;
import ru.ok.model.messages.MessageConversation;

public final class ConversationsCache {
    private static final ConversationsCache instance;
    private final Comparator<String> COMPARATOR;
    private volatile List<String> conversationIds;
    private final Map<String, Conversation> conversations;
    private final SQLiteDatabase db;

    /* renamed from: ru.ok.android.model.cache.ram.ConversationsCache.1 */
    class C03511 implements Comparator<String> {
        C03511() {
        }

        public int compare(String aId, String bId) {
            Conversation a = (Conversation) ConversationsCache.this.conversations.get(aId);
            Conversation b = (Conversation) ConversationsCache.this.conversations.get(bId);
            int aCount = a.getNewMessagesCount();
            int bCount = b.getNewMessagesCount();
            if (aCount > 0 && bCount <= 0) {
                return -1;
            }
            if (aCount <= 0 && bCount > 0) {
                return 1;
            }
            long aTime = a.getLastMsgTime();
            long bTime = b.getLastMsgTime();
            if (aTime > bTime) {
                return -1;
            }
            if (aTime >= bTime) {
                return 0;
            }
            return 1;
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.ConversationsCache.2 */
    class C03522 implements DatabaseOperation {
        final /* synthetic */ boolean val$atLeastOneUserChangedFinal;
        final /* synthetic */ List val$conversations2Update;
        final /* synthetic */ List val$deletedConversations;
        final /* synthetic */ boolean val$hasDeleted;
        final /* synthetic */ boolean val$hasUpdated;

        C03522(boolean z, List list, boolean z2, boolean z3, List list2) {
            this.val$hasDeleted = z;
            this.val$deletedConversations = list;
            this.val$hasUpdated = z2;
            this.val$atLeastOneUserChangedFinal = z3;
            this.val$conversations2Update = list2;
        }

        public void performOperation(SQLiteDatabase db) {
            long time = System.currentTimeMillis();
            if (this.val$hasDeleted) {
                SQLiteStatement delete = DBStatementsFactory.getStatement(db, Delete.QUERY);
                for (String id : this.val$deletedConversations) {
                    delete.bindString(1, id);
                    delete.execute();
                }
            }
            if (this.val$hasUpdated || this.val$atLeastOneUserChangedFinal) {
                SQLiteStatement insert = null;
                SQLiteStatement update = DBStatementsFactory.getStatement(db, Update.QUERY);
                for (Conversation c : this.val$conversations2Update) {
                    try {
                        byte[] bytes = c.toByteArray();
                        update.bindBlob(1, bytes);
                        update.bindString(2, c.getId());
                        if (update.executeUpdateDelete() <= 0) {
                            if (insert == null) {
                                insert = DBStatementsFactory.getStatement(db, Insert.QUERY);
                            }
                            insert.bindBlob(1, bytes);
                            insert.bindString(2, c.getId());
                            insert.execute();
                        }
                    } catch (Throwable e) {
                        Logger.m178e(e);
                        StatisticManager.getInstance().addStatisticEvent("protobuf-conversation-write-fail", new Pair("reason", e.getMessage()));
                        throw e;
                    }
                }
            }
            String str = "Apply diff (d: %d, u: %d) for %.3f seconds";
            Object[] objArr = new Object[3];
            objArr[0] = Integer.valueOf(this.val$hasDeleted ? this.val$deletedConversations.size() : 0);
            objArr[1] = Integer.valueOf(this.val$conversations2Update.size());
            objArr[2] = Float.valueOf(((float) (System.currentTimeMillis() - time)) / 1000.0f);
            Logger.m173d(str, objArr);
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.ConversationsCache.3 */
    class C03533 implements DatabaseOperation {
        C03533() {
        }

        public void performOperation(SQLiteDatabase db) {
            db.execSQL("DELETE FROM conversations");
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.ConversationsCache.4 */
    class C03544 implements DatabaseOperation {
        final /* synthetic */ String val$conversationId;

        C03544(String str) {
            this.val$conversationId = str;
        }

        public void performOperation(SQLiteDatabase db) {
            SQLiteStatement statement = DBStatementsFactory.getStatement(db, Delete.QUERY);
            statement.bindString(1, this.val$conversationId);
            statement.execute();
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.ConversationsCache.5 */
    static class C03555 implements DatabaseOperation {
        final /* synthetic */ Conversation val$conversation;

        C03555(Conversation conversation) {
            this.val$conversation = conversation;
        }

        public void performOperation(SQLiteDatabase db) {
            SQLiteStatement update = DBStatementsFactory.getStatement(db, Update.QUERY);
            try {
                update.bindBlob(1, this.val$conversation.toByteArray());
                update.bindString(2, this.val$conversation.getId());
                update.execute();
            } catch (Throwable e) {
                Logger.m178e(e);
                StatisticManager.getInstance().addStatisticEvent("protobuf-conversation-write-fail", new Pair("reason", e.getMessage()));
            }
        }
    }

    static {
        instance = new ConversationsCache();
    }

    public static ConversationsCache getInstance() {
        return instance;
    }

    public ConversationsCache() {
        this.conversations = new HashMap();
        this.COMPARATOR = new C03511();
        this.db = OdnoklassnikiApplication.getDatabase(OdnoklassnikiApplication.getContext());
    }

    @Nullable
    public String findPrivateByUserId(@NonNull String userId) {
        for (Conversation c : getAllConversations()) {
            if (c.getType() == Type.PRIVATE) {
                for (Participant p : c.getParticipantsList()) {
                    if (userId.equals(p.getId())) {
                        return c.getId();
                    }
                }
                continue;
            }
        }
        return null;
    }

    @NonNull
    public synchronized List<Conversation> getAllConversations() {
        List<Conversation> result;
        result = new ArrayList();
        for (String conversationId : getAllConversationsIds()) {
            result.add(this.conversations.get(conversationId));
        }
        return result;
    }

    @NonNull
    public synchronized List<String> getAllConversationsIds() {
        if (this.conversationIds == null) {
            long time = System.currentTimeMillis();
            Cursor c = this.db.rawQuery(QueriesConversations.List.QUERY, null);
            try {
                this.conversationIds = new ArrayList();
                while (c.moveToNext()) {
                    Conversation conversation = extractConversation(c);
                    if (conversation != null) {
                        String id = conversation.getId();
                        this.conversations.put(id, conversation);
                        this.conversationIds.add(id);
                    }
                }
                resortList();
                Logger.m173d("Full list load time: %.4f seconds, count: %d", Float.valueOf(((float) (System.currentTimeMillis() - time)) / 1000.0f), Integer.valueOf(this.conversations.size()));
                c.close();
            } catch (Throwable th) {
                c.close();
            }
        }
        return this.conversationIds;
    }

    private static Conversation extractConversation(Cursor c) {
        try {
            return Conversation.parseFrom(c.getBlob(0));
        } catch (Throwable e) {
            Logger.m178e(e);
            StatisticManager.getInstance().addStatisticEvent("protobuf-conversation-read-fail", new Pair("reason", e.getMessage()));
            return null;
        }
    }

    public synchronized void applyDiff(@Nullable List<Conversation> conversations, @Nullable List<String> deletedConversations, boolean updateOnlines, @Nullable Runnable completeCallback) {
        boolean hasDeleted;
        boolean hasUpdated;
        List<String> allConversationsIds;
        List<Conversation> conversations2Update;
        boolean atLeastOneUserChanged;
        String currentUserId;
        Conversation conversation;
        Builder cb;
        int i;
        Participant p;
        String participantId;
        UserInfo user;
        OnlineType newOnline;
        boolean atLeastOneUserChangedFinal;
        if (deletedConversations != null) {
            if (!deletedConversations.isEmpty()) {
                hasDeleted = true;
                hasUpdated = conversations == null && !conversations.isEmpty();
                if (hasDeleted || hasUpdated || updateOnlines) {
                    allConversationsIds = getAllConversationsIds();
                    if (hasDeleted) {
                        for (String id : deletedConversations) {
                            this.conversationIds.remove(id);
                            MessagesCache.getInstance().removeConversationMessages(id);
                            this.conversations.remove(id);
                        }
                    }
                    conversations2Update = new ArrayList();
                    atLeastOneUserChanged = false;
                    if (updateOnlines) {
                        currentUserId = OdnoklassnikiApplication.getCurrentUser().uid;
                        for (String id2 : allConversationsIds) {
                            conversation = (Conversation) this.conversations.get(id2);
                            if (conversation != null && conversation.getType() == Type.PRIVATE) {
                                cb = null;
                                for (i = 0; i < conversation.getParticipantsCount(); i++) {
                                    p = conversation.getParticipants(i);
                                    participantId = p.getId();
                                    if (!TextUtils.equals(currentUserId, participantId)) {
                                        user = UsersCache.getInstance().getUser(participantId);
                                        if (user != null) {
                                            newOnline = ProtoProxy.api2Proto(user.online);
                                            if (newOnline == p.getOnline() || user.lastOnline != p.getLastOnline()) {
                                                if (cb == null) {
                                                    cb = conversation.toBuilder();
                                                }
                                                cb.setParticipants(i, p.toBuilder().setOnline(newOnline).setLastOnline(user.lastOnline));
                                                atLeastOneUserChanged = true;
                                                r3 = new Object[2];
                                                r3[0] = user.uid;
                                                r3[1] = conversation.getId();
                                                Logger.m173d("Online changed of user %s in conversation: %s", r3);
                                            }
                                        }
                                    }
                                }
                                if (cb != null) {
                                    Conversation newConversation = cb.build();
                                    conversations2Update.add(newConversation);
                                    this.conversations.put(id2, newConversation);
                                }
                            }
                        }
                    }
                    if (hasUpdated) {
                        for (Conversation conversation2 : conversations) {
                            String conversationId;
                            conversationId = conversation2.getId();
                            if (!this.conversationIds.contains(conversationId)) {
                                Logger.m173d("Add new conversation to list: %s", conversationId);
                                this.conversationIds.add(conversationId);
                            }
                            this.conversations.put(conversationId, conversation2);
                        }
                        conversations2Update.addAll(conversations);
                    }
                    if (!hasDeleted || hasUpdated || atLeastOneUserChanged) {
                        resortList();
                        atLeastOneUserChangedFinal = atLeastOneUserChanged;
                        DatabaseExecutor.getInstance().addOperation(new C03522(hasDeleted, deletedConversations, hasUpdated, atLeastOneUserChangedFinal, conversations2Update), completeCallback);
                    } else {
                        Logger.m172d("No conversation updated, exitting");
                    }
                }
            }
        }
        hasDeleted = false;
        if (conversations == null) {
        }
        allConversationsIds = getAllConversationsIds();
        if (hasDeleted) {
            for (String id22 : deletedConversations) {
                this.conversationIds.remove(id22);
                MessagesCache.getInstance().removeConversationMessages(id22);
                this.conversations.remove(id22);
            }
        }
        conversations2Update = new ArrayList();
        atLeastOneUserChanged = false;
        if (updateOnlines) {
            currentUserId = OdnoklassnikiApplication.getCurrentUser().uid;
            for (String id222 : allConversationsIds) {
                conversation2 = (Conversation) this.conversations.get(id222);
                cb = null;
                for (i = 0; i < conversation2.getParticipantsCount(); i++) {
                    p = conversation2.getParticipants(i);
                    participantId = p.getId();
                    if (!TextUtils.equals(currentUserId, participantId)) {
                        user = UsersCache.getInstance().getUser(participantId);
                        if (user != null) {
                            newOnline = ProtoProxy.api2Proto(user.online);
                            if (newOnline == p.getOnline()) {
                            }
                            if (cb == null) {
                                cb = conversation2.toBuilder();
                            }
                            cb.setParticipants(i, p.toBuilder().setOnline(newOnline).setLastOnline(user.lastOnline));
                            atLeastOneUserChanged = true;
                            r3 = new Object[2];
                            r3[0] = user.uid;
                            r3[1] = conversation2.getId();
                            Logger.m173d("Online changed of user %s in conversation: %s", r3);
                        }
                    }
                }
                if (cb != null) {
                    Conversation newConversation2 = cb.build();
                    conversations2Update.add(newConversation2);
                    this.conversations.put(id222, newConversation2);
                }
            }
        }
        if (hasUpdated) {
            for (Conversation conversation22 : conversations) {
                conversationId = conversation22.getId();
                if (!this.conversationIds.contains(conversationId)) {
                    Logger.m173d("Add new conversation to list: %s", conversationId);
                    this.conversationIds.add(conversationId);
                }
                this.conversations.put(conversationId, conversation22);
            }
            conversations2Update.addAll(conversations);
        }
        if (hasDeleted) {
        }
        resortList();
        atLeastOneUserChangedFinal = atLeastOneUserChanged;
        DatabaseExecutor.getInstance().addOperation(new C03522(hasDeleted, deletedConversations, hasUpdated, atLeastOneUserChangedFinal, conversations2Update), completeCallback);
    }

    public synchronized void rewriteConversations(@Nullable List<Conversation> conversations, @Nullable Runnable completeCallback) {
        List<String> deleted = new ArrayList(getAllConversationsIds());
        if (conversations != null) {
            for (Conversation conversation : conversations) {
                deleted.remove(conversation.getId());
            }
        }
        this.conversationIds.clear();
        for (Conversation conversation2 : conversations) {
            this.conversationIds.add(conversation2.getId());
        }
        applyDiff(conversations, deleted, false, completeCallback);
    }

    private void resortList() {
        Collections.sort(this.conversationIds, this.COMPARATOR);
    }

    public synchronized void clear() {
        this.conversationIds = null;
        this.conversations.clear();
        DatabaseExecutor.getInstance().addOperationSync(new C03533());
    }

    public synchronized Conversation getConversation(String conversationId) {
        Conversation conversation;
        conversation = (Conversation) this.conversations.get(conversationId);
        if (conversation == null) {
            Cursor c = this.db.rawQuery(Single.QUERY, new String[]{conversationId});
            try {
                if (c.moveToFirst()) {
                    conversation = extractConversation(c);
                    if (conversation != null) {
                        this.conversations.put(conversation.getId(), conversation);
                    }
                }
                c.close();
            } catch (Throwable th) {
                c.close();
            }
        }
        return conversation;
    }

    public void updateConversation(Conversation conversation) {
        applyDiff(Arrays.asList(new Conversation[]{conversation}), null, false, null);
    }

    public synchronized void removeConversation(String conversationId) {
        this.conversations.remove(conversationId);
        if (this.conversationIds != null) {
            this.conversationIds.remove(conversationId);
            OdnoklassnikiApplication.getContext().getContentResolver().notifyChange(OdklProvider.conversationUri(conversationId), null);
        }
        MessagesCache.getInstance().removeConversationMessages(conversationId);
        DatabaseExecutor.getInstance().addOperation(new C03544(conversationId));
    }

    public synchronized boolean updateConversationLastViewTime(String conversationId, String actorId, long actionTime) {
        boolean z = false;
        synchronized (this) {
            if (!TextUtils.isEmpty(conversationId)) {
                Conversation conversation = getConversation(conversationId);
                if (conversation != null) {
                    for (int i = 0; i < conversation.getParticipantsCount(); i++) {
                        Participant p = conversation.getParticipants(i);
                        if (TextUtils.equals(p.getId(), actorId) && p.getLastViewTime() < actionTime) {
                            Builder cb = conversation.toBuilder();
                            cb.setParticipants(i, p.toBuilder().setLastViewTime(actionTime));
                            Conversation newConversation = cb.build();
                            this.conversations.put(newConversation.getId(), newConversation);
                            updateConversationDB(newConversation);
                            z = true;
                            break;
                        }
                    }
                }
            }
        }
        return z;
    }

    public synchronized void removeParticipant(String conversationId, String userId) {
        Conversation conversation = getConversation(conversationId);
        if (conversation != null) {
            int length = conversation.getParticipantsCount();
            for (int i = 0; i < length; i++) {
                if (TextUtils.equals(conversation.getParticipants(i).getId(), userId)) {
                    Builder cb = conversation.toBuilder();
                    cb.removeParticipants(i);
                    Conversation newConversation = cb.build();
                    this.conversations.put(newConversation.getId(), newConversation);
                    updateConversationDB(newConversation);
                    break;
                }
            }
        }
    }

    private static void updateConversationDB(Conversation conversation) {
        DatabaseExecutor.getInstance().addOperation(new C03555(conversation));
    }

    public synchronized int getConversationsCount() {
        return getAllConversationsIds().size();
    }

    public synchronized boolean updateConversationLastViewMessage(String conversationId, MessageConversation lastMessage) {
        boolean z = false;
        synchronized (this) {
            if (lastMessage != null) {
                Conversation conversation = getConversation(conversationId);
                if (conversation == null) {
                    Logger.m185w("Conversation not found: %s", conversationId);
                } else {
                    Builder cb = conversation.toBuilder();
                    cb.setLastViewedMessageId(lastMessage.id != null ? lastMessage.id : "").setLastViewTime(lastMessage.date);
                    z = false;
                    if (conversation.getNewMessagesCount() > 0) {
                        cb.setNewMessagesCount(0);
                        z = true;
                    }
                    Logger.m173d("Set last viewed message id: %s for conversation: %s", lastMessage.id, conversationId);
                    updateConversation(cb.build());
                }
            }
        }
        return z;
    }

    public synchronized boolean eraseConversationLastViewedMessageId(String conversationId) {
        boolean z = false;
        synchronized (this) {
            Conversation conversation = getConversation(conversationId);
            if (conversation == null) {
                Logger.m185w("Conversation not found: %s", conversationId);
            } else if (!TextUtils.isEmpty(conversation.getLastViewedMessageId())) {
                getInstance().updateConversation(conversation.toBuilder().setLastViewedMessageId("").build());
                z = true;
            }
        }
        return z;
    }

    public synchronized boolean updateConversationTopic(String conversationId, String topic) {
        boolean z = false;
        synchronized (this) {
            Conversation conversation = getConversation(conversationId);
            if (conversation == null) {
                Logger.m185w("Conversation not found: %s", conversationId);
            } else {
                Builder cb = conversation.toBuilder();
                updateConversation(cb.setTopic(topic != null ? topic : "").setBuiltTopic(ProtoProxy.buildTopic(cb, topic)).build());
                z = true;
            }
        }
        return z;
    }
}
