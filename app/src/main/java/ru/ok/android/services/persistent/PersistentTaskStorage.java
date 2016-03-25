package ru.ok.android.services.persistent;

import android.content.ContentProviderClient;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import ru.ok.android.services.persistent.provider.PersistentTasksContract.PersistentTasks;
import ru.ok.android.services.persistent.provider.PersistentTasksProvider;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.utils.Logger;
import ru.ok.model.messages.Attachment;

public class PersistentTaskStorage implements PersistentTaskQueue {
    private final ContentProviderClient contentProviderClient;
    private final Context context;
    private volatile boolean isReset;

    PersistentTaskStorage(Context context) {
        this.context = context;
        this.contentProviderClient = context.getContentResolver().acquireContentProviderClient("ru.ok.android.persistent_tasks");
        if (this.contentProviderClient == null) {
            Logger.m177e("Failed to acquire content ptovider for authority: %s", "ru.ok.android.persistent_tasks");
        }
    }

    public void dispose() {
        ContentProviderClient contentProviderClient = this.contentProviderClient;
        if (contentProviderClient != null) {
            contentProviderClient.release();
        }
    }

    public void reset() {
        this.isReset = true;
        PersistentTasksProvider.clearDB(this.context);
    }

    public int addToQueue(PersistentTask task) throws PersistentException {
        if (this.isReset) {
            throw new PersistentException("PersistentStorage is reset");
        }
        Logger.m173d("%s", task);
        ContentProviderClient contentProviderClient = this.contentProviderClient;
        if (contentProviderClient == null) {
            throw new PersistentException("Persistent storage is not initialized (contentProviderClient is null)");
        }
        try {
            int taskId = (int) ContentUris.parseId(contentProviderClient.insert(PersistentTasks.CONTENT_URI, toContentValues(task)));
            task.setId(taskId);
            return taskId;
        } catch (Exception e) {
            throw new PersistentException("Failed to insert task: " + e, e);
        }
    }

    public int addInFrontOfQueue(PersistentTask task) throws PersistentException {
        Logger.m173d("%s", task);
        return addToQueue(task);
    }

    public PersistentTask getTask(int id) throws PersistentException {
        return queryOneTask(ContentUris.withAppendedId(PersistentTasks.CONTENT_URI, (long) id), null, null, null);
    }

    public ArrayList<PersistentTask> getAllTasks() throws PersistentException {
        if (this.isReset) {
            return new ArrayList();
        }
        ContentProviderClient contentProviderClient = this.contentProviderClient;
        if (contentProviderClient == null) {
            throw new PersistentException("Persistent storage is not initialized (contentProviderClient is null)");
        }
        Cursor cursor = null;
        ArrayList<PersistentTask> tasks = new ArrayList();
        try {
            cursor = contentProviderClient.query(PersistentTasks.CONTENT_URI, PersistentTasks.DEFAULT_PROJECTION, null, null, PersistentTasks.DEFAULT_ORDER);
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    tasks.add(fromDefaultCursor(cursor));
                    cursor.moveToNext();
                }
            }
            if (cursor == null) {
                return tasks;
            }
            try {
                cursor.close();
                return tasks;
            } catch (Throwable th) {
                return tasks;
            }
        } catch (Throwable e) {
            Logger.m177e("Failed to query tasks: %s", e);
            Logger.m178e(e);
            throw new PersistentException("Failed to query tasks: " + e, e);
        } catch (Throwable th2) {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable th3) {
                }
            }
        }
    }

    public void update(PersistentTask task) throws PersistentException {
        if (!this.isReset) {
            ContentProviderClient contentProviderClient = this.contentProviderClient;
            if (contentProviderClient == null) {
                throw new PersistentException("Persistent storage is not initialized (contentProviderClient is null)");
            }
            try {
                contentProviderClient.update(ContentUris.withAppendedId(PersistentTasks.CONTENT_URI, (long) task.getId()), toContentValues(task), null, null);
            } catch (Throwable e) {
                Logger.m177e("Failed to update task: %s", e);
                Logger.m178e(e);
                throw new PersistentException("Failed to update task: " + e, e);
            }
        }
    }

    public void remove(PersistentTask task) throws PersistentException {
        if (!this.isReset) {
            ContentProviderClient contentProviderClient = this.contentProviderClient;
            if (contentProviderClient == null) {
                throw new PersistentException("Persistent storage is not initialized (contentProviderClient is null)");
            }
            try {
                contentProviderClient.delete(ContentUris.withAppendedId(PersistentTasks.CONTENT_URI, (long) task.getId()), null, null);
            } catch (Throwable e) {
                Logger.m177e("Failed to delete task: %s", e);
                Logger.m178e(e);
                throw new PersistentException("Failed to delete task: " + e, e);
            }
        }
    }

    public PersistentTask firstNotCompleted(String uid) throws PersistentException {
        return queryOneTask(PersistentTasks.getContentUriWithLimit(1), "state<>? AND uid=?", new String[]{PersistentTaskState.COMPLETED.name(), uid}, PersistentTasks.DEFAULT_ORDER);
    }

    private PersistentTask queryOneTask(Uri uri, String selection, String[] selectionArgs, String order) throws PersistentException {
        if (this.isReset) {
            return null;
        }
        ContentProviderClient contentProviderClient = this.contentProviderClient;
        if (contentProviderClient == null) {
            throw new PersistentException("Persistent storage is not initialized (contentProviderClient is null)");
        }
        Cursor cursor = null;
        try {
            cursor = contentProviderClient.query(uri, PersistentTasks.DEFAULT_PROJECTION, selection, selectionArgs, order);
            if (cursor == null || !cursor.moveToFirst()) {
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Throwable th) {
                    }
                }
                return null;
            }
            PersistentTask fromDefaultCursor = fromDefaultCursor(cursor);
            if (cursor == null) {
                return fromDefaultCursor;
            }
            try {
                cursor.close();
                return fromDefaultCursor;
            } catch (Throwable th2) {
                return fromDefaultCursor;
            }
        } catch (IOException ioE) {
            deleteTask(uri, selection, selectionArgs);
            Logger.m180e(ioE, "Failed to query task: %s", ioE);
            StatisticManager.getInstance().reportError("failed_query_upload_task_no_block", "IOException", ioE);
            throw new PersistentException("Failed to query task(IO exception): " + ioE, ioE);
        } catch (ClassNotFoundException cnfE) {
            deleteTask(uri, selection, selectionArgs);
            Logger.m180e(cnfE, "Failed to query task: %s", cnfE);
            StatisticManager.getInstance().reportError("failed_query_upload_task_no_block", "ClassNotFoundException exception", cnfE);
            throw new PersistentException("Failed to query task(ClassNotFoundException exception): " + cnfE, cnfE);
        } catch (Exception e) {
            Logger.m180e(e, "Failed to query task: %s", e);
            StatisticManager.getInstance().reportError("failed_query_upload_task", "inexplicable exception", e);
            throw new PersistentException("Failed to query task: " + e, e);
        } catch (Throwable th3) {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable th4) {
                }
            }
        }
    }

    private void deleteTask(Uri url, String selection, String[] selectionArgs) {
        if (!this.isReset) {
            try {
                this.contentProviderClient.delete(url, selection, selectionArgs);
            } catch (Throwable e) {
                Logger.m177e("Failed delete task: %s", e);
                Logger.m178e(e);
            }
        }
    }

    private PersistentTask fromDefaultCursor(Cursor cursor) throws IOException, ClassNotFoundException {
        byte[] data = cursor.getBlob(3);
        PersistentTask task = (PersistentTask) new ReplaceObjectInputStream(new ByteArrayInputStream(data), Attachment.class.getClassLoader()).readObject();
        task.setId(cursor.getInt(0));
        return task;
    }

    private static ContentValues toContentValues(PersistentTask task) {
        ContentValues values = new ContentValues();
        int parentTaskId = task.getParentId();
        if (parentTaskId != 0) {
            values.put("parent_id", Integer.valueOf(parentTaskId));
        }
        values.put("state", task.getState().name());
        values.put("hidden", Integer.valueOf(task.isHidden() ? 1 : 0));
        values.put("data", serialize(task));
        values.put("uid", task.getUid());
        return values;
    }

    private static byte[] serialize(PersistentTask task) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(task);
            out.flush();
            out.close();
            return baos.toByteArray();
        } catch (Throwable e) {
            Logger.m177e("Failed to serialize task: %s", e);
            Logger.m178e(e);
            return new byte[0];
        }
    }
}
