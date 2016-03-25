package ru.ok.android.app;

import android.content.Context;
import java.lang.Thread.UncaughtExceptionHandler;
import ru.ok.android.utils.Utils;

public class VersionCodeCrushHandler implements UncaughtExceptionHandler {
    private static String cls;
    private static String file;
    private static String method;
    private static int versionCode;
    private final UncaughtExceptionHandler nextHandler;

    static {
        cls = "InfoAboutApp";
        method = "VersionCode";
        file = "numberVersionCode";
    }

    public VersionCodeCrushHandler(Context context) {
        this.nextHandler = Thread.getDefaultUncaughtExceptionHandler();
        versionCode = Utils.getVersionCode(context);
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        if (this.nextHandler != null) {
            ex.setStackTrace(addVersionCodeInfo(ex.getStackTrace(), versionCode));
            this.nextHandler.uncaughtException(thread, ex);
        }
    }

    public static StackTraceElement[] addVersionCodeInfo(StackTraceElement[] stackTrace, int versionCode) {
        int originalLength = stackTrace == null ? 0 : stackTrace.length;
        StackTraceElement[] newStackTrace = new StackTraceElement[(originalLength + 1)];
        newStackTrace[originalLength] = new StackTraceElement(cls, method, file, versionCode);
        if (stackTrace != null) {
            System.arraycopy(stackTrace, 0, newStackTrace, 0, originalLength);
        }
        return newStackTrace;
    }
}
