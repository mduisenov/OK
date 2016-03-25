package ru.ok.android.db.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import java.util.Arrays;
import ru.ok.android.db.provider.OdklContract.UserPrivacySettings;
import ru.ok.android.utils.Logger;

class ProviderUserPrivacySettingsHelper {
    static Cursor query(ContentResolver cr, Uri uri, SQLiteDatabase db, int settingId, String uid, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String str = ">>> uri=%s settingId=%d uid=%s selection=%s selectionArgs=%s";
        Object[] objArr = new Object[5];
        objArr[0] = uri;
        objArr[1] = Integer.valueOf(settingId);
        objArr[2] = uid;
        objArr[3] = selection;
        objArr[4] = Logger.isLoggingEnable() ? Arrays.toString(selectionArgs) : "";
        Logger.m173d(str, objArr);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables("user_privacy_settings");
        boolean appendAdd = false;
        if (settingId != 0) {
            if (null != null) {
                qb.appendWhere(") AND (");
            }
            qb.appendWhere("privacy_setting_id");
            qb.appendWhere("=");
            qb.appendWhere(Integer.toString(settingId));
            appendAdd = true;
        }
        if (uid != null) {
            if (appendAdd) {
                qb.appendWhere(") AND (");
            }
            qb.appendWhere("privacy_mode");
            qb.appendWhere("='");
            qb.appendWhere(uid);
            qb.appendWhere("'");
        }
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        if (cursor != null) {
            cursor.setNotificationUri(cr, uri);
        }
        if (Logger.isLoggingEnable()) {
            str = "<<< row count=%d";
            objArr = new Object[1];
            objArr[0] = Integer.valueOf(cursor == null ? 0 : cursor.getCount());
            Logger.m173d(str, objArr);
        }
        return cursor;
    }

    static Uri insert(SQLiteDatabase db, Uri uri, int settingId, String uid, ContentValues values) {
        Logger.m173d(">>> settingId=%d uid=%s values=%s", Integer.valueOf(settingId), uid, values);
        fixContentValuesForInsert(uri, settingId, uid, values);
        if (ProviderUtils.insert(db, "user_privacy_settings", values) == -1) {
            Logger.m184w("<<< Failed to insert row");
            return null;
        }
        if (settingId == 0) {
            settingId = values.getAsInteger("privacy_setting_id").intValue();
        }
        if (uid == null) {
            uid = values.getAsString("uid");
        }
        Logger.m173d("<<< uri=%s", UserPrivacySettings.getUri(settingId, uid));
        return UserPrivacySettings.getUri(settingId, uid);
    }

    static void fixContentValuesForInsert(Uri uri, int settingId, String uid, ContentValues values) {
        boolean valuesHasSettingId = values.containsKey("privacy_setting_id");
        boolean valuesHasUid = values.containsKey("uid");
        if (settingId != 0) {
            if (valuesHasSettingId) {
                Logger.m176e("<<< Column privacy_setting_id not allowed");
                throw new IllegalArgumentException("Column privacy_setting_id not allowed for insert to this uri: " + uri);
            }
            values.put("privacy_setting_id", Integer.valueOf(settingId));
        } else if (!valuesHasSettingId) {
            throw new IllegalArgumentException("Required column not specified: privacy_setting_id");
        }
        if (uid != null) {
            if (valuesHasUid) {
                Logger.m176e("<<< Column uid not allowed");
                throw new IllegalArgumentException("Column uid not allowed for insert to this uri: " + uri);
            } else {
                values.put("uid", uid);
            }
        } else if (!valuesHasUid) {
            throw new IllegalArgumentException("Required column not specified: uid");
        }
    }

    static int bulkInsert(SQLiteDatabase db, Uri uri, int settingId, String uid, ContentValues[] values) {
        Logger.m173d(">>> settingId=%d uid=%s values=%s", Integer.valueOf(settingId), uid, values);
        int rowCount = 0;
        for (ContentValues rowValues : values) {
            fixContentValuesForInsert(uri, settingId, uid, rowValues);
            if (ProviderUtils.insert(db, "user_privacy_settings", rowValues) == -1) {
                Logger.m176e("Failed to insert row: " + rowValues);
            } else {
                rowCount++;
            }
        }
        Logger.m173d("<<< inserted %d rows", Integer.valueOf(rowCount));
        return rowCount;
    }

    static int delete(SQLiteDatabase db, int settingId, String uid, String selection, String[] selectionArgs) {
        String arrays;
        String str = ">>> settingId=%d uid=%s selection=%s selectionArgs=%s";
        Object[] objArr = new Object[4];
        objArr[0] = Integer.valueOf(settingId);
        objArr[1] = uid;
        objArr[2] = selection;
        if (Logger.isLoggingEnable()) {
            arrays = Arrays.toString(selectionArgs);
        } else {
            arrays = selectionArgs;
        }
        objArr[3] = arrays;
        Logger.m173d(str, objArr);
        int affectedRows = db.delete("user_privacy_settings", buildSelection(settingId, uid, selection), selectionArgs);
        Logger.m172d("<<< deleted %d rows");
        return affectedRows;
    }

    static int update(SQLiteDatabase db, int settingId, String uid, String selection, String[] selectionArgs, ContentValues values) {
        String arrays;
        String str = ">>> settingId=%d uid=%s selection=%s selectionArgs=%s values=%s";
        Object[] objArr = new Object[5];
        objArr[0] = Integer.valueOf(settingId);
        objArr[1] = uid;
        objArr[2] = selection;
        if (Logger.isLoggingEnable()) {
            arrays = Arrays.toString(selectionArgs);
        } else {
            arrays = selectionArgs;
        }
        objArr[3] = arrays;
        objArr[4] = values;
        Logger.m173d(str, objArr);
        Logger.m173d("<<< updated %d rows", Integer.valueOf(ProviderUtils.update(db, "user_privacy_settings", values, buildSelection(settingId, uid, selection), selectionArgs)));
        return ProviderUtils.update(db, "user_privacy_settings", values, buildSelection(settingId, uid, selection), selectionArgs);
    }

    static String buildSelection(int settingId, String uid, String selection) {
        StringBuilder selectionBuilder = null;
        boolean appendAnd = false;
        if (settingId != 0) {
            if (null == null) {
                selectionBuilder = new StringBuilder();
            }
            if (null != null) {
                selectionBuilder.append(" AND ");
            }
            selectionBuilder.append('(').append("privacy_setting_id").append('=').append(settingId).append(')');
            appendAnd = true;
        }
        if (uid != null) {
            if (selectionBuilder == null) {
                selectionBuilder = new StringBuilder();
            }
            if (appendAnd) {
                selectionBuilder.append(" AND ");
            }
            selectionBuilder.append('(').append("uid").append("='").append(uid).append("')");
            appendAnd = true;
        }
        if (selection != null) {
            if (selectionBuilder == null) {
                selectionBuilder = new StringBuilder();
            }
            if (appendAnd) {
                selectionBuilder.append(" AND ");
            }
            selectionBuilder.append('(').append(selection).append(')');
        }
        return selectionBuilder == null ? "1" : selectionBuilder.toString();
    }
}
