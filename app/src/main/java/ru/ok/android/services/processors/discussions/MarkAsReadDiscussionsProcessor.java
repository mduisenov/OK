package ru.ok.android.services.processors.discussions;

import android.os.Message;
import android.os.Messenger;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.app.Messages;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.discussions.MarkAsReadDiscussionsRequest;

public final class MarkAsReadDiscussionsProcessor {
    @Subscribe(on = 2131623944, to = 2131624079)
    public void markAsReadDiscussions(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit to mark as read discussions processor");
        markAsRead(msg.replyTo);
    }

    private void markAsRead(Messenger replyTo) {
        try {
            JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new MarkAsReadDiscussionsRequest());
            Messages.safeSendMessage(Message.obtain(null, 186, 0, 0), replyTo);
        } catch (Exception e) {
            Logger.m173d("message mark as read get error %s", e);
            Logger.m173d("send message get error %s", e);
            Message mes = Message.obtain(null, 187, 0, 0);
            mes.obj = e;
            Messages.safeSendMessage(mes, replyTo);
        }
    }
}
