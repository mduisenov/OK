package ru.ok.android.services.app;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import ru.ok.android.utils.Logger;

public final class Messages {
    public static void safeSendMessage(Message msg, Messenger replyTo) {
        if (replyTo != null) {
            try {
                replyTo.send(msg);
            } catch (RemoteException e) {
                Logger.m172d("RemoteException" + e.getMessage());
            }
        }
    }
}
