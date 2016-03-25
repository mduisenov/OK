package ru.mail.libverify.utils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import java.util.concurrent.ConcurrentHashMap;

public class VerificationService extends IntentService {
    private static final ConcurrentHashMap<Object, Object> f59a;
    private final long f60b;

    static {
        f59a = new ConcurrentHashMap();
    }

    public VerificationService() {
        super("VerificationService");
        setIntentRedelivery(true);
        this.f60b = System.currentTimeMillis();
    }

    public static void m123a() {
        C0204d.m139c("VerificationService", "releaseAll");
        f59a.clear();
        synchronized (f59a) {
            f59a.notify();
        }
    }

    public static void m124a(@NonNull Context context, @NonNull Object obj) {
        C0204d.m139c("VerificationService", "acquire " + obj);
        if (!f59a.containsKey(obj)) {
            f59a.put(obj, obj);
            context.startService(new Intent(context, VerificationService.class));
        }
    }

    public static void m125a(@NonNull Object obj) {
        C0204d.m139c("VerificationService", "release " + obj);
        if (f59a.remove(obj) != null && f59a.size() == 0) {
            synchronized (f59a) {
                f59a.notify();
            }
        }
    }

    public void onDestroy() {
        C0204d.m139c("VerificationService", "service destroyed");
        f59a.clear();
        super.onDestroy();
    }

    protected void onHandleIntent(Intent intent) {
        C0204d.m139c("VerificationService", "onHandleIntent started");
        while (f59a.size() > 0) {
            C0204d.m139c("VerificationService", "onHandleIntent wait loop " + Integer.toString(f59a.size()));
            try {
                synchronized (f59a) {
                    f59a.wait(60000);
                }
                long currentTimeMillis = System.currentTimeMillis() - this.f60b;
                if (currentTimeMillis >= 0) {
                    if (currentTimeMillis > 1800000) {
                    }
                }
                C0204d.m139c("VerificationService", "onHandleIntent wait for keep alive operation expired");
                break;
            } catch (Throwable e) {
                C0204d.m130a("VerificationService", "onHandleIntent wait failed", e);
            }
        }
        C0204d.m139c("VerificationService", "onHandleIntent completed");
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        C0204d.m139c("VerificationService", "onStartCommand");
        return super.onStartCommand(intent, i, i2);
    }
}
