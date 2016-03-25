package ru.ok.android.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public final class ConnectionReceiver extends BroadcastReceiver {
    private MusicManager musicManager;

    public ConnectionReceiver(MusicManager manager) {
        this.musicManager = manager;
    }

    public void onReceive(Context context, Intent intent) {
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction()) && !intent.getBooleanExtra("noConnectivity", false)) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService("connectivity");
            if (manager != null) {
                NetworkInfo info = manager.getActiveNetworkInfo();
                if (info != null && info.getState() == State.CONNECTED) {
                    this.musicManager.notifyConnectionAvailable();
                }
            }
        }
    }
}
