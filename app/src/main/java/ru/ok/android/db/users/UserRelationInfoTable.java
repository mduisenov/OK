package ru.ok.android.db.users;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public final class UserRelationInfoTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "TEXT PRIMARY KEY");
        columns.put("friend_invitation_sent", "INTEGER");
        columns.put("can_send_message", "INTEGER");
        columns.put("is_block", "INTEGER");
        columns.put("can_group_invite", "INTEGER");
        columns.put("can_friend_invite", "INTEGER");
    }

    public String getTableName() {
        return "user_relation_info";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 59) {
            sqlCommands.add(createBaseTableCreateScript());
        } else {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
        }
    }
}
