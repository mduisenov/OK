package ru.ok.android.services.processors.photo;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.photo.JsonGetTagsParser;
import ru.ok.java.api.request.image.GetTagsRequest;

public final class GetPhotoTagsProcessor {
    @Subscribe(on = 2131623944, to = 2131624004)
    public void getPhotoTags(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resultCode = -2;
        String photoId = bundleInput.getString("pid");
        bundleOutput.putString("pid", photoId);
        try {
            List[] parseResult = JsonGetTagsParser.parse(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new GetTagsRequest(photoId)).getResultAsObject());
            bundleOutput.putParcelableArrayList("tags", (ArrayList) parseResult[1]);
            bundleOutput.putParcelableArrayList("usrs", (ArrayList) parseResult[0]);
            resultCode = -1;
        } catch (Throwable exc) {
            Logger.m178e(exc);
        }
        GlobalBus.send(2131624181, new BusEvent(bundleInput, bundleOutput, resultCode));
    }
}
