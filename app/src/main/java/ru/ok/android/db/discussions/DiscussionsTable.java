package ru.ok.android.db.discussions;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.DataBaseHelper;
import ru.ok.android.db.base.BaseTable;

public class DiscussionsTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("discussion_id", "TEXT PRIMARY KEY");
        columns.put("discussion_type", "TEXT PRIMARY_KEY");
        columns.put("discussion_data", "BLOB");
    }

    public String getTableName() {
        return "discussions";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (newVersion >= 68) {
            sqlCommands.add(DataBaseHelper.createBaseTableDeleteScript(this));
            sqlCommands.add(createBaseTableCreateScript());
            return;
        }
        super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
    }
}
