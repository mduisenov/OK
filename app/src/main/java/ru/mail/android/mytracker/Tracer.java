package ru.mail.android.mytracker;

import android.util.Log;

public class Tracer {
    private static final String TAG = "[myTracker]";
    static volatile boolean enabled;
    static LogHandler logHandler;

    static {
        enabled = false;
    }

    public static void m38d(String msg) {
        if (!enabled) {
            return;
        }
        if (logHandler != null) {
            LogHandler logHandler = logHandler;
            if (msg == null) {
                msg = "null";
            }
            logHandler.d(msg);
            return;
        }
        String str = TAG;
        if (msg == null) {
            msg = "null";
        }
        Log.d(str, msg);
    }

    public static void m41i(String msg) {
        if (logHandler != null) {
            LogHandler logHandler = logHandler;
            if (msg == null) {
                msg = "null";
            }
            logHandler.i(msg);
            return;
        }
        String str = TAG;
        if (msg == null) {
            msg = "null";
        }
        Log.i(str, msg);
    }

    public static void m39e(String msg) {
        if (!enabled) {
            return;
        }
        if (logHandler != null) {
            LogHandler logHandler = logHandler;
            if (msg == null) {
                msg = "null";
            }
            logHandler.e(msg);
            return;
        }
        String str = TAG;
        if (msg == null) {
            msg = "null";
        }
        Log.e(str, msg);
    }

    public static void m40e(String msg, Throwable tr) {
        if (!enabled) {
            return;
        }
        if (logHandler != null) {
            LogHandler logHandler = logHandler;
            if (msg == null) {
                msg = "null";
            }
            logHandler.e(msg, tr);
            return;
        }
        String str = TAG;
        if (msg == null) {
            msg = "null";
        }
        Log.e(str, msg, tr);
    }

    private Tracer() {
    }
}
