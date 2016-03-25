package ru.ok.android.services.processors.presents;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.util.Locale;
import java.util.Map;
import org.json.JSONException;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.app.SpritesHelper;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.presents.activity.SendPresentActivity;
import ru.ok.android.ui.presents.helpers.PresentSettingsHelper;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.JsonGetTranslationsParser;
import ru.ok.java.api.json.presents.JsonGetSendInfoBatchParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.TranslationsRequest;
import ru.ok.java.api.request.TranslationsRequest.Item;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.param.BaseStringParam;
import ru.ok.java.api.request.payment.UserBalancesRequest;
import ru.ok.java.api.request.presents.PresentInfoRequest;
import ru.ok.java.api.request.presents.SendPresentRequest;
import ru.ok.java.api.request.users.UserInfoRequest;
import ru.ok.java.api.request.users.UserInfoRequest.FIELDS;
import ru.ok.java.api.response.presents.SendInfoResponse;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.presents.PresentType;

public class SendPresentProcessor {
    @Subscribe(on = 2131623944, to = 2131624013)
    public void loadPresentAndUser(@NonNull BusEvent event) {
        try {
            String presentId = event.bundleInput.getString("EXTRA_PRESENT_ID");
            String userId = event.bundleInput.getString("EXTRA_USER_ID");
            SendInfoResponse response = loadInfo(userId, presentId);
            loadImages(response);
            String localizedName = (String) loadTranslations(userId, response.presentInfo.presentType.isLive).get("present_to");
            if (TextUtils.isEmpty(localizedName)) {
                throw new IllegalStateException("present_to not found in translations response");
            }
            response.localizedName = localizedName.substring(0, 1).toUpperCase() + localizedName.substring(1);
            Bundle output = new Bundle();
            output.putParcelable("EXTRA_RESPONSE", response);
            GlobalBus.send(2131624188, new BusEvent(event.bundleInput, output, -1));
        } catch (Exception e) {
            GlobalBus.send(2131624188, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131624100)
    public void sendPresent(@NonNull BusEvent event) {
        try {
            String presentId = event.bundleInput.getString("EXTRA_PRESENT_ID");
            JsonHttpResult result = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new SendPresentRequest(event.bundleInput.getString("EXTRA_USER_ID"), presentId, event.bundleInput.getString("EXTRA_MESSAGE"), event.bundleInput.getString("EXTRA_TOKEN"), event.bundleInput.getString("EXTRA_HOLIDAY_ID"), event.bundleInput.getString("EXTRA_PRESENT_TYPE")));
            Bundle output = new Bundle();
            output.putString("EXTRA_RESULT", result.getResultAsObject().getString("result"));
            GlobalBus.send(2131624242, new BusEvent(event.bundleInput, output, -1));
        } catch (Exception e) {
            GlobalBus.send(2131624242, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    private void loadImages(@NonNull SendInfoResponse response) {
        String presentPicUrl = response.presentInfo.presentType.photoSize.getUrl();
        String userPicUrl = response.userInfo.getPic288();
        PresentType presentType = response.presentInfo.presentType;
        if (presentType.isAnimated && PresentSettingsHelper.isAnimatedPresentsEnabled()) {
            SpritesHelper.prefetchSync(presentType, SendPresentActivity.getPresentSize(presentType.isLive, TextUtils.equals(response.userInfo.uid, OdnoklassnikiApplication.getCurrentUser().getId())));
        } else if (presentPicUrl != null) {
            FrescoOdkl.prefetchSync(presentPicUrl);
        }
        if (userPicUrl != null) {
            FrescoOdkl.prefetchSync(userPicUrl);
        }
    }

    @NonNull
    private SendInfoResponse loadInfo(@NonNull String userId, @NonNull String presentId) throws JSONException, BaseApiException {
        return new JsonGetSendInfoBatchParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(createBatchRequest(userId, presentId)).getResultAsObject()).parse();
    }

    @NonNull
    private Map<String, String> loadTranslations(@NonNull String userId, boolean isLive) throws BaseApiException, JSONException {
        return new JsonGetTranslationsParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(createUserNameTranslationRequest(userId, isLive)).getResultAsObject()).parse();
    }

    @NonNull
    private BaseRequest createBatchRequest(@NonNull String userId, @NonNull String presentId) throws JSONException, BaseApiException {
        return new BatchRequest(new BatchRequests().addRequest(createUserInfoRequest(userId)).addRequest(createPresentInfoRequest(presentId)).addRequest(createUserBalanceRequest()));
    }

    @NonNull
    private BaseRequest createUserInfoRequest(@NonNull String userId) throws BaseApiException, JSONException {
        return new UserInfoRequest(new BaseStringParam(userId), new RequestFieldsBuilder().addFields(FIELDS.NAME, FIELDS.FIRST_NAME, FIELDS.GENDER, FIELDS.PIC_288x288).build(), true);
    }

    @NonNull
    private BaseRequest createPresentInfoRequest(@NonNull String presentId) throws BaseApiException, JSONException {
        return new PresentInfoRequest(new BaseStringParam(presentId));
    }

    @NonNull
    private BaseRequest createUserBalanceRequest() {
        return new UserBalancesRequest(new BaseStringParam("PRESENTS"));
    }

    @NonNull
    private BaseRequest createUserNameTranslationRequest(@NonNull String userId, boolean isLive) {
        return new TranslationsRequest("PresentNotificationBean", null, "present_to", new Item[]{new Item("user", "sender", userId), new Item("isLive", String.valueOf(isLive))}, Locale.getDefault().toString());
    }
}
