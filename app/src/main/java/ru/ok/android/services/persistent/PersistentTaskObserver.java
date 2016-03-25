package ru.ok.android.services.persistent;

public abstract class PersistentTaskObserver {
    public final int taskId;

    protected abstract void onTaskUpdated(PersistentTask persistentTask);

    protected PersistentTaskObserver(int taskId) {
        this.taskId = taskId;
    }
}
