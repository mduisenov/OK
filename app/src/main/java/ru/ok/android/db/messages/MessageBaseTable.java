package ru.ok.android.db.messages;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.db.base.BaseTable;

public class MessageBaseTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("server_id", "TEXT UNIQUE ON CONFLICT REPLACE");
        columns.put("author_id", "TEXT");
        columns.put("author_type", "TEXT");
        columns.put(Message.ELEMENT, "TEXT");
        columns.put("message_edited", "TEXT");
        columns.put("reply_to_comment_id", "TEXT");
        columns.put("reply_to_id", "TEXT");
        columns.put("reply_to_type", "TEXT");
        columns.put("_date", "INTEGER");
        columns.put(NotificationCompat.CATEGORY_STATUS, "TEXT");
        columns.put("failure_reason", "TEXT");
        columns.put("uuid", "TEXT");
        columns.put("likes_count", "INTEGER");
        columns.put("is_liked", "INTEGER");
        columns.put("deletion_allowed", "INTEGER");
        columns.put("like_allowed", "INTEGER");
        columns.put("mark_as_spam_allowed", "INTEGER");
        columns.put("block_allowed", "INTEGER");
        columns.put("like_id", "TEXT");
        columns.put("like_last_date", "INTEGER");
    }

    public String getTableName() {
        return null;
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
        if (oldVersion < 70) {
            sqlCommands.add("ALTER TABLE " + getTableName() + " ADD COLUMN " + "message_edited" + " TEXT");
        }
    }
}
