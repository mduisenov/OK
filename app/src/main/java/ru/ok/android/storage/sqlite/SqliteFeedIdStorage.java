package ru.ok.android.storage.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import ru.ok.android.storage.IFeedIdStorage;
import ru.ok.android.storage.StorageException;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.Feed;

public class SqliteFeedIdStorage implements IFeedIdStorage {
    private static AtomicReference<Statements> statementsRef;
    private final FeedIdsDBOpenHelper dbHelper;

    static class Statements {
        final SQLiteStatement insert;
        final SQLiteStatement select;

        Statements(SQLiteDatabase db) {
            this.insert = db.compileStatement("INSERT INTO feed_ids(digest) VALUES (?)");
            this.select = db.compileStatement("SELECT _id FROM feed_ids WHERE digest=?");
        }
    }

    public SqliteFeedIdStorage(Context context) {
        this.dbHelper = FeedIdsDBOpenHelper.getInstance(context);
    }

    public void generateFeedIds(List<Feed> feeds) throws StorageException {
        long startTime = System.currentTimeMillis();
        Logger.m173d("generateFeedIds >>> size=%d", Integer.valueOf(feeds.size()));
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        Statements stmnts = (Statements) statementsRef.getAndSet(null);
        if (stmnts == null) {
            stmnts = new Statements(db);
        }
        SQLiteStatement insert = stmnts.insert;
        SQLiteStatement select = stmnts.select;
        int processedCount = 0;
        db.beginTransaction();
        try {
            for (Feed feed : feeds) {
                insert.bindString(1, feed.digest);
                long id = insert.executeInsert();
                if (id < 0) {
                    select.bindString(1, feed.digest);
                    id = select.simpleQueryForLong();
                }
                feed.setId(id);
                processedCount++;
                r16 = new Object[2];
                r16[0] = feed.digest;
                r16[1] = Long.valueOf(id);
                Logger.m173d("generateFeedIds: digest=%s id=%d", r16);
            }
            db.setTransactionSuccessful();
            statementsRef.set(stmnts);
            db.endTransaction();
            Logger.m173d("SqliteFeedIdStorage", "generateFeedIds <<< %d feed ids in %d ms", Integer.valueOf(feeds.size()), Long.valueOf(System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            Logger.m180e(e, "generateFeedIds failed: %s", e);
            int rollbackCount = 0;
            for (Feed feed2 : feeds) {
                if (rollbackCount == processedCount) {
                    break;
                }
                rollbackCount++;
                feed2.setId(0);
            }
            throw new StorageException("Failed to get feed ids", e);
        } catch (Throwable th) {
            statementsRef.set(stmnts);
            db.endTransaction();
            Logger.m173d("SqliteFeedIdStorage", "generateFeedIds <<< %d feed ids in %d ms", Integer.valueOf(feeds.size()), Long.valueOf(System.currentTimeMillis() - startTime));
        }
    }

    static {
        statementsRef = new AtomicReference();
    }
}
