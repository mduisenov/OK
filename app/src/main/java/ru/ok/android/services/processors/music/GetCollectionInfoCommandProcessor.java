package ru.ok.android.services.processors.music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.java.api.wmf.http.HttpGetCollectionInfoRequest;
import ru.ok.java.api.wmf.json.JsonGetMusicCollectionParser;

public class GetCollectionInfoCommandProcessor extends CommandProcessor {
    public static final String BASE_COMMAND_NAME;

    static {
        BASE_COMMAND_NAME = GetCollectionInfoCommandProcessor.class.getName();
    }

    public GetCollectionInfoCommandProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static String commandName(long collectionId) {
        return BASE_COMMAND_NAME + "/" + collectionId;
    }

    public static void fillIntent(Intent intent, long collectionId) {
        intent.putExtra("COLLECTION_ID", collectionId);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        String str = "collection_out_extra";
        outBundle.putParcelable(str, new JsonGetMusicCollectionParser(this._transportProvider.execJsonHttpMethod(new HttpGetCollectionInfoRequest(data.getLongExtra("COLLECTION_ID", 0), ConfigurationPreferences.getInstance().getWmfServer()))).parse());
        return 1;
    }

    public static boolean isIt(String commandName) {
        return commandName.startsWith(BASE_COMMAND_NAME);
    }
}
