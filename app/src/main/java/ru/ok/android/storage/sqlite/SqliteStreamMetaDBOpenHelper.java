package ru.ok.android.storage.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.io.File;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.db.CustomDirDBOpenHelper;

public class SqliteStreamMetaDBOpenHelper extends CustomDirDBOpenHelper {
    private static volatile SqliteStreamMetaDBOpenHelper instance;

    static SqliteStreamMetaDBOpenHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SqliteStreamMetaDBOpenHelper.class) {
                if (instance == null) {
                    instance = new SqliteStreamMetaDBOpenHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private SqliteStreamMetaDBOpenHelper(Context context) {
        super(context, new File(context.getFilesDir(), "storages"), "stream_meta.sqlite3", 2);
    }

    public void onCreate(SQLiteDatabase db) {
        long startTime = System.currentTimeMillis();
        Logger.m172d("onCreate >>>");
        Logger.m173d("onCreate: %s", "CREATE TABLE stream_meta (_id INTEGER PRIMARY KEY AUTOINCREMENT, cuid TEXT NOT NULL, type INTEGER NOT NULL, ctx_id TEXT, page_key TEXT NON NULL, page_number INTEGER NON NULL DEFAULT -1, ts INTEGER NON NULL, UNIQUE (cuid, type,ctx_id,page_key) ON CONFLICT REPLACE)");
        db.execSQL("CREATE TABLE stream_meta (_id INTEGER PRIMARY KEY AUTOINCREMENT, cuid TEXT NOT NULL, type INTEGER NOT NULL, ctx_id TEXT, page_key TEXT NON NULL, page_number INTEGER NON NULL DEFAULT -1, ts INTEGER NON NULL, UNIQUE (cuid, type,ctx_id,page_key) ON CONFLICT REPLACE)");
        Logger.m173d("onCreate: %s", "CREATE INDEX stream_meta_idx_ts ON stream_meta (cuid,ts)");
        db.execSQL("CREATE INDEX stream_meta_idx_ts ON stream_meta (cuid,ts)");
        Logger.m173d("onCreate <<< %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.m173d("onUpgrade: oldVersion=%d newVersion=%d", Integer.valueOf(oldVersion), Integer.valueOf(newVersion));
        if (oldVersion < 2 && newVersion >= 2) {
            Logger.m173d("onUpgrade: %s", "ALTER TABLE stream_meta ADD COLUMN page_number INTEGER NON NULL DEFAULT -1");
            db.execSQL("ALTER TABLE stream_meta ADD COLUMN page_number INTEGER NON NULL DEFAULT -1");
        }
    }
}
