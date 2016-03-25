package ru.ok.android.db.provider;

import android.database.sqlite.SQLiteDatabase;
import ru.ok.android.db.SQLiteUtils;
import ru.ok.android.utils.Logger;

class ProviderAllTablesHelper {
    static int delete(SQLiteDatabase db, String selection, String[] selectionArgs) {
        int count = 0;
        for (String table : SQLiteUtils.queryTableNames(db)) {
            if (SQLiteUtils.queryTableColumns(db, table).contains("_last_update")) {
                try {
                    count += db.delete(table, selection, selectionArgs);
                } catch (Exception e) {
                    Logger.m180e(e, "Failed to update '%s' table", table);
                }
            } else {
                Logger.m185w("Table %s do not have %s column", table, "_last_update");
            }
        }
        return count;
    }
}
