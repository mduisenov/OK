package ru.ok.android.services.processors.music;

import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.services.app.Messages;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.wmf.http.HttpGetUserMusicCollectionsRequest;
import ru.ok.java.api.wmf.json.JsonGetMusicCollectionsParser;
import ru.ok.model.wmf.UserTrackCollection;

public final class GetUserMusicCollectionsProcessor {
    @Subscribe(on = 2131623944, to = 2131624076)
    public void getUserMusicCollections(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get user music collections processor");
        getUserAlbums(msg.replyTo, msg.getData().getString("user_id"));
    }

    private void getUserAlbums(Messenger replayTo, String userId) {
        try {
            UserTrackCollection[] collections = getUserMusicCollectionsValue(userId);
            updateDb(collections, userId);
            Message mes = Message.obtain(null, 215, 0, 0);
            mes.obj = collections;
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get user collections " + collections.toString());
        } catch (Exception e) {
            Logger.m172d("Error get user collections " + e.getMessage());
            Message msg = Message.obtain(null, 216, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private UserTrackCollection[] getUserMusicCollectionsValue(String userId) throws Exception {
        return new JsonGetMusicCollectionsParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetUserMusicCollectionsRequest(userId, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }

    private void updateDb(UserTrackCollection[] collections, String userId) {
        if (!TextUtils.isEmpty(userId)) {
            MusicStorageFacade.insertUserMusicCollections(OdnoklassnikiApplication.getContext(), userId, collections);
        }
    }
}
