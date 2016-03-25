package ru.ok.android.storage.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteStatement;
import ru.ok.android.db.access.DBStatementsFactory;
import ru.ok.android.storage.IFeedBannerStatsStorage;
import ru.ok.android.storage.StorageException;

public class SqliteFeedBannerStatsStorage implements IFeedBannerStatsStorage {
    private final String currentUserId;
    private final BannerStatsDBOpenHelper dbHelper;

    public SqliteFeedBannerStatsStorage(Context context, String currentUserId) {
        this.dbHelper = BannerStatsDBOpenHelper.getInstance(context);
        this.currentUserId = currentUserId;
    }

    public boolean checkSaveFeedIsShown(String uuid) throws StorageException {
        return checkSaveFeed(uuid, 1);
    }

    public boolean checkSaveFeedIsShownOnScroll(String uuid) throws StorageException {
        return checkSaveFeed(uuid, 2);
    }

    private boolean checkSaveFeed(String uuid, int type) throws StorageException {
        SQLiteStatement insert = DBStatementsFactory.getStatement(this.dbHelper.getWritableDatabase(), "INSERT INTO stats (cuid,type,uuid,ts) VALUES (?,?,?,?)");
        insert.bindString(1, this.currentUserId);
        insert.bindLong(2, (long) type);
        insert.bindString(3, uuid);
        insert.bindLong(4, System.currentTimeMillis());
        if (insert.executeInsert() < 0) {
            return true;
        }
        return false;
    }

    public void removeOldRecords(long limitTs) throws StorageException {
        SQLiteStatement delete = DBStatementsFactory.getStatement(this.dbHelper.getWritableDatabase(), "DELETE FROM stats WHERE cuid=? AND ts<?");
        delete.bindString(1, this.currentUserId);
        delete.bindLong(2, limitTs);
        delete.executeUpdateDelete();
    }
}
