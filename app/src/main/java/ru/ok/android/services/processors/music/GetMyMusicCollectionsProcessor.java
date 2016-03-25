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
import ru.ok.java.api.wmf.http.HttpGetMyMusicCollectionsRequest;
import ru.ok.java.api.wmf.json.JsonGetMusicCollectionsParser;
import ru.ok.model.wmf.UserTrackCollection;

public final class GetMyMusicCollectionsProcessor {
    @Subscribe(on = 2131623944, to = 2131624058)
    public void getMyMusicCollections(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get my music collections processor");
        getMyCollections(msg.replyTo);
    }

    private void getMyCollections(Messenger replayTo) {
        try {
            UserTrackCollection[] collections = getMyMusicCollectionsValue();
            updateDb(collections);
            Message mes = Message.obtain(null, 219, 0, 0);
            mes.obj = collections;
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get my collections " + collections.toString());
        } catch (Exception e) {
            Logger.m172d("Error get my collections " + e.getMessage());
            Message msg = Message.obtain(null, 220, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private UserTrackCollection[] getMyMusicCollectionsValue() throws Exception {
        return new JsonGetMusicCollectionsParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetMyMusicCollectionsRequest(ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }

    private void updateDb(UserTrackCollection[] collections) {
        if (OdnoklassnikiApplication.getCurrentUser() != null) {
            MusicStorageFacade.insertUserMusicCollections(OdnoklassnikiApplication.getContext(), OdnoklassnikiApplication.getCurrentUser().uid, collections);
        }
    }
}
