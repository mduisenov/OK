package ru.ok.android.services.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import ru.ok.android.storage.StorageException;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.model.local.LocalModifs;

public class LocalModifsSqliteStorage<TLocal extends LocalModifs> implements ILocalModifsStorage<TLocal> {
    private final String currentUserId;
    private final LocalModifsDBHelper dbHelper;
    private final AtomicReference<SQLiteStatement> insertStatement;
    private final String[] selectionCuid;
    private final LocalModifsSerializer<TLocal> serializer;

    protected LocalModifsSqliteStorage(Context context, String currentUserId, String name, LocalModifsSerializer<TLocal> serializer) {
        this.insertStatement = new AtomicReference();
        this.dbHelper = LocalModifsDBHelper.getInstance(context, name + ".sqlite");
        this.serializer = serializer;
        this.currentUserId = currentUserId;
        this.selectionCuid = new String[]{currentUserId};
    }

    @NonNull
    public ArrayList<TLocal> getById(@NonNull ArrayList<String> ids) throws StorageException {
        return query(createQuery((ArrayList) ids), ids.size());
    }

    @NonNull
    public ArrayList<TLocal> getByStatus(int... status) throws StorageException {
        return query(createQuery(status), 0);
    }

    private ArrayList<TLocal> query(String query, int expectedSize) throws StorageException {
        ArrayList<TLocal> items;
        long startTime = System.currentTimeMillis();
        if (expectedSize > 0) {
            items = new ArrayList(expectedSize);
        } else {
            items = new ArrayList();
        }
        Cursor cursor = null;
        try {
            cursor = this.dbHelper.getWritableDatabase().rawQuery(query, this.selectionCuid);
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    items.add(fromCursor(cursor));
                    cursor.moveToNext();
                }
            }
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                }
            }
            Logger.m173d("getByStatus: read %d rows in %d ms", Integer.valueOf(items.size()), Long.valueOf(System.currentTimeMillis() - startTime));
            return items;
        } catch (Exception e2) {
            throw new StorageException("Failed to query likes: " + e2, e2);
        } catch (Throwable th) {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e3) {
                }
            }
            Logger.m173d("getByStatus: read %d rows in %d ms", Integer.valueOf(items.size()), Long.valueOf(System.currentTimeMillis() - startTime));
        }
    }

    private String createQuery(ArrayList<String> likeIds) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append("id,sync_status,attempts,synced_ts,data").append(" FROM ").append("local_modifs").append(" WHERE ").append("cuid").append("=? AND ").append("id").append(" IN (");
        int size = likeIds.size();
        for (int i = 0; i < size; i++) {
            sb.append('\'').append((String) likeIds.get(i)).append('\'');
            if (i + 1 < size) {
                sb.append(", ");
            }
        }
        sb.append(')');
        return sb.toString();
    }

    private String createQuery(int... status) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append("id,sync_status,attempts,synced_ts,data").append(" FROM ").append("local_modifs");
        int size = status.length;
        if (size > 0) {
            sb.append(" WHERE ");
            sb.append("cuid").append("=? AND ");
            sb.append("sync_status");
            if (size == 1) {
                sb.append('=').append(status[0]);
            } else {
                sb.append(" IN (");
                for (int i = 0; i < size; i++) {
                    if (i > 0) {
                        sb.append(',');
                    }
                    sb.append(status[i]);
                }
                sb.append(')');
            }
        }
        return sb.toString();
    }

    private TLocal fromCursor(Cursor cursor) {
        return this.serializer.createItem(cursor.getString(0), cursor.getInt(1), cursor.getInt(2), cursor.getLong(3), cursor.getBlob(4));
    }

    public void update(TLocal item) throws StorageException {
        try {
            SQLiteDatabase db = this.dbHelper.getWritableDatabase();
            SQLiteStatement insert = (SQLiteStatement) this.insertStatement.getAndSet(null);
            if (insert == null) {
                insert = createInsertStatement(db);
            }
            insert.bindString(1, this.currentUserId);
            insert.bindString(2, item.id);
            insert.bindLong(3, (long) item.syncStatus);
            insert.bindLong(4, (long) item.failedAttemptsCount);
            if (item.syncedTs > 0) {
                insert.bindLong(5, item.syncedTs);
            } else {
                insert.bindNull(5);
            }
            byte[] data = this.serializer.getCustomDataBytes(item);
            if (data == null) {
                insert.bindNull(6);
            } else {
                insert.bindBlob(6, data);
            }
            long rowId = insert.executeInsert();
            if (rowId < 0) {
                throw new StorageException("Inserting like failed, rowId=" + rowId);
            }
        } catch (Exception e) {
            throw new StorageException("Failed to insert like: " + e, e);
        }
    }

    public int getSize() throws StorageException {
        int i = 0;
        try {
            Cursor cursor = this.dbHelper.getWritableDatabase().rawQuery("SELECT COUNT(*) FROM local_modifs WHERE cuid=?", this.selectionCuid);
            if (cursor == null || !cursor.moveToFirst()) {
                IOUtils.closeSilently(cursor);
            } else {
                i = cursor.getInt(0);
                IOUtils.closeSilently(cursor);
            }
            return i;
        } catch (Exception e) {
            throw new StorageException("Failed to get size: " + e, e);
        } catch (Throwable th) {
            IOUtils.closeSilently(null);
        }
    }

    public int deleteOlder(long syncedTs) throws StorageException {
        long startTime = System.currentTimeMillis();
        Logger.m173d("deleteOlder >>> syncedTs=%d", Long.valueOf(syncedTs));
        try {
            Logger.m173d("deleteOlder <<< deleted %d rows in %d ms", Integer.valueOf(this.dbHelper.getWritableDatabase().delete("local_modifs", "cuid=? AND synced_ts<=" + syncedTs, this.selectionCuid)), Long.valueOf(System.currentTimeMillis() - startTime));
            return this.dbHelper.getWritableDatabase().delete("local_modifs", "cuid=? AND synced_ts<=" + syncedTs, this.selectionCuid);
        } catch (Exception e) {
            throw new StorageException("Failed to delete: " + e, e);
        }
    }

    public int deleteOlder(@NonNull ArrayList<String> ids, long syncedTs) throws StorageException {
        long startTime = System.currentTimeMillis();
        Logger.m173d("deleteOlder >>> syncedTs=%d ids=%s", Long.valueOf(syncedTs), ids);
        StringBuilder selection = new StringBuilder();
        selection.append("cuid").append("=? AND ");
        selection.append("synced_ts").append("<=").append(syncedTs);
        selection.append(" AND ");
        selection.append("id").append(" IN (");
        int size = ids.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                selection.append(',');
            }
            selection.append('\'').append((String) ids.get(i)).append('\'');
        }
        selection.append(")");
        try {
            Logger.m173d("deleteOlder <<< deleted %d rows in %d ms", Integer.valueOf(this.dbHelper.getWritableDatabase().delete("local_modifs", selection.toString(), this.selectionCuid)), Long.valueOf(System.currentTimeMillis() - startTime));
            return this.dbHelper.getWritableDatabase().delete("local_modifs", selection.toString(), this.selectionCuid);
        } catch (Exception e) {
            throw new StorageException("Failed to delete: " + e, e);
        }
    }

    public int delete(int status, int limit) throws StorageException {
        long startTime = System.currentTimeMillis();
        Logger.m173d("delete >>> status=%s limit=%d", LocalModifs.statusToString(status), Integer.valueOf(limit));
        try {
            Logger.m173d("deleteOlder <<< deleted %d rows in %d ms", Integer.valueOf(this.dbHelper.getWritableDatabase().delete("local_modifs", "_id IN (SELECT _id FROM local_modifs WHERE cuid=? AND sync_status=" + status + " LIMIT " + limit + ")", this.selectionCuid)), Long.valueOf(System.currentTimeMillis() - startTime));
            return this.dbHelper.getWritableDatabase().delete("local_modifs", "_id IN (SELECT _id FROM local_modifs WHERE cuid=? AND sync_status=" + status + " LIMIT " + limit + ")", this.selectionCuid);
        } catch (Exception e) {
            throw new StorageException("Failed to delete: " + e, e);
        }
    }

    public TLocal getBySyncedTime(int n) throws StorageException {
        long startTime = System.currentTimeMillis();
        Logger.m173d("getBySyncedTime >>> n=%d", Integer.valueOf(n));
        TLocal item = null;
        try {
            Cursor cursor = this.dbHelper.getWritableDatabase().rawQuery(createQueryBySyncedTime(n), this.selectionCuid);
            if (cursor != null && cursor.moveToFirst()) {
                item = fromCursor(cursor);
            }
            IOUtils.closeSilently(cursor);
            Logger.m173d("getBySyncedTime <<< %s, %d ms", item, Long.valueOf(System.currentTimeMillis() - startTime));
            return item;
        } catch (Exception e) {
            throw new StorageException("Failed to query by synced time: " + e, e);
        } catch (Throwable th) {
            IOUtils.closeSilently(null);
            Logger.m173d("getBySyncedTime <<< %s, %d ms", null, Long.valueOf(System.currentTimeMillis() - startTime));
        }
    }

    @Nullable
    public TLocal getMostRecentSynced() throws StorageException {
        long startTime = System.currentTimeMillis();
        Logger.m172d("getMostRecentSynced >>>");
        TLocal item = null;
        try {
            Cursor cursor = this.dbHelper.getWritableDatabase().rawQuery("SELECT id,sync_status,attempts,synced_ts,data FROM local_modifs WHERE cuid=? AND synced_ts NOT NULL ORDER BY synced_ts DESC LIMIT 1", this.selectionCuid);
            if (cursor != null && cursor.moveToFirst()) {
                item = fromCursor(cursor);
            }
            IOUtils.closeSilently(cursor);
            Logger.m173d("getMostRecentSynced <<< %s, %d ms", item, Long.valueOf(System.currentTimeMillis() - startTime));
            return item;
        } catch (Exception e) {
            throw new StorageException("Failed to query like: " + e, e);
        } catch (Throwable th) {
            IOUtils.closeSilently(null);
            Logger.m173d("getMostRecentSynced <<< %s, %d ms", null, Long.valueOf(System.currentTimeMillis() - startTime));
        }
    }

    private String createQueryBySyncedTime(int n) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append("id,sync_status,attempts,synced_ts,data").append(" FROM ").append("local_modifs").append(" WHERE ").append("cuid").append("=?").append(" ORDER BY ").append("CASE WHEN synced_ts ISNULL THEN 9223372036854775807 ELSE synced_ts END DESC").append(" LIMIT 1 OFFSET ").append(n);
        return sb.toString();
    }

    private SQLiteStatement createInsertStatement(SQLiteDatabase db) {
        return db.compileStatement("INSERT INTO local_modifs (cuid,id,sync_status,attempts,synced_ts,data) VALUES (?,?,?,?,?,?)");
    }
}
