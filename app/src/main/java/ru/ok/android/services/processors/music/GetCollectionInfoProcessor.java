package ru.ok.android.services.processors.music;

import android.os.Message;
import android.os.Messenger;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.app.Messages;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.wmf.http.HttpGetCollectionInfoRequest;
import ru.ok.java.api.wmf.json.JsonGetMusicCollectionParser;
import ru.ok.model.wmf.UserTrackCollection;

public final class GetCollectionInfoProcessor {
    @Subscribe(on = 2131623944, to = 2131624049)
    public void getCollectionInfo(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get collection info processor");
        getCollectionMusic(msg.replyTo, ((Long) msg.obj).longValue());
    }

    private void getCollectionMusic(Messenger replayTo, long collection) {
        try {
            UserTrackCollection collectionValue = getCollectionMusicValue(collection);
            Message mes = Message.obtain(null, 261, 0, 0);
            mes.obj = collectionValue;
            Messages.safeSendMessage(mes, replayTo);
        } catch (Exception e) {
            Message msg = Message.obtain(null, 262, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private UserTrackCollection getCollectionMusicValue(long collection) throws Exception {
        return new JsonGetMusicCollectionParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetCollectionInfoRequest(collection, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }
}
