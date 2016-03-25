package ru.ok.android.db.group;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import ru.ok.android.db.base.BaseTable;

public final class GroupCountersTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "TEXT PRIMARY KEY");
        columns.put("themes", "INTEGER");
        columns.put("photo_albums", "INTEGER");
        columns.put("members", "INTEGER");
        columns.put("videos", "INTEGER");
        columns.put("news", "INTEGER");
        columns.put("links", "INTEGER");
        columns.put("presents", "INTEGER");
        columns.put("black_list", "INTEGER");
        columns.put(DeliveryReceiptRequest.ELEMENT, "INTEGER");
    }

    public String getTableName() {
        return "groups_counters";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 59) {
            sqlCommands.add(createBaseTableCreateScript());
        } else if (oldVersion >= 60 || newVersion < 60) {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
        } else {
            sqlCommands.add("ALTER TABLE " + getTableName() + " ADD COLUMN " + "black_list" + " INTEGER");
            sqlCommands.add("ALTER TABLE " + getTableName() + " ADD COLUMN " + DeliveryReceiptRequest.ELEMENT + " INTEGER");
        }
    }
}
