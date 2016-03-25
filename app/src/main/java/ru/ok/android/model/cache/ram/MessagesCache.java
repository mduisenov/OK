package ru.ok.android.model.cache.ram;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Pair;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.DatabaseExecutor;
import ru.ok.android.db.DatabaseExecutor.DatabaseOperation;
import ru.ok.android.db.access.DBStatementsFactory;
import ru.ok.android.db.access.QueriesMessages.CountServerId;
import ru.ok.android.db.access.QueriesMessages.Insert;
import ru.ok.android.db.access.QueriesMessages.QueryToSend;
import ru.ok.android.proto.MessagesProto.Attach;
import ru.ok.android.proto.MessagesProto.Attach.Type;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.proto.MessagesProto.Message.Builder;
import ru.ok.android.proto.MessagesProto.Message.EditInfo;
import ru.ok.android.proto.MessagesProto.Message.Status;
import ru.ok.android.services.persistent.PersistentTaskService;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.messaging.attach.AudioSendAttachmentTask;
import ru.ok.android.services.processors.messaging.attach.PhotoSendAttachmentsTask;
import ru.ok.android.services.processors.messaging.attach.SendAttachmentsTask;
import ru.ok.android.services.processors.messaging.attach.VideoSendAttachmentsTask;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.bus.BusMessagingHelper;
import ru.ok.java.api.response.MessageSendResponse;

public final class MessagesCache {
    private static final MessagesCache instance;
    private final Comparator<Integer> COMPARATOR_IDS;
    private final Comparator<MessageModel> COMPARATOR_MESSAGES;
    private final Map<Integer, MessageModel> allMessages;
    private final SQLiteDatabase db;
    private final LruCache<String, Info> messages;
    private List<Integer> pendingMessages;

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.10 */
    class AnonymousClass10 implements DatabaseOperation {
        final /* synthetic */ MessageModel val$message;
        final /* synthetic */ MessageModel val$resultMessage;
        final /* synthetic */ boolean val$trueEdit;

        AnonymousClass10(MessageModel messageModel, boolean z, MessageModel messageModel2) {
            this.val$resultMessage = messageModel;
            this.val$trueEdit = z;
            this.val$message = messageModel2;
        }

        public void performOperation(SQLiteDatabase db) {
            try {
                byte[] byteArray = this.val$resultMessage.message.toByteArray();
                SQLiteStatement update;
                if (this.val$trueEdit) {
                    update = DBStatementsFactory.getStatement(db, "UPDATE messages SET data = ?, status_editing = ?, _date_editing = ? WHERE _id = ?");
                    update.bindBlob(1, byteArray);
                    update.bindLong(2, (long) this.val$resultMessage.statusEdited.getNumber());
                    update.bindLong(3, this.val$resultMessage.dateEdited);
                    update.bindLong(4, (long) this.val$message.databaseId);
                    update.execute();
                    return;
                }
                update = DBStatementsFactory.getStatement(db, "UPDATE messages SET data = ?, status = ?, _date = ? WHERE _id = ?");
                update.bindBlob(1, byteArray);
                update.bindLong(2, (long) this.val$resultMessage.status.getNumber());
                update.bindLong(3, this.val$resultMessage.date);
                update.bindLong(4, (long) this.val$message.databaseId);
                update.execute();
            } catch (Exception e) {
                StatisticManager.getInstance().addStatisticEvent("protobuf-message-write-fail", new Pair("reason", e.getMessage()));
            }
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.11 */
    class AnonymousClass11 implements DatabaseOperation {
        final /* synthetic */ List val$messages2Update;

        AnonymousClass11(List list) {
            this.val$messages2Update = list;
        }

        public void performOperation(SQLiteDatabase db) {
            SQLiteStatement update = DBStatementsFactory.getStatement(db, String.format("UPDATE messages SET data = ?, _date_editing = ? WHERE _id = ?", new Object[0]));
            for (MessageModel updated : this.val$messages2Update) {
                try {
                    update.bindBlob(1, updated.message.toByteArray());
                    update.bindLong(2, updated.dateEdited);
                    update.bindLong(3, (long) updated.databaseId);
                    update.execute();
                } catch (Exception e) {
                    StatisticManager.getInstance().addStatisticEvent("protobuf-message-write-fail", new Pair("reason", e.getMessage()));
                    return;
                }
            }
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.12 */
    class AnonymousClass12 implements DatabaseOperation {
        final /* synthetic */ ArrayList val$serverIds;

        AnonymousClass12(ArrayList arrayList) {
            this.val$serverIds = arrayList;
        }

        public void performOperation(SQLiteDatabase db) {
            SQLiteStatement statement = DBStatementsFactory.getStatement(db, "DELETE FROM messages WHERE server_id = ?");
            Iterator i$ = this.val$serverIds.iterator();
            while (i$.hasNext()) {
                statement.bindString(1, (String) i$.next());
                statement.execute();
            }
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.13 */
    class AnonymousClass13 implements DatabaseOperation {
        final /* synthetic */ Collection val$databaseIds;

        AnonymousClass13(Collection collection) {
            this.val$databaseIds = collection;
        }

        public void performOperation(SQLiteDatabase db) {
            SQLiteStatement statement = DBStatementsFactory.getStatement(db, "DELETE FROM messages WHERE _id = ?");
            for (Integer intValue : this.val$databaseIds) {
                statement.bindLong(1, (long) intValue.intValue());
                statement.execute();
            }
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.14 */
    class AnonymousClass14 implements DatabaseOperation {
        final /* synthetic */ List val$updated;

        AnonymousClass14(List list) {
            this.val$updated = list;
        }

        public void performOperation(SQLiteDatabase db) {
            SQLiteStatement update = DBStatementsFactory.getStatement(db, "UPDATE messages SET data = ? WHERE _id = ?");
            for (MessageModel message : this.val$updated) {
                try {
                    update.bindBlob(1, message.message.toByteArray());
                    update.bindLong(2, (long) message.databaseId);
                    update.execute();
                } catch (Exception e) {
                    StatisticManager.getInstance().addStatisticEvent("protobuf-message-write-fail", new Pair("reason", e.getMessage()));
                }
            }
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.15 */
    class AnonymousClass15 implements DatabaseOperation {
        final /* synthetic */ int val$databaseId;
        final /* synthetic */ long val$date;
        final /* synthetic */ Status val$status;

        AnonymousClass15(Status status, long j, int i) {
            this.val$status = status;
            this.val$date = j;
            this.val$databaseId = i;
        }

        public void performOperation(SQLiteDatabase db) {
            SQLiteStatement update = DBStatementsFactory.getStatement(db, "UPDATE messages SET status = ?, _date = ? WHERE _id = ?");
            update.bindLong(1, (long) this.val$status.getNumber());
            update.bindLong(2, this.val$date);
            update.bindLong(3, (long) this.val$databaseId);
            update.execute();
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.16 */
    static class AnonymousClass16 implements Runnable {
        final /* synthetic */ String val$attachLocalFile;

        AnonymousClass16(String str) {
            this.val$attachLocalFile = str;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r6 = this;
            r2 = 10;
            r1 = new java.io.File;	 Catch:{ Exception -> 0x0021 }
            r4 = r6.val$attachLocalFile;	 Catch:{ Exception -> 0x0021 }
            r1.<init>(r4);	 Catch:{ Exception -> 0x0021 }
            r3 = r2;
        L_0x000a:
            r4 = r1.exists();	 Catch:{ Exception -> 0x0029 }
            if (r4 == 0) goto L_0x002c;
        L_0x0010:
            r4 = r1.delete();	 Catch:{ Exception -> 0x0029 }
            if (r4 != 0) goto L_0x002c;
        L_0x0016:
            r2 = r3 + -1;
            if (r3 <= 0) goto L_0x0028;
        L_0x001a:
            r4 = 2000; // 0x7d0 float:2.803E-42 double:9.88E-321;
            java.lang.Thread.sleep(r4);	 Catch:{ Exception -> 0x0021 }
            r3 = r2;
            goto L_0x000a;
        L_0x0021:
            r0 = move-exception;
        L_0x0022:
            r4 = "Failed to delete audio recording local file";
            ru.ok.android.utils.Logger.m184w(r4);
        L_0x0028:
            return;
        L_0x0029:
            r0 = move-exception;
            r2 = r3;
            goto L_0x0022;
        L_0x002c:
            r2 = r3;
            goto L_0x0028;
            */
            throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.model.cache.ram.MessagesCache.16.run():void");
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.17 */
    class AnonymousClass17 extends ResultReceiver {
        final /* synthetic */ int val$databaseId;

        AnonymousClass17(Handler x0, int i) {
            this.val$databaseId = i;
            super(x0);
        }

        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String str = "resultCode=%s data=%s";
            Object[] objArr = new Object[2];
            objArr[0] = resultCode == 1 ? "OK" : "not OK";
            objArr[1] = resultData;
            Logger.m173d(str, objArr);
            if (resultCode == 1) {
                Logger.m173d("message=%d taskId=%d", Integer.valueOf(this.val$databaseId), Integer.valueOf(resultData.getInt("task_id")));
                synchronized (MessagesCache.this) {
                    MessagesCache.this.updateMessageBlob(this.val$databaseId, MessagesCache.this.getMessage(this.val$databaseId).message.toBuilder().setTaskId(taskId).build());
                }
            }
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.1 */
    class C03561 extends LruCache<String, Info> {
        C03561(int x0) {
            super(x0);
        }

        protected void entryRemoved(boolean evicted, String key, Info oldValue, Info newValue) {
            super.entryRemoved(evicted, key, oldValue, newValue);
            Logger.m173d("Evict conversation %s", key);
            for (Integer m : oldValue.messageIds) {
                MessagesCache.this.allMessages.remove(m);
            }
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.2 */
    class C03572 implements Comparator<Integer> {
        C03572() {
        }

        public int compare(Integer a, Integer b) {
            MessageModel messageA = MessagesCache.this.getMessage(a.intValue());
            MessageModel messageB = MessagesCache.this.getMessage(b.intValue());
            long aDate = messageA.date;
            long bDate = messageB.date;
            if (aDate > bDate) {
                return 1;
            }
            return aDate < bDate ? -1 : 0;
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.3 */
    class C03583 implements Comparator<MessageModel> {
        C03583() {
        }

        public int compare(MessageModel a, MessageModel b) {
            long aDate = a.date;
            long bDate = b.date;
            if (aDate > bDate) {
                return 1;
            }
            return aDate < bDate ? -1 : 0;
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.4 */
    class C03594 implements DatabaseOperation {
        final /* synthetic */ List val$chunk;
        final /* synthetic */ String val$conversationId;
        final /* synthetic */ List val$result;
        final /* synthetic */ List val$updated;

        C03594(List list, List list2, List list3, String str) {
            this.val$chunk = list;
            this.val$result = list2;
            this.val$updated = list3;
            this.val$conversationId = str;
        }

        public void performOperation(SQLiteDatabase db) {
            SQLiteStatement insert = DBStatementsFactory.getStatement(db, "INSERT INTO messages (server_id, conversation_id, _date, _date_editing, data, status) VALUES (?, ?, ?, ?, ?, ?)");
            SQLiteStatement update = DBStatementsFactory.getStatement(db, "UPDATE messages SET data = ?, status = ? WHERE server_id = ?");
            for (MessageModel m : this.val$chunk) {
                SQLiteDatabase sQLiteDatabase = db;
                Cursor cursor = sQLiteDatabase.rawQuery("SELECT _id FROM messages WHERE server_id = ?", new String[]{String.valueOf(m.serverId)});
                int databaseId;
                if (cursor.moveToFirst()) {
                    databaseId = cursor.getInt(0);
                    MessageModel updatedWithDatabaseId = m.toBuilder().setDatabaseId(databaseId).build();
                    MessageModel existing = MessagesCache.this.getMessageNotSynchronized(databaseId);
                    if (existing != null) {
                        updatedWithDatabaseId = MessagesCache.this.mergeUpdated(existing, updatedWithDatabaseId);
                    }
                    this.val$result.add(updatedWithDatabaseId);
                    this.val$updated.add(Integer.valueOf(databaseId));
                    try {
                        update.bindBlob(1, updatedWithDatabaseId.message.toByteArray());
                        update.bindLong(2, (long) Status.RECEIVED.getNumber());
                        update.bindString(3, updatedWithDatabaseId.serverId);
                        update.executeUpdateDelete();
                    } catch (Exception e) {
                        StatisticManager.getInstance().addStatisticEvent("protobuf-message-write-fail", new Pair("reason", e.getMessage()));
                    } finally {
                        cursor.close();
                    }
                } else {
                    cursor.close();
                    try {
                        byte[] blob = m.message.toByteArray();
                        insert.bindString(1, m.serverId);
                        insert.bindString(2, this.val$conversationId);
                        insert.bindLong(3, m.date);
                        insert.bindLong(4, m.dateEdited);
                        insert.bindBlob(5, blob);
                        insert.bindLong(6, (long) Status.RECEIVED.getNumber());
                        databaseId = (int) insert.executeInsert();
                        List list = this.val$result;
                        r22.add(new MessageModel(databaseId, m.serverId, m.conversationId, m.date, m.dateEdited, m.status, m.statusEdited, m.message));
                    } catch (Exception e2) {
                        StatisticManager.getInstance().addStatisticEvent("protobuf-message-write-fail", new Pair("reason", e2.getMessage()));
                    }
                }
            }
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.5 */
    class C03605 implements DatabaseOperation {
        final /* synthetic */ MessageModel val$message;
        final /* synthetic */ AtomicReference val$ref;

        C03605(MessageModel messageModel, AtomicReference atomicReference) {
            this.val$message = messageModel;
            this.val$ref = atomicReference;
        }

        public void performOperation(SQLiteDatabase db) {
            SQLiteStatement insert = DBStatementsFactory.getStatement(db, Insert.QUERY);
            insert.bindString(1, this.val$message.conversationId);
            insert.bindLong(2, this.val$message.date);
            try {
                insert.bindBlob(3, this.val$message.message.toByteArray());
                insert.bindLong(4, (long) this.val$message.status.getNumber());
                this.val$ref.set(this.val$message.toBuilder().setDatabaseId((int) insert.executeInsert()).build());
            } catch (Exception e) {
                StatisticManager.getInstance().addStatisticEvent("protobuf-message-write-fail", new Pair("reason", e.getMessage()));
            }
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.6 */
    class C03616 implements DatabaseOperation {
        final /* synthetic */ int val$databaseId;
        final /* synthetic */ Status val$status;

        C03616(Status status, int i) {
            this.val$status = status;
            this.val$databaseId = i;
        }

        public void performOperation(SQLiteDatabase db) {
            SQLiteStatement update = DBStatementsFactory.getStatement(db, "UPDATE messages SET status = ? WHERE _id = ?");
            update.bindLong(1, (long) this.val$status.getNumber());
            update.bindLong(2, (long) this.val$databaseId);
            update.execute();
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.7 */
    class C03627 implements DatabaseOperation {
        final /* synthetic */ int val$databaseId;
        final /* synthetic */ Status val$status;

        C03627(Status status, int i) {
            this.val$status = status;
            this.val$databaseId = i;
        }

        public void performOperation(SQLiteDatabase db) {
            SQLiteStatement update = DBStatementsFactory.getStatement(db, "UPDATE messages SET status_editing = ? WHERE _id = ?");
            update.bindLong(1, (long) this.val$status.getNumber());
            update.bindLong(2, (long) this.val$databaseId);
            update.execute();
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.8 */
    class C03638 implements DatabaseOperation {
        final /* synthetic */ MessageModel val$resultMessage;

        C03638(MessageModel messageModel) {
            this.val$resultMessage = messageModel;
        }

        public void performOperation(SQLiteDatabase db) {
            SQLiteStatement update = DBStatementsFactory.getStatement(db, "UPDATE messages SET data = ?, status = ?, _date = ?, server_id = ? WHERE _id = ?");
            try {
                update.bindBlob(1, this.val$resultMessage.message.toByteArray());
                update.bindLong(2, (long) this.val$resultMessage.status.getNumber());
                update.bindLong(3, this.val$resultMessage.date);
                update.bindString(4, this.val$resultMessage.serverId);
                update.bindLong(5, (long) this.val$resultMessage.databaseId);
                update.executeUpdateDelete();
            } catch (Exception e) {
                StatisticManager.getInstance().addStatisticEvent("protobuf-message-write-fail", new Pair("reason", e.getMessage()));
            }
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.MessagesCache.9 */
    class C03649 implements DatabaseOperation {
        final /* synthetic */ int val$databaseId;
        final /* synthetic */ Message val$message;

        C03649(Message message, int i) {
            this.val$message = message;
            this.val$databaseId = i;
        }

        public void performOperation(SQLiteDatabase db) {
            SQLiteStatement update = DBStatementsFactory.getStatement(db, "UPDATE messages SET data = ? WHERE _id = ?");
            try {
                update.bindBlob(1, this.val$message.toByteArray());
                update.bindLong(2, (long) this.val$databaseId);
                update.execute();
            } catch (Exception e) {
                StatisticManager.getInstance().addStatisticEvent("protobuf-message-write-fail", new Pair("reason", e.getMessage()));
            }
        }
    }

    final class Info {
        final String conversationId;
        long maxMessageDate;
        String maxMessageServerId;
        @NonNull
        final List<Integer> messageIds;
        long minMessageDate;
        String minMessageServerId;

        Info(String conversationId) {
            this.messageIds = new ArrayList();
            this.conversationId = conversationId;
        }

        public void updateMinMaxMessages() {
            if (!this.messageIds.isEmpty()) {
                MessageModel firstMessage;
                int i = this.messageIds.size() - 1;
                while (i >= 0) {
                    MessageModel lastMessage = (MessageModel) MessagesCache.this.allMessages.get(Integer.valueOf(((Integer) this.messageIds.get(i)).intValue()));
                    if (lastMessage.status != Status.RECEIVED) {
                        i--;
                    } else {
                        if (this.maxMessageDate == 0 || this.maxMessageDate < lastMessage.date) {
                            this.maxMessageDate = lastMessage.date;
                            this.maxMessageServerId = lastMessage.serverId;
                        }
                        while (i < this.messageIds.size()) {
                            firstMessage = (MessageModel) MessagesCache.this.allMessages.get(Integer.valueOf(((Integer) this.messageIds.get(i)).intValue()));
                            if (firstMessage.status == Status.RECEIVED) {
                            } else if (this.minMessageDate != 0 || this.minMessageDate > firstMessage.date) {
                                this.minMessageDate = firstMessage.date;
                                this.minMessageServerId = firstMessage.serverId;
                                return;
                            } else {
                                return;
                            }
                        }
                    }
                }
                for (i = 0; i < this.messageIds.size(); i++) {
                    firstMessage = (MessageModel) MessagesCache.this.allMessages.get(Integer.valueOf(((Integer) this.messageIds.get(i)).intValue()));
                    if (firstMessage.status == Status.RECEIVED) {
                        if (this.minMessageDate != 0) {
                        }
                        this.minMessageDate = firstMessage.date;
                        this.minMessageServerId = firstMessage.serverId;
                        return;
                    }
                }
            }
        }
    }

    static {
        instance = new MessagesCache();
    }

    private MessagesCache() {
        this.messages = new C03561(20);
        this.allMessages = new HashMap();
        this.COMPARATOR_IDS = new C03572();
        this.COMPARATOR_MESSAGES = new C03583();
        this.db = OdnoklassnikiApplication.getDatabase(OdnoklassnikiApplication.getContext());
    }

    public static MessagesCache getInstance() {
        return instance;
    }

    private synchronized void cleanupConversationUploads(String conversationId) {
        try {
            Iterator<Integer> it = getPendingMessages().iterator();
            while (it.hasNext()) {
                MessageModel message = getMessage(((Integer) it.next()).intValue());
                if (message == null) {
                    Logger.m185w("Message not found: %d", messageId);
                } else if (TextUtils.equals(conversationId, message.conversationId)) {
                    it.remove();
                    if (message.message.getTaskId() != 0) {
                        cleanupUploads(message.message);
                    }
                }
            }
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to stop conversation tasks");
        }
    }

    private static void cleanupUploads(Message message) {
        Logger.m173d("message=%s taskId=%d", message, Integer.valueOf(message.getTaskId()));
        Context context = OdnoklassnikiApplication.getContext();
        Logger.m173d("Cancelling upload task: taskId=%d", Integer.valueOf(taskId));
        context.startService(PersistentTaskService.createCancelTaskIntent(context, taskId));
        for (Attach attachment : message.getAttachesList()) {
            if (attachment.hasAudio() && !TextUtils.isEmpty(attachment.getAudio().getPath())) {
                try {
                    new File(attachment.getAudio().getPath()).delete();
                } catch (Throwable e) {
                    Logger.m179e(e, "Failed to delete attachment local file");
                }
            }
        }
    }

    private List<MessageModel> ids2Messages(List<Integer> messageIds) {
        List<MessageModel> messages = new ArrayList(messageIds.size());
        for (Integer messageId : messageIds) {
            MessageModel message = getMessage(messageId.intValue());
            if (message == null) {
                Logger.m185w("Message not found: %d", messageId);
            } else {
                messages.add(message);
            }
        }
        return messages;
    }

    @NonNull
    public synchronized List<MessageModel> getMessagesBefore(@NonNull String conversationId, long date, int size) {
        List<MessageModel> emptyList;
        Info info = getInfo(conversationId);
        List<Integer> messages = info.messageIds;
        String selectByDate;
        String selectFirst;
        SQLiteDatabase sQLiteDatabase;
        Cursor cursor;
        List<Integer> result;
        int databaseId;
        if (messages.isEmpty()) {
            if (date != 0) {
                Logger.m184w("No one message loaded, but we want to fetch prev chunk");
                emptyList = Collections.emptyList();
            }
            selectByDate = "SELECT _id, conversation_id, server_id, _date, _date_editing, status, status_editing, data FROM messages WHERE _date < ? AND conversation_id = ? ORDER BY _date DESC LIMIT ?";
            selectFirst = "SELECT _id, conversation_id, server_id, _date, _date_editing, status, status_editing, data FROM messages WHERE conversation_id = ? ORDER BY _date DESC LIMIT ?";
            sQLiteDatabase = this.db;
            if (date <= 0) {
                selectByDate = selectFirst;
            }
            cursor = sQLiteDatabase.rawQuery(selectByDate, date > 0 ? new String[]{String.valueOf(date), conversationId, String.valueOf(size)} : new String[]{conversationId, String.valueOf(size)});
            result = new ArrayList();
            while (cursor.moveToNext()) {
                try {
                    databaseId = cursor.getInt(0);
                    if (((MessageModel) this.allMessages.get(Integer.valueOf(databaseId))) == null) {
                        this.allMessages.put(Integer.valueOf(databaseId), messageModelFromCursor(cursor));
                    }
                    result.add(Integer.valueOf(databaseId));
                } catch (Throwable e) {
                    Logger.m178e(e);
                } catch (Throwable th) {
                    cursor.close();
                }
            }
            cursor.close();
            if (!result.isEmpty()) {
                Collections.sort(result, this.COMPARATOR_IDS);
                info.messageIds.addAll(0, result);
                info.updateMinMaxMessages();
            }
            emptyList = ids2Messages(result);
        } else if (date == 0) {
            emptyList = ids2Messages(messages.subList(Math.max(0, messages.size() - size), messages.size()));
        } else {
            boolean found = false;
            for (int i = 0; i < messages.size(); i++) {
                MessageModel message = getMessage(((Integer) messages.get(i)).intValue());
                if (message == null) {
                    Logger.m185w("Message not found: %d", messageId);
                } else {
                    if (message.date == date) {
                        found = true;
                        if (i == 0) {
                            date = message.date;
                            if (!found) {
                                Logger.m185w("We did not found message with date: %d", Long.valueOf(date));
                            }
                            selectByDate = "SELECT _id, conversation_id, server_id, _date, _date_editing, status, status_editing, data FROM messages WHERE _date < ? AND conversation_id = ? ORDER BY _date DESC LIMIT ?";
                            selectFirst = "SELECT _id, conversation_id, server_id, _date, _date_editing, status, status_editing, data FROM messages WHERE conversation_id = ? ORDER BY _date DESC LIMIT ?";
                            sQLiteDatabase = this.db;
                            if (date <= 0) {
                                selectByDate = selectFirst;
                            }
                            if (date > 0) {
                            }
                            cursor = sQLiteDatabase.rawQuery(selectByDate, date > 0 ? new String[]{String.valueOf(date), conversationId, String.valueOf(size)} : new String[]{conversationId, String.valueOf(size)});
                            result = new ArrayList();
                            while (cursor.moveToNext()) {
                                databaseId = cursor.getInt(0);
                                if (((MessageModel) this.allMessages.get(Integer.valueOf(databaseId))) == null) {
                                    this.allMessages.put(Integer.valueOf(databaseId), messageModelFromCursor(cursor));
                                }
                                result.add(Integer.valueOf(databaseId));
                            }
                            cursor.close();
                            if (result.isEmpty()) {
                                Collections.sort(result, this.COMPARATOR_IDS);
                                info.messageIds.addAll(0, result);
                                info.updateMinMaxMessages();
                            }
                            emptyList = ids2Messages(result);
                        } else {
                            int endIndex = i;
                            int startIndex = 0;
                            if (endIndex > size) {
                                startIndex = endIndex - size;
                            }
                            emptyList = ids2Messages(messages.subList(startIndex, endIndex));
                        }
                    }
                }
            }
            if (found) {
                Logger.m185w("We did not found message with date: %d", Long.valueOf(date));
            }
            selectByDate = "SELECT _id, conversation_id, server_id, _date, _date_editing, status, status_editing, data FROM messages WHERE _date < ? AND conversation_id = ? ORDER BY _date DESC LIMIT ?";
            selectFirst = "SELECT _id, conversation_id, server_id, _date, _date_editing, status, status_editing, data FROM messages WHERE conversation_id = ? ORDER BY _date DESC LIMIT ?";
            sQLiteDatabase = this.db;
            if (date <= 0) {
                selectByDate = selectFirst;
            }
            if (date > 0) {
            }
            cursor = sQLiteDatabase.rawQuery(selectByDate, date > 0 ? new String[]{String.valueOf(date), conversationId, String.valueOf(size)} : new String[]{conversationId, String.valueOf(size)});
            result = new ArrayList();
            while (cursor.moveToNext()) {
                databaseId = cursor.getInt(0);
                if (((MessageModel) this.allMessages.get(Integer.valueOf(databaseId))) == null) {
                    this.allMessages.put(Integer.valueOf(databaseId), messageModelFromCursor(cursor));
                }
                result.add(Integer.valueOf(databaseId));
            }
            cursor.close();
            if (result.isEmpty()) {
                Collections.sort(result, this.COMPARATOR_IDS);
                info.messageIds.addAll(0, result);
                info.updateMinMaxMessages();
            }
            emptyList = ids2Messages(result);
        }
        return emptyList;
    }

    private static int containsMessageDB(List<Integer> messageIds, int messageId) {
        for (int i = 0; i < messageIds.size(); i++) {
            if (((Integer) messageIds.get(i)).intValue() == messageId) {
                return i;
            }
        }
        return -1;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.util.List<ru.ok.android.model.cache.ram.MessageModel> addMessages(@android.support.annotation.NonNull java.lang.String r18, @android.support.annotation.Nullable java.util.List<ru.ok.android.model.cache.ram.MessageModel> r19, long r20, @android.support.annotation.NonNull ru.ok.java.api.request.paging.PagingDirection r22) {
        /*
        r17 = this;
        monitor-enter(r17);
        if (r19 == 0) goto L_0x0009;
    L_0x0003:
        r2 = r19.isEmpty();	 Catch:{ all -> 0x0050 }
        if (r2 == 0) goto L_0x000f;
    L_0x0009:
        r5 = java.util.Collections.emptyList();	 Catch:{ all -> 0x0050 }
    L_0x000d:
        monitor-exit(r17);
        return r5;
    L_0x000f:
        r0 = r17;
        r2 = r0.COMPARATOR_MESSAGES;	 Catch:{ all -> 0x0050 }
        r0 = r19;
        java.util.Collections.sort(r0, r2);	 Catch:{ all -> 0x0050 }
        r17.removeSentAttachments(r18, r19);	 Catch:{ all -> 0x0050 }
        r6 = new java.util.ArrayList;	 Catch:{ all -> 0x0050 }
        r6.<init>();	 Catch:{ all -> 0x0050 }
        r5 = new java.util.ArrayList;	 Catch:{ all -> 0x0050 }
        r5.<init>();	 Catch:{ all -> 0x0050 }
        r15 = ru.ok.android.db.DatabaseExecutor.getInstance();	 Catch:{ all -> 0x0050 }
        r2 = new ru.ok.android.model.cache.ram.MessagesCache$4;	 Catch:{ all -> 0x0050 }
        r3 = r17;
        r4 = r19;
        r7 = r18;
        r2.<init>(r4, r5, r6, r7);	 Catch:{ all -> 0x0050 }
        r15.addOperationSync(r2);	 Catch:{ all -> 0x0050 }
        r10 = r17.getInfo(r18);	 Catch:{ all -> 0x0050 }
        r13 = r10.messageIds;	 Catch:{ all -> 0x0050 }
        r2 = 0;
        r2 = (r20 > r2 ? 1 : (r20 == r2 ? 0 : -1));
        if (r2 != 0) goto L_0x0053;
    L_0x0043:
        r2 = r13.size();	 Catch:{ all -> 0x0050 }
        r0 = r17;
        r0.insertChunk(r5, r13, r2, r6);	 Catch:{ all -> 0x0050 }
    L_0x004c:
        r10.updateMinMaxMessages();	 Catch:{ all -> 0x0050 }
        goto L_0x000d;
    L_0x0050:
        r2 = move-exception;
        monitor-exit(r17);
        throw r2;
    L_0x0053:
        r9 = 0;
    L_0x0054:
        r2 = r13.size();	 Catch:{ all -> 0x0050 }
        if (r9 >= r2) goto L_0x004c;
    L_0x005a:
        r8 = r13.get(r9);	 Catch:{ all -> 0x0050 }
        r8 = (java.lang.Integer) r8;	 Catch:{ all -> 0x0050 }
        r2 = r8.intValue();	 Catch:{ all -> 0x0050 }
        r0 = r17;
        r12 = r0.getMessage(r2);	 Catch:{ all -> 0x0050 }
        if (r12 != 0) goto L_0x007b;
    L_0x006c:
        r2 = "Message not found: %d";
        r3 = 1;
        r3 = new java.lang.Object[r3];	 Catch:{ all -> 0x0050 }
        r4 = 0;
        r3[r4] = r8;	 Catch:{ all -> 0x0050 }
        ru.ok.android.utils.Logger.m185w(r2, r3);	 Catch:{ all -> 0x0050 }
    L_0x0078:
        r9 = r9 + 1;
        goto L_0x0054;
    L_0x007b:
        r2 = r12.date;	 Catch:{ all -> 0x0050 }
        r2 = (r2 > r20 ? 1 : (r2 == r20 ? 0 : -1));
        if (r2 != 0) goto L_0x0078;
    L_0x0081:
        r2 = ru.ok.java.api.request.paging.PagingDirection.FORWARD;	 Catch:{ all -> 0x0050 }
        r0 = r22;
        if (r0 != r2) goto L_0x00b4;
    L_0x0087:
        r11 = r9 + 1;
    L_0x0089:
        r2 = r13.size();	 Catch:{ all -> 0x0050 }
        if (r11 >= r2) goto L_0x00a7;
    L_0x008f:
        r2 = r13.get(r11);	 Catch:{ all -> 0x0050 }
        r2 = (java.lang.Integer) r2;	 Catch:{ all -> 0x0050 }
        r2 = r2.intValue();	 Catch:{ all -> 0x0050 }
        r0 = r17;
        r14 = r0.getMessage(r2);	 Catch:{ all -> 0x0050 }
        if (r14 == 0) goto L_0x00a7;
    L_0x00a1:
        r2 = r14.status;	 Catch:{ all -> 0x0050 }
        r3 = ru.ok.android.proto.MessagesProto.Message.Status.RECEIVED;	 Catch:{ all -> 0x0050 }
        if (r2 != r3) goto L_0x00af;
    L_0x00a7:
        r2 = r9 + 1;
        r0 = r17;
        r0.insertChunk(r5, r13, r2, r6);	 Catch:{ all -> 0x0050 }
        goto L_0x004c;
    L_0x00af:
        r9 = r9 + 1;
        r11 = r11 + 1;
        goto L_0x0089;
    L_0x00b4:
        r0 = r17;
        r0.insertChunk(r5, r13, r9, r6);	 Catch:{ all -> 0x0050 }
        goto L_0x004c;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.model.cache.ram.MessagesCache.addMessages(java.lang.String, java.util.List, long, ru.ok.java.api.request.paging.PagingDirection):java.util.List<ru.ok.android.model.cache.ram.MessageModel>");
    }

    private void insertChunk(List<MessageModel> chunk, List<Integer> messageIds, int startIndex, List<Integer> updated) {
        int inserted = 0;
        for (int i = 0; i < chunk.size(); i++) {
            MessageModel candidate = (MessageModel) chunk.get(i);
            int databaseId = candidate.databaseId;
            if (!updated.contains(Integer.valueOf(databaseId))) {
                messageIds.add(startIndex + inserted, Integer.valueOf(databaseId));
                this.allMessages.put(Integer.valueOf(databaseId), candidate);
                inserted++;
            } else if (((MessageModel) this.allMessages.get(Integer.valueOf(databaseId))) == null) {
                this.allMessages.put(Integer.valueOf(databaseId), candidate);
                messageIds.add(startIndex + inserted, Integer.valueOf(databaseId));
                inserted++;
            } else {
                this.allMessages.put(Integer.valueOf(databaseId), candidate);
                int index = containsMessageDB(messageIds, databaseId);
                if (index < 0) {
                    messageIds.add(startIndex + inserted, Integer.valueOf(databaseId));
                    inserted++;
                } else if (index >= startIndex + inserted) {
                    inserted = (index - startIndex) + 1;
                }
            }
        }
    }

    @Nullable
    public synchronized Pair<String, Long> getMessageWithMaxDate(String conversationId) {
        Pair<String, Long> pair;
        Info info = getInfo(conversationId);
        if (info.maxMessageDate > 0) {
            pair = new Pair(info.maxMessageServerId, Long.valueOf(info.maxMessageDate));
        } else {
            Logger.m184w("Fetch max date without cached messages");
            Cursor cursor = this.db.rawQuery("SELECT server_id, _date FROM messages WHERE status = " + Status.RECEIVED.getNumber() + " AND " + "conversation_id" + " = ? " + "ORDER BY " + "_date" + " DESC LIMIT 1", new String[]{conversationId});
            try {
                if (cursor.moveToFirst()) {
                    String serverId = cursor.getString(0);
                    info.maxMessageDate = cursor.getLong(1);
                    info.maxMessageServerId = serverId;
                    pair = new Pair(info.maxMessageServerId, Long.valueOf(info.maxMessageDate));
                } else {
                    cursor.close();
                    pair = null;
                }
            } finally {
                cursor.close();
            }
        }
        return pair;
    }

    @Nullable
    public synchronized Pair<String, Long> getMessageWithMinDate(String conversationId) {
        Pair<String, Long> pair;
        Info info = getInfo(conversationId);
        if (info.minMessageDate > 0) {
            pair = new Pair(info.minMessageServerId, Long.valueOf(info.minMessageDate));
        } else {
            Logger.m184w("Fetch min date without cached messages");
            Cursor cursor = this.db.rawQuery("SELECT server_id, _date FROM messages WHERE status = " + Status.RECEIVED.getNumber() + " AND " + "conversation_id" + " = ? " + "ORDER BY " + "_date" + " ASC LIMIT 1", new String[]{conversationId});
            try {
                if (cursor.moveToFirst()) {
                    String serverId = cursor.getString(0);
                    info.minMessageDate = cursor.getLong(1);
                    info.minMessageServerId = serverId;
                    pair = new Pair(info.minMessageServerId, Long.valueOf(info.minMessageDate));
                } else {
                    cursor.close();
                    pair = null;
                }
            } finally {
                cursor.close();
            }
        }
        return pair;
    }

    @NonNull
    private Info getInfo(String conversationId) {
        Info info = (Info) this.messages.get(conversationId);
        if (info != null) {
            return info;
        }
        info = new Info(conversationId);
        this.messages.put(conversationId, info);
        return info;
    }

    @Nullable
    public synchronized MessageModel addNewMessage(MessageModel message) {
        MessageModel resultModel;
        Logger.m173d("%s", message);
        Info info = getInfo(message.conversationId);
        AtomicReference<MessageModel> ref = new AtomicReference();
        DatabaseExecutor.getInstance().addOperationSync(new C03605(message, ref));
        resultModel = (MessageModel) ref.get();
        if (resultModel == null) {
            resultModel = null;
        } else {
            int databaseId = resultModel.databaseId;
            info.messageIds.add(Integer.valueOf(databaseId));
            this.allMessages.put(Integer.valueOf(databaseId), resultModel);
            getPendingMessages().add(Integer.valueOf(databaseId));
        }
        return resultModel;
    }

    public synchronized void clear() {
        this.messages.evictAll();
        this.allMessages.clear();
        this.pendingMessages = null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.support.annotation.Nullable
    public synchronized ru.ok.android.model.cache.ram.MessageModel getPendingMessage() {
        /*
        r3 = this;
        monitor-enter(r3);
        r1 = r3.getPendingMessages();	 Catch:{ all -> 0x0020 }
        r2 = r1.isEmpty();	 Catch:{ all -> 0x0020 }
        if (r2 != 0) goto L_0x001e;
    L_0x000b:
        r2 = 0;
        r2 = r1.get(r2);	 Catch:{ all -> 0x0020 }
        r2 = (java.lang.Integer) r2;	 Catch:{ all -> 0x0020 }
        r2 = r2.intValue();	 Catch:{ all -> 0x0020 }
        r0 = r3.getMessage(r2);	 Catch:{ all -> 0x0020 }
        if (r0 == 0) goto L_0x001e;
    L_0x001c:
        monitor-exit(r3);
        return r0;
    L_0x001e:
        r0 = 0;
        goto L_0x001c;
    L_0x0020:
        r2 = move-exception;
        monitor-exit(r3);
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.model.cache.ram.MessagesCache.getPendingMessage():ru.ok.android.model.cache.ram.MessageModel");
    }

    @NonNull
    private synchronized List<Integer> getPendingMessages() {
        if (this.pendingMessages == null) {
            Cursor cursor = this.db.rawQuery(QueryToSend.QUERY, null);
            this.pendingMessages = new ArrayList();
            while (cursor.moveToNext()) {
                try {
                    MessageModel message = getMessage(cursor.getInt(0));
                    if (message == null) {
                        Logger.m185w("Message not found: %d", Integer.valueOf(databaseId));
                    } else {
                        this.pendingMessages.add(Integer.valueOf(message.databaseId));
                    }
                } catch (Throwable th) {
                    cursor.close();
                }
            }
            cursor.close();
            Logger.m173d("%d pending messages loaded", Integer.valueOf(this.pendingMessages.size()));
        } else {
            Iterator<Integer> it = this.pendingMessages.iterator();
            while (it.hasNext()) {
                MessageModel m = getMessage(((Integer) it.next()).intValue());
                if (m == null) {
                    Logger.m185w("Message not found: %d", id);
                    it.remove();
                } else if (!((m.status == Status.WAITING || m.status == Status.SENDING || m.status == Status.FAILED || m.message.hasEditInfo()) && (!m.message.hasEditInfo() || m.statusEdited == Status.WAITING || m.statusEdited == Status.SENDING || m.statusEdited == Status.FAILED))) {
                    it.remove();
                }
            }
        }
        return this.pendingMessages;
    }

    public synchronized void updateStatus(int databaseId, Status status) {
        updateStatus(getMessage(databaseId), status);
    }

    public synchronized void updateStatus(@Nullable MessageModel message, Status status) {
        if (message == null) {
            Logger.m184w("Null message passed");
        } else {
            MessageModel resultMessage = message.toBuilder().setStatus(status).build();
            int databaseId = resultMessage.databaseId;
            this.allMessages.put(Integer.valueOf(databaseId), resultMessage);
            if (status == Status.WAITING && !getPendingMessages().contains(Integer.valueOf(databaseId))) {
                this.pendingMessages.add(Integer.valueOf(databaseId));
            }
            BusMessagingHelper.messageUpdated(message.databaseId);
            DatabaseExecutor.getInstance().addOperation(new C03616(status, databaseId));
        }
    }

    public synchronized void updateEditStatus(@Nullable MessageModel message, Status status) {
        if (message == null) {
            Logger.m184w("Null message passed");
        } else {
            MessageModel resultModel = message.toBuilder().setStatusEdited(status).build();
            int databaseId = resultModel.databaseId;
            if (status == Status.WAITING && !getPendingMessages().contains(Integer.valueOf(databaseId))) {
                this.pendingMessages.add(Integer.valueOf(databaseId));
            }
            this.allMessages.put(Integer.valueOf(databaseId), resultModel);
            BusMessagingHelper.messageUpdated(databaseId);
            DatabaseExecutor.getInstance().addOperation(new C03627(status, databaseId));
        }
    }

    @Nullable
    public synchronized MessageModel getMessage(int databaseId) {
        return getMessageNotSynchronized(databaseId);
    }

    @Nullable
    private MessageModel getMessageNotSynchronized(int databaseId) {
        MessageModel message = (MessageModel) this.allMessages.get(Integer.valueOf(databaseId));
        if (message != null) {
            return message;
        }
        Cursor cursor = this.db.rawQuery("SELECT _id, conversation_id, server_id, _date, _date_editing, status, status_editing, data FROM messages WHERE _id = ?", new String[]{String.valueOf(databaseId)});
        try {
            if (cursor.moveToFirst()) {
                message = messageModelFromCursor(cursor);
                this.allMessages.put(Integer.valueOf(databaseId), message);
                return message;
            }
            cursor.close();
            return null;
        } catch (Throwable e) {
            Logger.m178e(e);
            return null;
        } finally {
            cursor.close();
        }
    }

    @NonNull
    private MessageModel messageModelFromCursor(Cursor cursor) throws InvalidProtocolBufferException {
        try {
            int databaseId = cursor.getInt(0);
            String conversationId = cursor.getString(1);
            return new MessageModel(databaseId, cursor.getString(2), conversationId, cursor.getLong(3), cursor.getLong(4), Status.valueOf(cursor.getInt(5)), Status.valueOf(cursor.getInt(6)), Message.parseFrom(cursor.getBlob(7)));
        } catch (InvalidProtocolBufferException e) {
            StatisticManager.getInstance().addStatisticEvent("protobuf-message-read-fail", new Pair("reason", e.getMessage()));
            throw e;
        }
    }

    @Nullable
    public synchronized Attach getAttachByUUID(int messageId, long attachmentUUID) {
        Attach attach;
        MessageModel message = getMessage(messageId);
        if (message == null) {
            Logger.m185w("Can't find message: %d", Integer.valueOf(messageId));
            attach = null;
        } else {
            for (Attach attach2 : message.message.getAttachesList()) {
                if (attach2.getUuid() == attachmentUUID) {
                    break;
                }
            }
            Logger.m185w("Attach %d not found in message: %d -> %d", Long.valueOf(attachmentUUID), Integer.valueOf(messageId));
            attach2 = null;
        }
        return attach2;
    }

    public synchronized void updateBySentResult(String conversationId, int databaseId, MessageSendResponse result) {
        MessageModel message = getMessage(databaseId);
        if (message == null) {
            Logger.m185w("Can't find message by databaseId: %d", Integer.valueOf(databaseId));
        } else {
            SQLiteStatement countStatement = DBStatementsFactory.getStatement(this.db, CountServerId.QUERY);
            countStatement.bindString(1, result.serverId);
            if (countStatement.simpleQueryForLong() > 0) {
                Logger.m185w("We already has message with same server id: %s", serverId);
                removeMessagesDatabaseIds(conversationId, Arrays.asList(new Integer[]{Integer.valueOf(databaseId)}));
                BusMessagingHelper.messageUpdated(databaseId);
            } else {
                Builder messageBuilder = message.message.toBuilder();
                if (!TextUtils.isEmpty(result.serverText)) {
                    messageBuilder.setText(result.serverText);
                }
                messageBuilder.setFailureReason("");
                MessageModel resultMessage = message.toBuilder().setServerId(result.serverId).setStatus(Status.SENT).setDate(result.date).setMessage(messageBuilder.build()).build();
                this.allMessages.put(Integer.valueOf(resultMessage.databaseId), resultMessage);
                BusMessagingHelper.messageUpdated(databaseId);
                Logger.m173d("Update %d with server id %s", Integer.valueOf(databaseId), result.serverId);
                DatabaseExecutor.getInstance().addOperation(new C03638(resultMessage));
            }
        }
    }

    public synchronized int getPotentialOverdueCount() {
        int count;
        count = 0;
        for (Integer intValue : getPendingMessages()) {
            MessageModel message = getMessage(intValue.intValue());
            if (message == null) {
                Logger.m185w("Message not found: %d", Integer.valueOf(messageId));
            } else if ((!message.message.hasEditInfo() && (message.status == Status.WAITING || message.status == Status.FAILED)) || (message.message.hasEditInfo() && (message.statusEdited == Status.WAITING || message.statusEdited == Status.FAILED))) {
                count++;
            }
        }
        return count;
    }

    @Nullable
    public synchronized List<MessageModel> getOverdueMessages() {
        List<MessageModel> overdueMessages;
        List<Integer> pendingMessages = getPendingMessages();
        long newestMessageDate = 0;
        for (Integer pendingId : pendingMessages) {
            MessageModel pending = getMessage(pendingId.intValue());
            if (pending == null) {
                Logger.m185w("Message not found: %d", pendingId);
            } else if ((pending.status == Status.WAITING || pending.status == Status.FAILED) && !pending.message.hasEditInfo() && pending.date > newestMessageDate) {
                newestMessageDate = pending.date;
            }
        }
        boolean allowOverdue = newestMessageDate > 0 && newestMessageDate < System.currentTimeMillis() - 180000;
        Logger.m173d("Newest message date: %d, allow overdue: %s", Long.valueOf(newestMessageDate), Boolean.valueOf(allowOverdue));
        overdueMessages = null;
        Iterator<Integer> it = pendingMessages.iterator();
        while (it.hasNext()) {
            pending = getMessage(((Integer) it.next()).intValue());
            if (pending == null) {
                Logger.m185w("Message not found: %d", Integer.valueOf(pendingId));
                it.remove();
            } else {
                if (allowOverdue && ((pending.status == Status.WAITING || pending.status == Status.FAILED) && !pending.message.hasEditInfo())) {
                    if (overdueMessages == null) {
                        overdueMessages = new ArrayList();
                    }
                    Logger.m173d("Sending overdue message found: %s", pending);
                    overdueMessages.add(pending);
                    it.remove();
                }
                if (pending.message.hasEditInfo() && ((pending.statusEdited == Status.WAITING || pending.statusEdited == Status.FAILED) && pending.dateEdited < System.currentTimeMillis() - 180000)) {
                    if (overdueMessages == null) {
                        overdueMessages = new ArrayList();
                    }
                    Logger.m173d("Edit overdue message found: %s", pending);
                    overdueMessages.add(pending);
                    it.remove();
                }
            }
        }
        if (overdueMessages != null) {
            for (MessageModel m : overdueMessages) {
                updateStatusAndFailureReason(m.databaseId, Status.OVERDUE, ErrorType.NO_INTERNET_TOO_LONG);
            }
        }
        return overdueMessages;
    }

    private synchronized MessageModel updateMessageBlob(int databaseId, Message message) {
        MessageModel result;
        MessageModel existing = getMessage(databaseId);
        result = null;
        if (existing != null) {
            result = existing.toBuilder().setMessage(message).build();
            this.allMessages.put(Integer.valueOf(databaseId), result);
        }
        DatabaseExecutor.getInstance().addOperation(new C03649(message, databaseId));
        return result;
    }

    public synchronized void updateAttachmentStatusTokenDate(int messageId, String attachmentLocalId, Attach.Status attachmentStatus, String token, long tokenCreationDate) {
        Attach attach = findAttachmentByLocalId(messageId, attachmentLocalId);
        if (attach == null) {
            Logger.m185w("Attach not found: %s", attachmentLocalId);
        } else {
            replaceAttach(messageId, attach.toBuilder().setStatus(attachmentStatus).setPhoto(attach.getPhoto().toBuilder().setRemoteToken(token).setTokenCreationDate(tokenCreationDate)).build());
        }
    }

    private Message replaceAttach(int messageId, Attach attach) {
        MessageModel message = getMessage(messageId);
        if (message == null) {
            Logger.m185w("Message not found: %d", Integer.valueOf(messageId));
        } else {
            for (int i = 0; i < message.message.getAttachesCount(); i++) {
                if (attach.getUuid() == message.message.getAttaches(i).getUuid()) {
                    Logger.m173d("Attachment found: %s", attach);
                    updateMessageBlob(messageId, message.message.toBuilder().setAttaches(i, attach).build());
                }
            }
        }
        return null;
    }

    @Nullable
    public synchronized Attach findAttachmentByLocalId(int messageId, String attachmentLocalId) {
        Attach attach;
        MessageModel message = getMessage(messageId);
        if (message == null) {
            Logger.m185w("Message not found: %d", Integer.valueOf(messageId));
            attach = null;
        } else {
            for (Attach attach2 : message.message.getAttachesList()) {
                if (attach2.getType() == Type.PHOTO && TextUtils.equals(attach2.getPhoto().getLocalId(), attachmentLocalId)) {
                    Logger.m173d("Attachment found: %s", attach2);
                    break;
                }
            }
            attach2 = null;
        }
        return attach2;
    }

    public synchronized void updateAttachmentStatus(int messageId, long attachmentId, Attach.Status newStatus) {
        MessageModel message = getMessage(messageId);
        if (message != null) {
            for (Attach attach : message.message.getAttachesList()) {
                if (attach.getUuid() == attachmentId) {
                    replaceAttach(messageId, attach.toBuilder().setStatus(newStatus).build());
                    break;
                }
            }
        }
        Logger.m185w("Message not found: %d", Integer.valueOf(messageId));
    }

    public synchronized void updateAttachMediaServerId(int messageId, long attachDatabaseId, long videoId) {
        Attach attach = getAttachByUUID(messageId, attachDatabaseId);
        if (attach != null) {
            if (attach.hasVideo()) {
                replaceAttach(messageId, attach.toBuilder().setVideo(attach.getVideo().toBuilder().setServerId(videoId).build()).build());
            } else if (attach.hasAudio()) {
                replaceAttach(messageId, attach.toBuilder().setAudio(attach.getAudio().toBuilder().setServerId(videoId).build()).build());
            }
        }
    }

    public synchronized void editMessage(int databaseId, String newText, long editDate) {
        boolean trueEdit = true;
        synchronized (this) {
            MessageModel message = getMessage(databaseId);
            if (message == null) {
                Logger.m185w("Message not found: %d", Integer.valueOf(databaseId));
            } else if (TextUtils.isEmpty(newText)) {
                Logger.m185w("Empty text passed for message: %d", message);
            } else {
                MessageModel resultMessage;
                MessageModel.Builder mb = message.toBuilder();
                if (message.status == Status.FAILED || message.status == Status.SERVER_ERROR || message.status == Status.WAITING) {
                    trueEdit = false;
                }
                if (trueEdit) {
                    resultMessage = mb.setStatusEdited(Status.WAITING).setDateEdited(editDate).setMessage(message.message.toBuilder().setEditInfo(EditInfo.newBuilder().setNewText(newText)).build()).build();
                } else {
                    resultMessage = mb.setStatus(Status.WAITING).setDate(editDate).setMessage(message.message.toBuilder().setText(newText).build()).build();
                }
                this.allMessages.put(Integer.valueOf(databaseId), resultMessage);
                BusMessagingHelper.messageUpdated(databaseId);
                if (!getPendingMessages().contains(Integer.valueOf(databaseId))) {
                    this.pendingMessages.add(Integer.valueOf(databaseId));
                }
                DatabaseExecutor.getInstance().addOperation(new AnonymousClass10(resultMessage, trueEdit, message));
            }
        }
    }

    public synchronized long getMinLastUpdateTime(String conversationId, long startDate, long endDate) {
        long j;
        List<Integer> conversationMessages = getInfo(conversationId).messageIds;
        if (!conversationMessages.isEmpty()) {
            MessageModel firstMessage = getMessage(((Integer) conversationMessages.get(0)).intValue());
            MessageModel lastMessage = getMessage(((Integer) conversationMessages.get(conversationMessages.size() - 1)).intValue());
            if (firstMessage == null || lastMessage == null) {
                Logger.m185w("One of message not found: %d, %d", (Object[]) new Object[]{Integer.valueOf(firstId), Integer.valueOf(lastId)});
                j = 0;
            } else {
                if (startDate >= firstMessage.date) {
                    if (endDate <= lastMessage.date) {
                        j = Long.MAX_VALUE;
                        for (int i = 0; i < conversationMessages.size(); i++) {
                            MessageModel message = getMessage(((Integer) conversationMessages.get(i)).intValue());
                            if (message.date >= startDate) {
                                if (message.date > endDate) {
                                    break;
                                }
                                long updateTime = message.message.getUpdateTime();
                                if (updateTime < j) {
                                    j = updateTime;
                                }
                            }
                        }
                    }
                }
            }
        }
        Logger.m185w("Messages not cached: [%s] %d - %d", (Object[]) new Object[]{conversationId, Long.valueOf(startDate), Long.valueOf(endDate)});
        j = 0;
        return j;
    }

    public synchronized void updateMessages(@NonNull String conversationId, @NonNull List<MessageModel> messages) {
        if (!messages.isEmpty()) {
            MessageModel m;
            Info info = getInfo(conversationId);
            List<MessageModel> messages2Update = new ArrayList();
            Iterator<MessageModel> it = messages.iterator();
            while (it.hasNext()) {
                MessageModel updated = (MessageModel) it.next();
                for (Integer intValue : info.messageIds) {
                    m = getMessage(intValue.intValue());
                    if (TextUtils.equals(m.serverId, updated.serverId)) {
                        BusMessagingHelper.messageUpdated(m.databaseId);
                        messages2Update.add(mergeUpdated(m, updated));
                        it.remove();
                        break;
                    }
                }
            }
            for (MessageModel m2 : messages) {
                MessageModel message = getMessageByServerIdDB(m2.serverId);
                if (message != null) {
                    messages2Update.add(mergeUpdated(message, m2));
                }
            }
            DatabaseExecutor.getInstance().addOperation(new AnonymousClass11(messages2Update));
        }
    }

    private MessageModel mergeUpdated(MessageModel currentMessage, MessageModel updated) {
        Builder builder = currentMessage.message.toBuilder();
        if (updated.message.hasEditInfo()) {
            builder.setEditInfo(updated.message.getEditInfo());
        }
        builder.clearAttaches();
        builder.addAllAttaches(updated.message.getAttachesList());
        MessageModel result = currentMessage.toBuilder().setDate(updated.date).setStatus(updated.status).setMessage(builder.setText(updated.message.getText() != null ? updated.message.getText() : "").setCapabilities(updated.message.getCapabilities()).build()).build();
        this.allMessages.put(Integer.valueOf(result.databaseId), result);
        return result;
    }

    public synchronized void removeMessages(@NonNull String conversationId, @NonNull ArrayList<String> serverIds) {
        if (!serverIds.isEmpty()) {
            Iterator<Integer> it = getInfo(conversationId).messageIds.iterator();
            while (it.hasNext()) {
                MessageModel m = getMessage(((Integer) it.next()).intValue());
                if (serverIds.contains(m.serverId)) {
                    this.allMessages.remove(Integer.valueOf(m.databaseId));
                    it.remove();
                }
            }
            DatabaseExecutor.getInstance().addOperation(new AnonymousClass12(serverIds));
        }
    }

    public synchronized void removeMessagesDatabaseIds(@NonNull String conversationId, @NonNull Collection<Integer> databaseIds) {
        if (!databaseIds.isEmpty()) {
            Iterator<Integer> it = getInfo(conversationId).messageIds.iterator();
            while (it.hasNext()) {
                MessageModel m = getMessage(((Integer) it.next()).intValue());
                if (databaseIds.contains(Integer.valueOf(m.databaseId))) {
                    cleanupUploads(m.message);
                    it.remove();
                }
            }
            for (Integer databaseId : databaseIds) {
                this.allMessages.remove(databaseId);
                if (this.pendingMessages != null) {
                    this.pendingMessages.remove(databaseId);
                }
            }
            DatabaseExecutor.getInstance().addOperation(new AnonymousClass13(databaseIds));
        }
    }

    public synchronized void updateLastUpdateTime(String conversationId, long startDate, long endDate, long updateTime) {
        List<Integer> conversationMessageIds = getInfo(conversationId).messageIds;
        if (!conversationMessageIds.isEmpty()) {
            List<MessageModel> updated = new ArrayList();
            MessageModel firstMessage = getMessage(((Integer) conversationMessageIds.get(0)).intValue());
            MessageModel lastMessage = getMessage(((Integer) conversationMessageIds.get(conversationMessageIds.size() - 1)).intValue());
            if (startDate >= firstMessage.date && endDate <= lastMessage.date) {
                for (int i = 0; i < conversationMessageIds.size(); i++) {
                    MessageModel message = getMessage(((Integer) conversationMessageIds.get(i)).intValue());
                    if (message.date >= startDate) {
                        if (message.date > endDate) {
                            break;
                        }
                        MessageModel updatedMessage = message.toBuilder().setMessage(message.message.toBuilder().setUpdateTime(updateTime).build()).build();
                        this.allMessages.put(Integer.valueOf(updatedMessage.databaseId), updatedMessage);
                        updated.add(updatedMessage);
                    }
                }
            }
            DatabaseExecutor.getInstance().addOperation(new AnonymousClass14(updated));
        }
    }

    public synchronized long getMessageTime(@NonNull String conversationId, @NonNull String serverId) {
        long j;
        for (Integer intValue : getInfo(conversationId).messageIds) {
            MessageModel m = getMessage(intValue.intValue());
            if (TextUtils.equals(serverId, m.serverId)) {
                j = m.date;
                break;
            }
        }
        j = "SELECT _date FROM messages WHERE server_id = ?";
        Cursor cursor = this.db.rawQuery(j, new String[]{serverId});
        try {
            if (cursor.moveToFirst()) {
                j = cursor.getLong(0);
            } else {
                cursor.close();
                j = 0;
            }
        } finally {
            cursor.close();
        }
        return j;
    }

    public synchronized void undoMessageEdit(int databaseId) {
        MessageModel message = getMessage(databaseId);
        if (message != null) {
            updateMessageBlob(databaseId, message.message.toBuilder().clearEditInfo().clearFailureReason().build());
            BusMessagingHelper.messageUpdated(databaseId);
        }
    }

    public synchronized void updateStatusAndFailureReason(int databaseId, Status status, ErrorType reason) {
        MessageModel message = getMessage(databaseId);
        if (message == null) {
            Logger.m185w("Message not found: %d", Integer.valueOf(databaseId));
        } else {
            message = updateMessageBlob(databaseId, message.message.toBuilder().setFailureReason(reason != null ? reason.name() : "").build());
            if (message.message.hasEditInfo()) {
                updateEditStatus(message, status);
            } else {
                updateStatus(message, status);
            }
        }
    }

    public synchronized void updateStatusAndDate(int databaseId, Status status, long date) {
        MessageModel message = getMessage(databaseId);
        if (message == null) {
            Logger.m185w("Message not found: %d", Integer.valueOf(databaseId));
        } else {
            this.allMessages.put(Integer.valueOf(databaseId), message.toBuilder().setStatus(status).setDate(date).build());
            if (status == Status.WAITING && !getPendingMessages().contains(Integer.valueOf(databaseId))) {
                this.pendingMessages.add(Integer.valueOf(databaseId));
            }
            BusMessagingHelper.messageUpdated(message.databaseId);
            DatabaseExecutor.getInstance().addOperation(new AnonymousClass15(status, date, databaseId));
        }
    }

    private void removeSentAttachments(String conversationId, List<MessageModel> messages) {
        for (MessageModel messageModel : messages) {
            if (messageModel.message.getAttachesCount() > 0) {
                MessageModel existing = getMessageByServerId(conversationId, messageModel.serverId);
                if (existing != null) {
                    for (Attach attachment : existing.message.getAttachesList()) {
                        Type type = attachment.getType();
                        if (type == Type.AUDIO_RECORDING || type == Type.VIDEO || type == Type.MOVIE) {
                            if (attachment.hasAudio()) {
                                if (!TextUtils.isEmpty(attachment.getAudio().getPath())) {
                                    deleteLocalAttachmentFile(attachment.getAudio().getPath());
                                }
                            } else if (attachment.hasVideo() && !TextUtils.isEmpty(attachment.getVideo().getPath())) {
                                deleteLocalAttachmentFile(attachment.getVideo().getPath());
                            }
                        }
                    }
                }
            }
        }
    }

    public synchronized MessageModel getMessageByServerId(@NonNull String conversationId, @NonNull String serverId) {
        MessageModel message;
        for (Integer intValue : getInfo(conversationId).messageIds) {
            message = getMessage(intValue.intValue());
            if (TextUtils.equals(message.serverId, serverId)) {
                break;
            }
        }
        message = getMessageByServerIdDB(serverId);
        return message;
    }

    public static void deleteLocalAttachmentFile(String attachLocalFile) {
        ThreadUtil.execute(new AnonymousClass16(attachLocalFile));
    }

    @Nullable
    private MessageModel getMessageByServerIdDB(String serverId) {
        Cursor cursor = this.db.rawQuery("SELECT _id, conversation_id, server_id, _date, _date_editing, status, status_editing data FROM messages WHERE server_id = ?", new String[]{serverId});
        try {
            if (cursor.moveToFirst()) {
                MessageModel messageModelFromCursor = messageModelFromCursor(cursor);
                cursor.close();
                return messageModelFromCursor;
            }
        } catch (Throwable e) {
            Logger.m178e(e);
        } catch (Throwable th) {
            cursor.close();
        }
        cursor.close();
        return null;
    }

    public void removeConversationMessages(String conversationId) {
        cleanupConversationUploads(conversationId);
        this.messages.remove(conversationId);
    }

    public boolean processAttachmentsMessage(Context context, int databaseId) {
        MessageModel message = getMessage(databaseId);
        if (message == null) {
            Logger.m173d("Message not found: %d", Integer.valueOf(databaseId));
            return true;
        } else if (message.message.getAttachesCount() <= 0) {
            return true;
        } else {
            SendAttachmentsTask sendAttachmentsTask = null;
            ArrayList toUploadPhoto = null;
            boolean allUploadsCompleted = true;
            boolean doCancelTask = false;
            for (Attach attachment : message.message.getAttachesList()) {
                Attach attachment2;
                Type type = attachment2.getType();
                if (type == Type.PHOTO && isTokenExpired(attachment2)) {
                    attachment2 = attachment2.toBuilder().setPhoto(attachment2.getPhoto().toBuilder().clearTokenCreationDate().clearRemoteToken()).setStatus(Attach.Status.WAITING).build();
                    replaceAttach(databaseId, attachment2);
                }
                Attach.Status status = attachment2.getStatus();
                Logger.m173d("attachment.status=%d", status);
                allUploadsCompleted &= status == Attach.Status.UPLOADED ? 1 : 0;
                if (status == Attach.Status.RETRY) {
                    Logger.m173d("Re-trying to upload attachment: %s", attachment2);
                    doCancelTask = true;
                    attachment2 = attachment2.toBuilder().setStatus(Attach.Status.WAITING).build();
                    replaceAttach(databaseId, attachment2);
                }
                if (attachment2.getStatus() == Attach.Status.WAITING) {
                    if (type == Type.VIDEO || type == Type.MOVIE) {
                        sendAttachmentsTask = new VideoSendAttachmentsTask(OdnoklassnikiApplication.getCurrentUser().uid, message.databaseId, message.conversationId, attachment2.getUuid());
                        break;
                    } else if (type == Type.AUDIO_RECORDING) {
                        sendAttachmentsTask = new AudioSendAttachmentTask(OdnoklassnikiApplication.getCurrentUser().uid, message.databaseId, message.conversationId, attachment2.getUuid());
                        break;
                    } else if (attachment2.getType() == Type.PHOTO) {
                        if (toUploadPhoto == null) {
                            toUploadPhoto = new ArrayList();
                        }
                        toUploadPhoto.add(attachment2);
                    }
                }
            }
            if (doCancelTask) {
                int taskId = message.message.getTaskId();
                if (taskId > 0) {
                    Logger.m173d("Cancelling task for message: taskId=%d message=%s", Integer.valueOf(taskId), message);
                    context.startService(PersistentTaskService.createCancelTaskIntent(context, taskId));
                }
            }
            if (!(toUploadPhoto == null || toUploadPhoto.isEmpty())) {
                int size = toUploadPhoto.size();
                long[] ids = new long[size];
                for (int i = 0; i < size; i++) {
                    ids[i] = ((Attach) toUploadPhoto.get(i)).getUuid();
                }
                sendAttachmentsTask = new PhotoSendAttachmentsTask(OdnoklassnikiApplication.getCurrentUser().uid, message.databaseId, null, ids);
            }
            if (sendAttachmentsTask != null) {
                Logger.m173d("starting task for attachment: %s", sendAttachmentsTask);
                startUploadForMessage(context, databaseId, sendAttachmentsTask);
                return false;
            }
            Logger.m173d("<<< allUploadsCompleted=%s", Boolean.valueOf(allUploadsCompleted));
            return allUploadsCompleted;
        }
    }

    private static boolean isTokenExpired(Attach attachment) {
        long deadline = System.currentTimeMillis() - 86400000;
        long token = attachment.getPhoto().getTokenCreationDate();
        return token > 0 && token < deadline;
    }

    private void startUploadForMessage(Context context, int databaseId, SendAttachmentsTask task) {
        Logger.m173d("message=%d task=%s", Integer.valueOf(databaseId), task);
        getInstance().updateStatus(databaseId, Status.WAITING_ATTACHMENT);
        PersistentTaskService.submit(context, task, new AnonymousClass17(null, databaseId));
    }

    public synchronized void prefetch(String conversationId, int count) {
        Logger.m173d("%s - %d", conversationId, Integer.valueOf(count));
        if (getInfo(conversationId).messageIds.isEmpty()) {
            getMessagesBefore(conversationId, 0, count);
        }
    }

    public synchronized void removeAttachments(int databaseId, @Nullable List<Long> withErrors, @Nullable List<Long> retryAttachments) {
        MessageModel message = getMessage(databaseId);
        if (message == null) {
            Logger.m185w("Message not found: %d", Integer.valueOf(databaseId));
        } else {
            Builder blobBuilder = message.message.toBuilder();
            int i = 0;
            while (i < blobBuilder.getAttachesCount()) {
                Attach attach = blobBuilder.getAttaches(i);
                long uuid = attach.getUuid();
                if (withErrors != null && withErrors.contains(Long.valueOf(uuid))) {
                    blobBuilder.removeAttaches(i);
                    i--;
                } else if (retryAttachments != null && retryAttachments.contains(Long.valueOf(uuid))) {
                    blobBuilder.setAttaches(i, attach.toBuilder().setStatus(Attach.Status.RETRY));
                }
                i++;
            }
            updateMessageBlob(databaseId, blobBuilder.build());
        }
    }
}
