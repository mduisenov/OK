package ru.ok.android.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.google.android.gms.plus.PlusShare;
import ru.ok.android.db.WebCache.UrlTitleInfo;
import ru.ok.android.db.access.DBStatementsFactory;
import ru.ok.android.utils.Logger;

public class WebCacheDb {
    private WebCacheDataBaseHelper dbHelper;

    static final class InsertSubtitleExist {
        static final String QUERY;

        static {
            QUERY = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, '', ?)", new Object[]{"titles", "key", PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, "subtitle_exist"});
        }
    }

    static final class InsertTitle {
        static final String QUERY;

        static {
            QUERY = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)", new Object[]{"titles", "key", PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE});
        }
    }

    public WebCacheDb(Context context) {
        this.dbHelper = WebCacheDataBaseHelper.getInstance(context);
    }

    public void insertTitle(String id, String title) {
        if (id == null || title == null) {
            Logger.m176e(String.format("Error insert title null value: id %s title %s", new Object[]{id, title}));
            return;
        }
        SQLiteDatabase database = this.dbHelper.getWritableDatabase();
        SQLiteStatement update = DBStatementsFactory.getStatement(database, "UPDATE titles SET title=? WHERE key=?");
        update.bindString(2, id);
        update.bindString(1, title);
        if (update.executeUpdateDelete() == 0) {
            SQLiteStatement statement = DBStatementsFactory.getStatement(database, InsertTitle.QUERY);
            statement.bindString(1, id);
            statement.bindString(2, title);
            statement.executeInsert();
        }
    }

    public void insertSubtitleExist(String id, boolean exist) {
        if (id == null) {
            Logger.m176e("Error insert subtitle exist with null id");
            return;
        }
        SQLiteDatabase database = this.dbHelper.getWritableDatabase();
        long subtitleExistValue = exist ? 1 : 0;
        SQLiteStatement update = DBStatementsFactory.getStatement(database, "UPDATE titles SET subtitle_exist=? WHERE key=?");
        update.bindString(2, id);
        update.bindLong(1, subtitleExistValue);
        if (update.executeUpdateDelete() == 0) {
            SQLiteStatement insert = DBStatementsFactory.getStatement(database, InsertSubtitleExist.QUERY);
            insert.bindString(1, id);
            insert.bindLong(2, subtitleExistValue);
            insert.executeInsert();
        }
    }

    public UrlTitleInfo queryTitle(String id) {
        Cursor cursor = this.dbHelper.getReadableDatabase().query("titles", null, "key = ?", new String[]{id}, null, null, null);
        if (cursor == null) {
            return null;
        }
        UrlTitleInfo ret = null;
        try {
            if (cursor.moveToFirst()) {
                boolean z;
                String string = cursor.getString(1);
                if (cursor.getLong(2) != 0) {
                    z = true;
                } else {
                    z = false;
                }
                ret = new UrlTitleInfo(string, z);
            }
            cursor.close();
            return ret;
        } catch (Throwable th) {
            cursor.close();
        }
    }

    public void deleteAllFromTitles() {
        this.dbHelper.getWritableDatabase().delete("titles", null, null);
    }
}
