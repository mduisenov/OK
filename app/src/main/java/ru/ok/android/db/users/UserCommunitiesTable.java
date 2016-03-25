package ru.ok.android.db.users;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public final class UserCommunitiesTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("server_id", "TEXT");
        columns.put("user_id", "TEXT");
        columns.put("type", "TEXT");
        columns.put("name", "TEXT");
        columns.put("year_start", "INTEGER");
        columns.put("year_finish", "INTEGER");
    }

    public String getTableName() {
        return "user_communities";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 59) {
            sqlCommands.add(createBaseTableCreateScript());
        } else {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
        }
    }
}
