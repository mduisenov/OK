package ru.ok.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.utils.Logger;

public final class WebCacheDataBaseHelper extends SQLiteOpenHelper {
    private static volatile WebCacheDataBaseHelper instance;

    public static WebCacheDataBaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (WebCacheDataBaseHelper.class) {
                if (instance == null) {
                    instance = new WebCacheDataBaseHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private WebCacheDataBaseHelper(Context context) {
        super(context, "odnoklassniki.webcache.db", null, 2);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table titles(key text primary key not null, title text not null, subtitle_exist integer default 0);");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            performUpgrade(db, oldVersion);
        } catch (Exception e) {
            Logger.m180e(e, "Failed to upgrade database: %d -> %d", Integer.valueOf(oldVersion), Integer.valueOf(newVersion));
            String errorMessage = String.format("%s. Thread: %s", new Object[]{e.getMessage(), Thread.currentThread()});
            StatisticManager.getInstance().addStatisticEvent("webcache-upgrade-failed", new Pair("reason", errorMessage));
            db.execSQL("DROP TABLE IF EXISTS titles");
            onCreate(db);
        }
    }

    private void performUpgrade(SQLiteDatabase db, int oldVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE titles ADD COLUMN subtitle_exist INTEGER DEFAULT 0");
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
