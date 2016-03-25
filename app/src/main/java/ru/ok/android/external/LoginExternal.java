package ru.ok.android.external;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.ui.nativeRegistration.NativeLoginActivity;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NetUtils;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.Settings;

public class LoginExternal extends Activity {
    private final Handler handler;
    protected String mAppId;
    protected String mAppSecret;
    protected View mProgressBar;
    protected String[] mScopes;
    protected String mStoredCookie;
    protected WebView mWebView;

    /* renamed from: ru.ok.android.external.LoginExternal.1 */
    class C02651 implements Runnable {
        final /* synthetic */ String val$authHash;

        /* renamed from: ru.ok.android.external.LoginExternal.1.1 */
        class C02641 implements Runnable {
            C02641() {
            }

            public void run() {
                LoginExternal.this.continueFromWeb();
            }
        }

        C02651(String str) {
            this.val$authHash = str;
        }

        public void run() {
            LoginExternal.setAuthHashCookie(this.val$authHash);
            ThreadUtil.queueOnMain(new C02641(), 1);
        }
    }

    /* renamed from: ru.ok.android.external.LoginExternal.2 */
    class C02662 implements Runnable {
        final /* synthetic */ int val$messageResId;
        final /* synthetic */ OnClickListener val$negativeListener;
        final /* synthetic */ OnClickListener val$positiveListener;

        C02662(OnClickListener onClickListener, int i, OnClickListener onClickListener2) {
            this.val$positiveListener = onClickListener;
            this.val$messageResId = i;
            this.val$negativeListener = onClickListener2;
        }

        public void run() {
            new Builder(LoginExternal.this).setTitle(LocalizationManager.getString(LoginExternal.this.getApplicationContext(), 2131165791)).setPositiveButton(LocalizationManager.getString(LoginExternal.this.getApplicationContext(), 2131166460), this.val$positiveListener).setMessage(this.val$messageResId).setCancelable(false).setNegativeButton(LocalizationManager.getString(LoginExternal.this.getApplicationContext(), 2131165476), this.val$negativeListener).show();
            LoginExternal.this.mProgressBar.setVisibility(4);
        }
    }

    /* renamed from: ru.ok.android.external.LoginExternal.3 */
    class C02673 implements Runnable {
        C02673() {
        }

        public void run() {
            CookieManager.getInstance().removeAllCookie();
            LoginExternal.syncCookies();
            CookieManager.getInstance().setCookie("ok.ru", LoginExternal.this.mStoredCookie);
            LoginExternal.syncCookies();
        }
    }

    protected final class AuthWebViewClient extends WebViewClient {
        private boolean showPage;

        /* renamed from: ru.ok.android.external.LoginExternal.AuthWebViewClient.1 */
        class C02681 implements Runnable {
            C02681() {
            }

            public void run() {
                LoginExternal.this.mProgressBar.setVisibility(4);
                LoginExternal.this.mWebView.setVisibility(0);
            }
        }

        /* renamed from: ru.ok.android.external.LoginExternal.AuthWebViewClient.2 */
        class C02692 implements OnClickListener {
            C02692() {
            }

            public void onClick(DialogInterface dialog, int which) {
                LoginExternal.this.mProgressBar.setVisibility(0);
                LoginExternal.this.continueFromWeb();
            }
        }

        /* renamed from: ru.ok.android.external.LoginExternal.AuthWebViewClient.3 */
        class C02703 implements OnClickListener {
            C02703() {
            }

            public void onClick(DialogInterface dialog, int which) {
                LoginExternal.this.returnError(2131165984);
            }
        }

        /* renamed from: ru.ok.android.external.LoginExternal.AuthWebViewClient.4 */
        class C02714 implements Runnable {
            final /* synthetic */ String val$code;

            C02714(String str) {
                this.val$code = str;
            }

            public void run() {
                AuthWebViewClient.this.parseTokenResponseAndReturn(AuthWebViewClient.this.getTokenByCode(this.val$code));
            }
        }

        protected AuthWebViewClient() {
            this.showPage = true;
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("okauth://auth")) {
                Bundle params = getUrlParameters(url);
                String code = params.getString("code");
                if (code != null) {
                    continueWithCode(code);
                } else {
                    String accessToken = params.getString("access_token");
                    String sessionSecretKey = params.getString("session_secret_key");
                    if (accessToken == null || sessionSecretKey == null) {
                        LoginExternal.this.returnError(params.getString("error", null));
                    } else {
                        LoginExternal.this.returnRequest(accessToken, null, sessionSecretKey);
                    }
                }
            } else {
                view.loadUrl(url);
            }
            this.showPage = true;
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (this.showPage) {
                LoginExternal.this.handler.removeCallbacksAndMessages(null);
                LoginExternal.this.handler.postDelayed(new C02681(), 500);
            }
        }

        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            showError();
            super.onReceivedSslError(view, handler, error);
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            showError();
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        private void showError() {
            this.showPage = false;
            LoginExternal.this.mWebView.setVisibility(4);
            LoginExternal.this.showErrorDialog(2131165984, new C02692(), new C02703());
        }

        private final Bundle getUrlParameters(String url) {
            Bundle bundle = new Bundle();
            String[] separated = url.split("[#?]");
            for (int keep = 1; keep < separated.length; keep++) {
                for (String param : separated[keep].split("&")) {
                    String[] keyvalue = param.split("=");
                    String key = URLDecoder.decode(keyvalue[0]);
                    String value = null;
                    if (keyvalue.length > 1) {
                        value = URLDecoder.decode(keyvalue[1]);
                    }
                    bundle.putString(key, value);
                }
            }
            return bundle;
        }

        protected final void continueWithCode(String code) {
            ThreadUtil.execute(new C02714(code));
        }

        protected final String getTokenByCode(String code) {
            String url = String.format("%s/oauth/token.do", new Object[]{ConfigurationPreferences.getInstance().getApiAddress()});
            HashMap<String, String> params = new HashMap();
            params.put("code", code);
            params.put("redirect_uri", "okauth://auth");
            params.put("grant_type", "authorization_code");
            params.put("client_id", LoginExternal.this.mAppId);
            params.put("client_secret", LoginExternal.this.mAppSecret);
            return NetUtils.performRequest(url, params);
        }

        protected final void parseTokenResponseAndReturn(String response) {
            String accessToken = null;
            String refreshToken = null;
            try {
                JSONObject json = new JSONObject(response);
                accessToken = json.getString("access_token");
                refreshToken = json.getString("refresh_token");
            } catch (JSONException e) {
            }
            LoginExternal.this.returnRequest(accessToken, refreshToken, null);
        }
    }

    public LoginExternal() {
        this.handler = new Handler();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().setAcceptCookie(true);
        stashCookies();
        setContentView(2130903281);
        setResult(0);
        if (savedInstanceState != null) {
            this.mAppId = savedInstanceState.getString("client_id");
            this.mAppSecret = savedInstanceState.getString("client_secret");
            this.mScopes = savedInstanceState.getStringArray("scopes");
            this.mStoredCookie = savedInstanceState.getString("cookie");
        }
        if (this.mAppId == null) {
            this.mAppId = getIntent().getStringExtra("client_id");
            this.mAppSecret = getIntent().getStringExtra("client_secret");
            this.mScopes = getIntent().getStringArrayExtra("scopes");
        }
        if (this.mAppId == null || this.mAppSecret == null) {
            returnError("No app data provided");
            return;
        }
        this.mProgressBar = findViewById(2131624680);
        this.mWebView = (WebView) findViewById(2131625023);
        this.mWebView.setWebViewClient(new AuthWebViewClient());
        this.mWebView.setWebChromeClient(new WebChromeClient());
        this.mWebView.getSettings().setSavePassword(false);
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        String authHash = getAuthHash();
        this.mProgressBar.setVisibility(0);
        if (TextUtils.isEmpty(authHash)) {
            startActivityForResult(new Intent(this, NativeLoginActivity.class), 100500);
        } else {
            showOAuth(authHash);
        }
    }

    private String getAuthHash() {
        return Settings.getStrValue(this, "authHash");
    }

    private void showOAuth(String authHash) {
        ThreadUtil.execute(new C02651(authHash));
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("client_id", this.mAppId);
        outState.putString("client_secret", this.mAppSecret);
        outState.putStringArray("scopes", this.mScopes);
        outState.putString("cookie", this.mStoredCookie);
        super.onSaveInstanceState(outState);
    }

    protected final void showErrorDialog(int messageResId, OnClickListener positiveListener, OnClickListener negativeListener) {
        ThreadUtil.executeOnMain(new C02662(positiveListener, messageResId, negativeListener));
    }

    protected static final void setAuthHashCookie(String authHash) {
        CookieManager.getInstance().setCookie(".ok.ru", "AUTHCODE=" + authHash);
        syncCookies();
    }

    private static void syncCookies() {
        if (VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().flush();
            return;
        }
        CookieSyncManager.getInstance().sync();
        try {
            Thread.sleep(2000);
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }

    protected final void stashCookies() {
        this.mStoredCookie = CookieManager.getInstance().getCookie("ok.ru");
        CookieManager.getInstance().removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }

    protected final void continueFromWeb() {
        String responseType = "6C6B6397C2BCE5EDB7290039".equals(this.mAppSecret) ? "token" : "code";
        String scope = this.mScopes != null ? URLEncoder.encode(TextUtils.join(";", this.mScopes)) : "";
        this.mWebView.loadUrl(String.format("%soauth/authorize?client_id=%s&response_type=%s&redirect_uri=%s&layout=a&scope=%s", new Object[]{ConfigurationPreferences.getInstance().getPortalServer(), this.mAppId, responseType, URLEncoder.encode("okauth://auth"), scope}));
        this.mWebView.requestFocus();
    }

    protected void onDestroy() {
        super.onDestroy();
        this.handler.removeCallbacksAndMessages(null);
        ThreadUtil.execute(new C02673());
    }

    protected final void returnRequest(String accessToken, String refreshToken, String sessionSecretKey) {
        Intent intent = new Intent();
        intent.putExtra("access_token", accessToken);
        if (refreshToken != null) {
            intent.putExtra("refresh_token", refreshToken);
        }
        if (sessionSecretKey != null) {
            intent.putExtra("session_secret_key", sessionSecretKey);
        }
        setResult(-1, intent);
        finish();
    }

    protected final void returnError(int errorId) {
        returnError(LocalizationManager.getString(getApplicationContext(), errorId));
    }

    protected final void returnError(String error) {
        Intent intent = new Intent();
        intent.putExtra("error", error);
        setResult(-1, intent);
        finish();
    }

    protected void onResume() {
        super.onResume();
        if (this.mWebView != null) {
            this.mWebView.resumeTimers();
        }
    }

    protected void onPause() {
        if (this.mWebView != null) {
            this.mWebView.pauseTimers();
        }
        super.onPause();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 100500:
                if (-1 == resultCode) {
                    showOAuth(getAuthHash());
                } else {
                    returnError(2131165482);
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
