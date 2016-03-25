package ru.ok.android.utils;

import java.lang.Thread.UncaughtExceptionHandler;

public abstract class ChainedUncaughtExceptionHandler implements UncaughtExceptionHandler {
    private final UncaughtExceptionHandler nextHandler;

    protected abstract void handleUncaughtException(Thread thread, Throwable th);

    protected ChainedUncaughtExceptionHandler() {
        this.nextHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public final void uncaughtException(Thread thread, Throwable ex) {
        handleUncaughtException(thread, ex);
        if (this.nextHandler != null) {
            this.nextHandler.uncaughtException(thread, ex);
        }
    }
}
