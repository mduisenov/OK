package ru.ok.android.services.processors.music;

import android.os.Message;
import android.os.Messenger;
import com.google.android.gms.location.LocationStatusCodes;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.app.Messages;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.wmf.http.HttpGetTracksForPlayListRequest;
import ru.ok.java.api.wmf.json.JsonGetPlayListInfoParser;
import ru.ok.model.wmf.UserTrackCollection;

public final class GetPlayListInfoProcessor {
    @Subscribe(on = 2131623944, to = 2131624060)
    public void getPlayListInfo(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get collection info processor");
        getPlaylistInfo(msg.replyTo, ((Long) msg.obj).longValue(), msg.getData().getInt("start_position", 0));
    }

    private void getPlaylistInfo(Messenger replayTo, long collection, int start) {
        try {
            UserTrackCollection playList = getCollectionMusicValue(collection, start);
            Message mes = Message.obtain(null, 264, 0, 0);
            mes.obj = playList;
            Messages.safeSendMessage(mes, replayTo);
        } catch (Exception e) {
            Logger.m172d("Error get collection music " + e.getMessage());
            Message msg = Message.obtain(null, 265, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private UserTrackCollection getCollectionMusicValue(long collection, int start) throws Exception {
        return new JsonGetPlayListInfoParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetTracksForPlayListRequest(collection, start, LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, ConfigurationPreferences.getInstance().getWmfServer())), collection).parse();
    }
}
