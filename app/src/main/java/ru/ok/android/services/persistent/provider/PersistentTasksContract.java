package ru.ok.android.services.persistent.provider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.model.UserInfo;

public final class PersistentTasksContract {

    public static final class PersistentTaskGroups implements BaseColumns {
        public static final Uri CONTENT_URI;

        static {
            CONTENT_URI = Uri.parse("content://ru.ok.android.persistent_tasks/task_groups");
        }
    }

    public static final class PersistentTasks implements BaseColumns {
        public static final Uri CONTENT_URI;
        public static final String DEFAULT_ORDER;
        public static final String[] DEFAULT_PROJECTION;
        private static final String EXPR_GROUP_ID_ERROR_PAUSED;
        private static final String EXPR_GROUP_ID_FAILED;
        private static final String EXPR_GROUP_ID_WAITING;
        public static final String ORDER_REJECTED_LAST;
        private static final String[] PROJECTION_COUNT;

        static {
            CONTENT_URI = Uri.parse("content://ru.ok.android.persistent_tasks/tasks");
            DEFAULT_PROJECTION = new String[]{"_id", "parent_id", "state", "data", "hidden", "topological_order", "group_id", "uid"};
            EXPR_GROUP_ID_WAITING = "group_id IN (SELECT DISTINCT group_id FROM tasks WHERE state='" + PersistentTaskState.WAIT.name() + "'" + ")";
            EXPR_GROUP_ID_ERROR_PAUSED = "group_id IN (SELECT DISTINCT group_id FROM tasks WHERE state='" + PersistentTaskState.ERROR.name() + "'" + " OR " + "state" + "='" + PersistentTaskState.PAUSED.name() + "'" + " )";
            EXPR_GROUP_ID_FAILED = "group_id IN (SELECT DISTINCT group_id FROM tasks WHERE state='" + PersistentTaskState.FAILED.name() + "'" + ")";
            ORDER_REJECTED_LAST = "CASE WHEN " + EXPR_GROUP_ID_WAITING + " THEN 3" + " WHEN " + EXPR_GROUP_ID_ERROR_PAUSED + " THEN 2" + " WHEN " + EXPR_GROUP_ID_FAILED + " THEN 1" + " ELSE 0" + " END ASC";
            DEFAULT_ORDER = ORDER_REJECTED_LAST + "," + "priority" + " ASC," + "topological_order" + " ASC," + "tasks" + "." + "_id" + " ASC";
            PROJECTION_COUNT = new String[]{"COUNT(*)"};
        }

        public static Uri getContentUriWithLimit(int limit) {
            return CONTENT_URI.buildUpon().appendQueryParameter("limit", Integer.toString(limit)).build();
        }

        public static int queryCurrentUserTasksCount(ContentResolver cr) {
            UserInfo currentUser = OdnoklassnikiApplication.getCurrentUser();
            if (currentUser == null) {
                return 0;
            }
            if (TextUtils.isEmpty(currentUser.uid)) {
                return 0;
            }
            Cursor cursor = null;
            int i;
            try {
                cursor = cr.query(CONTENT_URI, PROJECTION_COUNT, "parent_id IS NULL", null, null);
                if (cursor == null || !cursor.moveToFirst()) {
                    IOUtils.closeSilently(cursor);
                    return 0;
                }
                i = cursor.getInt(0);
                return i;
            } catch (Throwable e) {
                i = "Failed to query current user tasks count";
                Logger.m179e(e, (String) i);
            } finally {
                IOUtils.closeSilently(cursor);
            }
        }
    }
}
