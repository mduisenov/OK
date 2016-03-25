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
import ru.ok.java.api.wmf.http.HttpGetTracksForArtistRequest;
import ru.ok.java.api.wmf.json.JsonGetMusicParser;
import ru.ok.model.wmf.GetTracksResponse;
import ru.ok.model.wmf.Track;

public final class GetArtistTrackProcessor {
    @Subscribe(on = 2131623944, to = 2131624047)
    public void getArtistTrack(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get artist tracks processor");
        getArtistTracks(msg.replyTo, ((Long) msg.obj).longValue());
    }

    private void getArtistTracks(Messenger replayTo, long artistId) {
        try {
            Track[] tracks = getArtistTracksValue(artistId).tracks;
            Message mes = Message.obtain(null, 211, 0, 0);
            mes.obj = tracks;
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get artist tracks " + tracks.toString());
        } catch (Exception e) {
            Logger.m172d("Error get artist music " + e.getMessage());
            Message msg = Message.obtain(null, 212, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private GetTracksResponse getArtistTracksValue(long artistId) throws Exception {
        return new JsonGetMusicParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetTracksForArtistRequest(artistId, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }
}
