package ru.mail.libverify.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/* renamed from: ru.mail.libverify.utils.d */
public class C0204d {
    private static volatile LogReceiver f61a;
    private static volatile Context f62b;
    private static volatile LogReceiver f63c;

    static {
        f61a = null;
        f62b = null;
    }

    public static void m128a(@NonNull Context context, @Nullable LogReceiver logReceiver) {
        f62b = context;
        f63c = logReceiver;
    }

    public static void m129a(String str, String str2) {
        if (C0204d.m133a()) {
            C0204d.m134b().e(str, str2);
        }
    }

    public static void m130a(String str, String str2, Throwable th) {
        if (C0204d.m133a()) {
            C0204d.m134b().e(str, str2, th);
        }
    }

    public static void m131a(String str, String str2, Object... objArr) {
        if (C0204d.m133a()) {
            C0204d.m134b().e(str, String.format(str2, objArr));
        }
    }

    public static void m132a(String str, Throwable th, String str2, Object... objArr) {
        if (C0204d.m133a()) {
            C0204d.m134b().e(str, String.format(str2, objArr), th);
        }
    }

    private static boolean m133a() {
        return f63c != null;
    }

    private static LogReceiver m134b() {
        LogReceiver logReceiver = f61a;
        if (logReceiver == null) {
            synchronized (C0204d.class) {
                logReceiver = f61a;
                if (logReceiver == null && f63c != null) {
                    logReceiver = f63c;
                    f61a = logReceiver;
                }
            }
        }
        return logReceiver;
    }

    public static void m135b(String str, String str2) {
        if (C0204d.m133a()) {
            C0204d.m134b().d(str, str2);
        }
    }

    public static void m136b(String str, String str2, Throwable th) {
        if (C0204d.m133a()) {
            C0204d.m134b().d(str, str2, th);
        }
    }

    public static void m137b(String str, String str2, Object... objArr) {
        if (C0204d.m133a()) {
            C0204d.m134b().d(str, String.format(str2, objArr));
        }
    }

    public static void m138b(String str, Throwable th, String str2, Object... objArr) {
        if (C0204d.m133a()) {
            C0204d.m134b().d(str, String.format(str2, objArr), th);
        }
    }

    public static void m139c(String str, String str2) {
        if (C0204d.m133a()) {
            C0204d.m134b().v(str, str2);
        }
    }

    public static void m140c(String str, String str2, Throwable th) {
        if (C0204d.m133a()) {
            C0204d.m134b().v(str, str2, th);
        }
    }

    public static void m141c(String str, String str2, Object... objArr) {
        if (C0204d.m133a()) {
            C0204d.m134b().v(str, String.format(str2, objArr));
        }
    }
}
