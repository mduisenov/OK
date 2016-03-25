package ru.ok.android.utils.controls.authorization;

import android.content.Context;
import android.os.Bundle;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.app.helper.ServiceHelper.CommandListener;
import ru.ok.android.app.helper.ServiceHelper.ResultCode;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.db.DataBaseHelper;
import ru.ok.android.fragments.web.VerificationFragment.VerificationValue;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.persistent.PersistentTaskService;
import ru.ok.android.services.processors.login.LoginByTokenProcessorNew;
import ru.ok.android.services.processors.login.LoginProcessorNew;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.controls.authorization.VerificationControl.VerificationCallBack;
import ru.ok.android.utils.settings.Settings;
import ru.ok.android.widget.music.MusicBaseWidget;

public class LoginControl {
    private CommandCallBack commandCallBack;
    private Context context;
    private VerificationControl verificationControl;

    /* renamed from: ru.ok.android.utils.controls.authorization.LoginControl.1 */
    static /* synthetic */ class C14431 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode;
        static final /* synthetic */ int[] f124x39cbf10a;

        static {
            f124x39cbf10a = new int[VerificationValue.values().length];
            try {
                f124x39cbf10a[VerificationValue.OK.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f124x39cbf10a[VerificationValue.FAIL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f124x39cbf10a[VerificationValue.CANCEL.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            $SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode = new int[ResultCode.values().length];
            try {
                $SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode[ResultCode.SUCCESS.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode[ResultCode.FAILURE.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    class CommandCallBack implements CommandListener, VerificationCallBack {
        Context context;
        OnLoginListener listener;

        CommandCallBack(Context context, OnLoginListener listener) {
            this.context = context;
            this.listener = listener;
        }

        public void onCommandResult(String commandName, ResultCode resultCode, Bundle data) {
            if (LoginProcessorNew.isIt(commandName) || LoginByTokenProcessorNew.isIt(commandName)) {
                if (LoginProcessorNew.isIt(commandName)) {
                    data.putInt("login_type", 10);
                } else if (LoginByTokenProcessorNew.isIt(commandName)) {
                    data.putInt("login_type", 11);
                }
                switch (C14431.$SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode[resultCode.ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        LoginControl.onLoginSuccess(this.context, data, this.listener);
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        if (!verificationDataError(data)) {
                            LoginControl.onLoginError(this.context, data, this.listener);
                        }
                    default:
                }
            }
        }

        protected boolean verificationDataError(Bundle data) {
            if (data != null) {
                int errorCode = data.getInt(LoginProcessorNew.KEY_TYPE_ERROR);
                if (errorCode == 403 || errorCode == 1200) {
                    return LoginControl.this.verificationControl.verification(data.getString("verificationUrl"), data, this);
                }
            }
            return false;
        }

        public void onVerification(VerificationValue value, String tokenForVerification, Bundle bundle) {
            switch (C14431.f124x39cbf10a[value.ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    if (bundle == null) {
                        return;
                    }
                    if (bundle.getInt("login_type") == 10) {
                        Utils.getServiceHelper().tryToLogin(bundle.getString(LoginProcessorNew.KEY_LOGIN), bundle.getString(LoginProcessorNew.KEY_PASSWD), (CommandListener) this, false, tokenForVerification);
                    } else if (bundle.getInt("login_type") == 11) {
                        String tokenForLogin = bundle.getString(LoginByTokenProcessorNew.KEY_TOKEN);
                        boolean forceLogin = bundle.getBoolean(LoginByTokenProcessorNew.KEY_FORCE_LOGIN);
                        Utils.getServiceHelper().tryToLogin(tokenForLogin, forceLogin, (CommandListener) this, false, tokenForVerification);
                    }
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    Bundle bundleError = new Bundle();
                    bundleError.putString("errorMessage", "no verification");
                    bundleError.putInt(LoginProcessorNew.KEY_TYPE_ERROR, 666);
                    bundleError.putInt(LoginProcessorNew.KEY_TYPE_MESSAGE, 10);
                    LoginControl.onLoginError(this.context, bundleError, this.listener);
                    if (bundle != null && bundle.getInt("login_type") == 10) {
                        NavigationHelper.login(this.context, null, true);
                    }
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    LogoutControl.generalLogoutLogic(this.context, null);
                    if (bundle != null && bundle.getInt("login_type") == 10) {
                        NavigationHelper.login(this.context, null, true);
                    }
                default:
            }
        }
    }

    public LoginControl(Context context) {
        this.context = context;
        this.verificationControl = new VerificationControl(context);
    }

    public void tryToLogin(String login, String passwd, OnLoginListener callback) {
        Utils.getServiceHelper().tryToLogin(login, passwd, createCommandCallback(this.context, callback), false, null);
    }

    public void tryToLogin(String token, boolean forceLogin, OnLoginListener callback) {
        Utils.getServiceHelper().tryToLogin(token, forceLogin, createCommandCallback(this.context, callback), false, null);
    }

    public void tryToLogin(String token, boolean forceLogin, OnLoginListener callBack, boolean isReLogin) {
        Utils.getServiceHelper().tryToLogin(token, forceLogin, createCommandCallback(this.context, callBack), isReLogin, null);
    }

    private static void onLoginSuccess(Context context, Bundle data, OnLoginListener listener) {
        generalLoginLogic(context);
        if (listener != null) {
            listener.onLoginSuccessful(data.getString("userUrl"), data.getString("verificationUrl"));
        }
    }

    private static void onLoginError(Context context, Bundle data, OnLoginListener listener) {
        String message = data.getString("errorMessage");
        int errorCode = data.getInt(LoginProcessorNew.KEY_TYPE_ERROR);
        int type = data.getInt(LoginProcessorNew.KEY_TYPE_MESSAGE);
        generalLoginErrorLogic(context, errorCode, type, null);
        if (listener != null) {
            listener.onLoginError(message, type, errorCode);
        }
    }

    public static void generalLoginLogic(Context context) {
        MusicBaseWidget.requestAllWidgetsUpdate(context);
        GlobalBus.send(2131623982, new BusEvent());
    }

    private static void generalLoginErrorLogic(Context context, int errorCode, int type, String userId) {
        if (type == 10 && errorCode == 401) {
            userBlocked(context, userId);
        }
    }

    private static void userBlocked(Context context, String userId) {
        DataBaseHelper.clearDbAsync(context);
        PersistentTaskService.reset(context);
        Settings.setNoLoginState(context);
        OdnoklassnikiApplication.setCurrentUser(null);
    }

    private CommandCallBack createCommandCallback(Context context, OnLoginListener listener) {
        this.commandCallBack = new CommandCallBack(context, listener);
        return this.commandCallBack;
    }
}
