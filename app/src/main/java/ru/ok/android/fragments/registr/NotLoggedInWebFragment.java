package ru.ok.android.fragments.registr;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.fragments.web.Cookie;
import ru.ok.android.fragments.web.WebBaseFragment;
import ru.ok.android.fragments.web.WebBaseFragment.DefaultWebViewClient;
import ru.ok.android.fragments.web.WebViewUtil;
import ru.ok.android.fragments.web.client.interceptor.UrlInterceptor;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.utils.Constants.Api;

public class NotLoggedInWebFragment extends WebBaseFragment {
    public static final String ANDROID_REG_LOGIN;
    public static final String ANDROID_REG_NO_LOGIN;
    private Map<String, String> additionalHttpHeaders;
    private String locale;

    /* renamed from: ru.ok.android.fragments.registr.NotLoggedInWebFragment.1 */
    class C03301 extends DefaultWebViewClient {
        C03301(Context x0) {
            super(x0);
        }

        public boolean isExternalUrl(String url) {
            return false;
        }
    }

    class LoginApphookInterceptor implements UrlInterceptor {
        LoginApphookInterceptor() {
        }

        public boolean handleUrl(String url) {
            Uri uri = Uri.parse(url);
            if (uri == null || TextUtils.isEmpty(uri.getPath()) || !uri.getPath().contains("/apphook/login")) {
                return false;
            }
            String login = uri.getQueryParameter("login");
            String token = uri.getQueryParameter("token");
            if (TextUtils.isEmpty(login) || TextUtils.isEmpty(token)) {
                NotLoggedInWebFragment.this.refreshCompleted();
                close();
            } else {
                login(token, login);
            }
            return true;
        }

        private void close() {
            if (NotLoggedInWebFragment.this.getActivity() instanceof OnLoginCallBack) {
                ((OnLoginCallBack) NotLoggedInWebFragment.this.getActivity()).onClose();
            }
        }

        private void login(String token, String userName) {
            WebBaseFragment.clearCookie();
            ((OnLoginCallBack) NotLoggedInWebFragment.this.getActivity()).onLogin(token, userName);
        }
    }

    public interface OnLoginCallBack {
        void onClose();

        void onLogin(String str, String str2);
    }

    public enum Page {
        Registration("registration", 2131166441),
        Recovery("recovery", 2131166278),
        FeedBack("feedback", 2131165872) {
            Float bottomPadding;

            Float getBottomPadding(@NonNull Activity activity) {
                if (this.bottomPadding == null) {
                    this.bottomPadding = Float.valueOf(NotLoggedInWebFragment.calculateBottomPadding(activity));
                }
                return this.bottomPadding;
            }
        },
        Faq("faq", 2131165851);
        
        public final int titleResId;
        private String urlPath;

        private Page(String urlPath, int titleResId) {
            this.urlPath = urlPath;
            this.titleResId = titleResId;
        }

        String createUrl(String baseUrl, String locale) {
            return Uri.parse(baseUrl).buildUpon().appendPath(this.urlPath).appendQueryParameter("current.locale", locale).build().toString();
        }

        @Nullable
        Float getBottomPadding(@NonNull Activity activity) {
            return null;
        }
    }

    public NotLoggedInWebFragment() {
        this.additionalHttpHeaders = null;
    }

    static {
        ANDROID_REG_NO_LOGIN = Api.CLIENT_NAME + "|unauth";
        ANDROID_REG_LOGIN = Api.CLIENT_NAME;
    }

    public static Bundle newArguments(Page type, boolean isLoggedIn) {
        Bundle args = new Bundle();
        args.putSerializable("page", type);
        args.putBoolean("is_logged_in", isLoggedIn);
        return args;
    }

    public Page getPage() {
        return (Page) getArguments().getSerializable("page");
    }

    public boolean isUserLoggedIn() {
        return getArguments().getBoolean("is_logged_in");
    }

    public void loadUrlByType(Page page) {
        String url = page.createUrl(ConfigurationPreferences.getInstance().getWebServer(), this.locale);
        if (getActivity() != null) {
            setCookie();
        }
        loadUrl(url);
    }

    public Map<String, String> getParams() {
        if (this.additionalHttpHeaders != null) {
            return this.additionalHttpHeaders;
        }
        Page page = getPage();
        Activity activity = getActivity();
        if (page == null || activity == null) {
            return null;
        }
        Float bottomPadding = page.getBottomPadding(activity);
        if (bottomPadding != null) {
            this.additionalHttpHeaders = Collections.singletonMap("Body-Style", "padding-bottom: " + bottomPadding + "px;");
        }
        return this.additionalHttpHeaders;
    }

    public DefaultWebViewClient createWebViewClient() {
        DefaultWebViewClient result = new C03301(getContext());
        if (!isUserLoggedIn()) {
            result.addInterceptor(new LoginApphookInterceptor());
        }
        return result;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCookie();
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setNoRefreshingMode();
        this.locale = Settings.getCurrentLocale(OdnoklassnikiApplication.getContext());
        if (getPage() != null) {
            loadUrlByType(getPage());
        }
        return view;
    }

    protected CharSequence getTitle() {
        Page page = getPage();
        int titleResId = page == null ? 0 : page.titleResId;
        if (titleResId != 0) {
            return LocalizationManager.getString(getContext(), titleResId);
        }
        return super.getTitle();
    }

    private void setCookie() {
        String cookieApp;
        if (isUserLoggedIn()) {
            cookieApp = ANDROID_REG_LOGIN;
        } else {
            cookieApp = ANDROID_REG_NO_LOGIN;
        }
        List<String> domains = WebViewUtil.getOkCookieDomainUrls();
        Cookie[] cookies = new Cookie[domains.size()];
        for (int i = 0; i < cookies.length; i++) {
            cookies[i] = new Cookie((String) domains.get(i), "APPCAPS", cookieApp);
        }
        WebBaseFragment.setCookie(getActivity(), cookies);
    }

    private static float calculateBottomPadding(@NonNull Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = activity.getResources().getDisplayMetrics().density;
        float dpHeight = ((float) outMetrics.heightPixels) / density;
        float dpWidth = ((float) outMetrics.widthPixels) / density;
        return dpHeight > dpWidth ? (dpHeight * 2.0f) / 3.0f : (dpWidth * 2.0f) / 3.0f;
    }
}
