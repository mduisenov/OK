package ru.ok.android.services.base;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ru.ok.android.utils.Logger;

public abstract class ThreadedService extends Service {
    private final ExecutorService executorService;
    private final Handler handler;
    private volatile int tasksCount;

    /* renamed from: ru.ok.android.services.base.ThreadedService.1 */
    class C04331 extends Handler {
        C04331() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Logger.m173d("Call stopSelf [%s]", ThreadedService.this.getClass().getSimpleName());
            ThreadedService.this.stopSelf();
        }
    }

    /* renamed from: ru.ok.android.services.base.ThreadedService.2 */
    class C04342 implements Runnable {
        final /* synthetic */ Intent val$intent;

        C04342(Intent intent) {
            this.val$intent = intent;
        }

        public void run() {
            try {
                ThreadedService.this.onHandleIntent(this.val$intent);
                synchronized (ThreadedService.this) {
                    if (ThreadedService.access$006(ThreadedService.this) <= 0) {
                        ThreadedService.this.handler.sendEmptyMessageDelayed(0, 5000);
                    }
                }
            } catch (Throwable th) {
                synchronized (ThreadedService.this) {
                }
                if (ThreadedService.access$006(ThreadedService.this) <= 0) {
                    ThreadedService.this.handler.sendEmptyMessageDelayed(0, 5000);
                }
            }
        }
    }

    protected abstract void onHandleIntent(Intent intent);

    public ThreadedService() {
        this.executorService = Executors.newCachedThreadPool();
        this.handler = new C04331();
    }

    static /* synthetic */ int access$006(ThreadedService x0) {
        int i = x0.tasksCount - 1;
        x0.tasksCount = i;
        return i;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        synchronized (this) {
            this.tasksCount++;
            this.handler.removeMessages(0);
        }
        this.executorService.execute(new C04342(intent));
        return 2;
    }

    public void onDestroy() {
        super.onDestroy();
        Logger.m183v("[%s]", getClass().getSimpleName());
        this.executorService.shutdown();
    }
}
