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
import ru.ok.java.api.wmf.http.HttpUnSubscribeCollectionRequest;

public final class UnSubscribeMusicCollectionProcessor {
    @Subscribe(on = 2131623944, to = 2131624083)
    public void unSubscribeCollection(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit un subscribe collection music processor");
        unSubscribeCollectionMusic(msg.replyTo, ((Long) msg.obj).longValue());
    }

    private void unSubscribeCollectionMusic(Messenger replayTo, long collection) {
        try {
            if (unSubscribeMusicValue(collection)) {
                updateDB(collection);
            }
            Message mes = Message.obtain(null, 292, 0, 0);
            mes.obj = Long.valueOf(collection);
            Messages.safeSendMessage(mes, replayTo);
        } catch (Exception e) {
            Message msg = Message.obtain(null, 293, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private boolean unSubscribeMusicValue(long collection) throws Exception {
        if (JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpUnSubscribeCollectionRequest(collection, ConfigurationPreferences.getInstance().getWmfServer())) != null) {
            return true;
        }
        return false;
    }

    private void updateDB(long collection) {
        MusicStorageFacade.deleteUserCollectionRelation(OdnoklassnikiApplication.getContext(), OdnoklassnikiApplication.getCurrentUser().uid, collection);
    }
}
