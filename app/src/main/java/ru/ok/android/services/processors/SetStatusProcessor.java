package ru.ok.android.services.processors;

import android.content.Intent;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.SetStatusRequest;

public final class SetStatusProcessor {
    @Subscribe(on = 2131623944, to = 2131624116)
    public void updateStates(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        if (msg.getData() != null && msg.getData().containsKey(NotificationCompat.CATEGORY_STATUS)) {
            setStatus(msg.getData().getString(NotificationCompat.CATEGORY_STATUS));
        }
    }

    private void setStatus(String status) {
        try {
            if (JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new SetStatusRequest(status)).getHttpStatus() == 200) {
                sendStatusChangedBroadcast(status);
                Logger.m172d("Status set successfully");
            }
            GlobalBus.send(2131624260, new BusEvent(null, -1));
        } catch (Exception e) {
            Logger.m173d("status set error %s", e);
            GlobalBus.send(2131624260, new BusEvent(null, -1));
        }
    }

    static void sendStatusChangedBroadcast(String status) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("ru.odnoklassniki.android.widget.STATUS_CHANGED");
        broadcastIntent.putExtra(NotificationCompat.CATEGORY_STATUS, status);
        OdnoklassnikiApplication.getContext().sendBroadcast(broadcastIntent);
    }
}
