package ru.ok.android.services.persistent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class PersistentConnectivityReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String action = intent == null ? null : intent.getAction();
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
            if (cm != null) {
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    Intent onConnected = new Intent(context, PersistentTaskService.class);
                    onConnected.setAction(action);
                    onConnected.putExtras(intent);
                    context.startService(onConnected);
                }
            }
        }
    }
}
