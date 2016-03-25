package ru.ok.android.db.users;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public final class FriendTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("friend_id", "TEXT PRIMARY KEY");
        columns.put("is_best_friend", "INTEGER DEFAULT 0");
        columns.put("best_friend_index", "INTEGER DEFAULT 0");
    }

    public String getTableName() {
        return "friends";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
        if (oldVersion < 70) {
            sqlCommands.add("ALTER TABLE friends ADD COLUMN is_best_friend INTEGER DEFAULT 0");
            sqlCommands.add("ALTER TABLE friends ADD COLUMN best_friend_index INTEGER DEFAULT 0");
        }
    }
}
