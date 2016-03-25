package ru.ok.android.services.processors.music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.java.api.wmf.http.HttpGetStatusMusicRequest;
import ru.ok.java.api.wmf.json.JsonGetMusicParser;

public final class StatusMusicProcessor extends CommandProcessor {
    private static final String BASE_COMMAND_NAME;

    static {
        BASE_COMMAND_NAME = StatusMusicProcessor.class.getName();
    }

    public StatusMusicProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static String commandName() {
        return BASE_COMMAND_NAME;
    }

    public static void fillIntent(Intent intent, long trackId) {
        intent.putExtra("TRACK_ID", trackId);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        Context context2 = context;
        context.startService(MusicService.newPlayIntent(context2, 0, new JsonGetMusicParser(this._transportProvider.execJsonHttpMethod(new HttpGetStatusMusicRequest(0, 100, data.getLongExtra("TRACK_ID", -1), ConfigurationPreferences.getInstance().getWmfServer()))).parse().tracks, MusicListType.STATUS_MUSIC, true, false));
        return 1;
    }
}
