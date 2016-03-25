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
import ru.ok.java.api.wmf.http.HttpPlay30Request;
import ru.ok.model.wmf.Track;

public final class Play30MusicProcessor {
    @Subscribe(on = 2131623944, to = 2131624080)
    public void play30Music(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit set music play 30 processor");
        set30(msg.replyTo, msg.obj);
    }

    private void set30(Messenger replayTo, Track track) {
        try {
            setPlay30Value(track);
            Message mes = Message.obtain(null, 190, 0, 0);
            mes.obj = track;
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("set music set play30 " + track.toString());
        } catch (Exception e) {
            Logger.m172d("Error set music play30 " + e.getMessage());
            Message msg = Message.obtain(null, 191, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private void setPlay30Value(Track track) throws Exception {
        JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpPlay30Request(track.id, ConfigurationPreferences.getInstance().getWmfServer()));
    }
}
