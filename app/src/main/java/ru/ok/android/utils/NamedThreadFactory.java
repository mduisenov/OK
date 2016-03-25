package ru.ok.android.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
    private final String mBaseName;
    private final AtomicInteger mCount;
    private final ThreadFactory mDefaultThreadFactory;

    public NamedThreadFactory(String baseName) {
        this.mCount = new AtomicInteger(0);
        this.mDefaultThreadFactory = Executors.defaultThreadFactory();
        this.mBaseName = baseName;
    }

    public Thread newThread(Runnable runnable) {
        Thread thread = this.mDefaultThreadFactory.newThread(runnable);
        thread.setName(this.mBaseName + "-" + this.mCount.getAndIncrement());
        return thread;
    }
}
