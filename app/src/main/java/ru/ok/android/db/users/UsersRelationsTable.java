package ru.ok.android.db.users;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.db.base.BaseTable;

public final class UsersRelationsTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("relation_type", "TEXT");
        columns.put("user1", "TEXT");
        columns.put("user2", "TEXT");
        columns.put(Message.ELEMENT, "MESSAGE");
    }

    public String getTableName() {
        return "users_relations";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion >= 59 || newVersion < 59) {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
        } else {
            sqlCommands.add(createBaseTableCreateScript());
        }
    }
}
