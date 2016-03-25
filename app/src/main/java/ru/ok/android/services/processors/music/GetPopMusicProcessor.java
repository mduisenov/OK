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
import ru.ok.java.api.wmf.http.HttpGetPopMusicRequest;
import ru.ok.java.api.wmf.json.JsonGetMusicParser;
import ru.ok.model.wmf.GetTracksResponse;
import ru.ok.model.wmf.Track;

public final class GetPopMusicProcessor {
    @Subscribe(on = 2131623944, to = 2131624063)
    public void getPopMusic(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get my music processor");
        getMyMusic(msg.replyTo, msg.getData().getInt("start_position", 0));
    }

    private void getMyMusic(Messenger replayTo, int start) {
        try {
            GetTracksResponse result = getPopMusicValue(start);
            updateDb(result.tracks);
            Message mes = Message.obtain(null, 227, 0, 0);
            mes.obj = result.tracks;
            Bundle data = new Bundle();
            data.putInt("start_position", start);
            data.putBoolean("all_content", result.hasMore);
            mes.setData(data);
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get pop music " + result.tracks.toString());
        } catch (Exception e) {
            Logger.m172d("Error get pop music " + e.getMessage());
            Message msg = Message.obtain(null, 228, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private GetTracksResponse getPopMusicValue(int start) throws Exception {
        return new JsonGetMusicParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetPopMusicRequest(start, LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }

    private void updateDb(Track[] tracks) {
        MusicStorageFacade.insertPopTracks(OdnoklassnikiApplication.getContext(), tracks);
    }
}
