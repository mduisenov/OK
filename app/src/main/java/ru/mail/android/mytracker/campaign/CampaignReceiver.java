package ru.mail.android.mytracker.campaign;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ru.mail.android.mytracker.Tracer;

public class CampaignReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String referrer = extras.getString("referrer");
                if (referrer != null) {
                    Tracer.m38d("CampaignReceiver got referrer: " + referrer);
                    try {
                        context.startService(getServiceIntent(referrer, context));
                    } catch (Throwable throwable) {
                        Tracer.m38d(throwable.toString());
                    }
                }
            }
        }
    }

    protected Intent getServiceIntent(String referrer, Context context) {
        Intent serviceIntent = new Intent(context, CampaignService.class);
        serviceIntent.putExtra("referrer", referrer);
        return serviceIntent;
    }
}
