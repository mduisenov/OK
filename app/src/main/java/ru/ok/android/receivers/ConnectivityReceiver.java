package ru.ok.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.services.messages.MessagesService;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.bus.BusMessagingHelper;
import ru.ok.android.utils.controls.events.EventsManager;

public final class ConnectivityReceiver extends BroadcastReceiver {
    public static boolean isWifi;

    static {
        isWifi = isWifi();
    }

    private static boolean isWifi() {
        NetworkInfo wifi = ((ConnectivityManager) OdnoklassnikiApplication.getContext().getSystemService("connectivity")).getNetworkInfo(1);
        if (wifi == null || !wifi.isConnected()) {
            return false;
        }
        return true;
    }

    public void onReceive(Context context, Intent intent) {
        isWifi = isWifi();
        if (isConnected(context, intent)) {
            Logger.m172d("Perform actions on connected");
            Utils.getServiceHelper().sendUndeliveredDiscussionComments();
            BusMessagingHelper.sendReadConversations();
            GlobalBus.send(2131624233, new BusEvent());
            EventsManager.getInstance().updateNow();
            if (!context.getResources().getBoolean(2131361798)) {
                MessagesService.sendActionSendAll(context);
            }
        }
    }

    public static boolean isConnected(Context context, Intent intent) {
        if (intent.getBooleanExtra("noConnectivity", false)) {
            return false;
        }
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService("connectivity");
        if (manager == null) {
            return false;
        }
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || info.getState() != State.CONNECTED) {
            return false;
        }
        return true;
    }
}
