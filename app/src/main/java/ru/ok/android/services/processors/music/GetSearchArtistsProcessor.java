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
import ru.ok.java.api.wmf.http.HttpGetSearchArtistsRequest;
import ru.ok.java.api.wmf.json.JsonGetArtistsParser;
import ru.ok.model.wmf.Artist;

public final class GetSearchArtistsProcessor {
    @Subscribe(on = 2131623944, to = 2131624067)
    public void getSearchArtists(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get search artists processor");
        getSearchArtists(msg.replyTo, msg.obj, msg.getData().getInt("start_position", 0));
    }

    private void getSearchArtists(Messenger replayTo, String text, int start) {
        try {
            Artist[] artists = getSearchArtistsValue(text, start);
            Message mes = Message.obtain(null, 198, 0, 0);
            mes.obj = artists;
            Bundle data = new Bundle();
            data.putInt("start_position", start);
            mes.setData(data);
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get search artists " + artists.toString());
        } catch (Exception e) {
            Logger.m172d("Error get search artists " + e.getMessage());
            Message msg = Message.obtain(null, 199, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private Artist[] getSearchArtistsValue(String text, int start) throws Exception {
        return new JsonGetArtistsParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetSearchArtistsRequest(text, start, LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }
}
