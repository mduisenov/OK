package ru.ok.android.services.processors.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import javax.net.ssl.SSLHandshakeException;
import ru.ok.android.app.MyTrackerUtils;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.access.AuthorizedUsersStorageFacade;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.processors.stream.UnreadStream;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.services.transport.exception.TransportLevelException;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ReferrerStorage;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.exceptions.NotSessionKeyException;
import ru.ok.java.api.exceptions.ServerReturnErrorException;
import ru.ok.java.api.exceptions.VerificationException;
import ru.ok.java.api.json.JsonResultLoginParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.GetCurrentUserHomePageRequest;
import ru.ok.java.api.request.GetRedirectRequest;
import ru.ok.java.api.request.LoginRequest;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.http.RequestHttpSerializer;
import ru.ok.model.login.ResultLogin;

public class LoginProcessorNew extends CommandProcessor {
    public static final String COMMAND_NAME;
    public static final String KEY_LOGIN;
    public static final String KEY_PASSWD;
    public static final String KEY_TYPE_ERROR;
    public static final String KEY_TYPE_MESSAGE;
    public static final String KEY_VERIFICATION_TOKEN;

    static {
        COMMAND_NAME = LoginProcessorNew.class.getName();
        KEY_TYPE_MESSAGE = COMMAND_NAME + ":key_type_message";
        KEY_TYPE_ERROR = COMMAND_NAME + ":key_type_error";
        KEY_LOGIN = COMMAND_NAME + ":key_type_login";
        KEY_PASSWD = COMMAND_NAME + ":key_type_passwd";
        KEY_VERIFICATION_TOKEN = COMMAND_NAME + ":key_type_verification";
    }

    public LoginProcessorNew(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static boolean isIt(String command) {
        return COMMAND_NAME.equals(command);
    }

    public static void fillIntent(Intent intent, String login, String passwd, String verificationToken) {
        intent.putExtra(KEY_LOGIN, login);
        intent.putExtra(KEY_PASSWD, passwd);
        intent.putExtra(KEY_VERIFICATION_TOKEN, verificationToken);
    }

    public static String commandName() {
        return COMMAND_NAME;
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        String login = data.getStringExtra(KEY_LOGIN);
        String passwd = data.getStringExtra(KEY_PASSWD);
        String verificationToken = data.getStringExtra(KEY_VERIFICATION_TOKEN);
        outBundle.putString(KEY_LOGIN, login);
        outBundle.putString(KEY_PASSWD, passwd);
        return onLogin(context, login, passwd, verificationToken, outBundle);
    }

    private int onLogin(Context context, String login, String password, String verificationToken, Bundle outBundle) {
        try {
            ResultLogin holder = login(login, password, verificationToken);
            if (TextUtils.isEmpty(holder.verificationUrl)) {
                Settings.storeStrValue(context, "login", login);
                Settings.storeToken(context, holder.authenticationToken);
                AuthorizedUsersStorageFacade.addUser(holder.uid, holder.authenticationToken);
                Settings.setAuthorizedUserCount(context, AuthorizedUsersStorageFacade.getAuthorizedUsersCount());
                Settings.storeStrValue(context, "authHash", holder.authenticationHash);
                Settings.storeUserName(context, login);
                toDo(context, outBundle);
            } else {
                outBundle.putString("verificationUrl", holder.verificationUrl);
            }
            ReferrerStorage.clear(context);
            UnreadStream.onLoggedIn();
            return 1;
        } catch (Exception e) {
            Logger.m172d("Error " + e.getMessage());
            Settings.storeStrValue(context, "userUrl", "");
            outBundle.putString("errorMessage", e.getMessage());
            Settings.storeUserName(context, login);
            Settings.storeToken(context, "");
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
                if (e.getCause() != null && (e.getCause() instanceof SSLHandshakeException)) {
                    outBundle.putInt(KEY_TYPE_ERROR, 555);
                }
            }
            return 2;
        }
    }

    private void toDo(Context context, Bundle outBundle) throws SerializeException, NotSessionKeyException {
        String url = "";
        String redirectUrl = Settings.getStrValue(context, "newUrl");
        if (TextUtils.isEmpty(redirectUrl)) {
            url = new RequestHttpSerializer(this._transportProvider.getStateHolder()).serialize(new GetCurrentUserHomePageRequest(this._transportProvider.getWebBaseUrl())).getURI().toString();
        } else {
            url = new RequestHttpSerializer(this._transportProvider.getStateHolder()).serialize(new GetRedirectRequest(this._transportProvider.getWebBaseUrl(), redirectUrl, null)).getURI().toString();
            Settings.clearSettingByKey(context, "newUrl");
        }
        outBundle.putString("userUrl", url);
    }

    private ResultLogin login(String login, String password, String verificationToken) throws BaseApiException {
        Context context = OdnoklassnikiApplication.getContext();
        BaseRequest request = new LoginRequest(login, password, verificationToken, ReferrerStorage.getReferrer(context), DeviceUtils.getDeviceId(context), "1");
        CookieSyncManager manager = CookieSyncManager.createInstance(context);
        CookieManager.getInstance().setCookie("odnoklassniki.ru", "APPCAPS=unauth");
        manager.sync();
        ResultLogin resultLogin = new JsonResultLoginParser(this._transportProvider.execJsonHttpMethod(request)).parse();
        this._transportProvider.getStateHolder().setLoginInfo(resultLogin, true);
        String uid = this._transportProvider.getStateHolder().getUserId();
        OdnoklassnikiApplication.onLoggedInUserId(uid);
        MyTrackerUtils.onLoginByPassword(login, uid);
        Logger.m172d("Login successful");
        return resultLogin;
    }
}
