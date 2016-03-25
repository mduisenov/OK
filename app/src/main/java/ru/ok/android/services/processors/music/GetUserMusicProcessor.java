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
import ru.ok.java.api.wmf.http.HttpGetUserMusicRequest;
import ru.ok.java.api.wmf.json.JsonGetMusicParser;
import ru.ok.model.wmf.GetTracksResponse;
import ru.ok.model.wmf.Track;

public final class GetUserMusicProcessor {
    @Subscribe(on = 2131623944, to = 2131624075)
    public void getUserMusic(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get user music processor");
        getUserMusic(msg.replyTo, msg.getData().getString("user_id"), msg.getData().getInt("start_position", 0));
    }

    private void getUserMusic(Messenger replayTo, String userId, int start) {
        try {
            GetTracksResponse result = getUserMusicValue(userId, start);
            updateDb(result.tracks, userId);
            Message mes = Message.obtain(null, 148, 0, 0);
            mes.obj = result.tracks;
            Bundle data = new Bundle();
            data.putInt("start_position", start);
            data.putBoolean("all_content", result.hasMore);
            mes.setData(data);
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get user music " + result.tracks.toString());
        } catch (Exception e) {
            Logger.m172d("Error get user music " + e.getMessage());
            Message msg = Message.obtain(null, 149, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private GetTracksResponse getUserMusicValue(String userId, int start) throws Exception {
        return new JsonGetMusicParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetUserMusicRequest(userId, start, LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }

    private void updateDb(Track[] tracks, String userId) {
        MusicStorageFacade.syncUserTracks(OdnoklassnikiApplication.getContext(), userId, tracks);
    }
}
