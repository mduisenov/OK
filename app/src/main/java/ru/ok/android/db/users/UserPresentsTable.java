package ru.ok.android.db.users;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public final class UserPresentsTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("PRESENT_ID", "TEXT");
        columns.put("USER_ID", "TEXT");
        columns.put("SENDER_ID", "TEXT");
        columns.put("PICTURE", "TEXT");
        columns.put("IS_BIG", "INTEGER");
        columns.put("TRACK_ID", "INTEGER DEFAULT 0");
        columns.put("IS_ANIMATED", "INTEGER");
        columns.put("SPRITE", "TEXT");
        columns.put("SPRITE_SIZE", "INTEGER");
        columns.put("ANIMATION_DURATION", "INTEGER DEFAULT 0");
        columns.put("ANIMATION_FRAMES_COUNT", "INTEGER DEFAULT 0");
        columns.put("ANIMATION_REPLAY_DELAY", "INTEGER DEFAULT 0");
    }

    public String getTableName() {
        return "user_presents";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion >= 59 || newVersion < 59) {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
            if (oldVersion < 81 && newVersion >= 81) {
                sqlCommands.add("ALTER TABLE user_presents ADD COLUMN IS_ANIMATED INTEGER");
                sqlCommands.add("ALTER TABLE user_presents ADD COLUMN SPRITE TEXT");
                sqlCommands.add("ALTER TABLE user_presents ADD COLUMN SPRITE_SIZE INTEGER");
                sqlCommands.add("ALTER TABLE user_presents ADD COLUMN ANIMATION_DURATION INTEGER DEFAULT 0");
                sqlCommands.add("ALTER TABLE user_presents ADD COLUMN ANIMATION_FRAMES_COUNT INTEGER DEFAULT 0");
                sqlCommands.add("ALTER TABLE user_presents ADD COLUMN ANIMATION_REPLAY_DELAY INTEGER DEFAULT 0");
                return;
            }
            return;
        }
        sqlCommands.add(createBaseTableCreateScript());
    }
}
