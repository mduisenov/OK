package ru.ok.android.ui.web;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.custom.scroll.ScrollTopView.OnClickScrollListener;
import ru.ok.android.ui.stream.view.StreamScrollTopView;
import ru.ok.android.utils.Logger;

public class HTML5WebView extends ScrollableWebView implements OnKeyListener, OnClickScrollListener {
    private WebViewBackInterceptor backInterceptor;
    private BroadcastReceiver broadcastReceiver;
    private Context mContext;
    private MyWebChromeClient mWebChromeClient;
    private ProgressCompletedListener progressCompletedListener;
    public ValueCallback<Uri> uploadCallback;
    private WebPageRefreshListener webPageRefreshListener;

    public interface WebPageRefreshListener {
        void onWebPageRefresh(HTML5WebView hTML5WebView);
    }

    /* renamed from: ru.ok.android.ui.web.HTML5WebView.1 */
    class C14101 implements WebPageRefreshListener {
        C14101() {
        }

        public void onWebPageRefresh(HTML5WebView webView) {
            webView.reload();
        }
    }

    /* renamed from: ru.ok.android.ui.web.HTML5WebView.2 */
    class C14112 extends BroadcastReceiver {
        C14112() {
        }

        public void onReceive(Context context, Intent intent) {
            if (HTML5WebView.this.uploadCallback != null) {
                String uri = intent.getStringExtra("fileChooserResult");
                if (TextUtils.isEmpty(uri)) {
                    HTML5WebView.this.uploadCallback.onReceiveValue(null);
                } else {
                    HTML5WebView.this.uploadCallback.onReceiveValue(Uri.parse(uri));
                }
                HTML5WebView.this.uploadCallback = null;
            }
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        private Bitmap mDefaultVideoPoster;
        private View mVideoProgressView;
        private int progress;

        private MyWebChromeClient() {
            this.progress = 0;
        }

        public Bitmap getDefaultVideoPoster() {
            Logger.m172d("here in on getDefaultVideoPoster");
            if (this.mDefaultVideoPoster == null) {
                this.mDefaultVideoPoster = BitmapFactory.decodeResource(HTML5WebView.this.getResources(), 2130837834);
            }
            return this.mDefaultVideoPoster;
        }

        public View getVideoLoadingProgressView() {
            Logger.m172d("here in on getVideoLoadingPregressView");
            if (this.mVideoProgressView == null) {
                this.mVideoProgressView = LayoutInflater.from(HTML5WebView.this.mContext).inflate(2130903569, null);
            }
            return this.mVideoProgressView;
        }

        public void onProgressChanged(WebView view, int newProgress) {
            Logger.m173d("load web progress = %d %s", Integer.valueOf(newProgress), HTML5WebView.this.getUrl());
            this.progress = newProgress;
            HTML5WebView.this.onProgressChange(newProgress);
        }

        public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
            callback.invoke(origin, true, false);
        }

        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView childView = new WebView(HTML5WebView.this.getContext());
            childView.getSettings().setJavaScriptEnabled(true);
            childView.setWebChromeClient(HTML5WebView.this.mWebChromeClient);
            HTML5WebView.this.addView(childView);
            resultMsg.obj.setWebView(childView);
            resultMsg.sendToTarget();
            return true;
        }
    }

    public interface ProgressCompletedListener {
        void onProgressCompleted();
    }

    public interface WebViewBackInterceptor {
        boolean onBack(WebView webView);
    }

    protected void refreshEvents() {
        startReload();
    }

    public void startReload() {
        StatisticManager.getInstance().addStatisticEvent("refresh_pull", new Pair[0]);
        if (this.webPageRefreshListener != null) {
            this.webPageRefreshListener.onWebPageRefresh(this);
        }
        this.scrollTopView.setNewEventCount(0);
    }

    public HTML5WebView(Context context) {
        this(context, null);
    }

    public HTML5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.webPageRefreshListener = new C14101();
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.mWebChromeClient = new MyWebChromeClient();
        setWebChromeClient(this.mWebChromeClient);
        setOnKeyListener(this);
        this.backInterceptor = new StCmdNotDuplicateBackInterceptor();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.broadcastReceiver == null) {
            this.broadcastReceiver = new C14112();
            LocalBroadcastManager.getInstance(this.mContext).registerReceiver(this.broadcastReceiver, new IntentFilter("ru.odnoklassniki.android.file.selected"));
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this.mContext).unregisterReceiver(this.broadcastReceiver);
            this.broadcastReceiver = null;
        }
    }

    protected void onConfigurationChanged(Configuration newConfig) {
        boolean z = true;
        super.onConfigurationChanged(newConfig);
        if (this.scrollTopView.getVisibility() == 0) {
            boolean z2;
            StreamScrollTopView streamScrollTopView = this.scrollTopView;
            if (getScrollY() > (getHeight() * 3) / 10) {
                z2 = true;
            } else {
                z2 = false;
            }
            if (getScrollY() >= (getHeight() * 2) / 10) {
                z = false;
            }
            streamScrollTopView.onScroll(z2, z, false, false);
        }
    }

    private void onProgressChange(int progress) {
        if (progress == 100) {
            if (this.progressCompletedListener != null) {
                this.progressCompletedListener.onProgressCompleted();
            }
            onRefreshFinish();
        }
    }

    public void onRefreshFinish() {
        this.scrollTopView.setNewEventCount(0);
        this.scrollTopView.onScroll(false, true, false, false);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && backInterceptorsHandle()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyCode == 4 && keyEvent.getAction() == 0 && backInterceptorsHandle()) {
            return true;
        }
        return false;
    }

    private boolean backInterceptorsHandle() {
        return this.backInterceptor != null && this.backInterceptor.onBack(this);
    }

    public void setProgressCompletedListener(ProgressCompletedListener progressCompletedListener) {
        this.progressCompletedListener = progressCompletedListener;
    }

    public void setWebPageRefreshListener(WebPageRefreshListener webPageRefreshListener) {
        this.webPageRefreshListener = webPageRefreshListener;
    }
}
