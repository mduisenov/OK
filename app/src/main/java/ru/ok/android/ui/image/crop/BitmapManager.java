package ru.ok.android.ui.image.crop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;
import java.io.FileDescriptor;
import java.util.WeakHashMap;

public class BitmapManager {
    private static BitmapManager sManager;
    private final WeakHashMap<Thread, ThreadStatus> mThreadStatus;

    private enum State {
        CANCEL,
        ALLOW
    }

    private static class ThreadStatus {
        public Options mOptions;
        public State mState;

        private ThreadStatus() {
            this.mState = State.ALLOW;
        }

        public String toString() {
            String s;
            if (this.mState == State.CANCEL) {
                s = "Cancel";
            } else if (this.mState == State.ALLOW) {
                s = "Allow";
            } else {
                s = "?";
            }
            return "thread state = " + s + ", options = " + this.mOptions;
        }
    }

    static {
        sManager = null;
    }

    private BitmapManager() {
        this.mThreadStatus = new WeakHashMap();
    }

    private synchronized ThreadStatus getOrCreateThreadStatus(Thread t) {
        ThreadStatus status;
        status = (ThreadStatus) this.mThreadStatus.get(t);
        if (status == null) {
            status = new ThreadStatus();
            this.mThreadStatus.put(t, status);
        }
        return status;
    }

    private synchronized void setDecodingOptions(Thread t, Options options) {
        getOrCreateThreadStatus(t).mOptions = options;
    }

    synchronized void removeDecodingOptions(Thread t) {
        ((ThreadStatus) this.mThreadStatus.get(t)).mOptions = null;
    }

    public synchronized boolean canThreadDecoding(Thread t) {
        boolean result = true;
        synchronized (this) {
            ThreadStatus status = (ThreadStatus) this.mThreadStatus.get(t);
            if (status != null) {
                if (status.mState == State.CANCEL) {
                    result = false;
                }
            }
        }
        return result;
    }

    public static synchronized BitmapManager instance() {
        BitmapManager bitmapManager;
        synchronized (BitmapManager.class) {
            if (sManager == null) {
                sManager = new BitmapManager();
            }
            bitmapManager = sManager;
        }
        return bitmapManager;
    }

    public Bitmap decodeFileDescriptor(FileDescriptor fd, Options options) {
        if (options.mCancel) {
            return null;
        }
        Thread thread = Thread.currentThread();
        if (canThreadDecoding(thread)) {
            setDecodingOptions(thread, options);
            Bitmap b = BitmapFactory.decodeFileDescriptor(fd, null, options);
            removeDecodingOptions(thread);
            return b;
        }
        Log.d("BitmapManager", "Thread " + thread + " is not allowed to decode.");
        return null;
    }
}
