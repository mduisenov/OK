package ru.ok.android.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HeadsetIntentReceiver extends BroadcastReceiver {
    private MusicManager manager;

    public HeadsetIntentReceiver(MusicManager manager) {
        this.manager = manager;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getIntExtra("state", -1) == 0 && this.manager.isPlaying()) {
            this.manager.pause();
        }
    }
}
