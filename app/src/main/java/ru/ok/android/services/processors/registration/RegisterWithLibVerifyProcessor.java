package ru.ok.android.services.processors.registration;

import android.os.Bundle;
import android.os.Parcelable;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.model.UserWithLogin;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.controls.nativeregistration.RegistrationConstants;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.json.users.JsonUserInfoParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.registration.RegisterWithLibVerifyRequest;

public final class RegisterWithLibVerifyProcessor {
    public static void process(String sessionId, String token, String phone) {
        Bundle bundle = new Bundle();
        bundle.putString(RegistrationConstants.KEY_TOKEN, token);
        bundle.putString(RegistrationConstants.KEY_SESSION_ID, sessionId);
        bundle.putString("phone", phone);
        GlobalBus.send(2131624095, new BusEvent(bundle));
    }

    @Subscribe(on = 2131623944, to = 2131624095)
    public void registerWithLibVerify(BusEvent event) {
        String token = event.bundleInput.getString(RegistrationConstants.KEY_TOKEN);
        String sessionId = event.bundleInput.getString(RegistrationConstants.KEY_SESSION_ID);
        String phone = event.bundleInput.getString("phone");
        BaseRequest request = new RegisterWithLibVerifyRequest(token, sessionId, phone, Settings.getCurrentLocale(OdnoklassnikiApplication.getContext()));
        JsonSessionTransportProvider transportProvider = JsonSessionTransportProvider.getInstance();
        Bundle bundleOutput = new Bundle();
        int resultCode = -1;
        try {
            JSONObject jsonObject = transportProvider.execJsonHttpMethod(request).getResultAsObject();
            boolean isPhoneAlreadyLogin = jsonObject.optBoolean("login_taken", false);
            boolean isAccountRecovery = jsonObject.optBoolean("account_recovery", false);
            String userId = jsonObject.optString("uid", null);
            String pin = jsonObject.optString("code", null);
            String registeredToken = jsonObject.optString("auth_token", null);
            ArrayList<UserWithLogin> users = getUsersFromJson(jsonObject, phone, isAccountRecovery);
            bundleOutput.putBoolean("account_recovery", isAccountRecovery);
            bundleOutput.putBoolean("phone_already_login", isPhoneAlreadyLogin);
            bundleOutput.putString("uid", userId);
            bundleOutput.putString("pin", pin);
            bundleOutput.putString(RegistrationConstants.KEY_TOKEN, registeredToken);
            if (isAccountRecovery) {
                bundleOutput.putParcelable("user_info", (Parcelable) users.get(0));
            } else {
                bundleOutput.putParcelableArrayList("user_list", users);
            }
        } catch (Throwable e) {
            Logger.m178e(e);
            CommandProcessor.fillErrorBundle(bundleOutput, e);
            resultCode = -2;
        }
        GlobalBus.send(2131624239, new BusEvent(event.bundleInput, bundleOutput, resultCode));
    }

    private ArrayList<UserWithLogin> getUsersFromJson(JSONObject jsonObject, String phone, boolean isAccountRecovery) throws JSONException, ResultParsingException {
        JSONArray jsonArray = jsonObject.optJSONArray("users");
        if (jsonArray == null || jsonArray.length() <= 0) {
            return null;
        }
        ArrayList<UserWithLogin> arrayList = new ArrayList();
        for (int i = 0; i < jsonArray.length(); i++) {
            String optString;
            JSONObject jsonUser = jsonArray.getJSONObject(i);
            UserWithLogin userInfo = new UserWithLogin(JsonUserInfoParser.parse(jsonUser));
            if (isAccountRecovery) {
                optString = jsonUser.optString("login");
            } else {
                optString = phone;
            }
            userInfo.login = optString;
            userInfo.picUrl = jsonUser.optString("pic128x128");
            arrayList.add(userInfo);
        }
        return arrayList;
    }
}
