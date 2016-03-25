package ru.ok.android.utils.animation;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Message;
import ru.ok.android.utils.animation.SyncBus.MessageCallback;

public final class PlayerAnimationHelper {
    private static final SyncBus SYNC_BUS;

    static {
        SYNC_BUS = new SyncBus();
    }

    public static void sendPlayerCollapsed() {
        sendEmptyMessage(2);
    }

    public static void sendPlayerCreated() {
        sendEmptyMessage(1);
    }

    public static void sendButtonCollapsed() {
        sendEmptyMessage(3);
    }

    public static void sendButtonRevealed() {
        sendEmptyMessage(4);
    }

    private static void sendEmptyMessage(int what) {
        sendMessage(Message.obtain(null, what));
    }

    public static Bundle sendMessage(Message message) {
        return SYNC_BUS.message(message);
    }

    public static void registerCallback(int what, MessageCallback messageCallback) {
        SYNC_BUS.registerCallback(what, messageCallback);
    }

    public static void unregisterCallback(int what, MessageCallback messageCallback) {
        SYNC_BUS.unregisterCallback(what, messageCallback);
    }

    public static boolean isAnimationEnabled() {
        return VERSION.SDK_INT >= 21;
    }
}
