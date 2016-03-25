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
import ru.ok.java.api.wmf.http.HttpGetSearchAlbumsRequest;
import ru.ok.java.api.wmf.json.JsonGetAlbumsParser;
import ru.ok.model.wmf.Album;

public final class GetSearchAlbumsProcessor {
    @Subscribe(on = 2131623944, to = 2131624066)
    public void getSearchAlbums(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get search albums processor");
        getSearchAlbums(msg.replyTo, msg.obj, msg.getData().getInt("start_position", 0));
    }

    private void getSearchAlbums(Messenger replayTo, String text, int start) {
        try {
            Album[] albums = getSearchAlbumsValue(text, start);
            Message mes = Message.obtain(null, 194, 0, 0);
            mes.obj = albums;
            Bundle data = new Bundle();
            data.putInt("start_position", start);
            mes.setData(data);
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get search albums " + albums.toString());
        } catch (Exception e) {
            Logger.m172d("Error get search albums " + e.getMessage());
            Message msg = Message.obtain(null, 195, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private Album[] getSearchAlbumsValue(String text, int start) throws Exception {
        return new JsonGetAlbumsParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetSearchAlbumsRequest(text, start, LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }
}
