package ru.ok.android.db.messages;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.DataBaseHelper;
import ru.ok.android.db.base.BaseTable;

public final class ConversationTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("server_id", "TEXT NOT NULL UNIQUE PRIMARY KEY");
        columns.put("data", "BLOB");
    }

    public String getTableName() {
        return "conversations";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 75) {
            sqlCommands.add(DataBaseHelper.createBaseTableDeleteScript(this));
            sqlCommands.add(createBaseTableCreateScript());
            return;
        }
        super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
    }
}
