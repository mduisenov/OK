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
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.wmf.http.HttpGetTracksForAlbumRequest;
import ru.ok.java.api.wmf.json.JsonAlbumInfoParse;
import ru.ok.java.api.wmf.json.JsonGetMusicParser;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.AlbumInfo;
import ru.ok.model.wmf.GetTracksResponse;

public final class GetAlbumInfoProcessor {
    @Subscribe(on = 2131623944, to = 2131624042)
    public void getAlbumInfo(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get album tracks processor");
        getAlbumInfo(msg.replyTo, ((Long) msg.obj).longValue());
    }

    private void getAlbumInfo(Messenger replayTo, long albumId) {
        try {
            JsonHttpResult result = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetTracksForAlbumRequest(albumId, ConfigurationPreferences.getInstance().getWmfServer()));
            Album album = getAlbumInfoValue(result);
            GetTracksResponse resultValue = getAlbumTrackValue(result);
            Message mes = Message.obtain(null, 253, 0, 0);
            mes.obj = new AlbumInfo(resultValue.tracks, album);
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get albums");
        } catch (Exception e) {
            Logger.m172d("Error get music " + e.getMessage());
            Message msg = Message.obtain(null, 254, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private Album getAlbumInfoValue(JsonHttpResult result) throws ResultParsingException {
        return new JsonAlbumInfoParse(result).parse();
    }

    private GetTracksResponse getAlbumTrackValue(JsonHttpResult result) throws BaseApiException {
        return new JsonGetMusicParser(result).parse();
    }
}
