package ru.ok.android.services.processors.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.registration.RegisterByPhoneRequest;

public class RegisterUserProcessor extends CommandProcessor {
    public static final String COMMAND_NAME;
    public static final String KEY_ACCOUNT_RECOVERY;
    public static final String KEY_LOGIN;
    public static final String KEY_PHONE_ALREADY_LOGIN;
    public static final String KEY_TYPE_ERROR;
    public static final String KEY_TYPE_MESSAGE;
    public static final String KEY_UID;

    static {
        COMMAND_NAME = RegisterUserProcessor.class.getName();
        KEY_TYPE_MESSAGE = COMMAND_NAME + ":key_type_message";
        KEY_TYPE_ERROR = COMMAND_NAME + ":key_type_error";
        KEY_LOGIN = COMMAND_NAME + ":key_type_login";
        KEY_UID = COMMAND_NAME + ":key_type_uid";
        KEY_ACCOUNT_RECOVERY = COMMAND_NAME + ":key_type_account_recovery";
        KEY_PHONE_ALREADY_LOGIN = COMMAND_NAME + ":key_type_phone_already_login";
    }

    public RegisterUserProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static void fillIntent(Intent intent, String login) {
        intent.putExtra(KEY_LOGIN, login);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        return onRegisterUser(context, data.getStringExtra(KEY_LOGIN), outBundle);
    }

    public static boolean isIt(String command) {
        return COMMAND_NAME.equals(command);
    }

    public static String commandName() {
        return COMMAND_NAME;
    }

    private int onRegisterUser(Context context, String login, Bundle outBundle) {
        try {
            Bundle result = registerUser(login, context);
            outBundle.putBoolean(KEY_PHONE_ALREADY_LOGIN, result.getBoolean(KEY_PHONE_ALREADY_LOGIN));
            outBundle.putString(KEY_UID, result.getString(KEY_UID));
            outBundle.putBoolean(KEY_ACCOUNT_RECOVERY, result.getBoolean(KEY_ACCOUNT_RECOVERY));
            return 1;
        } catch (Exception e) {
            Logger.m172d("Error " + e.getMessage());
            outBundle.clear();
            outBundle.putString("errorMessage", e.getMessage());
            CommandProcessor.fillErrorBundle(outBundle, e, true);
            return 2;
        }
    }

    private Bundle registerUser(String login, Context context) throws BaseApiException, JSONException {
        Bundle outBundle = new Bundle();
        BaseRequest request = new RegisterByPhoneRequest(login, Settings.getCurrentLocale(context));
        Log.d("RegisterUserRequest", request.toString());
        JsonHttpResult result = this._transportProvider.execJsonHttpMethod(request);
        Log.d("RegisterUserResult", result.getResultAsString());
        JSONObject jsonObject = result.getResultAsObject();
        String userId = jsonObject.optString("uid");
        boolean isPhoneAlreadyLogin = jsonObject.optBoolean("login_taken", false);
        outBundle.putBoolean(KEY_ACCOUNT_RECOVERY, jsonObject.optBoolean("account_recovery", false));
        outBundle.putString(KEY_UID, userId);
        outBundle.putBoolean(KEY_PHONE_ALREADY_LOGIN, isPhoneAlreadyLogin);
        return outBundle;
    }
}
