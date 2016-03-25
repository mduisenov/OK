package ru.ok.android.utils.animation;

import android.os.Bundle;
import android.os.Message;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.List;

public class SyncBus {
    private final SparseArray<List<MessageCallback>> callbacksMap;

    public interface MessageCallback {
        Bundle onMessage(Message message);
    }

    public SyncBus() {
        this.callbacksMap = new SparseArray();
    }

    public final Bundle message(Message message) {
        Bundle result = null;
        List<MessageCallback> callbacks = (List) this.callbacksMap.get(message.what);
        if (!(callbacks == null || callbacks.isEmpty())) {
            for (int i = callbacks.size() - 1; i >= 0; i--) {
                result = ((MessageCallback) callbacks.get(i)).onMessage(message);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    public final void registerCallback(int what, MessageCallback messageCallback) {
        List<MessageCallback> callbacks = (List) this.callbacksMap.get(what);
        if (callbacks == null) {
            callbacks = new ArrayList();
            this.callbacksMap.put(what, callbacks);
        }
        callbacks.add(messageCallback);
    }

    public final void unregisterCallback(int what, MessageCallback messageCallback) {
        List<MessageCallback> callbacks = (List) this.callbacksMap.get(what);
        if (callbacks != null) {
            callbacks.remove(messageCallback);
        }
    }
}
