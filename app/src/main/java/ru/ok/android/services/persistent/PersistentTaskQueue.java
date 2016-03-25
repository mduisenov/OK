package ru.ok.android.services.persistent;

import java.util.ArrayList;

interface PersistentTaskQueue {
    int addInFrontOfQueue(PersistentTask persistentTask) throws PersistentException;

    int addToQueue(PersistentTask persistentTask) throws PersistentException;

    void dispose();

    PersistentTask firstNotCompleted(String str) throws PersistentException;

    ArrayList<PersistentTask> getAllTasks() throws PersistentException;

    PersistentTask getTask(int i) throws PersistentException;

    void remove(PersistentTask persistentTask) throws PersistentException;

    void reset();

    void update(PersistentTask persistentTask) throws PersistentException;
}
