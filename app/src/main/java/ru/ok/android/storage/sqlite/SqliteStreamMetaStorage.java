package ru.ok.android.storage.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import ru.ok.android.db.access.DBStatementsFactory;
import ru.ok.android.storage.IStreamMetaStorage;
import ru.ok.android.storage.StorageException;
import ru.ok.android.ui.stream.data.StreamContext;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.StreamPageKey;

public class SqliteStreamMetaStorage implements IStreamMetaStorage {
    private final String currentUserId;
    private final SqliteStreamMetaDBOpenHelper dbHelper;

    public SqliteStreamMetaStorage(Context context, String currentUserId) {
        this.dbHelper = SqliteStreamMetaDBOpenHelper.getInstance(context);
        this.currentUserId = currentUserId;
    }

    public void put(StreamContext context, StreamPageKey key, long ts) throws StorageException {
        try {
            SQLiteStatement insert = DBStatementsFactory.getStatement(this.dbHelper.getWritableDatabase(), "INSERT INTO stream_meta(cuid,type,ctx_id,page_key,ts) VALUES (?,?,?,?,?)");
            insert.bindString(1, this.currentUserId);
            insert.bindLong(2, (long) context.type);
            if (context.id == null) {
                insert.bindNull(3);
            } else {
                insert.bindString(3, context.id);
            }
            insert.bindString(4, key.getKey());
            insert.bindLong(5, ts);
            if (insert.executeInsert() < 0) {
                throw new StorageException("Inserting stream meta failed");
            }
        } catch (Exception e) {
            throw new StorageException("Failed to insert stream meta: " + e, e);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.support.annotation.Nullable
    public java.util.HashMap<ru.ok.android.ui.stream.data.StreamContext, java.util.ArrayList<ru.ok.model.stream.StreamPageKey>> getOlder(long r26) throws ru.ok.android.storage.StorageException {
        /*
        r25 = this;
        r16 = java.lang.System.currentTimeMillis();
        r5 = 0;
        r12 = 0;
        r6 = 0;
        r0 = r25;
        r0 = r0.dbHelper;	 Catch:{ Exception -> 0x00f4 }
        r18 = r0;
        r7 = r18.getWritableDatabase();	 Catch:{ Exception -> 0x00f4 }
        r18 = "SELECT type,ctx_id,page_key,page_number FROM stream_meta WHERE cuid=? AND ts <= ?";
        r19 = 2;
        r0 = r19;
        r0 = new java.lang.String[r0];	 Catch:{ Exception -> 0x00f4 }
        r19 = r0;
        r20 = 0;
        r0 = r25;
        r0 = r0.currentUserId;	 Catch:{ Exception -> 0x00f4 }
        r21 = r0;
        r19[r20] = r21;	 Catch:{ Exception -> 0x00f4 }
        r20 = 1;
        r21 = java.lang.Long.toString(r26);	 Catch:{ Exception -> 0x00f4 }
        r19[r20] = r21;	 Catch:{ Exception -> 0x00f4 }
        r0 = r18;
        r1 = r19;
        r6 = r7.rawQuery(r0, r1);	 Catch:{ Exception -> 0x00f4 }
        if (r6 == 0) goto L_0x00c4;
    L_0x0038:
        r18 = r6.moveToFirst();	 Catch:{ Exception -> 0x00f4 }
        if (r18 == 0) goto L_0x00c4;
    L_0x003e:
        r13 = r12;
    L_0x003f:
        r18 = r6.isAfterLast();	 Catch:{ Exception -> 0x011d, all -> 0x011a }
        if (r18 != 0) goto L_0x00c3;
    L_0x0045:
        r18 = 0;
        r0 = r18;
        r15 = r6.getInt(r0);	 Catch:{ Exception -> 0x011d, all -> 0x011a }
        r18 = 1;
        r0 = r18;
        r3 = r6.getString(r0);	 Catch:{ Exception -> 0x011d, all -> 0x011a }
        r18 = 2;
        r0 = r18;
        r10 = r6.getString(r0);	 Catch:{ Exception -> 0x011d, all -> 0x011a }
        r18 = 3;
        r0 = r18;
        r14 = r6.getInt(r0);	 Catch:{ Exception -> 0x011d, all -> 0x011a }
        r18 = ru.ok.android.ui.stream.data.StreamContext.isValidType(r15);	 Catch:{ Exception -> 0x011d, all -> 0x011a }
        if (r18 != 0) goto L_0x0087;
    L_0x006b:
        r18 = "Invalid stream context type: %d";
        r19 = 1;
        r0 = r19;
        r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x011d, all -> 0x011a }
        r19 = r0;
        r20 = 0;
        r21 = java.lang.Integer.valueOf(r15);	 Catch:{ Exception -> 0x011d, all -> 0x011a }
        r19[r20] = r21;	 Catch:{ Exception -> 0x011d, all -> 0x011a }
        ru.ok.android.utils.Logger.m185w(r18, r19);	 Catch:{ Exception -> 0x011d, all -> 0x011a }
        r12 = r13;
    L_0x0082:
        r6.moveToNext();	 Catch:{ Exception -> 0x00f4 }
        r13 = r12;
        goto L_0x003f;
    L_0x0087:
        r4 = r15;
        r9 = ru.ok.model.stream.StreamPageKey.fromKeyAndPageNumber(r10, r14);	 Catch:{ Exception -> 0x00ae, all -> 0x011a }
        r2 = new ru.ok.android.ui.stream.data.StreamContext;	 Catch:{ Exception -> 0x011d, all -> 0x011a }
        r2.<init>(r4, r3);	 Catch:{ Exception -> 0x011d, all -> 0x011a }
        if (r13 != 0) goto L_0x0120;
    L_0x0093:
        r12 = new java.util.HashMap;	 Catch:{ Exception -> 0x011d, all -> 0x011a }
        r12.<init>();	 Catch:{ Exception -> 0x011d, all -> 0x011a }
    L_0x0098:
        r11 = r12.get(r2);	 Catch:{ Exception -> 0x00f4 }
        r11 = (java.util.ArrayList) r11;	 Catch:{ Exception -> 0x00f4 }
        if (r11 != 0) goto L_0x00a8;
    L_0x00a0:
        r11 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00f4 }
        r11.<init>();	 Catch:{ Exception -> 0x00f4 }
        r12.put(r2, r11);	 Catch:{ Exception -> 0x00f4 }
    L_0x00a8:
        r11.add(r9);	 Catch:{ Exception -> 0x00f4 }
        r5 = r5 + 1;
        goto L_0x0082;
    L_0x00ae:
        r8 = move-exception;
        r18 = "Failed to parse stream page key: %s,";
        r19 = 1;
        r0 = r19;
        r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x011d, all -> 0x011a }
        r19 = r0;
        r20 = 0;
        r19[r20] = r8;	 Catch:{ Exception -> 0x011d, all -> 0x011a }
        ru.ok.android.utils.Logger.m185w(r18, r19);	 Catch:{ Exception -> 0x011d, all -> 0x011a }
        r12 = r13;
        goto L_0x0082;
    L_0x00c3:
        r12 = r13;
    L_0x00c4:
        ru.ok.android.utils.IOUtils.closeSilently(r6);
        r18 = "getOlder: ts=%d, queried %d entries in %d ms";
        r19 = 3;
        r0 = r19;
        r0 = new java.lang.Object[r0];
        r19 = r0;
        r20 = 0;
        r21 = java.lang.Long.valueOf(r26);
        r19[r20] = r21;
        r20 = 1;
        r21 = java.lang.Integer.valueOf(r5);
        r19[r20] = r21;
        r20 = 2;
        r22 = java.lang.System.currentTimeMillis();
        r22 = r22 - r16;
        r21 = java.lang.Long.valueOf(r22);
        r19[r20] = r21;
        ru.ok.android.utils.Logger.m173d(r18, r19);
        return r12;
    L_0x00f4:
        r8 = move-exception;
    L_0x00f5:
        r18 = new ru.ok.android.storage.StorageException;	 Catch:{ all -> 0x0115 }
        r19 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0115 }
        r19.<init>();	 Catch:{ all -> 0x0115 }
        r20 = "Failed to query: ";
        r19 = r19.append(r20);	 Catch:{ all -> 0x0115 }
        r0 = r19;
        r19 = r0.append(r8);	 Catch:{ all -> 0x0115 }
        r19 = r19.toString();	 Catch:{ all -> 0x0115 }
        r0 = r18;
        r1 = r19;
        r0.<init>(r1, r8);	 Catch:{ all -> 0x0115 }
        throw r18;	 Catch:{ all -> 0x0115 }
    L_0x0115:
        r18 = move-exception;
    L_0x0116:
        ru.ok.android.utils.IOUtils.closeSilently(r6);
        throw r18;
    L_0x011a:
        r18 = move-exception;
        r12 = r13;
        goto L_0x0116;
    L_0x011d:
        r8 = move-exception;
        r12 = r13;
        goto L_0x00f5;
    L_0x0120:
        r12 = r13;
        goto L_0x0098;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.storage.sqlite.SqliteStreamMetaStorage.getOlder(long):java.util.HashMap<ru.ok.android.ui.stream.data.StreamContext, java.util.ArrayList<ru.ok.model.stream.StreamPageKey>>");
    }

    public void remove(HashMap<StreamContext, ArrayList<StreamPageKey>> keys) throws StorageException {
        long startTime = System.currentTimeMillis();
        int count = 0;
        try {
            SQLiteDatabase db = this.dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                for (Entry<StreamContext, ArrayList<StreamPageKey>> entry : keys.entrySet()) {
                    count += removeInTransaction(db, (StreamContext) entry.getKey(), (ArrayList) entry.getValue());
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                Logger.m173d("remove: deleted %d rows in %d ms", Integer.valueOf(count), Long.valueOf(System.currentTimeMillis() - startTime));
            } catch (Exception e) {
                throw new StorageException("Failed to delete: " + e, e);
            } catch (Throwable th) {
                db.endTransaction();
            }
        } catch (Exception e2) {
            throw new StorageException("Failed to open database: " + e2, e2);
        }
    }

    public void remove(StreamContext context, StreamPageKey key) throws StorageException {
        long startTime = System.currentTimeMillis();
        Logger.m173d("remove >>> context=%s key=%s", context, key);
        try {
            SQLiteDatabase db = this.dbHelper.getWritableDatabase();
            ArrayList<StreamPageKey> keys = new ArrayList(1);
            keys.add(key);
            removeInTransaction(db, context, keys);
            Logger.m173d("remove <<< %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            throw new StorageException("Failed to delete: " + e, e);
        }
    }

    private int removeInTransaction(SQLiteDatabase db, StreamContext context, ArrayList<StreamPageKey> keys) {
        SQLiteStatement delete;
        StringBuilder keysList = new StringBuilder();
        for (int i = keys.size() - 1; i >= 0; i--) {
            keysList.append('\'');
            keysList.append(((StreamPageKey) keys.get(i)).getKey());
            keysList.append('\'');
            if (i > 0) {
                keysList.append(',');
            }
        }
        if (context.id == null) {
            delete = DBStatementsFactory.getStatement(db, "DELETE FROM stream_meta WHERE cuid=? AND type=? AND ctx_id IS NULL AND page_key IN (?)");
            delete.bindString(1, this.currentUserId);
            delete.bindLong(2, (long) context.type);
            delete.bindString(3, keysList.toString());
        } else {
            delete = DBStatementsFactory.getStatement(db, "DELETE FROM stream_meta WHERE cuid=? AND type=? AND ctx_id=? AND page_key IN (?)");
            delete.bindString(1, this.currentUserId);
            delete.bindLong(2, (long) context.type);
            delete.bindString(3, context.id);
            delete.bindString(4, keysList.toString());
        }
        return delete.executeUpdateDelete();
    }
}
