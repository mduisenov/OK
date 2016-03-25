package ru.ok.android.db.users;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.DataBaseHelper;
import ru.ok.android.db.base.BaseTable;

public class UserSubscribeStreamTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("USER_ID", "TEXT PRIMARY KEY");
    }

    public String getTableName() {
        return "users_subscribe";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion >= 59 || newVersion < 59) {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
            return;
        }
        sqlCommands.add(DataBaseHelper.createBaseTableDeleteScript(this));
        sqlCommands.add(createBaseTableCreateScript());
    }
}
