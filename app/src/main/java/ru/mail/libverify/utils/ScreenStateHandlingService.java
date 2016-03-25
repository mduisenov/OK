package ru.mail.libverify.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.File;
import java.io.RandomAccessFile;
import ru.mail.libverify.storage.C0198j;

public class ScreenStateHandlingService extends Service {
    private static volatile boolean f53a;
    private static volatile long f54b;
    private static volatile boolean f55c;
    private final Runnable f56d;
    private Handler f57e;

    /* renamed from: ru.mail.libverify.utils.ScreenStateHandlingService.1 */
    class C02021 implements Runnable {
        final /* synthetic */ ScreenStateHandlingService f52a;

        C02021(ScreenStateHandlingService screenStateHandlingService) {
            this.f52a = screenStateHandlingService;
        }

        public final void run() {
            this.f52a.stopSelf();
        }
    }

    static {
        f53a = false;
        f54b = 0;
        f55c = false;
    }

    public ScreenStateHandlingService() {
        this.f56d = new C02021(this);
    }

    private Handler m118a() {
        if (this.f57e == null) {
            this.f57e = new Handler();
        }
        return this.f57e;
    }

    public static void m119a(@NonNull Context context) {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        context.registerReceiver(new ScreenStateReceiver(), intentFilter);
    }

    private static void m120a(@NonNull Context context, boolean z, boolean z2) {
        if (z) {
            f54b = 0;
            return;
        }
        if (!f53a) {
            f54b = m122c(context);
            f53a = true;
        }
        if (f54b == 0 || z2) {
            f54b = System.currentTimeMillis();
        }
    }

    public static synchronized C0198j m121b(@NonNull Context context) {
        C0198j c0198j;
        synchronized (ScreenStateHandlingService.class) {
            PowerManager powerManager = (PowerManager) context.getSystemService("power");
            if (powerManager != null) {
                if (VERSION.SDK_INT >= 20) {
                    f55c = powerManager.isInteractive();
                } else {
                    f55c = powerManager.isScreenOn();
                }
                m120a(context, f55c, false);
            }
            c0198j = new C0198j(f55c, f54b != 0 ? System.currentTimeMillis() - f54b : 0);
        }
        return c0198j;
    }

    private static long m122c(@NonNull Context context) {
        File file = new File(C0205j.m151b(context), "VERIFY_DEVICE_IDLE");
        if (!file.exists()) {
            return 0;
        }
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            long readLong = randomAccessFile.readLong();
            randomAccessFile.close();
            return readLong;
        } catch (Throwable e) {
            C0204d.m130a("ScreenStateHandlingService", "failed to read screen deactivation time", e);
            return 0;
        }
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        super.onDestroy();
        long j = f54b;
        File file = new File(C0205j.m151b((Context) this), "VERIFY_DEVICE_IDLE");
        if (j != 0) {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.writeLong(j);
                randomAccessFile.close();
            } catch (Throwable e) {
                C0204d.m130a("ScreenStateHandlingService", "failed to write screen deactivation time", e);
            }
        } else if (file.exists()) {
            boolean delete = file.delete();
            if (!delete) {
                File file2 = new File(C0205j.m151b((Context) this), "VERIFY_DEVICE_IDLE_TMP");
                if (file.renameTo(file2)) {
                    delete = file2.delete();
                } else {
                    C0204d.m129a("ScreenStateHandlingService", "failed to rename deactivation time file");
                }
            }
            C0204d.m141c("ScreenStateHandlingService", "deactivation time file delete result %s", Boolean.valueOf(delete));
        }
        C0204d.m139c("ScreenStateHandlingService", "service destroyed");
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null) {
            boolean z = f55c;
            synchronized (ScreenStateReceiver.class) {
                f55c = intent.getAction().equals("android.intent.action.SCREEN_ON");
                m120a(this, f55c, true);
            }
            m118a().removeCallbacks(this.f56d);
            m118a().postDelayed(this.f56d, 5000);
        }
        return super.onStartCommand(intent, i, i2);
    }
}
