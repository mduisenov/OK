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
import ru.ok.java.api.wmf.http.HttpGetAlbumsForArtistRequest;
import ru.ok.java.api.wmf.json.JsonGetAlbumsForArtistParser;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.Artist;

public final class GetAlbumsForArtistProcessor {
    @Subscribe(on = 2131623944, to = 2131624044)
    public void getAlbumsForArtist(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get artist albums processor");
        getSearchAlbums(msg.replyTo, msg.obj);
    }

    private void getSearchAlbums(Messenger replayTo, Artist artist) {
        try {
            Album[] albums = getArtistAlbumsValue(artist);
            Message mes = Message.obtain(null, 245, 0, 0);
            mes.obj = albums;
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get artist albums " + albums.toString());
        } catch (Exception e) {
            Logger.m172d("Error get artist albums " + e.getMessage());
            Message msg = Message.obtain(null, 246, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private Album[] getArtistAlbumsValue(Artist artist) throws Exception {
        return new JsonGetAlbumsForArtistParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetAlbumsForArtistRequest(artist, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }
}
