package ru.ok.android.services.processors.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.access.AuthorizedUsersStorageFacade;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.services.transport.exception.TransportLevelException;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ReferrerStorage;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.ServiceStateHolder;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.exceptions.NotSessionKeyException;
import ru.ok.java.api.exceptions.ServerReturnErrorException;
import ru.ok.java.api.exceptions.VerificationException;
import ru.ok.java.api.json.JsonResultLoginParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.GetCurrentUserHomePageRequest;
import ru.ok.java.api.request.GetRedirectRequest;
import ru.ok.java.api.request.LoginTokenRequest;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.http.RequestHttpSerializer;
import ru.ok.model.login.ResultLogin;

public class LoginByTokenProcessorNew extends LoginProcessorNew {
    public static final String COMMAND_NAME;
    public static final String KEY_FORCE_LOGIN;
    public static final String KEY_TOKEN;

    static {
        COMMAND_NAME = LoginByTokenProcessorNew.class.getName();
        KEY_TOKEN = COMMAND_NAME + ":key_token";
        KEY_FORCE_LOGIN = COMMAND_NAME + ":key_force_login";
    }

    public LoginByTokenProcessorNew(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static boolean isIt(String command) {
        return COMMAND_NAME.equals(command);
    }

    public static String commandName() {
        return COMMAND_NAME;
    }

    public static void fillIntent(Intent intent, String token, boolean forceLogin, String verificationToken) {
        intent.putExtra(KEY_TOKEN, token);
        intent.putExtra(KEY_FORCE_LOGIN, forceLogin);
        intent.putExtra(KEY_VERIFICATION_TOKEN, verificationToken);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        String token = data.getStringExtra(KEY_TOKEN);
        boolean forceLogin = data.getBooleanExtra(KEY_FORCE_LOGIN, false);
        String verificationToken = data.getStringExtra(KEY_VERIFICATION_TOKEN);
        outBundle.putString(KEY_TOKEN, token);
        outBundle.putBoolean(KEY_FORCE_LOGIN, forceLogin);
        return onLogin(context, token, verificationToken, forceLogin, outBundle);
    }

    public boolean hasSessionData() {
        return !TextUtils.isEmpty(this._transportProvider.getStateHolder().getSessionKey());
    }

    private int onLogin(Context context, String token, String verificationToken, boolean forceLogin, Bundle outBundle) {
        try {
            if (!hasSessionData() || forceLogin) {
                ResultLogin holder = login(token, verificationToken);
                if (TextUtils.isEmpty(holder.verificationUrl)) {
                    Settings.storeToken(context, holder.authenticationToken);
                    Settings.storeStrValue(context, "authHash", holder.authenticationHash);
                    AuthorizedUsersStorageFacade.addUser(holder.uid, holder.authenticationToken);
                    Settings.setAuthorizedUserCount(context, AuthorizedUsersStorageFacade.getAuthorizedUsersCount());
                    toDo(context, forceLogin, outBundle);
                } else {
                    outBundle.putString("verificationUrl", holder.verificationUrl);
                }
                ReferrerStorage.clear(context);
                return 1;
            }
            ReferrerStorage.clear(context);
            toDo(context, forceLogin, outBundle);
            return 1;
        } catch (Exception e) {
            Settings.storeStrValue(context, "userUrl", "");
            outBundle.putString("errorMessage", e.getMessage());
            if (e instanceof VerificationException) {
                VerificationException ve = (VerificationException) e;
                outBundle.putInt(KEY_TYPE_ERROR, ve.getErrorCode());
                outBundle.putInt(KEY_TYPE_MESSAGE, 10);
                outBundle.putString("verificationUrl", ve.getVerificationUrl());
            } else if (e instanceof ServerReturnErrorException) {
                outBundle.putInt(KEY_TYPE_ERROR, ((ServerReturnErrorException) e).getErrorCode());
                outBundle.putInt(KEY_TYPE_MESSAGE, 10);
            } else if (e instanceof TransportLevelException) {
                outBundle.putInt(KEY_TYPE_MESSAGE, 9);
            }
            return 2;
        }
    }

    public ResultLogin login(String token, String verificationToken) throws BaseApiException {
        try {
            return performLogin(token, verificationToken);
        } catch (BaseApiException e) {
            if (e instanceof ServerReturnErrorException) {
                ServerReturnErrorException se = (ServerReturnErrorException) e;
                if (se.getErrorCode() == 401 && (se.getErrorMessage().equals("AUTH_LOGIN : BLOCKED") || se.getErrorMessage().equals("AUTH_LOGIN : INVALID_CREDENTIALS") || se.getErrorMessage().equals("AUTH_LOGIN : LOGOUT_ALL"))) {
                    this._transportProvider.getStateHolder().clear();
                }
            }
            throw e;
        }
    }

    private void toDo(Context context, boolean forceLogin, Bundle outBundle) throws SerializeException, NotSessionKeyException {
        String url = "";
        String redirectUrl = Settings.getStrValue(context, "newUrl");
        if (TextUtils.isEmpty(redirectUrl)) {
            url = new RequestHttpSerializer(this._transportProvider.getStateHolder()).serialize(new GetCurrentUserHomePageRequest(this._transportProvider.getWebBaseUrl())).getURI().toString();
        } else {
            url = new RequestHttpSerializer(this._transportProvider.getStateHolder()).serialize(new GetRedirectRequest(ConfigurationPreferences.getInstance().getWebServer(), redirectUrl, null)).getURI().toString();
            Settings.clearSettingByKey(context, "newUrl");
        }
        outBundle.putString("userUrl", url);
    }

    private ResultLogin performLogin(String token, String verificationToken) throws BaseApiException {
        Logger.m173d(">>> token=%s", Logger.logSecret(token));
        Context context = OdnoklassnikiApplication.getContext();
        String str = token;
        String str2 = verificationToken;
        BaseRequest loginRequest = new LoginTokenRequest(str, str2, DeviceUtils.getDeviceId(context), ReferrerStorage.getReferrer(context), true, "1");
        ServiceStateHolder stateHolder = this._transportProvider.getStateHolder();
        Logger.m173d("stateHolder=%s", stateHolder);
        Logger.m173d("result=%s", this._transportProvider.execJsonHttpMethod(loginRequest));
        ResultLogin resultLogin = new JsonResultLoginParser(result).parse();
        this._transportProvider.getStateHolder().setLoginInfo(resultLogin, false);
        OdnoklassnikiApplication.onLoggedInUserId(this._transportProvider.getStateHolder().getUserId());
        this._transportProvider.getStateHolder().setAuthenticationToken(token);
        resultLogin.authenticationToken = token;
        Logger.m172d("<<< login performed Ok");
        return resultLogin;
    }
}
