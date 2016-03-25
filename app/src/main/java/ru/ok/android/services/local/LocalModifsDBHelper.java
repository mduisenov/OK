package ru.ok.android.services.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.io.File;
import java.util.HashMap;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.db.CustomDirDBOpenHelper;

class LocalModifsDBHelper extends CustomDirDBOpenHelper {
    private static HashMap<String, LocalModifsDBHelper> instances;

    static {
        instances = new HashMap();
    }

    static synchronized LocalModifsDBHelper getInstance(Context context, String filename) {
        LocalModifsDBHelper dbHelper;
        synchronized (LocalModifsDBHelper.class) {
            dbHelper = (LocalModifsDBHelper) instances.get(filename);
            if (dbHelper == null) {
                dbHelper = new LocalModifsDBHelper(context, filename);
                instances.put(filename, dbHelper);
            }
        }
        return dbHelper;
    }

    private LocalModifsDBHelper(Context context, String filename) {
        super(context, new File(context.getFilesDir(), "storages"), filename, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        Logger.m173d("onCreate: %s", "CREATE TABLE local_modifs (cuid TEXT NOT NULL, id TEXT NOT NULL, sync_status INTEGER NOT NULL, attempts INTEGER NOT NULL DEFAULT 0, synced_ts INTEGER, data BLOB, UNIQUE (cuid,id) ON CONFLICT REPLACE)");
        db.execSQL("CREATE TABLE local_modifs (cuid TEXT NOT NULL, id TEXT NOT NULL, sync_status INTEGER NOT NULL, attempts INTEGER NOT NULL DEFAULT 0, synced_ts INTEGER, data BLOB, UNIQUE (cuid,id) ON CONFLICT REPLACE)");
        Logger.m173d("onCreate: %s", "CREATE INDEX local_modifs_idx_1 ON local_modifs(cuid,synced_ts)");
        db.execSQL("CREATE INDEX local_modifs_idx_1 ON local_modifs(cuid,synced_ts)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.m173d("onUpgrade: oldVersion=%d newVersion=%d", Integer.valueOf(oldVersion), Integer.valueOf(newVersion));
        Logger.m173d("onUpgrade: %s", "DROP TABLE IF EXISTS local_modifs");
        db.execSQL("DROP TABLE IF EXISTS local_modifs");
        onCreate(db);
    }
}
