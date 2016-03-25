package ru.ok.android.services.processors.video;

import android.os.Bundle;
import android.support.annotation.AnyRes;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.JsonLikeInfoParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.like.LikeRequest;
import ru.ok.java.api.request.like.UnLikeRequest;
import ru.ok.model.stream.LikeInfo;

public final class VideoLikeProcessor {
    @Subscribe(on = 2131623944, to = 2131624010)
    public void like(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        processRequest(2131624185, event, new LikeRequest(bundleInput.getString("like_id"), bundleInput.getString("LOG_CONTEXT")));
    }

    @Subscribe(on = 2131623944, to = 2131624118)
    public void unlike(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        processRequest(2131624262, event, new UnLikeRequest(bundleInput.getString("like_id"), bundleInput.getString("LOG_CONTEXT")));
    }

    private void processRequest(@AnyRes int kind, BusEvent event, BaseRequest request) {
        Bundle bundleOutput;
        int resultCode = -2;
        try {
            LikeInfo resultLikeInfo = new JsonLikeInfoParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(request).getResultAsObject().optJSONObject("summary")).parse();
            bundleOutput = new Bundle();
            bundleOutput.putParcelable("like_info", resultLikeInfo);
            resultCode = -1;
        } catch (Exception exc) {
            bundleOutput = CommandProcessor.createErrorBundle(exc);
            Logger.m180e(exc, "Can't like/unlike video object: %s", request);
        }
        GlobalBus.send(kind, new BusEvent(event.bundleInput, bundleOutput, resultCode));
    }
}
