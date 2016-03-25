package ru.ok.android.services.persistent;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ParcelableUtils;

public abstract class PersistentTask implements Parcelable, Serializable {
    private static final long serialVersionUID = 0;
    private TaskException error;
    private int failureCount;
    private int id;
    private volatile boolean isCanceled;
    private final boolean isHidden;
    private volatile boolean isPausing;
    private int parentId;
    private PersistentTaskState state;
    final LinkedHashMap<Integer, PersistentTaskState> subTasks;
    private final String uid;

    public abstract PersistentTask copy();

    public abstract void createNotification(PersistentTaskContext persistentTaskContext, PersistentTaskNotificationBuilder persistentTaskNotificationBuilder);

    public abstract PersistentTaskState execute(PersistentTaskContext persistentTaskContext, Context context) throws TaskException;

    protected abstract PendingIntent getTaskDetailsIntent(PersistentTaskContext persistentTaskContext);

    protected PersistentTask(String uid, boolean isHidden) {
        this(uid, isHidden, 0);
    }

    protected PersistentTask(String uid, boolean isHidden, int parentId) {
        this.state = PersistentTaskState.SUBMITTED;
        this.uid = uid;
        this.isHidden = isHidden;
        this.parentId = parentId;
        this.subTasks = new LinkedHashMap();
    }

    protected void onPausing(PersistentTaskContext persistentContext) {
    }

    protected void onCancel(PersistentTaskContext persistentContext) {
    }

    protected void onStateChanged(PersistentTaskContext persistentContext) {
    }

    protected void onNewParams(PersistentTaskContext persistentContext, Bundle params) {
    }

    protected void onSubTaskCompleted(PersistentTaskContext persistentContext, PersistentTask subTask) {
    }

    protected void onSubTaskStateChanged(PersistentTaskContext persistentContext, PersistentTask subTask) {
    }

    public String getUid() {
        return this.uid;
    }

    public final boolean isHidden() {
        return this.isHidden;
    }

    public final int getId() {
        return this.id;
    }

    public final int getParentId() {
        return this.parentId;
    }

    protected void persist(PersistentTaskContext persistentContext) {
        persistentContext.save(this);
    }

    protected final int submitSubTask(PersistentTaskContext persistentContext, PersistentTask subTask) {
        if (subTask.getParentId() != this.id) {
            throw new IllegalArgumentException("Sub-task has wrong parent ID: " + subTask.getParentId() + ", expected " + this.id);
        }
        persistentContext.submitSubTask(subTask);
        int subTaskId = subTask.getId();
        this.subTasks.put(Integer.valueOf(subTaskId), PersistentTaskState.SUBMITTED);
        persist(persistentContext);
        return subTaskId;
    }

    protected <T extends PersistentTask> T getSubTask(PersistentTaskContext persistentContext, int subTaskId) {
        PersistentTask task = persistentContext.getTask(subTaskId);
        if (task == null || task.getParentId() != getId()) {
            return null;
        }
        return task;
    }

    final void setId(int id) {
        this.id = id;
    }

    public final PersistentTaskState getState() {
        return this.state;
    }

    public final boolean isPausing() {
        return this.isPausing;
    }

    public final boolean isCanceled() {
        return this.isCanceled;
    }

    public final int getFailureCount() {
        return this.failureCount;
    }

    public final int getFailureCountWithSubtasks(PersistentTaskContext persistentContext) {
        int totalFailureCount = getFailureCount();
        for (Integer intValue : getSubTaskIds()) {
            PersistentTask task = persistentContext.getTask(intValue.intValue());
            if (task != null) {
                totalFailureCount += task.getFailureCountWithSubtasks(persistentContext);
            }
        }
        return totalFailureCount;
    }

    public void setState(PersistentTaskContext persistentContext, PersistentTaskState state) {
        this.state = state;
        if (state == PersistentTaskState.PAUSED) {
            this.isPausing = false;
        }
        onStateChanged(persistentContext);
        persist(persistentContext);
        persistentContext.notifyOnChanged(this);
    }

    void setPausing(PersistentTaskContext persistentContext, boolean isPausing) {
        this.isPausing = isPausing;
        if (isPausing) {
            onPausing(persistentContext);
        }
    }

    void cancel(PersistentTaskContext persistentTaskContext) {
        this.isCanceled = true;
        this.isPausing = false;
        onCancel(persistentTaskContext);
    }

    protected void onSubTaskDeleted(int subTaskId) {
        Logger.m173d("subTaskId=%d this=%s", Integer.valueOf(subTaskId), this);
    }

    void setSubTaskState(int subTaskId, PersistentTaskState state) {
        this.subTasks.put(Integer.valueOf(subTaskId), state);
    }

    void detachFromParent(PersistentTaskContext persistentContext) {
        if (this.parentId != 0) {
            PersistentTask parent = persistentContext.getTask(this.parentId);
            if (parent != null) {
                parent.getSubTaskIds().remove(Integer.valueOf(getId()));
                parent.onSubTaskDeleted(getId());
            }
            this.parentId = 0;
        }
    }

    protected Collection<Integer> getSubTaskIds() {
        return this.subTasks.keySet();
    }

    protected final void setError(TaskException error) {
        this.error = error;
    }

    public final <T extends TaskException> T getError(Class<T> klass) {
        return klass.isInstance(this.error) ? this.error : null;
    }

    protected final void incrementFailureCount() {
        this.failureCount++;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        dest.writeString(this.uid);
        dest.writeInt(this.isHidden ? 1 : 0);
        dest.writeInt(this.id);
        dest.writeInt(this.parentId);
        ParcelableUtils.writeLinkedMap(this.subTasks, dest, flags);
        if (this.isPausing) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        dest.writeParcelable(this.state, flags);
        dest.writeInt(this.failureCount);
        dest.writeParcelable(this.error, flags);
        if (!this.isCanceled) {
            i2 = 0;
        }
        dest.writeInt(i2);
    }

    protected PersistentTask(Parcel src) {
        boolean z;
        boolean z2 = true;
        this.state = PersistentTaskState.SUBMITTED;
        ClassLoader cl = PersistentTask.class.getClassLoader();
        this.uid = src.readString();
        this.isHidden = src.readInt() != 0;
        this.id = src.readInt();
        this.parentId = src.readInt();
        this.subTasks = ParcelableUtils.readLinkedMap(src, cl);
        if (src.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.isPausing = z;
        this.state = (PersistentTaskState) src.readParcelable(cl);
        this.failureCount = src.readInt();
        this.error = (TaskException) src.readParcelable(cl);
        if (src.readInt() == 0) {
            z2 = false;
        }
        this.isCanceled = z2;
    }

    protected Parcel toParcel() {
        Parcel parcel = Parcel.obtain();
        writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        return parcel;
    }
}
