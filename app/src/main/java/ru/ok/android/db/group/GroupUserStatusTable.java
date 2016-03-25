package ru.ok.android.db.group;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public class GroupUserStatusTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("user_id", "TEXT");
        columns.put("group_id", "TEXT");
        columns.put(NotificationCompat.CATEGORY_STATUS, "TEXT");
    }

    public String getTableName() {
        return "group_user_status";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 59) {
            sqlCommands.add(createBaseTableCreateScript());
        } else {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
        }
    }
}
