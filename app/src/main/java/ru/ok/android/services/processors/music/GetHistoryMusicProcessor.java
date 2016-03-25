package ru.ok.android.services.processors.music;

import android.os.Bundle;
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
import ru.ok.java.api.wmf.http.HttpGetHistoryMusicRequest;
import ru.ok.java.api.wmf.json.JsonGetHistoryMusicParser;
import ru.ok.model.wmf.HistoryTrack;

public final class GetHistoryMusicProcessor {
    @Subscribe(on = 2131623944, to = 2131624056)
    public void getHistoryMusic(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get history music processor");
        getHistoryMusic(msg.replyTo, 0);
    }

    private void getHistoryMusic(Messenger replayTo, int start) {
        try {
            HistoryTrack[] tracks = getHistoryMusicValue(start);
            updateDb(tracks);
            Message mes = Message.obtain(null, 249, 0, 0);
            mes.obj = tracks;
            Bundle data = new Bundle();
            data.putInt("start_position", start);
            mes.setData(data);
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get history music " + tracks.toString());
        } catch (Exception e) {
            Logger.m172d("Error get history music " + e.getMessage());
            Message msg = Message.obtain(null, 250, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private HistoryTrack[] getHistoryMusicValue(int start) throws Exception {
        return new JsonGetHistoryMusicParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetHistoryMusicRequest(start, 100, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }

    private void updateDb(HistoryTrack[] tracks) {
        MusicStorageFacade.insertHistoryTracks(OdnoklassnikiApplication.getContext(), tracks);
    }
}
