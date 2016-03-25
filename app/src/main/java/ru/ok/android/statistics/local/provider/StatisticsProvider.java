package ru.ok.android.statistics.local.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.Logger;

public class StatisticsProvider extends ContentProvider {
    private ContentResolver contentResolver;
    private DBHelper dbHelper;
    private final UriMatcher uriMatcher;

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper() {
            super(StatisticsProvider.this.getContext(), "stats.db", null, 1);
        }

        public void onCreate(SQLiteDatabase db) {
            Logger.m173d("exec SQL: %s", "CREATE TABLE events (_id INTEGER PRIMARY KEY AUTOINCREMENT,event_name TEXT NOT NULL,event_ts INTEGER NOT NULL)");
            db.execSQL("CREATE TABLE events (_id INTEGER PRIMARY KEY AUTOINCREMENT,event_name TEXT NOT NULL,event_ts INTEGER NOT NULL)");
            Logger.m173d("exec SQL: %s", "CREATE TABLE params (_id INTEGER PRIMARY KEY AUTOINCREMENT,event_id INTEGER NOT NULL REFERENCES events(_id) ON DELETE CASCADE, param_name TEXT NOT NULL, param_value TEXT)");
            db.execSQL("CREATE TABLE params (_id INTEGER PRIMARY KEY AUTOINCREMENT,event_id INTEGER NOT NULL REFERENCES events(_id) ON DELETE CASCADE, param_name TEXT NOT NULL, param_value TEXT)");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Logger.m173d("oldVersion=%d newVersion=%d", Integer.valueOf(oldVersion), Integer.valueOf(newVersion));
        }
    }

    static class ProjectionMap {
        static final Map<String, String> joinEventsOnParams;

        static {
            joinEventsOnParams = new HashMap();
            joinEventsOnParams.put("_id", "events._id");
        }
    }

    public StatisticsProvider() {
        this.uriMatcher = new UriMatcher(-1);
        this.uriMatcher.addURI("ru.ok.android.stat", "events/#", 1);
        this.uriMatcher.addURI("ru.ok.android.stat", "events", 2);
        this.uriMatcher.addURI("ru.ok.android.stat", "event_params/#", 3);
        this.uriMatcher.addURI("ru.ok.android.stat", "event_params", 4);
        this.uriMatcher.addURI("ru.ok.android.stat", "insert_event_params", 5);
    }

    public boolean onCreate() {
        this.dbHelper = new DBHelper();
        this.contentResolver = getContext().getContentResolver();
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.database.Cursor query(android.net.Uri r13, java.lang.String[] r14, java.lang.String r15, java.lang.String[] r16, java.lang.String r17) {
        /*
        r12 = this;
        r2 = "uri=%s projection=%s selection=%s selectionArgs=%s sortOrder=%s";
        r1 = 5;
        r3 = new java.lang.Object[r1];
        r1 = 0;
        r3[r1] = r13;
        r4 = 1;
        r1 = ru.ok.android.utils.Logger.isLoggingEnable();
        if (r1 == 0) goto L_0x0034;
    L_0x0010:
        r1 = java.util.Arrays.toString(r14);
    L_0x0014:
        r3[r4] = r1;
        r1 = 2;
        r3[r1] = r15;
        r4 = 3;
        r1 = ru.ok.android.utils.Logger.isLoggingEnable();
        if (r1 == 0) goto L_0x0038;
    L_0x0020:
        r1 = java.util.Arrays.toString(r16);
    L_0x0024:
        r3[r4] = r1;
        r1 = 4;
        r3[r1] = r17;
        ru.ok.android.utils.Logger.m173d(r2, r3);
        r9 = r12.safeObtainReadableDB();
        if (r9 != 0) goto L_0x003c;
    L_0x0032:
        r8 = 0;
    L_0x0033:
        return r8;
    L_0x0034:
        r1 = "";
        goto L_0x0014;
    L_0x0038:
        r1 = "";
        goto L_0x0024;
    L_0x003c:
        r0 = new android.database.sqlite.SQLiteQueryBuilder;
        r0.<init>();
        r1 = r12.uriMatcher;
        r1 = r1.match(r13);
        switch(r1) {
            case 1: goto L_0x0064;
            case 2: goto L_0x007f;
            case 3: goto L_0x00cc;
            case 4: goto L_0x00e7;
            default: goto L_0x004a;
        };
    L_0x004a:
        r1 = new java.lang.IllegalArgumentException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Query not supported for uri: ";
        r2 = r2.append(r3);
        r2 = r2.append(r13);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
    L_0x0064:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "events._id=";
        r1 = r1.append(r2);
        r2 = r13.getLastPathSegment();
        r1 = r1.append(r2);
        r1 = r1.toString();
        r0.appendWhere(r1);
    L_0x007f:
        r10 = needJoinEventsOnParams(r14);
        if (r10 == 0) goto L_0x00c5;
    L_0x0085:
        r1 = "events JOIN params ON events._id=params.event_id";
        r0.setTables(r1);
        r1 = ru.ok.android.statistics.local.provider.StatisticsProvider.ProjectionMap.joinEventsOnParams;
        r0.setProjectionMap(r1);
    L_0x0090:
        r1 = ru.ok.android.utils.Logger.isLoggingEnable();
        if (r1 == 0) goto L_0x00af;
    L_0x0096:
        r4 = 0;
        r5 = 0;
        r7 = 0;
        r1 = r14;
        r2 = r15;
        r3 = r16;
        r6 = r17;
        r11 = r0.buildQuery(r1, r2, r3, r4, r5, r6, r7);
        r1 = "Performing query: %s";
        r2 = 1;
        r2 = new java.lang.Object[r2];
        r3 = 0;
        r2[r3] = r11;
        ru.ok.android.utils.Logger.m173d(r1, r2);
    L_0x00af:
        r5 = 0;
        r6 = 0;
        r1 = r9;
        r2 = r14;
        r3 = r15;
        r4 = r16;
        r7 = r17;
        r8 = r0.query(r1, r2, r3, r4, r5, r6, r7);
        if (r8 == 0) goto L_0x0033;
    L_0x00be:
        r1 = r12.contentResolver;
        r8.setNotificationUri(r1, r13);
        goto L_0x0033;
    L_0x00c5:
        r1 = "events";
        r0.setTables(r1);
        goto L_0x0090;
    L_0x00cc:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "params._id=";
        r1 = r1.append(r2);
        r2 = r13.getLastPathSegment();
        r1 = r1.append(r2);
        r1 = r1.toString();
        r0.appendWhere(r1);
    L_0x00e7:
        r1 = "params";
        r0.setTables(r1);
        goto L_0x0090;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.statistics.local.provider.StatisticsProvider.query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String):android.database.Cursor");
    }

    private static boolean needJoinEventsOnParams(String[] eventsProjection) {
        if (eventsProjection == null) {
            return false;
        }
        for (String column : eventsProjection) {
            if (!StaticsticsContract.isEventColumn(column)) {
                return true;
            }
        }
        return false;
    }

    public String getType(Uri uri) {
        switch (this.uriMatcher.match(uri)) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return "vnd.android.cursor.item/ru.ok.android.stat_event";
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return "vnd.android.cursor.dir/ru.ok.android.stat_event";
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return "vnd.android.cursor.item/ru.ok.android.stat_event_params";
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return "vnd.android.cursor.dir/ru.ok.android.stat_event_params";
            default:
                return null;
        }
    }

    public Uri insert(Uri uri, ContentValues values) {
        String table;
        String nullColumnHack;
        Logger.m173d("uri=%s values=(%s)", uri, values);
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        ArrayList<ContentValues> paramsValues = null;
        boolean insertEventWithParams = false;
        switch (this.uriMatcher.match(uri)) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                table = "events";
                nullColumnHack = "event_name";
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                table = "params";
                nullColumnHack = "param_value";
                break;
            case Message.UUID_FIELD_NUMBER /*5*/:
                insertEventWithParams = true;
                table = "events";
                nullColumnHack = "event_name";
                ContentValues eventValues = new ContentValues();
                paramsValues = new ArrayList();
                separateEventAndParams(values, eventValues, paramsValues);
                values = eventValues;
                break;
            default:
                throw new IllegalArgumentException("Insert not supported for uri: " + uri);
        }
        long rowId = db.insert(table, nullColumnHack, values);
        Uri rowUri = null;
        if (rowId != -1) {
            rowUri = ContentUris.withAppendedId(uri, rowId);
            Logger.m173d("Inserted new row: %s", rowUri);
            if (insertEventWithParams) {
                insertParams(db, rowId, paramsValues);
            }
            this.contentResolver.notifyChange(rowUri, null);
        } else {
            Logger.m184w("Insert failed");
        }
        return rowUri;
    }

    private static void separateEventAndParams(ContentValues inValues, ContentValues outEventValues, ArrayList<ContentValues> outParamsValues) {
        outEventValues.putAll(inValues);
        for (Entry<String, Object> entry : inValues.valueSet()) {
            String key = (String) entry.getKey();
            if (!StaticsticsContract.isEventColumn(key)) {
                outEventValues.remove(key);
                ContentValues paramRow = new ContentValues();
                paramRow.put("param_name", key);
                paramRow.put("param_value", String.valueOf(entry.getValue()));
                outParamsValues.add(paramRow);
            }
        }
    }

    private static void insertParams(SQLiteDatabase db, long eventId, ArrayList<ContentValues> paramsValues) {
        if (paramsValues != null && paramsValues.size() > 0) {
            Iterator i$ = paramsValues.iterator();
            while (i$.hasNext()) {
                ContentValues paramRow = (ContentValues) i$.next();
                paramRow.put("event_id", Long.valueOf(eventId));
                if (db.insert("params", "param_value", paramRow) != -1) {
                    Logger.m173d("Inserted event param: (%s)", paramRow);
                } else {
                    Logger.m185w("Failed to insert event param: (%s)", paramRow);
                }
            }
        }
    }

    public int bulkInsert(Uri uri, ContentValues[] values) {
        Logger.m173d("uri=%s values size=(%d)", uri, Integer.valueOf(values == null ? 0 : values.length));
        if ((values == null ? 0 : values.length) == 0) {
            return 0;
        }
        String table;
        String nullColumnHack;
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        boolean insertEventsAndParams = false;
        switch (this.uriMatcher.match(uri)) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                table = "params";
                nullColumnHack = "param_value";
                break;
            case Message.UUID_FIELD_NUMBER /*5*/:
                insertEventsAndParams = true;
                break;
            default:
                throw new IllegalArgumentException("Insert not supported for uri: " + uri);
        }
        table = "events";
        nullColumnHack = "event_name";
        int insertedCount = 0;
        ArrayList<ContentValues> paramValues = insertEventsAndParams ? new ArrayList() : null;
        for (ContentValues row : values) {
            ContentValues row2;
            if (insertEventsAndParams) {
                ContentValues eventValues = new ContentValues();
                paramValues.clear();
                separateEventAndParams(row2, eventValues, paramValues);
                row2 = eventValues;
            }
            long rowId = db.insert(table, nullColumnHack, row2);
            if (rowId != -1) {
                if (insertEventsAndParams) {
                    insertParams(db, rowId, paramValues);
                }
                Logger.m173d("Inserted row: (%s)", row2);
                insertedCount++;
            } else {
                Logger.m185w("Failed to insert row: (%s)", row2);
            }
        }
        Logger.m173d("Inserted %d rows", Integer.valueOf(insertedCount));
        if (insertedCount <= 0) {
            return insertedCount;
        }
        this.contentResolver.notifyChange(uri, null);
        return insertedCount;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int delete(android.net.Uri r11, java.lang.String r12, java.lang.String[] r13) {
        /*
        r10 = this;
        r9 = 1;
        r8 = 0;
        r5 = "uri=%s selection=%s selectionArgs=%s";
        r4 = 3;
        r6 = new java.lang.Object[r4];
        r6[r8] = r11;
        r6[r9] = r12;
        r7 = 2;
        r4 = ru.ok.android.utils.Logger.isLoggingEnable();
        if (r4 == 0) goto L_0x0065;
    L_0x0013:
        r4 = java.util.Arrays.toString(r13);
    L_0x0017:
        r6[r7] = r4;
        ru.ok.android.utils.Logger.m173d(r5, r6);
        r4 = r10.dbHelper;
        r0 = r4.getWritableDatabase();
        r2 = 0;
        r3 = 0;
        r4 = r10.uriMatcher;
        r4 = r4.match(r11);
        switch(r4) {
            case 1: goto L_0x0069;
            case 2: goto L_0x006d;
            case 3: goto L_0x0071;
            case 4: goto L_0x0075;
            default: goto L_0x002d;
        };
    L_0x002d:
        if (r2 == 0) goto L_0x0045;
    L_0x002f:
        if (r12 != 0) goto L_0x0079;
    L_0x0031:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "_id=";
        r4 = r4.append(r5);
        r4 = r4.append(r2);
        r12 = r4.toString();
    L_0x0045:
        if (r12 != 0) goto L_0x004a;
    L_0x0047:
        r12 = "1";
    L_0x004a:
        r1 = r0.delete(r3, r12, r13);
        r4 = "Deleted %d rows";
        r5 = new java.lang.Object[r9];
        r6 = java.lang.Integer.valueOf(r1);
        r5[r8] = r6;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        if (r1 <= 0) goto L_0x0064;
    L_0x005e:
        r4 = r10.contentResolver;
        r5 = 0;
        r4.notifyChange(r11, r5);
    L_0x0064:
        return r1;
    L_0x0065:
        r4 = "";
        goto L_0x0017;
    L_0x0069:
        r2 = r11.getLastPathSegment();
    L_0x006d:
        r3 = "events";
        goto L_0x002d;
    L_0x0071:
        r2 = r11.getLastPathSegment();
    L_0x0075:
        r3 = "params";
        goto L_0x002d;
    L_0x0079:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "(_id=";
        r4 = r4.append(r5);
        r4 = r4.append(r2);
        r5 = ") AND (";
        r4 = r4.append(r5);
        r4 = r4.append(r12);
        r5 = ")";
        r4 = r4.append(r5);
        r12 = r4.toString();
        goto L_0x0045;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.statistics.local.provider.StatisticsProvider.delete(android.net.Uri, java.lang.String, java.lang.String[]):int");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int update(android.net.Uri r11, android.content.ContentValues r12, java.lang.String r13, java.lang.String[] r14) {
        /*
        r10 = this;
        r9 = 1;
        r8 = 0;
        r5 = "uri=%s selection=%s selectionArgs=%s values=(%s)";
        r4 = 4;
        r6 = new java.lang.Object[r4];
        r6[r8] = r11;
        r6[r9] = r13;
        r7 = 2;
        r4 = ru.ok.android.utils.Logger.isLoggingEnable();
        if (r4 == 0) goto L_0x0063;
    L_0x0013:
        r4 = java.util.Arrays.toString(r14);
    L_0x0017:
        r6[r7] = r4;
        r4 = 3;
        r6[r4] = r12;
        ru.ok.android.utils.Logger.m173d(r5, r6);
        r4 = r10.dbHelper;
        r1 = r4.getWritableDatabase();
        r2 = 0;
        r3 = 0;
        r4 = r10.uriMatcher;
        r4 = r4.match(r11);
        switch(r4) {
            case 1: goto L_0x0067;
            case 2: goto L_0x006b;
            case 3: goto L_0x006f;
            case 4: goto L_0x0073;
            default: goto L_0x0030;
        };
    L_0x0030:
        if (r2 == 0) goto L_0x0048;
    L_0x0032:
        if (r13 != 0) goto L_0x0077;
    L_0x0034:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "_id=";
        r4 = r4.append(r5);
        r4 = r4.append(r2);
        r13 = r4.toString();
    L_0x0048:
        r0 = r1.update(r3, r12, r13, r14);
        r4 = "Updated %d rows";
        r5 = new java.lang.Object[r9];
        r6 = java.lang.Integer.valueOf(r0);
        r5[r8] = r6;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        if (r0 <= 0) goto L_0x0062;
    L_0x005c:
        r4 = r10.contentResolver;
        r5 = 0;
        r4.notifyChange(r11, r5);
    L_0x0062:
        return r0;
    L_0x0063:
        r4 = "";
        goto L_0x0017;
    L_0x0067:
        r2 = r11.getLastPathSegment();
    L_0x006b:
        r3 = "events";
        goto L_0x0030;
    L_0x006f:
        r2 = r11.getLastPathSegment();
    L_0x0073:
        r3 = "params";
        goto L_0x0030;
    L_0x0077:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "(_id=";
        r4 = r4.append(r5);
        r4 = r4.append(r2);
        r5 = ") AND (";
        r4 = r4.append(r5);
        r4 = r4.append(r13);
        r5 = ")";
        r4 = r4.append(r5);
        r13 = r4.toString();
        goto L_0x0048;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.statistics.local.provider.StatisticsProvider.update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[]):int");
    }

    private SQLiteDatabase safeObtainReadableDB() {
        try {
            SQLiteDatabase db = this.dbHelper.getWritableDatabase();
            if (db != null) {
                return db;
            }
            Logger.m176e("Stats DB is null");
            return null;
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to obtain stats DB");
            return null;
        }
    }
}
