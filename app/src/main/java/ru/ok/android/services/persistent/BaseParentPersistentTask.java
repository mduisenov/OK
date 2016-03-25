package ru.ok.android.services.persistent;

import android.os.Parcel;
import android.util.Pair;
import java.util.Map.Entry;

public abstract class BaseParentPersistentTask extends PersistentTask {
    private static final long serialVersionUID = 1;

    public abstract void createNotification(PersistentTaskContext persistentTaskContext, PersistentTask persistentTask, PersistentTaskNotificationBuilder persistentTaskNotificationBuilder);

    protected BaseParentPersistentTask(String uid, boolean isHidden) {
        super(uid, isHidden);
    }

    protected BaseParentPersistentTask(String uid, boolean isHidden, int parentId) {
        super(uid, isHidden, parentId);
    }

    protected BaseParentPersistentTask(Parcel src) {
        super(src);
    }

    protected Pair<Integer, PersistentTaskState> getActiveSubTaskState() {
        Entry<Integer, PersistentTaskState> activeSubTask = null;
        for (Entry<Integer, PersistentTaskState> subTask : this.subTasks.entrySet()) {
            if (((PersistentTaskState) subTask.getValue()) != PersistentTaskState.COMPLETED) {
                activeSubTask = subTask;
                break;
            }
        }
        if (activeSubTask == null) {
            return null;
        }
        return new Pair(activeSubTask.getKey(), activeSubTask.getValue());
    }

    public void createNotification(PersistentTaskContext persistentContext, PersistentTaskNotificationBuilder notificationBuilder) {
        PersistentTask activeSubTask = null;
        Pair<Integer, PersistentTaskState> activeSubTaskState = getActiveSubTaskState();
        if (activeSubTaskState != null) {
            activeSubTask = persistentContext.getTask(((Integer) activeSubTaskState.first).intValue());
        }
        createNotification(persistentContext, activeSubTask, notificationBuilder);
    }
}
