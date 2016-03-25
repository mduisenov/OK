package ru.ok.android.db.group;

import android.database.sqlite.SQLiteDatabase;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public final class GroupMembersTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("gm_group_id", "TEXT NOT NULL");
        columns.put("gm_user_id", "TEXT NOT NULL");
    }

    public String getTableName() {
        return "group_members";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion >= 59 || newVersion < 59) {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
        } else {
            sqlCommands.add(createBaseTableCreateScript());
        }
    }

    protected List<String> getIndexedColumnsNames() {
        return Arrays.asList(new String[]{"gm_group_id", "gm_user_id"});
    }
}
