package ru.ok.android.services.processors.music;

import android.os.Bundle;
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
import ru.ok.java.api.wmf.http.HttpGetSearchTracksMusicRequest;
import ru.ok.java.api.wmf.json.JsonGetMusicParser;
import ru.ok.model.wmf.GetTracksResponse;

public final class GetSearchMusicProcessor {
    @Subscribe(on = 2131623944, to = 2131624068)
    public void getSearch(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        getSearchMusic(msg.replyTo, msg.obj, msg.getData().getInt("start_position", 0));
    }

    private void getSearchMusic(Messenger replayTo, String text, int start) {
        try {
            GetTracksResponse result = getSearchMusicValue(text, start);
            Message mes = Message.obtain(null, 173, 0, 0);
            mes.obj = result.tracks;
            Bundle data = new Bundle();
            data.putInt("start_position", start);
            data.putBoolean("all_content", result.hasMore);
            mes.setData(data);
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get search music " + result.tracks.toString());
        } catch (Exception e) {
            Logger.m172d("Error get search music " + e.getMessage());
            Message msg = Message.obtain(null, 174, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private GetTracksResponse getSearchMusicValue(String text, int start) throws Exception {
        return new JsonGetMusicParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetSearchTracksMusicRequest(text, start, LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }
}
