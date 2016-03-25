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
import ru.ok.java.api.wmf.http.HttpGetTracksForAlbumRequest;
import ru.ok.java.api.wmf.json.JsonGetMusicParser;
import ru.ok.model.wmf.GetTracksResponse;
import ru.ok.model.wmf.Track;

public final class GetAlbumTracksProcessor {
    @Subscribe(on = 2131623944, to = 2131624043)
    public void getAlbumTracks(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get album tracks processor");
        getAlbumTracks(msg.replyTo, ((Long) msg.obj).longValue());
    }

    private void getAlbumTracks(Messenger replayTo, long albumId) {
        try {
            Track[] tracks = getAlbumTracksValue(albumId).tracks;
            Message mes = Message.obtain(null, 207, 0, 0);
            mes.obj = tracks;
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get albums tracks " + tracks.toString());
        } catch (Exception e) {
            Logger.m172d("Error get tracks music " + e.getMessage());
            Message msg = Message.obtain(null, 208, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private GetTracksResponse getAlbumTracksValue(long albumId) throws Exception {
        return new JsonGetMusicParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetTracksForAlbumRequest(albumId, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }
}
