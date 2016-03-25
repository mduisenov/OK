package ru.mail.libverify.api;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import ru.mail.libverify.statistics.d;
import ru.mail.libverify.storage.g;
import ru.mail.libverify.utils.C0204d;
import ru.mail.libverify.utils.LogReceiver;
import ru.mail.libverify.utils.VerificationService;

public final class VerificationFactory {
    private static volatile r f6a;
    private static g f7b;
    private static volatile boolean f8c;

    static {
        f6a = null;
        f7b = null;
        f8c = false;
    }

    private static r m42a(@NonNull Context context) {
        if (f6a == null) {
            synchronized (r.class) {
                if (f6a == null) {
                    f7b = new g(context.getApplicationContext());
                    f6a = new r(f7b, Collections.singletonList(new d(f7b)), null, null);
                }
            }
        }
        return f6a;
    }

    public static C0177b getGcmApi(@NonNull Context context) {
        return m42a(context);
    }

    public static VerificationApi getInstance(@NonNull Context context, @NonNull String str, @NonNull String str2, @Nullable LogReceiver logReceiver, @Nullable UncaughtExceptionListener uncaughtExceptionListener) {
        if (f6a == null) {
            synchronized (r.class) {
                if (f6a == null) {
                    f8c = true;
                    f7b = new g(context.getApplicationContext(), str, str2);
                    f6a = new r(f7b, Collections.singletonList(new d(f7b)), logReceiver, uncaughtExceptionListener);
                }
            }
        }
        if (!f8c) {
            synchronized (r.class) {
                g gVar = f7b;
                Context applicationContext = context.getApplicationContext();
                synchronized (gVar) {
                    gVar.e = applicationContext;
                    gVar.a = str;
                    gVar.b = str2;
                    gVar.c = null;
                    gVar.d = null;
                }
                if (gVar.f != null) {
                    gVar.f.e();
                }
            }
        }
        return f6a;
    }

    public static C0178c getIpcApi(@NonNull Context context) {
        return m42a(context);
    }

    @Nullable
    public static C0179e getNetworkApi() {
        return f6a;
    }

    public static C0180f getNotificationApi(@NonNull Context context) {
        return m42a(context);
    }

    public static String[] getRequiredPermissions() {
        return r.f();
    }

    @Nullable
    public static C0181o getSmsApi() {
        return f6a;
    }

    public static p getTestApi(@NonNull Context context) {
        return null;
    }

    public static C0184q getUIControlsApi(@NonNull Context context) {
        return m42a(context);
    }

    public static boolean hasInstallation(@NonNull Context context) {
        return ru.mail.libverify.storage.d.b(context);
    }

    public static void release() {
        if (f6a != null) {
            synchronized (r.class) {
                if (f6a != null) {
                    r rVar = f6a;
                    C0204d.m135b("VerificationApi", "shutdown started");
                    if (VERSION.SDK_INT < 18) {
                        rVar.c.quit();
                    } else {
                        rVar.c.quitSafely();
                    }
                    if (rVar.d != null) {
                        rVar.d.shutdown();
                        try {
                            rVar.d.awaitTermination(500, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            C0204d.m135b("VerificationApi", "taskExecutor shutdown failure");
                        }
                    }
                    rVar.a.r().m109a();
                    VerificationService.m123a();
                    C0204d.m135b("VerificationApi", "shutdown completed");
                    f6a = null;
                    f7b = null;
                    f8c = false;
                }
            }
        }
    }
}
