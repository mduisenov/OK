package ru.mail.android.mytracker.async;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import ru.mail.android.mytracker.async.commands.AsyncCommand;

public class AsyncCommandExecutor {
    private static final AsyncCommandExecutor executor;
    private ScheduledExecutorService executorService;

    static {
        executor = new AsyncCommandExecutor();
    }

    public static AsyncCommandExecutor getExecutor() {
        return executor;
    }

    public AsyncCommandExecutor() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void execute(AsyncCommand command) {
        this.executorService.execute(command);
    }

    public Future<?> submit(AsyncCommand command) {
        return this.executorService.submit(command);
    }
}
