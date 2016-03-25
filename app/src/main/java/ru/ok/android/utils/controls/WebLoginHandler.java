package ru.ok.android.utils.controls;

import android.content.Context;
import android.text.TextUtils;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.fragments.web.WebBaseFragment;
import ru.ok.android.utils.AdvertisingUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.WebUrlCreator;
import ru.ok.android.utils.controls.authorization.AuthorizationControl;
import ru.ok.android.utils.controls.authorization.OnLoginListener;
import ru.ok.android.utils.settings.Settings;

public class WebLoginHandler {
    private Context context;

    public interface WebLoginListener {
        void onLoginFailed(String str, String str2, int i, int i2);

        void onLoginSuccessful(String str);
    }

    static class FragmentWebLoginListener implements WebLoginListener {
        final WebBaseFragment fragment;

        public FragmentWebLoginListener(WebBaseFragment fragment) {
            this.fragment = fragment;
        }

        public void onLoginSuccessful(String goToUrl) {
            Context context = this.fragment.getActivity();
            if (context != null) {
                Logger.m173d("reload Url = login = %s", goToUrl);
                if (TextUtils.isEmpty(goToUrl)) {
                    this.fragment.reloadUrl();
                    return;
                }
                this.fragment.loadUrl(WebUrlCreator.getGoToUrl(goToUrl, AdvertisingUtils.getInfo(context)));
            }
        }

        public void onLoginFailed(String goToUrl, String message, int type, int errorCode) {
            if (this.fragment.getActivity() != null) {
                this.fragment.onError(goToUrl);
                if (errorCode == 401) {
                    this.fragment.onUserBlocked();
                }
            }
        }
    }

    public class LoginResult implements OnLoginListener {
        private final String goToUrl;
        private final WebLoginListener listener;

        LoginResult(WebLoginListener listener, String goToUrl) {
            this.listener = listener;
            this.goToUrl = goToUrl;
        }

        public void onLoginSuccessful(String urlNew, String verificationUrl) {
            if (this.listener != null) {
                this.listener.onLoginSuccessful(this.goToUrl);
            }
        }

        public void onLoginError(String message, int type, int errorCode) {
            Logger.m173d("errorCode=%d type=%d message=%s", Integer.valueOf(errorCode), Integer.valueOf(type), message);
            if (this.listener != null) {
                this.listener.onLoginFailed(this.goToUrl, message, type, errorCode);
            }
        }
    }

    public WebLoginHandler() {
        this.context = OdnoklassnikiApplication.getContext();
    }

    public void reCreateSession(WebBaseFragment fragment, String url) {
        reCreateSession(new FragmentWebLoginListener(fragment), url);
    }

    public void reCreateSession(WebLoginListener listener, String goToUrl) {
        AuthorizationControl.getInstance().login(Settings.getToken(this.context), true, new LoginResult(listener, goToUrl), true);
    }
}
