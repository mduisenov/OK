package ru.ok.android.fragments.web.client;

import android.content.Context;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import ru.ok.android.app.WebHttpLoader;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NetUtils;

public class ExternalLoadingWebViewClient extends WebViewClient {
    protected final Context context;

    public ExternalLoadingWebViewClient(Context context) {
        this.context = context;
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Logger.m172d("shouldOverrideUrlLoading: " + WebHttpLoader.decodeUrl(url));
        if (!NetUtils.isConnectionAvailable(this.context, false)) {
            onReceivedConnectionError(view, url);
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    protected void onReceivedConnectionError(WebView view, String url) {
        Logger.m172d("Connection error: " + WebHttpLoader.decodeUrl(url));
    }

    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        handler.proceed("dev", "OdklDev1");
    }
}
