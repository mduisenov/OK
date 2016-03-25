package ru.ok.android.services.persistent;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Iterator;
import ru.ok.android.utils.Logger;

public class CachedPersistentTaskQueue implements PersistentTaskQueue {
    private final PersistentTaskQueue queue;
    private final SparseArray<PersistentTask> tasksById;

    public CachedPersistentTaskQueue(PersistentTaskQueue queue) {
        this.tasksById = new SparseArray();
        this.queue = queue;
        initCache(queue);
    }

    private void initCache(PersistentTaskQueue src) {
        this.tasksById.clear();
        try {
            Iterator i$ = src.getAllTasks().iterator();
            while (i$.hasNext()) {
                PersistentTask task = (PersistentTask) i$.next();
                this.tasksById.put(task.getId(), task);
            }
        } catch (Throwable e) {
            Logger.m177e("Failed to restore persistent queue: %s", e);
            Logger.m178e(e);
        }
    }

    public synchronized int addToQueue(PersistentTask task) throws PersistentException {
        int id;
        id = this.queue.addToQueue(task);
        this.tasksById.put(id, task);
        return id;
    }

    public synchronized int addInFrontOfQueue(PersistentTask task) throws PersistentException {
        int id;
        id = this.queue.addInFrontOfQueue(task);
        this.tasksById.put(id, task);
        return id;
    }

    public synchronized PersistentTask getTask(int id) throws PersistentException {
        PersistentTask task;
        task = (PersistentTask) this.tasksById.get(id);
        if (task == null) {
            task = this.queue.getTask(id);
            this.tasksById.put(id, task);
        }
        return task;
    }

    public synchronized ArrayList<PersistentTask> getAllTasks() throws PersistentException {
        return this.queue.getAllTasks();
    }

    public synchronized void update(PersistentTask task) throws PersistentException {
        this.queue.update(task);
        this.tasksById.put(task.getId(), task);
    }

    public synchronized void remove(PersistentTask task) throws PersistentException {
        this.queue.remove(task);
        this.tasksById.remove(task.getId());
    }

    public synchronized PersistentTask firstNotCompleted(String uid) throws PersistentException {
        PersistentTask task;
        task = this.queue.firstNotCompleted(uid);
        if (task != null) {
            this.tasksById.put(task.getId(), task);
        }
        return task;
    }

    public void dispose() {
        this.queue.dispose();
    }

    public synchronized void reset() {
        this.tasksById.clear();
        this.queue.reset();
    }
}
