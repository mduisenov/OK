package ru.ok.android.services.persistent.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import java.util.HashMap;
import java.util.Map;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.persistent.provider.PersistentTasksContract.PersistentTaskGroups;
import ru.ok.android.services.persistent.provider.PersistentTasksContract.PersistentTasks;
import ru.ok.android.utils.Logger;

public class PersistentTasksProvider extends ContentProvider {
    private static final String CREATE_TASKS_TABLE;
    private static final String[] PROJECTION_GROUP_ID_TOPOLOGICAL_ORDER;
    private static Map<String, String> tasksJoinGroupsProjectionMap;
    private ContentResolver contentResolver;
    private DBHelper dbHelper;
    private UriMatcher uriMatcher;

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, "persistent_tasks.db", null, 6);
        }

        public void onCreate(SQLiteDatabase db) {
            Logger.m172d(">>> creating tables...");
            Logger.m173d("Executing SQL: %s", "CREATE TABLE IF NOT EXISTS groups (_id INTEGER PRIMARY KEY REFERENCES tasks(_id) ON DELETE CASCADE,priority INTEGER NOT NULL)");
            db.execSQL("CREATE TABLE IF NOT EXISTS groups (_id INTEGER PRIMARY KEY REFERENCES tasks(_id) ON DELETE CASCADE,priority INTEGER NOT NULL)");
            Logger.m173d("Executing SQL: %s", PersistentTasksProvider.CREATE_TASKS_TABLE);
            db.execSQL(PersistentTasksProvider.CREATE_TASKS_TABLE);
            Logger.m172d("<<< created all tables");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Logger.m173d("oldVersion=%d newVersion=%d", Integer.valueOf(oldVersion), Integer.valueOf(newVersion));
            if (oldVersion < 6) {
                dropAllTables(db);
                onCreate(db);
            }
        }

        private void dropAllTables(SQLiteDatabase db) {
            Logger.m173d("Executing SQL: %s", "DROP TABLE tasks");
            db.execSQL("DROP TABLE tasks");
            Logger.m173d("Executing SQL: %s", "DROP TABLE groups");
            db.execSQL("DROP TABLE groups");
        }
    }

    public boolean onCreate() {
        Context context = getContext();
        this.contentResolver = context.getContentResolver();
        this.dbHelper = new DBHelper(context);
        this.uriMatcher = new UriMatcher(-1);
        this.uriMatcher.addURI("ru.ok.android.persistent_tasks", "tasks/#", 2);
        this.uriMatcher.addURI("ru.ok.android.persistent_tasks", "tasks", 1);
        this.uriMatcher.addURI("ru.ok.android.persistent_tasks", "task_groups/#", 3);
        this.uriMatcher.addURI("ru.ok.android.persistent_tasks", "task_groups", 4);
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.database.Cursor query(android.net.Uri r23, java.lang.String[] r24, java.lang.String r25, java.lang.String[] r26, java.lang.String r27) {
        /*
        r22 = this;
        r4 = "uri=%s projection=%s selection=%s selectionArgs=%s sortOrder=%s";
        r5 = 5;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r5[r6] = r23;
        r6 = 1;
        r5[r6] = r24;
        r6 = 2;
        r5[r6] = r26;
        r6 = 3;
        r5[r6] = r26;
        r6 = 4;
        r5[r6] = r27;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        r0 = r22;
        r4 = r0.dbHelper;
        r12 = r4.getWritableDatabase();
        r3 = new android.database.sqlite.SQLiteQueryBuilder;
        r3.<init>();
        r4 = "limit";
        r0 = r23;
        r10 = r0.getQueryParameter(r4);
        r4 = "limit=%s";
        r5 = 1;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r5[r6] = r10;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        r0 = r22;
        r4 = r0.uriMatcher;
        r0 = r23;
        r4 = r4.match(r0);
        switch(r4) {
            case 1: goto L_0x007e;
            case 2: goto L_0x0063;
            case 3: goto L_0x00fb;
            case 4: goto L_0x00e0;
            default: goto L_0x0047;
        };
    L_0x0047:
        r4 = new java.lang.IllegalArgumentException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Uri not supported: ";
        r5 = r5.append(r6);
        r0 = r23;
        r5 = r5.append(r0);
        r5 = r5.toString();
        r4.<init>(r5);
        throw r4;
    L_0x0063:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "tasks._id=";
        r4 = r4.append(r5);
        r5 = r23.getLastPathSegment();
        r4 = r4.append(r5);
        r4 = r4.toString();
        r3.appendWhere(r4);
    L_0x007e:
        if (r24 == 0) goto L_0x00d9;
    L_0x0080:
        r0 = r24;
        r1 = r25;
        r2 = r27;
        r4 = hasGroupsColumns(r0, r1, r2);
        if (r4 == 0) goto L_0x00d9;
    L_0x008c:
        r4 = "tasks JOIN groups ON group_id=groups._id";
        r3.setTables(r4);
        r4 = getTasksJoinGroupsProjectionMap();
        r3.setProjectionMap(r4);
    L_0x0099:
        r4 = ru.ok.android.utils.Logger.isLoggingEnable();
        if (r4 == 0) goto L_0x00b8;
    L_0x009f:
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r4 = r24;
        r5 = r25;
        r9 = r27;
        r21 = r3.buildQuery(r4, r5, r6, r7, r8, r9, r10);
        r4 = "Performing query: %s";
        r5 = 1;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r5[r6] = r21;
        ru.ok.android.utils.Logger.m173d(r4, r5);
    L_0x00b8:
        r16 = 0;
        r17 = 0;
        r11 = r3;
        r13 = r24;
        r14 = r25;
        r15 = r26;
        r18 = r27;
        r19 = r10;
        r20 = r11.query(r12, r13, r14, r15, r16, r17, r18, r19);
        if (r20 == 0) goto L_0x00d8;
    L_0x00cd:
        r0 = r22;
        r4 = r0.contentResolver;
        r0 = r20;
        r1 = r23;
        r0.setNotificationUri(r4, r1);
    L_0x00d8:
        return r20;
    L_0x00d9:
        r4 = "tasks";
        r3.setTables(r4);
        goto L_0x0099;
    L_0x00e0:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "_id=";
        r4 = r4.append(r5);
        r5 = r23.getLastPathSegment();
        r4 = r4.append(r5);
        r4 = r4.toString();
        r3.appendWhere(r4);
    L_0x00fb:
        r4 = "groups";
        r3.setTables(r4);
        goto L_0x0047;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.services.persistent.provider.PersistentTasksProvider.query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String):android.database.Cursor");
    }

    private static Map<String, String> getTasksJoinGroupsProjectionMap() {
        if (tasksJoinGroupsProjectionMap == null) {
            HashMap<String, String> map = new HashMap();
            map.put("_id", "tasks._id");
            map.put("uid", "uid");
            map.put("data", "data");
            map.put("group_id", "group_id");
            map.put("hidden", "hidden");
            map.put("parent_id", "parent_id");
            map.put("state", "state");
            map.put("topological_order", "topological_order");
            map.put("priority", "priority");
            tasksJoinGroupsProjectionMap = map;
        }
        return tasksJoinGroupsProjectionMap;
    }

    private static boolean hasGroupsColumns(String[] projection, String selection, String order) {
        if (projection == null) {
            return false;
        }
        for (String columnName : projection) {
            if ("priority".equals(columnName)) {
                return true;
            }
        }
        if (selection != null && selection.contains("priority")) {
            return true;
        }
        if (order == null || !order.contains("priority")) {
            return false;
        }
        return true;
    }

    public String getType(Uri uri) {
        switch (this.uriMatcher.match(uri)) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return "vnd.android.cursor.dir/ru.ok.android_tasks";
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return "vnd.android.cursor.item/ru.ok.android_tasks";
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return "vnd.android.cursor.dir/ru.ok.android_task_groups";
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return "vnd.android.cursor.item/ru.ok.android_task_groups";
            default:
                return null;
        }
    }

    public Uri insert(Uri uri, ContentValues values) {
        Logger.m173d("uri=%s values=%s", uri, values);
        switch (this.uriMatcher.match(uri)) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return insertTask(values);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                throw new UnsupportedOperationException("Direct insert of groups is not supported. New group is created when new root task is inserted");
            default:
                throw new IllegalArgumentException("Uri not supported for insert: " + uri);
        }
    }

    private Uri insertTask(ContentValues taskValues) {
        Throwable th;
        Long parentTaskId = taskValues.getAsLong("parent_id");
        Logger.m173d("isRootTask=%s values=%s", Boolean.valueOf(parentTaskId == null), taskValues);
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        long taskId = -1;
        Uri groupUri = null;
        ContentValues contentValues;
        if (parentTaskId == null) {
            db.beginTransaction();
            try {
                contentValues = new ContentValues(taskValues);
                try {
                    int priority;
                    if (contentValues.containsKey("priority")) {
                        priority = contentValues.getAsInteger("priority").intValue();
                        contentValues.remove("priority");
                    } else {
                        priority = 2;
                    }
                    contentValues.remove("group_id");
                    contentValues.put("topological_order", Integer.valueOf(10));
                    Logger.m173d("Inserting task values: %s", contentValues);
                    long rootTaskId = db.insert("tasks", "parent_id", contentValues);
                    if (rootTaskId != -1) {
                        ContentValues groupValues = new ContentValues();
                        groupValues.put("priority", Integer.valueOf(priority));
                        groupValues.put("_id", Long.valueOf(rootTaskId));
                        if (db.insert("groups", "priority", groupValues) != -1) {
                            Logger.m173d("Inserted group rowId=%d values=%s", Long.valueOf(db.insert("groups", "priority", groupValues)), groupValues);
                            taskValues = new ContentValues();
                            taskValues.put("group_id", Long.valueOf(rootTaskId));
                            if (db.update("tasks", taskValues, "_id=" + rootTaskId, null) == 1) {
                                db.setTransactionSuccessful();
                                taskId = rootTaskId;
                                groupUri = ContentUris.withAppendedId(PersistentTaskGroups.CONTENT_URI, rootTaskId);
                            } else {
                                Logger.m176e("Failed to update task with group_id");
                            }
                            db.endTransaction();
                        } else {
                            Logger.m176e("Insert of group failed");
                        }
                    }
                    taskValues = contentValues;
                    db.endTransaction();
                } catch (Throwable th2) {
                    th = th2;
                    taskValues = contentValues;
                    db.endTransaction();
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                db.endTransaction();
                throw th;
            }
        }
        contentValues = new ContentValues(taskValues);
        contentValues.remove("priority");
        dumpGroupIdAndTopologicalOrder(db, parentTaskId.intValue(), contentValues);
        contentValues.put("topological_order", Integer.valueOf(contentValues.getAsInteger("topological_order").intValue() - 1));
        Logger.m173d("inserting task values: %s", contentValues);
        taskId = db.insert("tasks", "parent_id", contentValues);
        taskValues = contentValues;
        if (taskId == -1) {
            Logger.m176e("Failed to insert.");
            return null;
        }
        Uri taskUri = ContentUris.withAppendedId(PersistentTasks.CONTENT_URI, taskId);
        this.contentResolver.notifyChange(taskUri, null);
        if (groupUri != null) {
            this.contentResolver.notifyChange(groupUri, null);
        }
        Logger.m173d("inserted task id=%d", Long.valueOf(taskId));
        return taskUri;
    }

    static {
        PROJECTION_GROUP_ID_TOPOLOGICAL_ORDER = new String[]{"group_id", "topological_order"};
        CREATE_TASKS_TABLE = "CREATE TABLE IF NOT EXISTS tasks (_id INTEGER PRIMARY KEY AUTOINCREMENT,uid TEXT NOT NULL,parent_id INTEGER REFERENCES tasks(_id) ON DELETE CASCADE,state TEXT NOT NULL DEFAULT " + PersistentTaskState.SUBMITTED + "," + "data" + " BLOB NOT NULL, " + "group_id" + " INTEGER REFERENCES " + "groups" + "(" + "_id" + ") ON DELETE CASCADE," + "topological_order" + " INTEGER NOT NULL, " + "hidden" + " INTEGER NOT NULL DEFAULT 0" + ")";
    }

    private void dumpGroupIdAndTopologicalOrder(SQLiteDatabase db, int taskId, ContentValues outValues) {
        Cursor cursor = null;
        try {
            String[] strArr = PROJECTION_GROUP_ID_TOPOLOGICAL_ORDER;
            String str = "_id=" + taskId;
            cursor = db.query("tasks", strArr, str, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                DatabaseUtils.cursorRowToContentValues(cursor, outValues);
            }
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable th) {
                }
            }
        } catch (Throwable e) {
            Logger.m177e("Failed to query group id: %s", e);
            Logger.m178e(e);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th2) {
        }
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        StringBuilder selectionBuilder = null;
        switch (this.uriMatcher.match(uri)) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                selectionBuilder = new StringBuilder();
                selectionBuilder.append('(').append("_id").append('=').append(uri.getLastPathSegment()).append(')');
                break;
            default:
                throw new IllegalArgumentException("Uri not supported for delete: " + uri);
        }
        String table = "tasks";
        if (selectionBuilder != null) {
            if (selection != null) {
                selectionBuilder.append(" AND (").append(selection).append(")");
            }
            selection = selectionBuilder.toString();
        }
        int deletedRowCount = this.dbHelper.getWritableDatabase().delete(table, selection, selectionArgs);
        Logger.m173d("Deleted %d rows", Integer.valueOf(deletedRowCount));
        if (deletedRowCount > 0) {
            this.contentResolver.notifyChange(uri, null);
        }
        return deletedRowCount;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int update(android.net.Uri r12, android.content.ContentValues r13, java.lang.String r14, java.lang.String[] r15) {
        /*
        r11 = this;
        r10 = 61;
        r9 = 40;
        r8 = 1;
        r7 = 0;
        r6 = 41;
        r3 = ">>> uri=%s values=%s selection=%s selectionArgs=%s";
        r4 = 4;
        r4 = new java.lang.Object[r4];
        r4[r7] = r12;
        r4[r8] = r13;
        r5 = 2;
        r4[r5] = r14;
        r5 = 3;
        r4[r5] = r15;
        ru.ok.android.utils.Logger.m173d(r3, r4);
        r1 = 0;
        r3 = r11.uriMatcher;
        r3 = r3.match(r12);
        switch(r3) {
            case 1: goto L_0x005e;
            case 2: goto L_0x003f;
            case 3: goto L_0x00b7;
            case 4: goto L_0x0098;
            default: goto L_0x0025;
        };
    L_0x0025:
        r3 = new java.lang.IllegalArgumentException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Uri not supported for update: ";
        r4 = r4.append(r5);
        r4 = r4.append(r12);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
    L_0x003f:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r3 = r1.append(r9);
        r4 = "_id";
        r3 = r3.append(r4);
        r3 = r3.append(r10);
        r4 = r12.getLastPathSegment();
        r3 = r3.append(r4);
        r3.append(r6);
    L_0x005e:
        r2 = "tasks";
    L_0x0061:
        if (r1 == 0) goto L_0x0077;
    L_0x0063:
        if (r14 == 0) goto L_0x0073;
    L_0x0065:
        r3 = " AND (";
        r3 = r1.append(r3);
        r3 = r3.append(r14);
        r3.append(r6);
    L_0x0073:
        r14 = r1.toString();
    L_0x0077:
        r3 = r11.dbHelper;
        r3 = r3.getWritableDatabase();
        r0 = r3.update(r2, r13, r14, r15);
        r3 = "Updated %d rows";
        r4 = new java.lang.Object[r8];
        r5 = java.lang.Integer.valueOf(r0);
        r4[r7] = r5;
        ru.ok.android.utils.Logger.m173d(r3, r4);
        if (r0 <= 0) goto L_0x0097;
    L_0x0091:
        r3 = r11.contentResolver;
        r4 = 0;
        r3.notifyChange(r12, r4);
    L_0x0097:
        return r0;
    L_0x0098:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r3 = r1.append(r9);
        r4 = "_id";
        r3 = r3.append(r4);
        r3 = r3.append(r10);
        r4 = r12.getLastPathSegment();
        r3 = r3.append(r4);
        r3.append(r6);
    L_0x00b7:
        r2 = "groups";
        goto L_0x0061;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.services.persistent.provider.PersistentTasksProvider.update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[]):int");
    }

    public static void clearDB(Context context) {
        Logger.m172d("");
        context.getContentResolver().delete(PersistentTasks.CONTENT_URI, null, null);
    }
}
