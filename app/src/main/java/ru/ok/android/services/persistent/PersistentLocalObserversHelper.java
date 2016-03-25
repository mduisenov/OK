package ru.ok.android.services.persistent;

import android.util.SparseArray;
import java.util.Iterator;
import java.util.LinkedList;
import ru.ok.android.utils.Logger;

class PersistentLocalObserversHelper {
    private final SparseArray<LinkedList<PersistentTaskObserver>> observersByTaskId;

    PersistentLocalObserversHelper() {
        this.observersByTaskId = new SparseArray();
    }

    synchronized void registerObserver(PersistentTaskObserver observer) {
        LinkedList<PersistentTaskObserver> observers = getObservers(observer.taskId, true);
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    synchronized void unregisterObserver(PersistentTaskObserver observer) {
        LinkedList<PersistentTaskObserver> observers = getObservers(observer.taskId, false);
        if (observers != null && observers.remove(observer) && observers.isEmpty()) {
            this.observersByTaskId.remove(observer.taskId);
        }
    }

    synchronized void notifyTaskUpdated(PersistentTask task) {
        LinkedList<PersistentTaskObserver> observers = getObservers(task.getId(), false);
        if (!(observers == null || observers.isEmpty())) {
            Iterator i$ = new LinkedList(observers).iterator();
            while (i$.hasNext()) {
                try {
                    ((PersistentTaskObserver) i$.next()).onTaskUpdated(task);
                } catch (Throwable e) {
                    Logger.m176e("Observer failed with " + e);
                    Logger.m178e(e);
                }
            }
        }
    }

    private LinkedList<PersistentTaskObserver> getObservers(int taskId, boolean createIfNull) {
        LinkedList<PersistentTaskObserver> observers = (LinkedList) this.observersByTaskId.get(taskId);
        if (observers != null || !createIfNull) {
            return observers;
        }
        observers = new LinkedList();
        this.observersByTaskId.put(taskId, observers);
        return observers;
    }
}
