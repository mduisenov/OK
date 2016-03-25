package ru.ok.android.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import ru.ok.android.utils.log.FileLogger;

public class Logger {
    public static final String METHOD_D = "d";
    public static final String METHOD_E = "e";
    public static final String METHOD_V = "v";
    public static final String METHOD_W = "w";
    private static volatile FileLogger fileLogger;
    private static volatile boolean logToFile;
    private static volatile boolean loggingEnable;

    static {
        loggingEnable = false;
        logToFile = false;
    }

    public static void m173d(String msg, Object... args) {
        if (isLoggingEnable()) {
            try {
                m172d(String.format(msg, args));
            } catch (Exception e) {
                m172d(msg);
            }
        }
    }

    public static void m177e(String msg, Object... args) {
        if (isLoggingEnable()) {
            try {
                m176e(String.format(msg, args));
            } catch (Exception e) {
                m176e(msg);
            }
        }
    }

    public static void m180e(Throwable e, String msg, Object... args) {
        if (isLoggingEnable()) {
            try {
                m179e(e, String.format(msg, args));
            } catch (Exception e2) {
                m179e(e, msg);
            }
        }
    }

    public static void m185w(String msg, Object... args) {
        if (isLoggingEnable()) {
            try {
                m184w(String.format(msg, args));
            } catch (Exception e) {
                m184w(msg);
            }
        }
    }

    public static void m187w(Throwable e, String msg, Object... args) {
        if (isLoggingEnable()) {
            try {
                m186w(e, String.format(msg, args));
            } catch (Exception e2) {
                m186w(e, msg);
            }
        }
    }

    public static void setLoggingEnabled(boolean isLoggingEnabled) {
        loggingEnable = isLoggingEnabled;
    }

    public static boolean isLoggingEnable() {
        return loggingEnable;
    }

    public static void setLogToFile(boolean logToFile, Context context) {
        synchronized (Logger.class) {
            logToFile = logToFile;
            if (logToFile && fileLogger == null) {
                fileLogger = FileLogger.from(context);
            } else if (!(logToFile || fileLogger == null)) {
                fileLogger = null;
            }
        }
    }

    public static boolean isLogToFile() {
        return logToFile;
    }

    public static void m178e(Throwable e) {
        if (isLoggingEnable()) {
            m179e(e, "error");
        }
    }

    public static void m175e(int message) {
        if (isLoggingEnable()) {
            m179e(null, "int value = " + message);
        }
    }

    public static void m174e(float message) {
        if (isLoggingEnable()) {
            m179e(null, "float value = " + message);
        }
    }

    public static void m181e(boolean message) {
        if (isLoggingEnable()) {
            m179e(null, "boolean value = " + message);
        }
    }

    public static void m176e(String message) {
        if (isLoggingEnable()) {
            m179e(null, message);
        }
    }

    public static void m172d(String message) {
        if (isLoggingEnable()) {
            String tag = extractClassName(METHOD_D);
            String msg = buildMessageString(METHOD_D, message);
            Log.d(tag, msg);
            if (isLogToFile()) {
                fileLogger.m188d(tag, msg);
            }
        }
    }

    public static void m184w(String message) {
        if (isLoggingEnable()) {
            String tag = extractClassName(METHOD_W);
            String msg = buildMessageString(METHOD_W, message);
            Log.w(tag, msg);
            if (isLogToFile()) {
                fileLogger.m191w(tag, msg);
            }
        }
    }

    public static void m186w(Throwable e, String message) {
        if (isLoggingEnable()) {
            String tag = extractClassName(METHOD_W);
            String msg = buildMessageString(METHOD_W, message);
            Log.w(tag, msg, e);
            if (isLogToFile()) {
                fileLogger.m192w(tag, msg, e);
            }
        }
    }

    public static void m179e(Throwable e, String message) {
        if (isLoggingEnable()) {
            String tag = extractClassName(METHOD_E);
            String msg = buildMessageString(METHOD_E, message);
            Log.e(tag, msg, e);
            if (isLogToFile()) {
                fileLogger.m189e(tag, msg, e);
            }
        }
    }

    public static void m182v(String message) {
        if (isLoggingEnable()) {
            String tag = extractClassName(METHOD_V);
            String msg = buildMessageString(METHOD_V, message);
            Log.v(tag, msg);
            if (isLogToFile()) {
                fileLogger.m190v(tag, msg);
            }
        }
    }

    public static void m183v(String msg, Object... args) {
        if (isLoggingEnable()) {
            try {
                m182v(String.format(msg, args));
            } catch (Exception e) {
                m182v(msg);
            }
        }
    }

    public static String logSecret(String secret) {
        String encodedSecret = "";
        if (!isLoggingEnable()) {
            return encodedSecret;
        }
        if (TextUtils.isEmpty(secret)) {
            return "<>";
        }
        return "<" + CommonUtils.md5(secret) + ">";
    }

    private static String extractClassName(String methodName) {
        return trace(methodName).getClassName();
    }

    private static String buildMessageString(String methodName, String message) {
        StackTraceElement element = trace(methodName);
        return element.getMethodName() + " (" + element.getLineNumber() + "): " + message;
    }

    private static StackTraceElement trace(String method) {
        StackTraceElement[] e = Thread.currentThread().getStackTrace();
        boolean methodFound = false;
        int i = 0;
        while (i < e.length) {
            boolean isNamesEquals = e[i].getMethodName().equals(method);
            if (methodFound && !isNamesEquals) {
                break;
            }
            methodFound = isNamesEquals;
            i++;
        }
        return e[i];
    }
}
