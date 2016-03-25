package ru.ok.android.db.access;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.provider.OdklContract.AuthorizedUsers;
import ru.ok.android.model.AuthorizedUser;
import ru.ok.android.model.UserWithLogin;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public final class AuthorizedUsersStorageFacade {
    public static void deleteEntry(String uid) {
        if (isEnabled()) {
            try {
                OdnoklassnikiApplication.getContext().getContentResolver().delete(AuthorizedUsers.getContentUri(uid), null, null);
            } catch (Throwable e) {
                Logger.m178e(e);
            }
        }
    }

    private static boolean isEnabled() {
        return AuthorizationPreferences.getMultipleLoginEnabled();
    }

    public static void addUser(String uid, String token) {
        if (isEnabled()) {
            ContentValues values = new ContentValues();
            values.put("uid", uid);
            values.put("timestamp", Long.valueOf(System.currentTimeMillis()));
            values.put("token", token);
            values.put("first_name", "");
            values.put("last_name", "");
            OdnoklassnikiApplication.getContext().getContentResolver().insert(AuthorizedUsers.getContentUri(), values);
        }
    }

    public static int getAuthorizedUsersCount() {
        if (!isEnabled()) {
            return 0;
        }
        try {
            Cursor c = OdnoklassnikiApplication.getContext().getContentResolver().query(AuthorizedUsers.getContentUri(), new String[]{"uid"}, null, null, null);
            if (c == null) {
                return 0;
            }
            int count = c.getCount();
            c.close();
            return count;
        } catch (Throwable e) {
            Logger.m178e(e);
            return 0;
        }
    }

    public static ArrayList<AuthorizedUser> getUsers() {
        ArrayList<AuthorizedUser> users = null;
        if (isEnabled()) {
            String[] projection = new String[]{"token", "uid", "login", "first_name", "last_name", "gender", "uri_pic"};
            String orderBy = "timestamp ASC";
            users = new ArrayList();
            Cursor c = null;
            try {
                c = OdnoklassnikiApplication.getContext().getContentResolver().query(AuthorizedUsers.getContentUri(), projection, null, null, orderBy);
                if (c != null) {
                    while (c.moveToNext()) {
                        AuthorizedUser authorizedUser = new AuthorizedUser();
                        UserWithLogin user = authorizedUser.user;
                        authorizedUser.token = c.getString(0);
                        user.uid = c.getString(1);
                        user.login = c.getString(2);
                        user.firstName = c.getString(3);
                        user.lastName = c.getString(4);
                        user.genderType = UserGenderType.byInteger(c.getInt(5));
                        user.picUrl = c.getString(6);
                        users.add(authorizedUser);
                    }
                }
                if (c != null) {
                    c.close();
                }
            } catch (Throwable e) {
                Logger.m178e(e);
                if (c != null) {
                    c.close();
                }
            } catch (Throwable th) {
                if (c != null) {
                    c.close();
                }
            }
        }
        return users;
    }

    public static void logOutCurrentUser(String uid) {
        if (isEnabled()) {
            ContentValues values = new ContentValues();
            values.put("token", "");
            try {
                OdnoklassnikiApplication.getContext().getContentResolver().update(AuthorizedUsers.getContentUri(uid), values, null, null);
            } catch (Throwable e) {
                Logger.m178e(e);
            }
        }
    }

    public static void updateUserInfoWithLogin(UserInfo user, String login) {
        if (isEnabled()) {
            ContentValues values = new ContentValues();
            if (!login.isEmpty()) {
                values.put("login", login);
            }
            values.put("first_name", user.firstName);
            values.put("last_name", user.lastName);
            values.put("gender", Integer.valueOf(user.genderType.toInteger()));
            values.put("uri_pic", user.getPicUrl());
            try {
                OdnoklassnikiApplication.getContext().getContentResolver().update(AuthorizedUsers.getContentUri(user.uid), values, null, null);
            } catch (Throwable e) {
                Logger.m178e(e);
            }
        }
    }
}
