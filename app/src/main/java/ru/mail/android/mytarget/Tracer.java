package ru.mail.android.mytarget;

import android.util.Log;

public class Tracer {
    private static final String TAG = "[myTarget]";
    public static boolean enabled;

    static {
        enabled = false;
    }

    public static void m35d(String msg) {
        if (enabled) {
            String str = TAG;
            if (msg == null) {
                msg = "null";
            }
            Log.d(str, msg);
        }
    }

    public static void m36e(String msg) {
        if (enabled) {
            String str = TAG;
            if (msg == null) {
                msg = "null";
            }
            Log.e(str, msg);
        }
    }

    public static void m37i(String msg) {
        String str = TAG;
        if (msg == null) {
            msg = "null";
        }
        Log.i(str, msg);
    }

    private Tracer() {
    }
}
