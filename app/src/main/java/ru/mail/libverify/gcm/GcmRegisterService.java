package ru.mail.libverify.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import java.io.IOException;
import ru.mail.libverify.api.C0177b;
import ru.mail.libverify.api.VerificationFactory;
import ru.mail.libverify.storage.C0197h;
import ru.mail.libverify.utils.C0204d;
import ru.mail.libverify.utils.C0205j;

public class GcmRegisterService extends IntentService {
    public GcmRegisterService() {
        super("GcmRegisterService");
        setIntentRedelivery(true);
    }

    private String m60a() {
        Object[] objArr;
        String str = null;
        try {
            str = GoogleCloudMessaging.getInstance(this).register("297109036349");
        } catch (IOException e) {
            objArr = new Object[]{e};
            C0204d.m141c("push", "Error: cannot get registration id from Google, error is %s", objArr);
        } catch (SecurityException e2) {
            objArr = new Object[]{e2};
            C0204d.m141c("push", "Error: not enough permissions to register GCM channel, error is %s", objArr);
        }
        return str;
    }

    public static void m61a(Context context, @NonNull C0197h c0197h) {
        C0204d.m141c("push", "Gcm register method invoked", new Object[0]);
        if (m66c(context, c0197h)) {
            C0204d.m141c("push", "GCM Device already registered in GCM", new Object[0]);
        } else if (m64a(context)) {
            C0204d.m141c("push", "GCM: start gcm registration service", new Object[0]);
            Object[] objArr = new Object[]{context.startService(new Intent(context, GcmRegisterService.class))};
            C0204d.m141c("push", "GCM: start gcm service result %s", objArr);
        }
    }

    private static void m62a(Context context, C0197h c0197h, String str) {
        String str2 = "push";
        C0204d.m141c(str2, "Saving regId %s on app version %s", str, Integer.valueOf(C0205j.m142a(context)));
        c0197h.m108a("gcm_registration_id", str);
        c0197h.m108a("gcm_app_version", Integer.toString(r0));
        c0197h.m109a();
    }

    public static void m63a(String str, Object... objArr) {
        C0204d.m141c("push", str, objArr);
    }

    private static boolean m64a(Context context) {
        try {
            int i = VERSION.SDK_INT;
            if (i < 8) {
                throw new UnsupportedOperationException("Device must be at least API Level 8 (instead of " + i + ")");
            }
            context.getPackageManager().getPackageInfo("com.google.android.gsf", 0);
            return true;
        } catch (NameNotFoundException e) {
            throw new UnsupportedOperationException("Device does not have package com.google.android.gsf");
        } catch (UnsupportedOperationException e2) {
            return false;
        }
    }

    public static String m65b(Context context, C0197h c0197h) {
        Object a = c0197h.m107a("gcm_registration_id");
        if (TextUtils.isEmpty(a)) {
            C0204d.m141c("push", "Registration not found.", new Object[0]);
            return "";
        } else if (TextUtils.equals(c0197h.m107a("gcm_app_version"), Integer.toString(C0205j.m142a(context)))) {
            return a;
        } else {
            C0204d.m141c("push", "App version changed.", new Object[0]);
            return "";
        }
    }

    private static boolean m66c(Context context, C0197h c0197h) {
        return !TextUtils.isEmpty(m65b(context, c0197h));
    }

    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            C0177b gcmApi = VerificationFactory.getGcmApi(this);
            if (m66c(this, gcmApi.m45b())) {
                C0204d.m141c("push", "GCM registration already successfully completed", new Object[0]);
                gcmApi.m43a();
                return;
            }
            m62a(this, gcmApi.m45b(), "");
            Object a = m60a();
            if (!TextUtils.isEmpty(a)) {
                m62a(this, gcmApi.m45b(), a);
                Object[] objArr = new Object[]{a};
                C0204d.m141c("push", "Gcm registration id %s was received and stored in shared preferences", objArr);
                gcmApi.m43a();
            }
        }
    }
}
