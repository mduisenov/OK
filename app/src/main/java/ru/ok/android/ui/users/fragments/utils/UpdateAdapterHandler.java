package ru.ok.android.ui.users.fragments.utils;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView.Adapter;
import ru.ok.android.utils.Logger;

public final class UpdateAdapterHandler extends Handler {
    private final Adapter adapter;

    public UpdateAdapterHandler(Adapter adapter) {
        this.adapter = adapter;
    }

    public void handleMessage(Message msg) {
        Logger.m173d("Update adapter %s", this.adapter);
        this.adapter.notifyDataSetChanged();
        sendEmptyMessageDelayed(0, 20000);
    }

    public void onResume() {
        if (!hasMessages(0)) {
            sendEmptyMessageDelayed(0, 1000);
        }
    }

    public void onPause() {
        removeMessages(0);
    }

    public void onDataReceived() {
        removeMessages(0);
        sendEmptyMessageDelayed(0, 20000);
    }
}
