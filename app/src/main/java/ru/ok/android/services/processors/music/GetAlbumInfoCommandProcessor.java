package ru.ok.android.services.processors.music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.wmf.http.HttpGetTracksForAlbumRequest;
import ru.ok.java.api.wmf.json.JsonAlbumInfoParse;
import ru.ok.java.api.wmf.json.JsonGetMusicParser;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.AlbumInfo;
import ru.ok.model.wmf.GetTracksResponse;

public class GetAlbumInfoCommandProcessor extends CommandProcessor {
    public static final String BASE_COMMAND_NAME;

    static {
        BASE_COMMAND_NAME = GetAlbumInfoCommandProcessor.class.getName();
    }

    public GetAlbumInfoCommandProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static String commandName(long albumId) {
        return BASE_COMMAND_NAME + "/" + albumId;
    }

    public static void fillIntent(Intent intent, long albumId) {
        intent.putExtra("ALBUM_ID", albumId);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        JsonHttpResult result = this._transportProvider.execJsonHttpMethod(new HttpGetTracksForAlbumRequest(data.getLongExtra("ALBUM_ID", 0), ConfigurationPreferences.getInstance().getWmfServer()));
        outBundle.putParcelable("command_album_out_extra", new AlbumInfo(getAlbumTrackValue(result).tracks, getAlbumInfoValue(result)));
        return 1;
    }

    private Album getAlbumInfoValue(JsonHttpResult result) throws BaseApiException {
        return new JsonAlbumInfoParse(result).parse();
    }

    private GetTracksResponse getAlbumTrackValue(JsonHttpResult result) throws BaseApiException {
        return new JsonGetMusicParser(result).parse();
    }

    public static boolean isIt(String commandName) {
        return commandName.startsWith(BASE_COMMAND_NAME);
    }
}
