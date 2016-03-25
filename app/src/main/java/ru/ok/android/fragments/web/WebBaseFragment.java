package ru.ok.android.fragments.web;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebView.WebViewTransport;
import android.widget.FrameLayout;
import java.util.Map;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.WebCache;
import ru.ok.android.db.WebCache.UrlTitleInfo;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.web.client.UrlInterceptWebViewClient;
import ru.ok.android.fragments.web.client.UrlInterceptWebViewClient.LoadState;
import ru.ok.android.fragments.web.client.WebClientUtils;
import ru.ok.android.fragments.web.client.interceptor.appparams.AppParamsInterceptor;
import ru.ok.android.fragments.web.client.interceptor.hooks.AppHooksBridge;
import ru.ok.android.fragments.web.client.interceptor.hooks.DefaultAppHooksInterceptor;
import ru.ok.android.fragments.web.client.interceptor.shortlinks.DefaultShortLinksInterceptor;
import ru.ok.android.fragments.web.client.interceptor.shortlinks.ShortLinksBridge;
import ru.ok.android.fragments.web.client.interceptor.stcmd.DefaultStCmdUrlBridge;
import ru.ok.android.fragments.web.client.interceptor.stcmd.StCmdUrlInterceptor;
import ru.ok.android.model.image.ImageForUpload;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.stream.view.StreamScrollTopView;
import ru.ok.android.ui.tabbar.HideTabbarListener;
import ru.ok.android.ui.web.HTML5WebView;
import ru.ok.android.ui.web.HTML5WebView.WebPageRefreshListener;
import ru.ok.android.ui.web.SwipeRefreshWebView;
import ru.ok.android.utils.DimenUtils;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NetUtils;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.controls.WebLoginHandler;
import ru.ok.android.utils.controls.authorization.AuthorizationControl;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.refresh.RefreshProvider;
import ru.ok.android.utils.refresh.RefreshProviderOnRefreshListener;
import ru.ok.android.utils.refresh.SwipeRefreshProvider;
import ru.ok.java.api.utils.Utils;

public abstract class WebBaseFragment extends BaseFragment implements WebPageRefreshListener, RefreshProviderOnRefreshListener {
    public static final WebCache webCache;
    protected AppHooksBridge appHooksBridge;
    private String currentWebSubTitle;
    private String currentWebTitle;
    private SmartEmptyViewAnimated emptyView;
    private final OnStubButtonClickListener emptyViewReloadListener;
    protected String errorUrl;
    private HideTabbarListener hideTabbarListener;
    protected WebState loadingState;
    protected ViewGroup mainView;
    protected RefreshProvider refreshProvider;
    protected StreamScrollTopView scrollTopView;
    protected ShortLinksBridge shortLinksBridge;
    protected DefaultStCmdUrlBridge stCmdUrlBridge;
    private SwipeRefreshWebView swipeRefreshWebView;
    private String webCacheTitle;
    private HTML5WebView webView;
    protected DefaultWebViewClient webViewClient;

    public class DefaultWebViewClient extends UrlInterceptWebViewClient {
        public DefaultWebViewClient(Context context) {
            super(context);
        }

        public LoadState getState() {
            return this.state;
        }

        protected void processExternalUrl(String url) {
            if (WebBaseFragment.this.getActivity() != null) {
                WebBaseFragment.this.getWebLinksProcessor().processUrl(url);
            }
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            this.state = LoadState.LOADING;
            WebBaseFragment.this.onLoadUrlStart(url);
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (this.state != LoadState.FINISH && this.state != LoadState.IDLE) {
                this.state = LoadState.FINISH;
                WebBaseFragment.this.onLoadUrlFinish(url);
            }
        }

        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            WebBaseFragment.this.onLoadRes(url);
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            this.state = LoadState.ERROR;
            super.onReceivedError(view, errorCode, description, failingUrl);
            WebBaseFragment.this.onError(failingUrl);
        }

        protected void onReceivedConnectionError(WebView view, String failingUrl) {
            this.state = LoadState.ERROR;
            super.onReceivedConnectionError(view, failingUrl);
            WebBaseFragment.this.onError(failingUrl);
        }
    }

    /* renamed from: ru.ok.android.fragments.web.WebBaseFragment.1 */
    class C03321 implements OnStubButtonClickListener {
        C03321() {
        }

        public void onStubButtonClick(Type type) {
            WebBaseFragment.this.getWebView().clearView();
            WebBaseFragment.this.reloadUrl();
        }
    }

    class AppHookMainBridge extends AppHooksBridge {
        public AppHookMainBridge(Activity activity) {
            super(activity);
        }

        public void onErrorUrlLoad(String url) {
            super.onErrorUrlLoad(url);
            WebBaseFragment.this.onError(url);
        }

        public void onSessionFailed(String goToUrl) {
            super.onSessionFailed(goToUrl);
            WebBaseFragment.this.onSessionFailForUrl(goToUrl);
        }

        public void onUploadVideo(String groupId) {
            super.onUploadVideo(groupId);
            WebBaseFragment.this.onAddMovie(groupId);
        }

        public void onErrorUserBlocked() {
            super.onErrorUserBlocked();
            WebBaseFragment.this.onUserBlocked();
        }

        public void onLoadSysLink(String url) {
            super.onLoadSysLink(url);
            WebBaseFragment.this.showLoadDialog();
        }

        public void onOutLinkOpenInBrowser(String url) {
            super.onOutLinkOpenInBrowser(url);
            WebBaseFragment.this.refreshCompleted();
        }
    }

    private class SubtileJSInterface {

        /* renamed from: ru.ok.android.fragments.web.WebBaseFragment.SubtileJSInterface.1 */
        class C03331 implements Runnable {
            C03331() {
            }

            public void run() {
                WebBaseFragment.this.setSubTitleIfVisible(WebBaseFragment.this.currentWebSubTitle);
            }
        }

        private SubtileJSInterface() {
        }

        @JavascriptInterface
        public void processSubtitleFromWeb(String url, String subtitle) {
            WebBaseFragment.this.currentWebSubTitle = subtitle;
            ThreadUtil.executeOnMain(new C03331());
            WebBaseFragment.webCache.saveSubtitleExist(url, !TextUtils.isEmpty(WebBaseFragment.this.currentWebSubTitle));
        }
    }

    protected enum WebState {
        PAGE_FINISH_LOADING,
        PAGE_STARTED,
        PAGE_LOADING_ABORT
    }

    public WebBaseFragment() {
        this.loadingState = WebState.PAGE_STARTED;
        this.errorUrl = "";
        this.emptyViewReloadListener = new C03321();
    }

    static {
        webCache = new WebCache(OdnoklassnikiApplication.getContext());
    }

    protected void onSessionFailForUrl(String goToUrl) {
        Logger.m172d("load Url = new = " + goToUrl);
        new WebLoginHandler().reCreateSession(this, goToUrl);
    }

    protected boolean onAddMovie(String groupId) {
        Logger.m172d("add movie groupId = " + groupId);
        return false;
    }

    public void onUserBlocked() {
        if (getActivity() != null) {
            AuthorizationControl.getInstance().logout(getActivity());
        }
    }

    protected void startUpdateLiveInternetStatistics() {
    }

    private void clearError() {
        this.errorUrl = "";
    }

    protected SmartEmptyViewAnimated getEmptyView() {
        return this.emptyView;
    }

    public void onLoadUrlStart(String url) {
        Logger.m173d("load url start %s ", url);
        onLoadUrlStartProcessTitle(url);
        clearError();
        this.emptyView.setState(State.LOADING);
        if (!this.emptyView.isShown() && !this.refreshProvider.isRefreshing()) {
            this.refreshProvider.refreshStart();
        }
    }

    public void onLoadUrlFinish(String url) {
        if (!(this.webView == null || this.hideTabbarListener == null)) {
            this.hideTabbarListener.onScroll(0, this.webView.getScrollY(), this.webView.publicComputeVerticalScrollRange() - this.webView.getHeight(), this.webView.getHeight());
        }
        Logger.m173d("load url finish %s", url);
        if (this.emptyView.getType() == Type.NO_INTERNET) {
            this.emptyView.setState(State.LOADED);
            onLoadUrlFinishProcessTitleNoConnection();
        } else if (TextUtils.equals(url, this.errorUrl)) {
            this.emptyView.setState(State.LOADED);
        } else {
            hideError();
            onLoadUrlFinishProcessTitle(url);
            executeJSFunction(getJsSubtitleFunction(url));
        }
        refreshCompleted();
    }

    private void onLoadUrlStartProcessTitle(String url) {
        UrlTitleInfo titleInfo = webCache.getTitle(url);
        if (titleInfo != null) {
            this.webCacheTitle = titleInfo.title;
            if (!TextUtils.isEmpty(this.webCacheTitle)) {
                this.currentWebTitle = this.webCacheTitle;
                setTitleIfVisible(this.webCacheTitle);
            }
            if (titleInfo.subtitleExist) {
                setSubTitleIfVisible(" ");
            }
        }
    }

    private void onLoadUrlFinishProcessTitle(String url) {
        if (this.webView != null) {
            String webViewTitle = this.webView.getTitle();
            if (webViewTitle != null) {
                webViewTitle = filterTitle(url, webViewTitle);
            }
            if (webViewTitle == null) {
                return;
            }
            if (this.webCacheTitle == null || !TextUtils.equals(this.webCacheTitle, webViewTitle)) {
                setTitleIfVisible(webViewTitle);
                webCache.saveTitle(url, webViewTitle);
                this.currentWebTitle = webViewTitle;
                this.webCacheTitle = null;
            }
        }
    }

    private String filterTitle(@NonNull String url, @NonNull String title) {
        if (title.startsWith(url)) {
            return null;
        }
        for (Pair pair : WebClientUtils.validHosts) {
            if (title.startsWith((String) pair.first)) {
                return null;
            }
        }
        return title;
    }

    private void onLoadUrlFinishProcessTitleNoConnection() {
        if (TextUtils.isEmpty(this.currentWebTitle) && getTitleResId() != 0) {
            String titleFromRes = getStringLocalized(getTitleResId());
            if (!TextUtils.isEmpty(titleFromRes)) {
                setTitleIfVisible(titleFromRes);
            }
            this.currentWebTitle = titleFromRes;
        }
    }

    protected int getTitleResId() {
        return 0;
    }

    protected CharSequence getTitle() {
        return this.currentWebTitle;
    }

    protected CharSequence getSubtitle() {
        return this.currentWebSubTitle;
    }

    public void onError(String errorUrl) {
        Logger.m173d("load error url", errorUrl);
        refreshCompleted();
        this.errorUrl = errorUrl;
        showError();
    }

    public void onLoadRes(String url) {
        Logger.m173d("load res for url = %s", url);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.hideTabbarListener = new HideTabbarListener(getActivity());
    }

    protected int getLayoutId() {
        return 2130903114;
    }

    @SuppressLint({"AddJavascriptInterface"})
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            restoreTitle(savedInstanceState);
        }
        this.mainView = (FrameLayout) LocalizationManager.inflate(getActivity(), getLayoutId(), container, false);
        this.emptyView = (SmartEmptyViewAnimated) this.mainView.findViewById(C0263R.id.empty_view);
        this.emptyView.setVisibility(8);
        this.emptyView.setButtonClickListener(this.emptyViewReloadListener);
        if ((getActivity() instanceof BaseCompatToolbarActivity) && !((BaseCompatToolbarActivity) getActivity()).isAppBarLocked()) {
            ((MarginLayoutParams) this.emptyView.getLayoutParams()).bottomMargin = DimenUtils.getToolbarHeight(getContext());
        }
        this.scrollTopView = (StreamScrollTopView) this.mainView.findViewById(2131624644);
        this.appHooksBridge = new AppHookMainBridge(getActivity());
        this.shortLinksBridge = new ShortLinksBridge(getActivity());
        this.stCmdUrlBridge = createStCmdUrlBridge();
        View refreshWebViewContent = initWebView(savedInstanceState);
        this.webView.setScrollTopView(this.scrollTopView);
        this.mainView.addView(refreshWebViewContent, 0);
        this.mainView.setBackgroundColor(-1);
        getWebView().addJavascriptInterface(new SubtileJSInterface(), "subtitle");
        return this.mainView;
    }

    private void restoreTitle(Bundle savedInstanceState) {
        this.currentWebTitle = savedInstanceState.getString("state_web_title");
        this.currentWebSubTitle = savedInstanceState.getString("state_web_subtitle");
        if (this.currentWebTitle != null) {
            setTitleIfVisible(this.currentWebTitle);
        }
        if (this.currentWebSubTitle != null) {
            setSubTitleIfVisible(this.currentWebSubTitle);
        }
    }

    private static JSFunction getJsSubtitleFunction(String url) {
        JSFunction ret = new JSFunction("subtitle", "processSubtitleFromWeb");
        ret.addParam(String.format("'%s'", new Object[]{url}));
        ret.addParam("document.getElementsByTagName('title')[0].getAttribute('data-name')");
        return ret;
    }

    protected DefaultStCmdUrlBridge createStCmdUrlBridge() {
        return new DefaultStCmdUrlBridge(getActivity());
    }

    public final void loadUrl(String url) {
        loadUrl(url, getParams());
    }

    public Map<String, String> getParams() {
        return null;
    }

    @TargetApi(8)
    public void loadUrl(String url, Map<String, String> headers) {
        if (getActivity() == null || isHidden()) {
            refreshCompleted();
        }
        if (!isDetached()) {
            clearError();
            Logger.m172d("load Url = " + url);
            if (headers != null) {
                getWebView().loadUrl(AppParamsInterceptor.manageUrl(url), headers);
            } else {
                getWebView().loadUrl(AppParamsInterceptor.manageUrl(url));
            }
        }
    }

    public void onWebPageRefresh(HTML5WebView webView) {
        if (!isDetached()) {
            clearError();
        }
    }

    public String getUrl() {
        return getWebView().getUrl();
    }

    public void executeJSFunction(JSFunction function) {
        getWebView().loadUrl(function.toString());
    }

    public void onResume() {
        super.onResume();
        onShow();
    }

    public void onPause() {
        super.onPause();
        onHide();
    }

    protected void onHideFragment() {
        super.onHideFragment();
        onHide();
        KeyBoardUtils.hideKeyBoard(getActivity(), getWebView().getWindowToken());
    }

    private void onHide() {
        refreshCompleted();
        saveWebLoadState();
        pauseTimers();
    }

    private void pauseTimers() {
        getWebView().onPause();
        getWebView().pauseTimers();
    }

    protected void onShowFragment() {
        super.onShowFragment();
        onShow();
    }

    private void onShow() {
        getWebView().onResume();
        getWebView().resumeTimers();
        retainWebLoadState();
    }

    protected void saveWebLoadState() {
        LoadState state = this.webViewClient.getState();
        if (state == LoadState.LOADING || state == LoadState.IDLE) {
            this.loadingState = WebState.PAGE_LOADING_ABORT;
            Logger.m172d("load web progress stop");
            getWebView().stopLoading();
            return;
        }
        this.loadingState = WebState.PAGE_FINISH_LOADING;
    }

    protected void retainWebLoadState() {
        if (this.loadingState == WebState.PAGE_LOADING_ABORT) {
            Logger.m172d("load web progress restart");
            webViewReload();
        }
    }

    public void onStop() {
        super.onStop();
        getWebView().stopLoading();
        this.refreshProvider.refreshCompleted();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getWebView().saveState(outState);
        outState.putString("state_web_title", this.currentWebTitle);
        outState.putString("state_web_subtitle", this.currentWebSubTitle);
    }

    public HTML5WebView getWebView() {
        return this.webView;
    }

    public void showRefreshingView() {
        this.refreshProvider.refreshStart();
    }

    public void showError() {
        this.emptyView.setVisibility(0);
        this.emptyView.setType(NetUtils.isConnectionAvailable(getContext(), false) ? Type.ERROR : Type.NO_INTERNET);
        this.emptyView.setState(State.LOADED);
    }

    public void hideError() {
        this.emptyView.setVisibility(8);
    }

    @TargetApi(11)
    protected View initWebView(Bundle savedInstanceState) {
        this.swipeRefreshWebView = (SwipeRefreshWebView) LayoutInflater.from(getActivity()).inflate(2130903530, null);
        HTML5WebView html5WebView = this.swipeRefreshWebView.getWebView();
        html5WebView.setHideTabbarListener(this.hideTabbarListener);
        html5WebView.setWebPageRefreshListener(this);
        if (savedInstanceState != null) {
            html5WebView.restoreState(savedInstanceState);
        }
        html5WebView.setAlwaysDrawnWithCacheEnabled(true);
        html5WebView.getClass();
        new WebViewTransport(html5WebView).setWebView(html5WebView);
        this.webViewClient = createWebViewClient();
        initWebViewClient(this.webViewClient);
        html5WebView.setWebViewClient(this.webViewClient);
        initWebViewSettings(html5WebView);
        this.refreshProvider = new SwipeRefreshProvider(this.swipeRefreshWebView);
        this.refreshProvider.setOnRefreshListener(this);
        this.webView = this.swipeRefreshWebView.getWebView();
        return this.swipeRefreshWebView;
    }

    public DefaultWebViewClient createWebViewClient() {
        return new DefaultWebViewClient(getContext() == null ? OdnoklassnikiApplication.getContext() : getContext());
    }

    protected void initWebViewClient(DefaultWebViewClient client) {
        client.addInterceptor(new DefaultAppHooksInterceptor(this.appHooksBridge)).addInterceptor(new DefaultShortLinksInterceptor(this.shortLinksBridge)).addInterceptor(new StCmdUrlInterceptor(this.stCmdUrlBridge));
    }

    public static void syncSettings(Context context, WebSettings settings) {
        settings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
        settings.setNeedInitialFocus(false);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(-1);
        settings.setSupportMultipleWindows(true);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(false);
        settings.setLoadsImagesAutomatically(true);
        settings.setRenderPriority(RenderPriority.HIGH);
        settings.setSaveFormData(false);
        settings.setSavePassword(false);
        String userAgent = settings.getUserAgentString();
        String okAppPart = WebViewUtil.getOkAppUserAgentPart();
        if (TextUtils.isEmpty(userAgent)) {
            userAgent = okAppPart;
        } else if (!userAgent.endsWith(okAppPart)) {
            userAgent = userAgent + " " + okAppPart;
        }
        WebViewUtil.setWebViewUserAgent(context, userAgent);
        settings.setUserAgentString(userAgent);
    }

    private static void initWebViewSettings(WebView webView) {
        if (VERSION.SDK_INT >= 19) {
            syncSettings(webView.getContext(), webView.getSettings());
            webView.clearFormData();
        } else {
            syncSettings(webView.getContext(), webView.getSettings());
            webView.clearFormData();
        }
    }

    public static void setCookie(Context context, Cookie[] cookies) {
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        for (Cookie cookie : cookies) {
            cookieManager.setCookie(cookie.getDomain(), cookie.getName() + '=' + cookie.getValue());
        }
        cookieSyncManager.sync();
        Logger.m172d("cookeis set ok = " + cookieManager.getCookie(cookies[0].getDomain()));
    }

    public static void clearCookie() {
        try {
            CookieSyncManager cookieSyncManager = CookieSyncManager.getInstance();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.removeAllCookie();
            cookieSyncManager.sync();
        } catch (IllegalStateException e) {
            Logger.m172d(" no clear cookies: CookieSyncManager::createInstance() needs to be called ");
        }
    }

    protected void onInternetAvailable() {
        super.onInternetAvailable();
        if (this.emptyView.getVisibility() == 0) {
            this.emptyView.setState(State.LOADING);
            this.emptyView.setType(Type.EMPTY);
            reloadUrl();
        }
    }

    public void showLoadDialog() {
        this.refreshProvider.refreshStart();
    }

    public void refreshCompleted() {
        this.refreshProvider.refreshCompleted();
    }

    public void setNoRefreshingMode() {
        this.refreshProvider.setRefreshEnabled(false);
    }

    protected boolean isScrolledTooMuch() {
        if (getWebView().getScrollY() > 160) {
            return true;
        }
        return false;
    }

    @Subscribe(on = 2131623946, to = 2131624225)
    public void onImageUploaded(BusEvent event) {
        if (event.resultCode == 1 && !isScrolledTooMuch()) {
            ImageForUpload image = (ImageForUpload) event.bundleOutput.getParcelable("img");
            if (image != null && image.getCurrentStatus() == 5) {
                String currentUrl = getUrl();
                Uri uri = currentUrl == null ? null : Uri.parse(currentUrl);
                boolean shouldReload = false;
                if (uri != null && image.getUploadTarget() == 2 && TextUtils.equals(uri.getQueryParameter("st.cmd"), "userProfile")) {
                    shouldReload = true;
                } else if (uri != null && image.getUploadTarget() == 1 && TextUtils.equals(uri.getQueryParameter("st.cmd"), "altGroupMain")) {
                    String groupId = uri.getQueryParameter("st.groupId");
                    if (groupId != null) {
                        try {
                            if (TextUtils.equals(String.valueOf(Utils.xorId(groupId)), image.getAlbumInfo().getGroupId())) {
                                shouldReload = true;
                            }
                        } catch (Exception exc) {
                            Logger.m177e("Error xoring group id on group avatar change", exc);
                        }
                    }
                }
                if (shouldReload) {
                    reloadUrl();
                }
            }
        }
    }

    public void onRefresh() {
        this.swipeRefreshWebView.onRefresh();
    }

    public void webViewReload() {
        getWebView().reload();
        this.refreshProvider.refreshStart();
    }

    public void reloadUrl() {
        webViewReload();
    }
}
