package ru.ok.android.services.processors.video;

import android.os.Bundle;
import java.util.ArrayList;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.video.MoviesParser;
import ru.ok.java.api.request.video.HttpGetSimilarMoviesRequest;
import ru.ok.java.api.request.video.HttpGetSimilarMoviesRequest.MOVIE_FIELDS;
import ru.ok.model.video.MovieInfo;

public final class GetSimilarMoviesProcessor {
    @Subscribe(on = 2131623944, to = 2131624125)
    public void getSimilarMoviesInfo(BusEvent e) {
        try {
            ArrayList<MovieInfo> resultParse = new MoviesParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetSimilarMoviesRequest(e.bundleInput.getString("VIDEO_ID"), 100, MOVIE_FIELDS.values())).getResultAsObject()).parse();
            Bundle output = new Bundle();
            output.putParcelableArrayList("MOVIES_INFOS", resultParse);
            GlobalBus.send(2131624268, new BusEvent(e.bundleInput, output, -1));
        } catch (Exception ex) {
            Logger.m180e(ex, "Failed to fetch movies infos id: %s", videoId);
            GlobalBus.send(2131624268, new BusEvent(e.bundleInput, CommandProcessor.createErrorBundle(ex), -2));
        }
    }
}
