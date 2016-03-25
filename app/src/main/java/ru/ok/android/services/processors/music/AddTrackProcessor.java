package ru.ok.android.services.processors.music;

import android.os.Message;
import android.os.Messenger;
import android.util.Pair;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.services.app.Messages;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.wmf.http.HttpAddTrackRequest;
import ru.ok.java.api.wmf.json.JsonAddTrackParser;
import ru.ok.model.wmf.Track;

public final class AddTrackProcessor {
    @Subscribe(on = 2131623944, to = 2131624038)
    public void addTrack(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        add(msg.replyTo, (Track[]) msg.obj);
    }

    private void add(Messenger replayTo, Track[] tracks) {
        try {
            Message mes;
            if (addTrackValue(tracks)) {
                updateDB(tracks);
                mes = Message.obtain(null, 158, 0, 0);
                mes.obj = tracks;
            } else {
                mes = Message.obtain(null, 159, 0, 0);
                mes.obj = tracks;
            }
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("add tracks " + tracks[0].toString());
        } catch (Exception e) {
            Logger.m172d("Error add tracks " + e.getMessage());
            Message msg = Message.obtain(null, 159, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private boolean addTrackValue(Track[] tracks) throws Exception {
        return new JsonAddTrackParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpAddTrackRequest(tracks, ConfigurationPreferences.getInstance().getWmfServer()))).parse().booleanValue();
    }

    private void updateDB(Track[] tracks) {
        int maxPosition = MusicStorageFacade.getMaxTracksPosition(OdnoklassnikiApplication.getContext(), OdnoklassnikiApplication.getCurrentUser().uid);
        List<Pair<Track, Integer>> list = new ArrayList();
        for (int i = 0; i < tracks.length; i++) {
            list.add(new Pair(tracks[i], Integer.valueOf((maxPosition + i) + 1)));
        }
        MusicStorageFacade.insertUserMusicTracks(OdnoklassnikiApplication.getContext(), OdnoklassnikiApplication.getCurrentUser().uid, list);
    }
}
