package ru.ok.android.services.processors.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import org.json.JSONException;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.request.registration.UserConfirmationByPhoneRequest;

public class ConfirmUserProcessor extends CommandProcessor {
    public static final String COMMAND_NAME;
    public static final String KEY_LOGIN;
    public static final String KEY_NEW_PASSWORD;
    public static final String KEY_PIN;
    public static final String KEY_TOKEN;
    public static final String KEY_TYPE_ERROR;
    public static final String KEY_TYPE_MESSAGE;
    public static final String KEY_UID;

    static {
        COMMAND_NAME = ConfirmUserProcessor.class.getName();
        KEY_TYPE_MESSAGE = COMMAND_NAME + ":key_type_message";
        KEY_TYPE_ERROR = COMMAND_NAME + ":key_type_error";
        KEY_PIN = COMMAND_NAME + ":key_type_pin";
        KEY_UID = COMMAND_NAME + ":key_type_uid";
        KEY_TOKEN = COMMAND_NAME + ":key_type_token";
        KEY_LOGIN = COMMAND_NAME + ":key_type_login";
        KEY_NEW_PASSWORD = COMMAND_NAME + ":key_type_new_password";
    }

    public ConfirmUserProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static void fillIntent(Intent intent, String uid, String login, String pin, String newPassword) {
        intent.putExtra(KEY_PIN, pin);
        intent.putExtra(KEY_NEW_PASSWORD, newPassword);
        intent.putExtra(KEY_UID, uid);
        intent.putExtra(KEY_LOGIN, login);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        String uid = data.getStringExtra(KEY_UID);
        String pin = data.getStringExtra(KEY_PIN);
        String newPassword = data.getStringExtra(KEY_NEW_PASSWORD);
        return onConfirmUser(context, uid, data.getStringExtra(KEY_LOGIN), pin, newPassword, outBundle);
    }

    public static boolean isIt(String command) {
        return COMMAND_NAME.equals(command);
    }

    public static String commandName() {
        return COMMAND_NAME;
    }

    private int onConfirmUser(Context context, String uid, String login, String pin, String newPassword, Bundle outBundle) {
        try {
            outBundle.putString(KEY_TOKEN, confirmUser(context, uid, login, pin, newPassword));
            return 1;
        } catch (Exception e) {
            Logger.m172d("Error " + e.getMessage());
            outBundle.clear();
            outBundle.putString("errorMessage", e.getMessage());
            CommandProcessor.fillErrorBundle(outBundle, e, true);
            return 2;
        }
    }

    private String confirmUser(Context context, String userId, String login, String pin, String newPassword) throws BaseApiException, JSONException {
        return this._transportProvider.execJsonHttpMethod(new UserConfirmationByPhoneRequest(userId, login, pin, newPassword, Settings.getCurrentLocale(context))).getResultAsObject().getString("auth_token");
    }
}
