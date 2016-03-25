package ru.ok.android.db.users;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;
import ru.ok.model.UserInfo;

public class AuthorizedUsersTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY");
        columns.put("uid", "TEXT UNIQUE NOT NULL");
        columns.put("login", "TEXT");
        columns.put("first_name", "TEXT");
        columns.put("last_name", "TEXT");
        columns.put("gender", "INTEGER");
        columns.put("uri_pic", "TEXT");
        columns.put("token", "TEXT");
        columns.put("timestamp", "INTEGER NOT NULL");
    }

    public String getTableName() {
        return "authorized_users";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 67 && newVersion >= 67) {
            sqlCommands.add(createBaseTableCreateScript());
        }
    }

    public void onAfterUpgrade(Context context, SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.m173d("oldVersion=%d newVersion=%d", Integer.valueOf(oldVersion), Integer.valueOf(newVersion));
        if (oldVersion < 68 && newVersion >= 68) {
            migrateAuthorizedUser(context, db);
        }
    }

    public static void migrateAuthorizedUser(Context context, SQLiteDatabase db) {
        if (Settings.getAuthorizedUserCount(context) != 0) {
            Logger.m172d("Already has authorized user");
        } else if (Settings.hasLoginData(context)) {
            UserInfo currentUser = Settings.getCurrentUser(context);
            String login = Settings.getUserName(context);
            String token = Settings.getToken(context);
            if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(token) && !TextUtils.isEmpty(currentUser.uid)) {
                Logger.m173d("Migrating user to new authorized users storage: login=%s uid=%s firstName=%s lastName=%s", login, currentUser.uid, currentUser.firstName, currentUser.lastName);
                try {
                    db.insert("authorized_users", "first_name", authorizedUserFromCurrentUser(currentUser, login, token));
                    Settings.setAuthorizedUserCount(context, 1);
                } catch (Exception e) {
                    Logger.m180e(e, "Failed to insert data to authorized users table: %s", e);
                }
            }
        } else {
            Logger.m184w("Has no login data");
        }
    }

    private static ContentValues authorizedUserFromCurrentUser(UserInfo currentUser, String login, String token) {
        ContentValues values = new ContentValues();
        values.put("uid", currentUser.uid);
        values.put("timestamp", Long.valueOf(System.currentTimeMillis()));
        values.put("token", token);
        if (currentUser.firstName != null) {
            values.put("first_name", currentUser.firstName);
        }
        if (currentUser.lastName != null) {
            values.put("last_name", currentUser.lastName);
        }
        values.put("login", login);
        if (currentUser.genderType != null) {
            values.put("gender", Integer.valueOf(currentUser.genderType.toInteger()));
        }
        String picUrl = currentUser.getPicUrl();
        if (picUrl != null) {
            values.put("uri_pic", picUrl);
        }
        return values;
    }
}
