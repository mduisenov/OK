package ru.mail.android.mytarget.core.async;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import java.lang.ref.WeakReference;
import ru.mail.android.mytarget.core.async.Request.ExecuteListener;

public abstract class AbstractRequest implements Request {
    private Runnable executeRunnable;
    private int failExecutions;
    private Handler handler;
    private boolean isSuccess;
    private WeakReference<ExecuteListener> listenerWeakReference;
    private int repeatsOnFail;
    private int successExecutions;
    private int totalExecutions;

    public AbstractRequest() {
        this.totalExecutions = 0;
        this.successExecutions = 0;
        this.failExecutions = 0;
        this.repeatsOnFail = 0;
        this.handler = new Handler(Looper.getMainLooper());
        this.executeRunnable = new 1(this);
    }

    public int getTotalExecutions() {
        return this.totalExecutions;
    }

    public int getFailExecutions() {
        return this.failExecutions;
    }

    public int getSuccessExecutions() {
        return this.successExecutions;
    }

    public boolean isSuccess() {
        return this.isSuccess;
    }

    public int getRepeatsOnFail() {
        return this.repeatsOnFail;
    }

    public void setRepeatsOnFail(int repeatsOnFail) {
        this.repeatsOnFail = repeatsOnFail;
    }

    public synchronized ExecuteListener getExecuteListener() {
        ExecuteListener executeListener;
        if (this.listenerWeakReference != null) {
            executeListener = (ExecuteListener) this.listenerWeakReference.get();
        } else {
            executeListener = null;
        }
        return executeListener;
    }

    public synchronized void setExecuteListener(ExecuteListener executeListener) {
        this.listenerWeakReference = null;
        if (executeListener != null) {
            this.listenerWeakReference = new WeakReference(executeListener);
        }
    }

    public void execute(Context context) {
        this.isSuccess = false;
        this.totalExecutions++;
    }

    protected void onExecute(boolean success) {
        if (success) {
            this.successExecutions++;
        } else {
            this.failExecutions++;
        }
        this.isSuccess = success;
        synchronized (this) {
            if (!(this.listenerWeakReference == null || this.listenerWeakReference.get() == null)) {
                this.handler.post(this.executeRunnable);
            }
        }
    }
}
