package ru.ok.android.db.messages;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.DataBaseHelper;
import ru.ok.android.db.base.BaseTable;

public final class MessageTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("conversation_id", "TEXT REFERENCES conversations(server_id) ON DELETE CASCADE");
        columns.put("server_id", "TEXT");
        columns.put("_date", "INTEGER");
        columns.put("_date_editing", "INTEGER");
        columns.put(NotificationCompat.CATEGORY_STATUS, "INTEGER");
        columns.put("status_editing", "INTEGER");
        columns.put("data", "BLOB");
    }

    protected List<String> getIndexedColumnsNames() {
        return Arrays.asList(new String[]{"conversation_id", "_date", NotificationCompat.CATEGORY_STATUS, "status_editing"});
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 77) {
            sqlCommands.add(DataBaseHelper.createBaseTableDeleteScript(this));
            sqlCommands.add(createBaseTableCreateScript());
            return;
        }
        super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
    }

    public String getTableName() {
        return "messages";
    }
}
