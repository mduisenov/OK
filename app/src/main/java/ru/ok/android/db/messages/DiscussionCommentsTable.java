package ru.ok.android.db.messages;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.DataBaseHelper;

public final class DiscussionCommentsTable extends MessageBaseTable {
    protected void fillColumns(Map<String, String> columns) {
        super.fillColumns(columns);
        columns.put("discussion_id", "TEXT");
        columns.put("discussion_type", "TEXT");
    }

    public String getTableName() {
        return "discussions_comments";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 51) {
            sqlCommands.add(DataBaseHelper.createBaseTableDeleteScript(this));
            sqlCommands.add(createBaseTableCreateScript());
        } else if (oldVersion < 76) {
            recreateTableAndCopyOldData(db, sqlCommands);
        } else {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
        }
    }
}
