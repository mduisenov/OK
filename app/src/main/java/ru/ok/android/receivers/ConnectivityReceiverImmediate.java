package ru.ok.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ru.ok.android.services.messages.MessagesService;
import ru.ok.android.utils.Logger;

public final class ConnectivityReceiverImmediate extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityReceiver.isConnected(context, intent)) {
            Logger.m172d("Perform actions on connected");
            MessagesService.sendActionSendAll(context);
        }
    }
}
