package ru.ok.android.services.processors.users;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import org.json.JSONException;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.app.helper.AccountsHelper;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.app.Messages;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.bus.BusProtocol;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.users.JsonCurrentUserBatchParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.batch.SupplierRequest;
import ru.ok.java.api.request.param.RequestJSONParam;
import ru.ok.java.api.request.users.GetCurrentUserInfoRequest;
import ru.ok.java.api.request.users.GetCurrentUserInfoRequest.FIELDS;
import ru.ok.java.api.request.users.UserInfoRequest;
import ru.ok.java.api.response.users.CurrentUserBatchResponse;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.UserInfo;

public final class CurrentUserInfoProcessor {
    @Subscribe(on = 2131623944, to = 2131624051)
    public void getCurrentUserInfo(BusEvent event) {
        int resultCode;
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get current user info processor");
        Messenger replyTo = msg.replyTo;
        Bundle bundleOutput = new Bundle();
        try {
            UserInfo info = getCurrentUserInfo();
            bundleOutput.putParcelable(BusProtocol.USER, info);
            OdnoklassnikiApplication.setCurrentUser(info);
            Context context = OdnoklassnikiApplication.getContext();
            AccountsHelper.registerAccountForUser(context, OdnoklassnikiApplication.getCurrentUser());
            AccountsHelper.storeAuthenticationToken(context, JsonSessionTransportProvider.getInstance().getStateHolder());
            resultCode = -1;
        } catch (Exception e) {
            CommandProcessor.fillErrorBundle(bundleOutput, e);
            Message mes = Message.obtain(null, 45, 0, 0);
            mes.obj = e;
            Messages.safeSendMessage(mes, replyTo);
            resultCode = -2;
        }
        GlobalBus.send(2131624216, new BusEvent(event.bundleInput, bundleOutput, resultCode));
    }

    @Subscribe(on = 2131623944, to = 2131624052)
    public void getCurrentUserInfoNew(BusEvent event) {
        int resultCode;
        Logger.m172d("visit get current user info processor");
        Bundle bundleOutput = new Bundle();
        try {
            UserInfo info = getCurrentUserInfo();
            bundleOutput.putParcelable(BusProtocol.USER, info);
            OdnoklassnikiApplication.setCurrentUser(info);
            AccountsHelper.updateUserInfo(OdnoklassnikiApplication.getContext(), info);
            resultCode = -1;
        } catch (Throwable e) {
            Logger.m178e(e);
            CommandProcessor.fillErrorBundle(bundleOutput, e);
            resultCode = -2;
        }
        GlobalBus.send(2131624216, new BusEvent(null, bundleOutput, resultCode));
    }

    private static UserInfo getCurrentUserInfo() throws Exception {
        BaseRequest currentUserRequest = new GetCurrentUserInfoRequest(new RequestFieldsBuilder().addFields(FIELDS.UID, FIELDS.LOCALE).build());
        return processGetCurrentUserInfoResult(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new BatchRequest(new BatchRequests().addRequest(currentUserRequest).addRequest(new UserInfoRequest(new RequestJSONParam(new SupplierRequest("users.getCurrentUser.uid")), GetUserInfoProcessor.getFieldsString(), true)), false)));
    }

    private static UserInfo processGetCurrentUserInfoResult(JsonHttpResult result) throws BaseApiException, JSONException {
        CurrentUserBatchResponse response = JsonCurrentUserBatchParser.parse(result.getResultAsObject());
        String locale = response.currentUser.locale;
        if (!TextUtils.isEmpty(locale)) {
            LocalizationManager manager = LocalizationManager.from(OdnoklassnikiApplication.getContext());
            if (manager != null) {
                manager.setLocaleTo(locale);
            }
        }
        return response.userInfo;
    }
}
