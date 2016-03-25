package ru.ok.android.db.users;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public final class UserCountersTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "TEXT PRIMARY KEY");
        columns.put("photos_personal", "INTEGER");
        columns.put("photos_in_photo_albums", "INTEGER");
        columns.put("photo_albums", "INTEGER");
        columns.put("presents", "INTEGER");
        columns.put("friends", "INTEGER");
        columns.put("groups", "INTEGER");
        columns.put("communities", "INTEGER");
        columns.put("schools", "INTEGER");
        columns.put("statuses", "INTEGER");
        columns.put("applications", "INTEGER");
        columns.put("happenings", "INTEGER");
        columns.put("friends_online", "INTEGER");
        columns.put("holidays", "INTEGER");
    }

    public String getTableName() {
        return "users_counters";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 59) {
            sqlCommands.add(createBaseTableCreateScript());
        } else {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
        }
    }
}
