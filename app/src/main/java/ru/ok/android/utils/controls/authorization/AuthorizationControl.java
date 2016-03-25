package ru.ok.android.utils.controls.authorization;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.Pair;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.dialogs.ConfirmLogOutDialog;
import ru.ok.android.ui.dialogs.ConfirmLogOutDialog.OnConfirmLogOutListener;
import ru.ok.android.utils.bus.BusProtocol;
import ru.ok.model.UserInfo;

public final class AuthorizationControl {
    private static volatile AuthorizationControl instance;
    final Context context;
    final LoginControl loginControl;
    final LogoutControl logoutControl;

    /* renamed from: ru.ok.android.utils.controls.authorization.AuthorizationControl.1 */
    class C14421 implements OnConfirmLogOutListener {
        final /* synthetic */ Activity val$activity;

        C14421(Activity activity) {
            this.val$activity = activity;
        }

        public void onLogOutConfirm() {
            StatisticManager.getInstance().addStatisticEvent("logged-out", new Pair[0]);
            AuthorizationControl.this.logout(this.val$activity);
        }

        public void onLogOutNoConfirm() {
        }
    }

    class OnGetCurrentUserFacade implements OnLoginListener {
        private OnLoginListener mainListener;

        OnGetCurrentUserFacade(OnLoginListener mainListener) {
            this.mainListener = mainListener;
        }

        public void onLoginSuccessful(String url, String verificationUrl) {
            GlobalBus.sendMessage(Message.obtain(null, 2131624051, 0, 0));
            if (this.mainListener != null) {
                this.mainListener.onLoginSuccessful(url, verificationUrl);
            }
        }

        public void onLoginError(String message, int type, int errorCode) {
            if (this.mainListener != null) {
                this.mainListener.onLoginError(message, type, errorCode);
            }
        }
    }

    class OnGetCurrentUserListenerFacade extends OnGetCurrentUserFacade {
        private final OnLoginUidListener loginUidListener;

        OnGetCurrentUserListenerFacade(OnLoginListener mainListener, OnLoginUidListener loginUidListener) {
            super(mainListener);
            this.loginUidListener = loginUidListener;
        }

        public void onLoginSuccessful(String url, String verificationUrl) {
            if (this.loginUidListener != null) {
                GlobalBus.register(this);
            }
            super.onLoginSuccessful(url, verificationUrl);
        }

        @Subscribe(on = 2131623946, to = 2131624216)
        public void onCurrentUser(BusEvent event) {
            GlobalBus.unregister(this);
            if (event.resultCode == -1 && this.loginUidListener != null) {
                UserInfo user = (UserInfo) event.bundleOutput.getParcelable(BusProtocol.USER);
                if (user != null) {
                    this.loginUidListener.onLoginUid(user.uid);
                }
            }
        }
    }

    public static AuthorizationControl getInstance() {
        if (instance == null) {
            synchronized (AuthorizationControl.class) {
                if (instance == null) {
                    instance = new AuthorizationControl(OdnoklassnikiApplication.getContext());
                }
            }
        }
        return instance;
    }

    private AuthorizationControl(Context context) {
        this.context = context;
        this.loginControl = new LoginControl(context);
        this.logoutControl = new LogoutControl(context);
    }

    public void showLogoutDialog(Activity activity) {
        if (!activity.isFinishing()) {
            ConfirmLogOutDialog confirmDialog = new ConfirmLogOutDialog(activity);
            confirmDialog.setOnConfirmLogOutListener(new C14421(activity));
            confirmDialog.getDialog().show();
        }
    }

    public void logout(Activity activity) {
        this.logoutControl.tryToLogout(new OnLogoutDefaultListener(activity));
    }

    public void login(String userName, String password, OnLoginListener callback) {
        this.loginControl.tryToLogin(userName, password, new OnGetCurrentUserFacade(callback));
    }

    public void login(String token, boolean forceLogin, OnLoginListener callback) {
        this.loginControl.tryToLogin(token, forceLogin, new OnGetCurrentUserFacade(callback));
    }

    public void login(String token, boolean forceLogin, OnLoginListener callback, OnLoginUidListener loginUidListener) {
        this.loginControl.tryToLogin(token, forceLogin, new OnGetCurrentUserListenerFacade(callback, loginUidListener));
    }

    public void login(String token, boolean forceLogin, OnLoginListener callback, boolean isRelogin) {
        this.loginControl.tryToLogin(token, forceLogin, new OnGetCurrentUserFacade(callback), isRelogin);
    }
}
