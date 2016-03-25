package ru.ok.android.db.music;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public class FriendsMusicTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("user_id", "TEXT");
        columns.put("count", "INTEGER");
        columns.put("add_time", "INTEGER");
    }

    public String getTableName() {
        return "friends_music";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 53 && newVersion >= 53) {
            sqlCommands.add(createBaseTableCreateScript());
        }
        if (oldVersion <= 53 && newVersion >= 54) {
            sqlCommands.add("ALTER TABLE friends_music ADD COLUMN add_time INTEGER");
        }
    }
}
