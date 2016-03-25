package ru.ok.android.ui.utils;

import android.os.Handler;
import android.os.Message;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import ru.ok.android.utils.Logger;

public abstract class SearchBaseHandler extends Handler {
    public abstract int getSearchUpdateDelay();

    public abstract void onSearchHandle(String str);

    public void handleMessage(Message msg) {
        if (msg.what == 1337) {
            onSearchHandle(msg.getData().getString(DiscoverInfo.ELEMENT));
        } else {
            super.handleMessage(msg);
        }
    }

    public void queueSearchUpdate(String query) {
        Logger.m173d("Search update queued for query \"%s\"", query);
        Message msg = Message.obtain();
        msg.what = 1337;
        msg.getData().putString(DiscoverInfo.ELEMENT, query);
        sendMessageDelayed(msg, (long) getSearchUpdateDelay());
    }

    public void removeQueuedUpdates() {
        Logger.m172d("Queued search removal requested");
        removeMessages(1337);
    }
}
