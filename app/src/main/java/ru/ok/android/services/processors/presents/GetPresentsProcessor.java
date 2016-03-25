package ru.ok.android.services.processors.presents;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.storage.Storages;
import ru.ok.java.api.json.presents.JsonGetPresentsParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.presents.PresentsRequest;
import ru.ok.java.api.request.presents.PresentsRequest.Direction;
import ru.ok.java.api.request.presents.PresentsRequest.Exclude;
import ru.ok.java.api.response.presents.PresentsResponse;
import ru.ok.model.presents.PresentInfo;

public class GetPresentsProcessor {
    @Subscribe(on = 2131623944, to = 2131624012)
    public void loadPresents(@NonNull BusEvent event) {
        try {
            String userId = event.bundleInput.getString("EXTRA_USER_ID");
            PresentsResponse presentsResponse = new JsonGetPresentsParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(createSentPresentsRequest(userId, event.bundleInput.getString("EXTRA_ANCHOR"), (Direction) event.bundleInput.getSerializable("EXTRA_PRESENT_DIRECTION"))).getResultAsObject()).parse();
            if (userId == null) {
                preloadDeletedPresents(presentsResponse.presents);
            }
            Bundle output = new Bundle();
            output.putParcelable("EXTRA_PRESENTS_RESPONSE", presentsResponse);
            GlobalBus.send(2131624187, new BusEvent(event.bundleInput, output, -1));
        } catch (Exception e) {
            GlobalBus.send(2131624187, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    private static void preloadDeletedPresents(@NonNull List<PresentInfo> presentInfos) {
        ArrayList<String> ids = new ArrayList();
        for (PresentInfo presentInfo : presentInfos) {
            ids.add(presentInfo.id);
        }
        Storages.getInstance(OdnoklassnikiApplication.getContext(), OdnoklassnikiApplication.getCurrentUser().getId()).getDeletedFeedsManager().preload(ids);
    }

    @NonNull
    private static BaseRequest createSentPresentsRequest(@Nullable String userId, @Nullable String anchor, @NonNull Direction direction) {
        String fields = "present.*, present_type.*, user.name, user.gender, user.pic50x50";
        Exclude exclude = null;
        if (direction == Direction.SENT) {
            exclude = Exclude.BADGE;
        }
        return new PresentsRequest(userId, direction, exclude, anchor, null, null, null, "present.*, present_type.*, user.name, user.gender, user.pic50x50");
    }
}
