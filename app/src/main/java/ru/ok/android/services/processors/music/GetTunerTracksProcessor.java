package ru.ok.android.services.processors.music;

import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatDelegate;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.music.AsyncStorageOperations;
import ru.ok.android.services.app.Messages;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.wmf.http.HttpGetTunerTracksRequest;
import ru.ok.java.api.wmf.json.JsonGetMusicParser;
import ru.ok.model.wmf.Track;

public final class GetTunerTracksProcessor {
    @Subscribe(on = 2131623944, to = 2131624073)
    public void getTunerTracks(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Messenger replyTo = msg.replyTo;
        String data = msg.getData().getString("tuner_data");
        try {
            Track[] tracks = getTracks(data);
            updateDB(data, tracks);
            Message mes = Message.obtain(null, AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR, 0, 0);
            mes.obj = tracks;
            Messages.safeSendMessage(mes, replyTo);
        } catch (Exception e) {
            Logger.m172d("Error get tuners " + e.getMessage());
            Message msgError = Message.obtain(null, AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msgError, replyTo);
        }
    }

    private Track[] getTracks(String data) throws Exception {
        return new JsonGetMusicParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetTunerTracksRequest(ConfigurationPreferences.getInstance().getWmfServer(), data))).parse().tracks;
    }

    private void updateDB(String data, Track[] tracks) {
        AsyncStorageOperations.insertTunerTracks(OdnoklassnikiApplication.getContext(), data, tracks);
    }
}
