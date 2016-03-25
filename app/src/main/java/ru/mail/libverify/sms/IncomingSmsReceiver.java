package ru.mail.libverify.sms;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class IncomingSmsReceiver extends WakefulBroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent intent2 = new Intent(intent);
        intent2.setClass(context, SmsHandlingService.class);
        WakefulBroadcastReceiver.startWakefulService(context, intent2);
        setResult(-1, null, null);
    }
}
