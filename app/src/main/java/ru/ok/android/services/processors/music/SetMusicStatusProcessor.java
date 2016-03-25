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
import ru.ok.java.api.wmf.http.HttpSetTrackAsStatusRequest;
import ru.ok.java.api.wmf.json.JsonSetStatusParser;
import ru.ok.model.wmf.Track;

public final class SetMusicStatusProcessor {
    @Subscribe(on = 2131623944, to = 2131624081)
    public void setMusicStatus(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit set status music processor");
        setStatus(msg.replyTo, msg.obj);
    }

    private void setStatus(Messenger replayTo, Track track) {
        try {
            Message mes;
            if (setMusicStatusValue(track)) {
                mes = Message.obtain(null, 153, 0, 0);
                mes.obj = track;
            } else {
                mes = Message.obtain(null, 154, 0, 0);
                mes.obj = track;
            }
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("set music status " + track.toString());
        } catch (Exception e) {
            Logger.m172d("Error set music status " + e.getMessage());
            Message msg = Message.obtain(null, 154, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private boolean setMusicStatusValue(Track track) throws Exception {
        return new JsonSetStatusParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpSetTrackAsStatusRequest(track.id, ConfigurationPreferences.getInstance().getWmfServer()))).parse().booleanValue();
    }
}
