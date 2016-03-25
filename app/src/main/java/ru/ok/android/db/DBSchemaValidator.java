package ru.ok.android.db;

import android.database.sqlite.SQLiteDatabase;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import ru.ok.android.db.base.BaseTable;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.utils.ObjectUtils;

class DBSchemaValidator {
    private final Collection<BaseTable> tables;

    DBSchemaValidator(Collection<BaseTable> tables) {
        this.tables = tables;
    }

    boolean isSchemaValid(SQLiteDatabase db) {
        Logger.m172d(">>> Checking DB Schema...");
        boolean isValid = hasRequiredTables(db, null) && allTablesAreValid(db);
        if (isValid) {
            Logger.m172d("<<< DB Schema is OK.");
        } else {
            Logger.m184w("<<< DB Schema is broken");
        }
        return isValid;
    }

    private boolean allTablesAreValid(SQLiteDatabase db) {
        for (BaseTable table : this.tables) {
            if (!isTableValid(db, table)) {
                return false;
            }
        }
        return true;
    }

    private boolean isTableValid(SQLiteDatabase db, BaseTable table) {
        String tableName = table.getTableName();
        try {
            if (ObjectUtils.setsEqual(new HashSet(table.getColumns().keySet()), SQLiteUtils.queryTableColumns(db, tableName))) {
                Logger.m173d("Table %s is OK.", tableName);
                return true;
            }
            Logger.m185w("Table %s has invalid columns set: %s (required: %s)", tableName, SQLiteUtils.queryTableColumns(db, tableName), new HashSet(table.getColumns().keySet()));
            return false;
        } catch (Throwable e) {
            Logger.m177e("Failed to check table %s, error occurred: %s", tableName, e);
            Logger.m178e(e);
            return false;
        }
    }

    private boolean hasRequiredTables(SQLiteDatabase db, Collection<String> outExtraTables) {
        try {
            Set<String> currentTables = SQLiteUtils.queryTableNames(db);
            Logger.m173d("Found tables: %s", currentTables);
            boolean missingTables = false;
            for (BaseTable table : this.tables) {
                if (!currentTables.remove(table.getTableName())) {
                    Logger.m185w("Table %s is missing", table.getTableName());
                    missingTables = true;
                }
            }
            if (outExtraTables != null) {
                outExtraTables.addAll(currentTables);
            }
            if (!currentTables.isEmpty()) {
                Logger.m185w("Found unknown extra tables: %s", currentTables);
            }
            if (missingTables) {
                return false;
            }
            return true;
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to query table names");
            return false;
        }
    }
}
