package ru.ok.android.storage.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.io.File;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.db.CustomDirDBOpenHelper;

class BannerStatsDBOpenHelper extends CustomDirDBOpenHelper {
    private static volatile BannerStatsDBOpenHelper instance;

    static BannerStatsDBOpenHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (BannerStatsDBOpenHelper.class) {
                if (instance == null) {
                    instance = new BannerStatsDBOpenHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private BannerStatsDBOpenHelper(Context context) {
        super(context, new File(context.getFilesDir(), "storages"), "banner_stats.sqlite3", 1);
    }

    public void onCreate(SQLiteDatabase db) {
        long startTime = System.currentTimeMillis();
        Logger.m172d("onCreate >>>");
        Logger.m173d("onCreate: %s", "CREATE TABLE stats (cuid TEXT NON NULL, type INTEGER NON NULL, uuid TEXT NON NULL, ts INTEGER NON NULL, UNIQUE (cuid, type,uuid) ON CONFLICT IGNORE)    ");
        db.execSQL("CREATE TABLE stats (cuid TEXT NON NULL, type INTEGER NON NULL, uuid TEXT NON NULL, ts INTEGER NON NULL, UNIQUE (cuid, type,uuid) ON CONFLICT IGNORE)    ");
        Logger.m173d("onCreate: %s", "CREATE INDEX stats_idx_ts ON stats (cuid,ts)");
        db.execSQL("CREATE INDEX stats_idx_ts ON stats (cuid,ts)");
        Logger.m173d("onCreate <<< %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.m173d("onUpgrade: oldVersion=%d newVersion=%d", Integer.valueOf(oldVersion), Integer.valueOf(newVersion));
        Logger.m173d("onUpgrade: %s", "DROP TABLE IF EXISTS stats");
        db.execSQL("DROP TABLE IF EXISTS stats");
        onCreate(db);
    }
}
