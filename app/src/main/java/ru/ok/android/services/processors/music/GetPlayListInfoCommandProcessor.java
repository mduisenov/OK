package ru.ok.android.services.processors.music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.java.api.wmf.http.HttpGetTracksForPlayListRequest;
import ru.ok.java.api.wmf.json.JsonGetPlayListInfoParser;

public class GetPlayListInfoCommandProcessor extends CommandProcessor {
    public static final String BASE_COMMAND_NAME;

    static {
        BASE_COMMAND_NAME = GetPlayListInfoCommandProcessor.class.getName();
    }

    public GetPlayListInfoCommandProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static String commandName(long id) {
        return BASE_COMMAND_NAME + "/" + id;
    }

    public static void fillIntent(Intent intent, long id) {
        intent.putExtra("PLAYLIST_ID", id);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        long pid = data.getLongExtra("PLAYLIST_ID", 0);
        String str = "playlist_out_extra";
        outBundle.putParcelable(str, new JsonGetPlayListInfoParser(this._transportProvider.execJsonHttpMethod(new HttpGetTracksForPlayListRequest(pid, 0, 1, ConfigurationPreferences.getInstance().getWmfServer())), pid).parse());
        return 1;
    }

    public static boolean isIt(String commandName) {
        return commandName.startsWith(BASE_COMMAND_NAME);
    }
}
