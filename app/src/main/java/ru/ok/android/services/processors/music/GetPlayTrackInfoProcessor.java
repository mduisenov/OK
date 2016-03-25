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
import ru.ok.java.api.wmf.http.HttpGetPlayTrackInfoRequest;
import ru.ok.java.api.wmf.json.JsonGetPlayTrackInfoParser;
import ru.ok.model.wmf.PlayTrackInfo;

public final class GetPlayTrackInfoProcessor {
    @Subscribe(on = 2131623944, to = 2131624061)
    public void getPlayTrackInfo(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get play info processor");
        getPlayTrack(msg.getData().getLong("tid"), msg.replyTo);
    }

    private void getPlayTrack(long trackId, Messenger replayTo) {
        try {
            PlayTrackInfo trackInfo = getPlayTrackValue(trackId);
            Message mes = Message.obtain(null, 137, 0, 0);
            mes.obj = trackInfo;
            Messages.safeSendMessage(mes, replayTo);
            Logger.m173d("Get play track info %s", trackInfo);
        } catch (Throwable e) {
            Logger.m179e(e, "Error get track info");
            Message msg = Message.obtain(null, 138, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private PlayTrackInfo getPlayTrackValue(long trackId) throws Exception {
        return new JsonGetPlayTrackInfoParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetPlayTrackInfoRequest(trackId, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }
}
