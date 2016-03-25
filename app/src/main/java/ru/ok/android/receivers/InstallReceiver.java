package ru.ok.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import ru.mail.android.mytracker.campaign.CampaignReceiver;
import ru.ok.android.utils.ReferrerStorage;

public class InstallReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String referrerString = extras.getString("referrer");
            if (!TextUtils.isEmpty(referrerString)) {
                ReferrerStorage.setReferrer(context, referrerString);
            }
        }
        try {
            new CampaignReceiver().onReceive(context, intent);
        } catch (Exception e) {
        }
    }
}
