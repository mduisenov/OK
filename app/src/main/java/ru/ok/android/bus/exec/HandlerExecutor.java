package ru.ok.android.bus.exec;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

public final class HandlerExecutor implements Executor {
    private static HandlerExecutor main;
    @NonNull
    private final Handler handler;

    public HandlerExecutor(@NonNull Looper looper) {
        this.handler = new Handler(looper);
    }

    @NonNull
    public static HandlerExecutor main() {
        if (main == null) {
            main = new HandlerExecutor(Looper.getMainLooper());
        }
        return main;
    }

    public void execute(@NonNull Runnable command) {
        this.handler.post(command);
    }
}
