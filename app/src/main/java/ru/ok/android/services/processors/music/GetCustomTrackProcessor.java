package ru.ok.android.services.processors.music;

import android.os.Bundle;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.java.api.wmf.http.HttpGetCustomTracksRequest;
import ru.ok.java.api.wmf.json.JsonGetMusicParser;
import ru.ok.model.wmf.GetTracksResponse;

public final class GetCustomTrackProcessor {
    @Subscribe(on = 2131623944, to = 2131624024)
    public void customTrack(BusEvent event) {
        try {
            GetTracksResponse result = new JsonGetMusicParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetCustomTracksRequest(event.bundleInput.getLong("track_id"), ConfigurationPreferences.getInstance().getWmfServer()))).parse();
            Bundle bundle = new Bundle();
            bundle.putParcelableArray("key_places_complaint_result", result.tracks);
            GlobalBus.send(2131624199, new BusEvent(event.bundleInput, bundle, -1));
        } catch (Exception e) {
            Bundle errorBundle = new Bundle();
            errorBundle.putSerializable("key_exception_custom_track_result", e);
            GlobalBus.send(2131624199, new BusEvent(event.bundleInput, errorBundle, -2));
        }
    }
}
