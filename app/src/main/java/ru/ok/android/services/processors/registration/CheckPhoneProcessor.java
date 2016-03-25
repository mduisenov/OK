package ru.ok.android.services.processors.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.model.UserWithLogin;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.json.users.JsonUserInfoParser;
import ru.ok.java.api.request.registration.CheckPhoneRequest;

public class CheckPhoneProcessor extends CommandProcessor {
    public static final String COMMAND_NAME;
    public static final String KEY_PHONE;
    public static final String KEY_PIN;
    public static final String KEY_TYPE_ERROR;
    public static final String KEY_TYPE_MESSAGE;
    public static final String KEY_UID;
    public static final String KEY_USER_LIST;
    public static final String KEY_USER_TOKEN;

    static {
        COMMAND_NAME = CheckPhoneProcessor.class.getName();
        KEY_TYPE_MESSAGE = COMMAND_NAME + ":key_type_message";
        KEY_TYPE_ERROR = COMMAND_NAME + ":key_type_error";
        KEY_PIN = COMMAND_NAME + ":key_type_pin";
        KEY_UID = COMMAND_NAME + ":key_type_uid";
        KEY_PHONE = COMMAND_NAME + ":key_type_phone";
        KEY_USER_LIST = COMMAND_NAME + ":key_type_user_list";
        KEY_USER_TOKEN = COMMAND_NAME + ":key_type_user_token";
    }

    public CheckPhoneProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static void fillIntent(Intent intent, String uid, String phone, String pin) {
        intent.putExtra(KEY_PIN, pin);
        intent.putExtra(KEY_UID, uid);
        intent.putExtra(KEY_PHONE, phone);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        String uid = data.getStringExtra(KEY_UID);
        String pin = data.getStringExtra(KEY_PIN);
        return onConfirmUser(uid, data.getStringExtra(KEY_PHONE), pin, outBundle, context);
    }

    public static boolean isIt(String command) {
        return COMMAND_NAME.equals(command);
    }

    public static String commandName() {
        return COMMAND_NAME;
    }

    private int onConfirmUser(String uid, String phone, String pin, Bundle outBundle, Context context) {
        try {
            Bundle bundle = checkPhone(uid, phone, pin, context);
            ArrayList<UserWithLogin> userInfos = (ArrayList) bundle.getSerializable(KEY_USER_LIST);
            if (userInfos != null) {
                outBundle.putSerializable(KEY_USER_LIST, userInfos);
                return 1;
            }
            outBundle.putString(KEY_USER_TOKEN, bundle.getString(KEY_USER_TOKEN));
            return 1;
        } catch (Exception e) {
            Logger.m172d("Error " + e.getMessage());
            outBundle.clear();
            outBundle.putString("errorMessage", e.getMessage());
            CommandProcessor.fillErrorBundle(outBundle, e, true);
            return 2;
        }
    }

    private ArrayList<UserWithLogin> getUsersFromJson(JSONObject jsonObject, String phone) {
        if (!jsonObject.has("users")) {
            return null;
        }
        ArrayList<UserWithLogin> userInfos = new ArrayList();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("users");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonUser = jsonArray.getJSONObject(i);
                UserWithLogin userInfo = new UserWithLogin(JsonUserInfoParser.parse(jsonUser));
                userInfo.login = phone;
                userInfo.picUrl = jsonUser.optString("pic128x128");
                userInfos.add(userInfo);
            }
            return userInfos;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (ResultParsingException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    private Bundle checkPhone(String userId, String phone, String pin, Context context) throws BaseApiException, JSONException {
        Bundle result = new Bundle();
        JsonHttpResult jsonHttpResult = this._transportProvider.execJsonHttpMethod(new CheckPhoneRequest(userId, pin, Settings.getCurrentLocale(context)));
        ArrayList<UserWithLogin> userInfos = getUsersFromJson(jsonHttpResult.getResultAsObject(), phone);
        if (userInfos == null || userInfos.size() <= 0) {
            result.putString(KEY_USER_TOKEN, jsonHttpResult.getResultAsObject().getString("auth_token"));
        } else {
            result.putSerializable(KEY_USER_LIST, userInfos);
        }
        return result;
    }
}
