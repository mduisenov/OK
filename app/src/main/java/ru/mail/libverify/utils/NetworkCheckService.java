package ru.mail.libverify.utils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

public class NetworkCheckService extends IntentService {
    private final long f46a;
    private final Object f47b;

    public NetworkCheckService() {
        super("NetworkCheckService");
        this.f47b = new Object();
        this.f46a = System.currentTimeMillis();
    }

    private long m111a() {
        long currentTimeMillis = System.currentTimeMillis() - this.f46a;
        return currentTimeMillis < 0 ? 0 : 600000 - currentTimeMillis;
    }

    public static void m112a(@NonNull Context context) {
        C0204d.m139c("NetworkCheckService", "start network checking");
        context.startService(new Intent(context, NetworkCheckService.class));
    }

    public void onDestroy() {
        C0204d.m139c("NetworkCheckService", "service destroyed");
        super.onDestroy();
    }

    protected void onHandleIntent(Intent intent) {
        C0204d.m139c("NetworkCheckService", "onHandleIntent started");
        long a = m111a();
        int i = 1;
        while (a > 0) {
            synchronized (this.f47b) {
                try {
                    C0204d.m141c("NetworkCheckService", "onHandleIntent on iteration = %d remaining time = %d", Integer.valueOf(i), Long.valueOf(a));
                    Object obj = this.f47b;
                    if (a < 0) {
                        a = 0;
                    } else {
                        long j = (long) ((i * i) * 200);
                        if (j <= a) {
                            a = j;
                        }
                    }
                    obj.wait(a);
                    NetworkStateReceiver.m117b(this);
                    if (NetworkStateReceiver.m115a()) {
                        C0204d.m139c("NetworkCheckService", "onHandleIntent internet connection detected");
                        C0204d.m139c("NetworkCheckService", "onHandleIntent completed");
                    }
                    int i2 = i + 1;
                    a = m111a();
                    i = i2;
                } catch (InterruptedException e) {
                    C0204d.m129a("NetworkCheckService", "onHandleIntent wait loop interrupted");
                }
            }
        }
        C0204d.m139c("NetworkCheckService", "onHandleIntent completed");
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        C0204d.m139c("NetworkCheckService", "onStartCommand");
        return super.onStartCommand(intent, i, i2);
    }
}
