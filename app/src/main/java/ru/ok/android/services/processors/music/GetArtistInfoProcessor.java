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
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.wmf.http.HttpGetTracksForArtistRequest;
import ru.ok.java.api.wmf.json.JsonArtistInfoParser;
import ru.ok.java.api.wmf.json.JsonGetMusicParser;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.ArtistInfo;
import ru.ok.model.wmf.GetTracksResponse;
import ru.ok.model.wmf.Track;

public final class GetArtistInfoProcessor {
    @Subscribe(on = 2131623944, to = 2131624045)
    public void getArtistInfo(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get artist tracks processor");
        getArtistInfo(msg.replyTo, ((Long) msg.obj).longValue());
    }

    private void getArtistInfo(Messenger replayTo, long artistId) {
        try {
            JsonHttpResult result = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetTracksForArtistRequest(artistId, ConfigurationPreferences.getInstance().getWmfServer()));
            Track[] tracks = getArtistTracksValue(result).tracks;
            Artist artist = getArtistValue(result);
            Message mes = Message.obtain(null, 257, 0, 0);
            mes.obj = new ArtistInfo(artist, tracks);
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get artist info " + tracks.toString());
        } catch (Exception e) {
            Logger.m172d("Error get artist info " + e.getMessage());
            Message msg = Message.obtain(null, 258, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private GetTracksResponse getArtistTracksValue(JsonHttpResult result) throws ResultParsingException {
        return new JsonGetMusicParser(result).parse();
    }

    private Artist getArtistValue(JsonHttpResult result) throws ResultParsingException {
        return new JsonArtistInfoParser(result).parse();
    }
}
