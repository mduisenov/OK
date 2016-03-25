package ru.ok.android.ui.fragments.messages.view.state;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.fragments.web.client.UrlInterceptWebViewClient;
import ru.ok.android.fragments.web.client.interceptor.UrlInterceptor;
import ru.ok.android.fragments.web.client.interceptor.appparams.AppParamsInterceptor;
import ru.ok.android.fragments.web.client.interceptor.hooks.AppHooksBridge;
import ru.ok.android.fragments.web.client.interceptor.hooks.DefaultAppHooksInterceptor;
import ru.ok.android.fragments.web.client.interceptor.shortlinks.DefaultShortLinksInterceptor;
import ru.ok.android.fragments.web.client.interceptor.shortlinks.ShortLinksBridge;
import ru.ok.android.fragments.web.client.interceptor.stcmd.DefaultStCmdUrlBridge;
import ru.ok.android.fragments.web.client.interceptor.stcmd.StCmdUrlInterceptor;
import ru.ok.android.fragments.web.hooks.WebLinksProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView.OnRepeatClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView.WebState;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NetUtils;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.controls.authorization.AuthorizationControl;
import ru.ok.android.utils.controls.authorization.OnLoginListener;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.request.mediatopic.MediatopicWebRequest;
import ru.ok.java.api.request.serializer.http.RequestHttpSerializer;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo.Type;
import ru.ok.java.api.response.discussion.info.DiscussionInfoResponse;

public final class DiscussionWebState extends DiscussionState {
    private final DiscussionGeneralInfo generalInfo;
    private WebHolder holder;
    private String lastUrl;
    private final WebLinksProcessor webLinksProcessor;

    /* renamed from: ru.ok.android.ui.fragments.messages.view.state.DiscussionWebState.1 */
    class C09051 implements OnRepeatClickListener {
        C09051() {
        }

        public void onRetryClick(SmartEmptyView emptyView) {
            DiscussionWebState.this.holder.webView.reload();
        }
    }

    class DefaultAppHookBridge extends AppHooksBridge implements OnLoginListener {
        public DefaultAppHookBridge(Activity activity) {
            super(activity);
        }

        public void onSessionFailed(String goToUrl) {
            Logger.m173d("goToUrl=%s", goToUrl);
            super.onSessionFailed(goToUrl);
            AuthorizationControl.getInstance().login(Settings.getToken(this.activity), true, (OnLoginListener) this, true);
        }

        public void onLoginSuccessful(String url, String verificationUrl) {
            DiscussionWebState.this.holder.webView.loadUrl(url);
        }

        public void onLoginError(String message, int type, int errorCode) {
            Logger.m173d("login error %d", Integer.valueOf(errorCode));
        }
    }

    class DefaultClient extends UrlInterceptWebViewClient {

        /* renamed from: ru.ok.android.ui.fragments.messages.view.state.DiscussionWebState.DefaultClient.1 */
        class C09061 implements Runnable {
            C09061() {
            }

            public void run() {
                if (NetUtils.isConnectionAvailable(DefaultClient.this.context, false)) {
                    DiscussionWebState.this.holder.emptyView.setVisibility(0);
                    DiscussionWebState.this.holder.emptyView.setWebState(WebState.PROGRESS);
                }
                DiscussionWebState.this.holder.webView.setVisibility(8);
            }
        }

        public DefaultClient(Context context) {
            super(context);
            init();
        }

        private void init() {
            if (this.context instanceof Activity) {
                Activity activity = this.context;
                addInterceptor(new DefaultAppHooksInterceptor(new DefaultAppHookBridge(activity)));
                addInterceptor(new DefaultShortLinksInterceptor(new ShortLinksBridge(activity)));
                addInterceptor(new StCmdUrlInterceptor(new DefaultStCmdUrlBridge(activity)));
                addInterceptor(new RenderInterceptor());
            }
            addInterceptor(new AppParamsInterceptor(DiscussionWebState.this.holder.webView));
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            ThreadUtil.executeOnMain(new C09061());
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            onError();
        }

        protected void onReceivedConnectionError(WebView view, String url) {
            super.onReceivedConnectionError(view, url);
            onError();
        }

        private void onError() {
            DiscussionWebState.this.holder.emptyView.setWebState(NetUtils.isConnectionAvailable(this.context, false) ? WebState.NO_INTERNET_DONT_WAIT_CONNECTION : WebState.NO_INTERNET);
            DiscussionWebState.this.holder.emptyView.setVisibility(0);
            DiscussionWebState.this.holder.webView.setVisibility(8);
        }

        private void hideProgress() {
            WebState webState = DiscussionWebState.this.holder.emptyView.getWebState();
            if (webState != WebState.NO_INTERNET && webState != WebState.NO_INTERNET_DONT_WAIT_CONNECTION) {
                DiscussionWebState.this.holder.emptyView.setWebState(WebState.EMPTY);
                DiscussionWebState.this.holder.emptyView.setVisibility(8);
                DiscussionWebState.this.holder.webView.setVisibility(0);
            }
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideProgress();
        }

        protected boolean isExternalUrl(String url) {
            return url == null || !url.contains("_render_as=widget");
        }
    }

    class RenderInterceptor implements UrlInterceptor {
        RenderInterceptor() {
        }

        public boolean handleUrl(String url) {
            if (TextUtils.equals(Uri.parse(url).getQueryParameter("_render_as"), "widget")) {
                return false;
            }
            DiscussionWebState.this.webLinksProcessor.processUrl(url);
            return true;
        }
    }

    public DiscussionWebState(DiscussionInfoResponse infoResponse, WebLinksProcessor webLinksProcessor) {
        this.webLinksProcessor = webLinksProcessor;
        this.generalInfo = infoResponse.generalInfo;
    }

    public View createContentView(Context context) {
        View view = DiscussionInfoViewFactory.webView(context);
        this.holder = (WebHolder) view.getTag();
        this.holder.webView.setWebViewClient(new DefaultClient(context));
        this.holder.emptyView.setOnRepeatClickListener(new C09051());
        return view;
    }

    public final void onContentClicked() {
    }

    public boolean isDateVisible() {
        return true;
    }

    public boolean isMessageVisible() {
        return false;
    }

    public void onShow() {
        if (this.holder != null && this.holder.webView != null) {
            this.holder.webView.onResume();
            this.holder.webView.resumeTimers();
        }
    }

    public void onHide() {
        if (this.holder != null && this.holder.webView != null) {
            this.holder.webView.onPause();
            this.holder.webView.pauseTimers();
        }
    }

    public void configureView(View contentView, DiscussionInfoResponse discussion) {
        String groupId;
        String cityId;
        String userId;
        String statusId = this.generalInfo.id;
        if (this.generalInfo.group != null) {
            groupId = this.generalInfo.group.id;
        } else {
            groupId = null;
        }
        if (this.generalInfo.type == Type.CITY_NEWS) {
            cityId = this.generalInfo.topicOwnerId;
        } else {
            cityId = null;
        }
        if (this.generalInfo.user != null) {
            userId = this.generalInfo.user.id;
        } else {
            userId = null;
        }
        if (TextUtils.equals(userId, OdnoklassnikiApplication.getCurrentUser().uid)) {
            userId = null;
        }
        JsonSessionTransportProvider provider = JsonSessionTransportProvider.getInstance();
        try {
            String url = new RequestHttpSerializer(provider.getStateHolder()).serialize(new MediatopicWebRequest(provider.getWebBaseUrl(), statusId, userId, groupId, cityId)).getURI().toString();
            if (!TextUtils.equals(this.lastUrl, url)) {
                WebHolder holder = (WebHolder) contentView.getTag();
                Logger.m173d("Load url: %s", url);
                WebView webView = holder.webView;
                this.lastUrl = url;
                webView.loadUrl(url);
            }
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }
}
