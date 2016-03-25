package ru.mail.android.mytracker.async.commands;

public interface AsyncCommand extends Runnable {
    ExecuteListener getExecuteListener();

    void setExecuteListener(ExecuteListener executeListener);
}
