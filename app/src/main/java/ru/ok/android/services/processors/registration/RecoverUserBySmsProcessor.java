package ru.ok.android.services.processors.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import org.json.JSONException;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.request.registration.RecoverUserBySmsRequest;

public class RecoverUserBySmsProcessor extends CommandProcessor {
    public static final String COMMAND_NAME;
    public static final String KEY_PASSWORD;
    public static final String KEY_PIN;
    public static final String KEY_TOKEN;
    public static final String KEY_TYPE_ERROR;
    public static final String KEY_TYPE_MESSAGE;
    public static final String KEY_UID;

    static {
        COMMAND_NAME = RecoverUserBySmsProcessor.class.getName();
        KEY_TYPE_MESSAGE = COMMAND_NAME + ":key_type_message";
        KEY_TYPE_ERROR = COMMAND_NAME + ":key_type_error";
        KEY_PIN = COMMAND_NAME + ":key_type_pin";
        KEY_UID = COMMAND_NAME + ":key_type_uid";
        KEY_TOKEN = COMMAND_NAME + ":key_type_token";
        KEY_PASSWORD = COMMAND_NAME + ":key_type_password";
    }

    public RecoverUserBySmsProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static void fillIntent(Intent intent, String uid, String pin, String password) {
        intent.putExtra(KEY_PIN, pin);
        intent.putExtra(KEY_UID, uid);
        intent.putExtra(KEY_PASSWORD, password);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        return onRecoverUser(context, data.getStringExtra(KEY_UID), data.getStringExtra(KEY_PIN), data.getStringExtra(KEY_PASSWORD), outBundle);
    }

    public static boolean isIt(String command) {
        return COMMAND_NAME.equals(command);
    }

    public static String commandName() {
        return COMMAND_NAME;
    }

    private int onRecoverUser(Context context, String uid, String pin, String password, Bundle outBundle) {
        try {
            recoverUser(context, uid, pin, password);
            return 1;
        } catch (Exception e) {
            Logger.m172d("Error " + e.getMessage());
            outBundle.clear();
            outBundle.putString("errorMessage", e.getMessage());
            CommandProcessor.fillErrorBundle(outBundle, e, true);
            return 2;
        }
    }

    private void recoverUser(Context context, String userId, String pin, String password) throws BaseApiException, JSONException {
        this._transportProvider.execJsonHttpMethod(new RecoverUserBySmsRequest(userId, pin, password));
    }
}
