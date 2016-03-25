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
import ru.ok.java.api.wmf.json.JsonGetMyMusicParser;
import ru.ok.model.wmf.GetMyMusicResponse;

public final class GetMyMusicProcessor {
    @Subscribe(on = 2131623944, to = 2131624057)
    public void getMyMusic(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get my music processor");
        getMyMusic(msg.replyTo, msg.getData().getInt("start_position", 0));
    }

    private void getMyMusic(Messenger replayTo, int start) {
        try {
            GetMyMusicResponse result = getMyMusicValue(start);
            updateDb(result);
            Message mes = Message.obtain(null, 129, 0, 0);
            mes.obj = result.tracks;
            Bundle data = new Bundle();
            data.putInt("start_position", start);
            data.putBoolean("all_content", result.hasMore);
            mes.setData(data);
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get my music " + result.tracks.toString());
        } catch (Exception e) {
            Logger.m172d("Error get my music " + e.getMessage());
            Message msg = Message.obtain(null, 130, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private GetMyMusicResponse getMyMusicValue(int start) throws Exception {
        return new JsonGetMyMusicParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetUserMusicRequest(null, start, LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }

    private void updateDb(GetMyMusicResponse response) {
        MusicStorageFacade.insertExtensionTracks(OdnoklassnikiApplication.getContext(), response.extension);
        MusicStorageFacade.syncUserTracks(OdnoklassnikiApplication.getContext(), OdnoklassnikiApplication.getCurrentUser().uid, response.tracks);
    }
}
