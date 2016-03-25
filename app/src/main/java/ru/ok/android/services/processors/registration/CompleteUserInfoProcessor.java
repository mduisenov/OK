package ru.ok.android.services.processors.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.json.JSONException;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.controls.authorization.LoginControl;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.ServiceStateHolder;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.registration.ChangePasswordRequest;
import ru.ok.java.api.request.registration.UpdateUserInfoRequest;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public class CompleteUserInfoProcessor extends CommandProcessor {
    public static final String COMMAND_NAME;
    public static final String KEY_NEW_PW;
    public static final String KEY_OLD_PW;
    public static final String KEY_PERSON_INFO;
    public static final String KEY_TYPE_ERROR;
    public static final String KEY_TYPE_MESSAGE;

    static {
        COMMAND_NAME = CompleteUserInfoProcessor.class.getName();
        KEY_TYPE_MESSAGE = COMMAND_NAME + ":key_type_message";
        KEY_TYPE_ERROR = COMMAND_NAME + ":key_type_error";
        KEY_OLD_PW = COMMAND_NAME + ":key_type_old_pw";
        KEY_NEW_PW = COMMAND_NAME + ":key_type_new_pw";
        KEY_PERSON_INFO = COMMAND_NAME + ":key_type_user_info";
    }

    public CompleteUserInfoProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static void fillIntent(Intent intent, String oldPassword, String newPassword, UserInfo personInfo) {
        intent.putExtra(KEY_OLD_PW, oldPassword);
        intent.putExtra(KEY_NEW_PW, newPassword);
        intent.putExtra(KEY_PERSON_INFO, personInfo);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        return onUpdateInfo(context, data.getStringExtra(KEY_OLD_PW), data.getStringExtra(KEY_NEW_PW), (UserInfo) data.getParcelableExtra(KEY_PERSON_INFO), outBundle);
    }

    public static boolean isIt(String command) {
        return COMMAND_NAME.equals(command);
    }

    public static String commandName() {
        return COMMAND_NAME;
    }

    private int onUpdateInfo(Context context, String oldPassword, String newPassword, UserInfo personInfo, Bundle outBundle) {
        try {
            updateInfo(context, oldPassword, newPassword, personInfo);
            outBundle.putParcelable(KEY_PERSON_INFO, personInfo);
            return 1;
        } catch (Exception e) {
            Logger.m172d("Error " + e.getMessage());
            outBundle.clear();
            outBundle.putString("errorMessage", e.getMessage());
            CommandProcessor.fillErrorBundle(outBundle, e, true);
            return 2;
        }
    }

    private void updateInfo(Context context, String oldPassword, String newPassword, UserInfo personInfo) throws BaseApiException, JSONException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        BatchRequests requests = new BatchRequests();
        int genderToint = personInfo.genderType == UserGenderType.MALE ? 1 : 2;
        String birthday = "";
        try {
            birthday = df.format(personInfo.birthday);
        } catch (Throwable e) {
            Logger.m178e(e);
        }
        requests.addRequest(new UpdateUserInfoRequest(birthday, personInfo.location.countryCode, personInfo.location.city, personInfo.firstName, personInfo.lastName, genderToint));
        BaseRequest changePasswordRequest = null;
        if (!(newPassword.isEmpty() || newPassword.equals(oldPassword))) {
            changePasswordRequest = new ChangePasswordRequest(oldPassword, newPassword);
            requests.addRequest(changePasswordRequest);
        }
        JsonHttpResult jsonHttpResult = this._transportProvider.execJsonHttpMethod(new BatchRequest(requests), this._transportProvider.getStateHolder().getBaseUrl().replace("http://", "https://"));
        if (changePasswordRequest != null) {
            GlobalBus.send(2131624041, new BusEvent());
            String newToken = jsonHttpResult.getResultAsObject().getJSONObject("users_changePassword_response").optString("auth_token", null);
            ServiceStateHolder serviceStateHolder = this._transportProvider.getStateHolder();
            serviceStateHolder.setAuthenticationHash(null);
            Settings.storeStrValue(context, "authHash", null);
            if (newToken != null) {
                Settings.storeToken(context, newToken);
                serviceStateHolder.setAuthenticationToken(newToken);
                LoginControl.generalLoginLogic(context);
                return;
            }
            Settings.storeToken(context, "");
        }
    }
}
