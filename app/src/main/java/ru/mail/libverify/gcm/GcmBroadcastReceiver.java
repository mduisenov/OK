package ru.mail.libverify.gcm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import ru.mail.libverify.utils.C0205j;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        GcmRegisterService.m63a("Got push: %s", C0205j.m144a(intent.getExtras()));
        if (WakefulBroadcastReceiver.startWakefulService(context, intent.setComponent(new ComponentName(context.getPackageName(), GcmIntentService.class.getName()))) == null) {
            GcmRegisterService.m63a("GcmBroadcastReceiver.onReceive: startWakefulService returned null.", new Object[0]);
        }
        setResultCode(-1);
    }
}
