package ru.ok.android.services.processors.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.model.UserWithLogin;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.users.JsonUserInfoParser;
import ru.ok.java.api.request.registration.GetExistingUserBySmsRequest;

public class GetExistingUserBySmsProcessor extends CommandProcessor {
    public static final String COMMAND_NAME;
    public static final String KEY_LOGIN;
    public static final String KEY_PHONE;
    public static final String KEY_PIN;
    public static final String KEY_TYPE_ERROR;
    public static final String KEY_TYPE_MESSAGE;
    public static final String KEY_USER;

    static {
        COMMAND_NAME = GetExistingUserBySmsProcessor.class.getName();
        KEY_TYPE_MESSAGE = COMMAND_NAME + ":key_type_message";
        KEY_TYPE_ERROR = COMMAND_NAME + ":key_type_error";
        KEY_PIN = COMMAND_NAME + ":key_type_pin";
        KEY_PHONE = COMMAND_NAME + ":key_type_phone";
        KEY_USER = COMMAND_NAME + ":key_type_user";
        KEY_LOGIN = COMMAND_NAME + ":key_type_login";
    }

    public GetExistingUserBySmsProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static void fillIntent(Intent intent, String phone, String pin) {
        intent.putExtra(KEY_PIN, pin);
        intent.putExtra(KEY_PHONE, phone);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        return getExistingUser(context, data.getStringExtra(KEY_PHONE), data.getStringExtra(KEY_PIN), outBundle);
    }

    public static boolean isIt(String command) {
        return COMMAND_NAME.equals(command);
    }

    public static String commandName() {
        return COMMAND_NAME;
    }

    private int getExistingUser(Context context, String phone, String pin, Bundle outBundle) {
        try {
            outBundle.putParcelable(KEY_USER, getUser(context, phone, pin));
            return 1;
        } catch (Exception e) {
            Logger.m172d("Error " + e.getMessage());
            outBundle.clear();
            outBundle.putString("errorMessage", e.getMessage());
            CommandProcessor.fillErrorBundle(outBundle, e, true);
            return 2;
        }
    }

    private UserWithLogin getUser(Context context, String phone, String pin) throws BaseApiException, JSONException {
        JSONObject jsonObject = this._transportProvider.execJsonHttpMethod(new GetExistingUserBySmsRequest(phone, pin)).getResultAsObject().getJSONObject("user");
        UserWithLogin user = new UserWithLogin(JsonUserInfoParser.parse(jsonObject));
        user.picUrl = jsonObject.optString("pic128x128");
        user.login = jsonObject.optString("login");
        return user;
    }
}
