package ru.ok.android.statistics.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import ru.ok.android.statistics.StatisticAgent;
import ru.ok.android.statistics.local.provider.StaticsticsContract.Events;
import ru.ok.android.utils.Logger;

public class LocalStatisticAgent implements StatisticAgent {
    ContentResolver contentResolver;
    private volatile boolean isRunning;
    private final Object startStopLock;
    WorkerHandler workerHandler;

    static final class EventMessage {
        private static EventMessage sPool;
        private static int sPoolSize;
        private static final Object sPoolSync;
        String name;
        private EventMessage next;
        Pair<String, String>[] params;
        long ts;

        static EventMessage obtain(String name, long ts, Pair<String, String>[] params) {
            EventMessage m = obtain();
            m.name = name;
            m.ts = ts;
            m.params = params;
            return m;
        }

        static EventMessage obtain() {
            synchronized (sPoolSync) {
                if (sPool != null) {
                    EventMessage m = sPool;
                    sPool = m.next;
                    m.next = null;
                    sPoolSize--;
                    return m;
                }
                return new EventMessage();
            }
        }

        void recycle() {
            clearForRecycle();
            synchronized (sPoolSync) {
                if (sPoolSize < 10) {
                    this.next = sPool;
                    sPool = this;
                    sPoolSize++;
                }
            }
        }

        private void clearForRecycle() {
            this.name = null;
            this.ts = 0;
            this.params = null;
        }

        private EventMessage() {
        }

        static {
            sPoolSync = new Object();
            sPoolSize = 0;
        }
    }

    class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                EventMessage m = msg.obj;
                LocalStatisticAgent.this.doAddEvent(m.name, m.ts, m.params);
                m.recycle();
            }
        }

        void postAddEvent(String name, Pair<String, String>[] params) {
            sendMessage(Message.obtain(this, 1, EventMessage.obtain(name, System.currentTimeMillis(), params)));
        }
    }

    public LocalStatisticAgent() {
        this.isRunning = false;
        this.startStopLock = new Object();
    }

    private void start(Context context) {
        if (context != null) {
            this.contentResolver = context.getContentResolver();
            HandlerThread workerThread = new HandlerThread("LocalStat", 1);
            workerThread.start();
            this.workerHandler = new WorkerHandler(workerThread.getLooper());
        }
    }

    public void startSession(Context context) {
        if (!this.isRunning) {
            synchronized (this.startStopLock) {
                if (!this.isRunning) {
                    start(context);
                }
                this.isRunning = true;
            }
        }
    }

    public void endSession(Context context) {
    }

    public void addEvent(String name, Pair<String, String>[] params) {
        Logger.m173d("name=%s", name);
        WorkerHandler wh = this.workerHandler;
        if (wh != null) {
            wh.postAddEvent(name, params);
        } else {
            Logger.m184w("LocalStatisticAgent not running!");
        }
    }

    private void doAddEvent(String name, long ts, Pair<String, String>[] params) {
        Logger.m173d("name=%s", name);
        ContentResolver cr = this.contentResolver;
        if (cr == null) {
            Logger.m184w("LocalStatisticAgent not running!");
            return;
        }
        boolean hasParams = params != null && params.length > 0;
        Uri insertUri = hasParams ? Events.INSERT_WITH_PARAMS_URI : Events.CONTENT_URI;
        ContentValues values = new ContentValues();
        values.put("event_name", name);
        values.put("event_ts", Long.valueOf(ts));
        if (hasParams) {
            for (Pair<String, String> param : params) {
                values.put((String) param.first, (String) param.second);
            }
        }
        try {
            if (cr.insert(insertUri, values) == null) {
                Logger.m184w("Event was not stored!");
            }
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to store event");
        }
    }

    public void setUserId(String userId) {
    }

    public void reportError(String errorId, String message, Throwable cause) {
    }
}
