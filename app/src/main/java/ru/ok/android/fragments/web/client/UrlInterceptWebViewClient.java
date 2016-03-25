package ru.ok.android.fragments.web.client;

import android.content.Context;
import android.webkit.WebView;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.fragments.web.client.interceptor.UrlInterceptor;
import ru.ok.android.utils.Logger;

public class UrlInterceptWebViewClient extends ExternalLoadingWebViewClient {
    private final List<UrlInterceptor> interceptors;
    protected LoadState state;

    public enum LoadState {
        IDLE,
        LOADING,
        FINISH,
        ERROR
    }

    public UrlInterceptWebViewClient(Context context) {
        super(context);
        this.interceptors = new ArrayList();
        this.state = LoadState.IDLE;
    }

    public UrlInterceptWebViewClient addInterceptor(UrlInterceptor interceptor) {
        this.interceptors.add(interceptor);
        return this;
    }

    protected boolean isExternalUrl(String url) {
        return true;
    }

    protected void processExternalUrl(String url) {
        Logger.m173d("External url %s", url);
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Logger.m173d("load Url redirect %s", url);
        if (!interceptUrl(url)) {
            return super.shouldOverrideUrlLoading(view, url);
        }
        this.state = LoadState.FINISH;
        return true;
    }

    private boolean interceptUrl(String url) {
        for (UrlInterceptor interceptor : this.interceptors) {
            if (interceptor.handleUrl(url)) {
                return true;
            }
        }
        if (!isExternalUrl(url)) {
            return false;
        }
        processExternalUrl(url);
        return true;
    }
}
