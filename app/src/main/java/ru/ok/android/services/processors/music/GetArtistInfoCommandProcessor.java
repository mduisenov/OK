package ru.ok.android.services.processors.music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.wmf.http.HttpGetTracksForArtistRequest;
import ru.ok.java.api.wmf.json.JsonArtistInfoParser;
import ru.ok.java.api.wmf.json.JsonGetMusicParser;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.ArtistInfo;
import ru.ok.model.wmf.GetTracksResponse;

public class GetArtistInfoCommandProcessor extends CommandProcessor {
    public static final String BASE_COMMAND_NAME;

    static {
        BASE_COMMAND_NAME = GetArtistInfoCommandProcessor.class.getName();
    }

    public GetArtistInfoCommandProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static String commandName(long artistId) {
        return BASE_COMMAND_NAME + "/" + artistId;
    }

    public static void fillIntent(Intent intent, long artistId) {
        intent.putExtra("ARTIST_ID", artistId);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        JsonHttpResult result = this._transportProvider.execJsonHttpMethod(new HttpGetTracksForArtistRequest(data.getLongExtra("ARTIST_ID", 0), ConfigurationPreferences.getInstance().getWmfServer()));
        outBundle.putParcelable("command_album_out_extra", new ArtistInfo(getArtistValue(result), getArtistTracksValue(result).tracks));
        return 1;
    }

    private GetTracksResponse getArtistTracksValue(JsonHttpResult result) throws BaseApiException {
        return new JsonGetMusicParser(result).parse();
    }

    private Artist getArtistValue(JsonHttpResult result) throws BaseApiException {
        return new JsonArtistInfoParser(result).parse();
    }

    public static boolean isIt(String commandName) {
        return commandName.startsWith(BASE_COMMAND_NAME);
    }
}
