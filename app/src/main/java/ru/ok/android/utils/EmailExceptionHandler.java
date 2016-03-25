package ru.ok.android.utils;

import android.content.Context;

public class EmailExceptionHandler extends ChainedUncaughtExceptionHandler {
    private final Context context;

    public EmailExceptionHandler(Context context) {
        this.context = context;
    }

    protected void handleUncaughtException(Thread thread, Throwable ex) {
    }
}
