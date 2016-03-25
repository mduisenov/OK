package ru.ok.android.utils;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {
    public static final int DEFAULT_THREAD_POOL_SIZE;
    public static final ExecutorService executorService;
    private static Handler sHandler;
    private static Executor singleThreadExecutor;

    static {
        DEFAULT_THREAD_POOL_SIZE = Math.min(4, Runtime.getRuntime().availableProcessors());
        executorService = new ThreadPoolExecutor(0, DEFAULT_THREAD_POOL_SIZE, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), new NamedThreadFactory("ThreadUtil"));
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public static void execute(Runnable runnable) {
        executorService.execute(runnable);
    }

    public static Handler getMainThreadHandler() {
        if (sHandler == null) {
            sHandler = new Handler(Looper.getMainLooper());
        }
        return sHandler;
    }

    public static void executeOnMain(Runnable toExecute) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            toExecute.run();
        } else {
            queueOnMain(toExecute, 0);
        }
    }

    public static void executeOnMainSync(Runnable toExecute) {
        ConditionVariable conditionVariable = new ConditionVariable();
        executeOnMain(new 1(toExecute, conditionVariable));
        conditionVariable.block();
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public static void queueOnMain(Runnable toExecute, long delayMillis) {
        getMainThreadHandler().postDelayed(toExecute, delayMillis);
    }

    public static void queueOnMain(Runnable toExecute) {
        getMainThreadHandler().post(toExecute);
    }

    public static Executor getSingleThreadExecutor() {
        return singleThreadExecutor;
    }
}
