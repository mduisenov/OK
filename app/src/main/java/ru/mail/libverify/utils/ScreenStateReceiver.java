package ru.mail.libverify.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenStateReceiver extends BroadcastReceiver {
    private static volatile Boolean f58a;

    static {
        f58a = null;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent != null && context != null) {
            C0204d.m137b("ScreenStateReceiver", "screen active state %s", Boolean.valueOf(intent.getAction().equals("android.intent.action.SCREEN_ON")));
            if (!Boolean.valueOf(intent.getAction().equals("android.intent.action.SCREEN_ON")).equals(f58a)) {
                Intent intent2 = new Intent(context, ScreenStateHandlingService.class);
                intent2.setAction(intent.getAction());
                context.startService(intent2);
            }
        }
    }
}
