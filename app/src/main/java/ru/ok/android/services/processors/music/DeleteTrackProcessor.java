package ru.ok.android.services.processors.music;

import android.os.Message;
import android.os.Messenger;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.services.app.Messages;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.wmf.http.HttpDeleteTrackRequest;
import ru.ok.java.api.wmf.json.JsonDeleteTrackParser;
import ru.ok.model.wmf.Track;

public final class DeleteTrackProcessor {
    @Subscribe(on = 2131623944, to = 2131624040)
    public void deleteTrack(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        delete(msg.replyTo, (Track[]) msg.obj);
    }

    private void delete(Messenger replayTo, Track[] tracks) {
        try {
            Message mes;
            if (delTrackValue(tracks)) {
                updateDB(tracks);
                mes = Message.obtain(null, 163, 0, 0);
                mes.obj = tracks;
            } else {
                mes = Message.obtain(null, 164, 0, 0);
                mes.obj = tracks;
            }
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("delete track");
        } catch (Exception e) {
            Logger.m172d("Error delete track " + e.getMessage());
            Message msg = Message.obtain(null, 164, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private boolean delTrackValue(Track[] tracks) throws Exception {
        return new JsonDeleteTrackParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpDeleteTrackRequest(tracks, ConfigurationPreferences.getInstance().getWmfServer()))).parse().booleanValue();
    }

    private void updateDB(Track[] tracks) {
        MusicStorageFacade.deleteUserTracks(OdnoklassnikiApplication.getContext(), OdnoklassnikiApplication.getCurrentUser().uid, tracks);
    }
}
