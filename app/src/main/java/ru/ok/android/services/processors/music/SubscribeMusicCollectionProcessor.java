package ru.ok.android.services.processors.music;

import android.os.Message;
import android.os.Messenger;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.services.app.Messages;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.wmf.http.HttpSubscribeCollectionRequest;

public final class SubscribeMusicCollectionProcessor {
    @Subscribe(on = 2131623944, to = 2131624082)
    public void subscribeCollection(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get subscribe collection music processor");
        subscribeCollectionMusic(msg.replyTo, ((Long) msg.obj).longValue());
    }

    private void subscribeCollectionMusic(Messenger replayTo, long collection) {
        try {
            if (subscribeMusicValue(collection)) {
                updateDB(collection);
            }
            Message mes = Message.obtain(null, 290, 0, 0);
            mes.obj = Long.valueOf(collection);
            Messages.safeSendMessage(mes, replayTo);
        } catch (Exception e) {
            Message msg = Message.obtain(null, 291, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private boolean subscribeMusicValue(long collection) throws Exception {
        return JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpSubscribeCollectionRequest(collection, ConfigurationPreferences.getInstance().getWmfServer())) != null;
    }

    private void updateDB(long collection) {
        MusicStorageFacade.addUserCollectionRelation(OdnoklassnikiApplication.getContext(), OdnoklassnikiApplication.getCurrentUser().uid, collection);
    }
}
