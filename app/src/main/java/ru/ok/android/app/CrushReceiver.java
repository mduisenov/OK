package ru.ok.android.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CrushReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.e("=======", "====== " + intent);
    }
}
