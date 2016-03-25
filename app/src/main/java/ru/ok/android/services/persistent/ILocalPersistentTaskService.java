package ru.ok.android.services.persistent;

public interface ILocalPersistentTaskService {
    PersistentTaskContext getPersistentContext();

    PersistentTask getTask(int i);

    void registerObserver(PersistentTaskObserver persistentTaskObserver);

    void resume(PersistentTask persistentTask);

    void unregisterObserver(PersistentTaskObserver persistentTaskObserver);

    void update(PersistentTask persistentTask);
}
