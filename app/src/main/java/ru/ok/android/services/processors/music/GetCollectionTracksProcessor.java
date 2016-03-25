package ru.ok.android.services.processors.music;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import com.google.android.gms.location.LocationStatusCodes;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.services.app.Messages;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.wmf.http.HttpGetTracksForPlayListRequest;
import ru.ok.java.api.wmf.json.JsonGetMusicParser;
import ru.ok.model.wmf.GetTracksResponse;
import ru.ok.model.wmf.Track;

public final class GetCollectionTracksProcessor {
    @Subscribe(on = 2131623944, to = 2131624050)
    public void getCollectionTracks(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get collection music processor");
        getCollectionMusic(msg.replyTo, ((Long) msg.obj).longValue(), msg.getData().getInt("start_position", 0));
    }

    private void getCollectionMusic(Messenger replayTo, long collection, int start) {
        try {
            GetTracksResponse result = getCollectionMusicValue(collection, start);
            Track[] tracks = result.tracks;
            updateDb(tracks, collection);
            Message mes = Message.obtain(null, 223, 0, 0);
            mes.obj = tracks;
            Bundle data = new Bundle();
            data.putBoolean("all_content", result.hasMore);
            data.putInt("start_position", start);
            mes.setData(data);
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get collection music " + tracks.toString());
        } catch (Exception e) {
            Logger.m172d("Error get collection music " + e.getMessage());
            Message msg = Message.obtain(null, 224, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private GetTracksResponse getCollectionMusicValue(long collection, int start) throws Exception {
        return new JsonGetMusicParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetTracksForPlayListRequest(collection, start, LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }

    private void updateDb(Track[] tracks, long collectionId) {
        MusicStorageFacade.insertCollectionTracks(OdnoklassnikiApplication.getContext(), collectionId, tracks);
    }
}
