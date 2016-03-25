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
import ru.ok.java.api.wmf.http.HttpGetPopMusicRequest;
import ru.ok.java.api.wmf.json.JsonGetPopMusicCollectionsParser;
import ru.ok.model.wmf.UserTrackCollection;

public final class GetPopMusicCollectionsProcessor {
    @Subscribe(on = 2131623944, to = 2131624064)
    public void getPopMusicCollections(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get pop music collections processor");
        getMyCollections(msg.replyTo);
    }

    private void getMyCollections(Messenger replayTo) {
        try {
            UserTrackCollection[] collections = getPopMusicCollectionsValue();
            updateDb(collections);
            Message mes = Message.obtain(null, 231, 0, 0);
            mes.obj = collections;
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get pop collections " + collections.toString());
        } catch (Exception e) {
            Logger.m172d("Error get pop collections " + e.getMessage());
            Message msg = Message.obtain(null, 232, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private UserTrackCollection[] getPopMusicCollectionsValue() throws Exception {
        return new JsonGetPopMusicCollectionsParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetPopMusicRequest(0, 200, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }

    private void updateDb(UserTrackCollection[] collections) {
        MusicStorageFacade.insertPopMusicCollections(OdnoklassnikiApplication.getContext(), collections);
    }
}
