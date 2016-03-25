package ru.ok.android.services.persistent;

import android.content.Context;

public interface PersistentTaskContext {
    void cancelSubTask(PersistentTask persistentTask);

    Context getContext();

    <T extends PersistentTask> T getTask(int i);

    void notifyOnChanged(PersistentTask persistentTask);

    void save(PersistentTask persistentTask);

    void scheduleRetry(PersistentTask persistentTask, long j);

    void submitSubTask(PersistentTask persistentTask);
}
