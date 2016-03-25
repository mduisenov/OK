package ru.ok.android.videochat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class VideoChatBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("ru.odnoklassniki.android.videochat.END_CALL")) {
            VideochatController.instance().onUserClosed();
        }
    }
}
