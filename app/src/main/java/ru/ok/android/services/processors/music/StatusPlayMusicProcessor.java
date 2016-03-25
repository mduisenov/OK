package ru.ok.android.services.processors.music;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Arrays;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.java.api.wmf.http.HttpGetPlayStatusMusicRequest;
import ru.ok.java.api.wmf.json.JsonGetMusicParser;
import ru.ok.model.wmf.Track;

public final class StatusPlayMusicProcessor {
    public static Bundle fillBundle(long trackId, String playListIds, String uid) {
        Bundle bundle = new Bundle();
        bundle.putLong("TRACK_ID", trackId);
        bundle.putString("TRACKS", playListIds);
        bundle.putString("UID", uid);
        return bundle;
    }

    @Subscribe(on = 2131623944, to = 2131624087)
    public void playStatusMusic(BusEvent event) {
        long trackId = event.bundleInput.getLong("TRACK_ID", -1);
        try {
            Track[] tracks = new JsonGetMusicParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetPlayStatusMusicRequest(event.bundleInput.getString("TRACKS"), event.bundleInput.getString("UID"), ConfigurationPreferences.getInstance().getWmfServer()))).parse().tracks;
            for (int i = 0; i < tracks.length; i++) {
                if (tracks[i].id == trackId) {
                    MusicService.startPlayMusic(OdnoklassnikiApplication.getContext(), i, new ArrayList(Arrays.asList(tracks)), MusicListType.STATUS_MUSIC);
                    return;
                }
            }
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }
}
