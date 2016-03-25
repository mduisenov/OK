package ru.ok.android.utils;

public class StackTraceUtils {
    public static boolean stackTraceContainsMethod(String method) {
        StackTraceElement[] e = Thread.currentThread().getStackTrace();
        for (StackTraceElement methodName : e) {
            if (methodName.getMethodName().equals(method)) {
                return true;
            }
        }
        return false;
    }
}
