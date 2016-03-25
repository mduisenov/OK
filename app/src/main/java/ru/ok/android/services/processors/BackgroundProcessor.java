package ru.ok.android.services.processors;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import ru.ok.android.utils.NamedThreadFactory;
import ru.ok.android.utils.ThreadUtil;

public abstract class BackgroundProcessor {
    private static final Executor executorService;

    static {
        executorService = new ThreadPoolExecutor(0, ThreadUtil.DEFAULT_THREAD_POOL_SIZE, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), new NamedThreadFactory("HandleProcessor"));
    }

    protected void doAsync(Runnable runnable) {
        executorService.execute(runnable);
    }
}
