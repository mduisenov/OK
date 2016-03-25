package ru.ok.android.services.processors.presents;

import android.os.Bundle;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONException;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.app.SpritesHelper;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.presents.activity.PresentReceivedActivity;
import ru.ok.android.ui.presents.helpers.PresentSettingsHelper;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.presents.JsonAcceptPresentParser;
import ru.ok.java.api.json.presents.JsonGetPresentNotificationParser;
import ru.ok.java.api.request.param.BaseStringParam;
import ru.ok.java.api.request.presents.AcceptPresentRequest;
import ru.ok.java.api.request.presents.PresentNotificationRequest;
import ru.ok.java.api.response.presents.PresentNotificationResponse;
import ru.ok.model.presents.PresentInfo;
import ru.ok.model.presents.PresentType;

public final class ReceivePresentProcessor {
    @Subscribe(on = 2131623944, to = 2131624014)
    public void loadPresentNotification(@NonNull BusEvent event) {
        try {
            String presentNotificationId = event.bundleInput.getString("PRESENT_NOTIFICATION_ID");
            if (presentNotificationId == null) {
                throw new IllegalArgumentException("Present notification ID can't be null");
            }
            PresentNotificationResponse response = loadPresentNotification(presentNotificationId);
            if (response.presentInfo.sender != null) {
                String senderPicUrl = response.presentInfo.sender.getPicUrl();
                if (senderPicUrl != null) {
                    FrescoOdkl.prefetchSync(senderPicUrl);
                }
            }
            PresentType presentType = response.presentInfo.presentType;
            String presentPicUrl = presentType.photoSize.getUrl();
            String userPicUrl = OdnoklassnikiApplication.getCurrentUser().getPic600();
            if (presentType.isAnimated && PresentSettingsHelper.isAnimatedPresentsEnabled()) {
                SpritesHelper.prefetchSync(presentType, PresentReceivedActivity.getPresentSize(presentType));
            } else if (presentPicUrl != null) {
                FrescoOdkl.prefetchSync(presentPicUrl);
            }
            if (userPicUrl != null) {
                FrescoOdkl.prefetchSync(userPicUrl);
            }
            Bundle output = new Bundle();
            output.putParcelable("PRESENT_NOTIFICATION", response);
            GlobalBus.send(2131624189, new BusEvent(event.bundleInput, output, -1));
        } catch (Exception e) {
            GlobalBus.send(2131624189, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623947)
    public void acceptPresent(@NonNull BusEvent event) {
        processPresent(event, true, 2131624126);
    }

    @Subscribe(on = 2131623944, to = 2131623962)
    public void declinePresent(@NonNull BusEvent event) {
        processPresent(event, false, 2131624141);
    }

    @NonNull
    private PresentNotificationResponse loadPresentNotification(@NonNull String presentNotificationId) throws BaseApiException, JSONException {
        return new JsonGetPresentNotificationParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new PresentNotificationRequest(new BaseStringParam(presentNotificationId))).getResultAsObject()).parse();
    }

    private void processPresent(@NonNull BusEvent event, boolean accept, @AnyRes int resultKind) {
        try {
            JsonHttpResult result = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new AcceptPresentRequest(new BaseStringParam(event.bundleInput.getString("PRESENT_NOTIFICATION_ID")), accept));
            Bundle output = new Bundle();
            if (accept) {
                ArrayList<PresentInfo> presentInfos = new JsonAcceptPresentParser(result.getResultAsObject()).parse();
                output.putParcelableArrayList("PRESENT_INFOS", presentInfos);
                Iterator i$ = presentInfos.iterator();
                while (i$.hasNext()) {
                    PresentInfo presentInfo = (PresentInfo) i$.next();
                    PresentType presentType = presentInfo.presentType;
                    if (presentType.isAnimated) {
                        SpritesHelper.prefetchSync(presentType, PresentReceivedActivity.getPresentSize(presentInfo.presentType));
                    } else {
                        FrescoOdkl.prefetchSync(presentInfo.presentType.photoSize.getUrl());
                    }
                }
            }
            GlobalBus.send(resultKind, new BusEvent(event.bundleInput, output, -1));
        } catch (Exception e) {
            GlobalBus.send(resultKind, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }
}
