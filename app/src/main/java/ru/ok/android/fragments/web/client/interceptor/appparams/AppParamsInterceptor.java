package ru.ok.android.fragments.web.client.interceptor.appparams;

import android.text.TextUtils;
import android.webkit.WebView;
import ru.ok.android.fragments.web.AppParamsManagerImpl;
import ru.ok.android.fragments.web.client.interceptor.UrlInterceptor;
import ru.ok.android.utils.Logger;

public final class AppParamsInterceptor implements UrlInterceptor {
    private final WebView webView;

    public interface AppParamsManager {
        void clear();

        boolean isEmpty();

        String popAppParams();

        void pushAppParam(WebAppParam webAppParam);
    }

    public enum WebAppParam {
        FEEDS('f'),
        DISCUSSIONS('d'),
        DISCUSSIONS_MARK('e'),
        GUESTS('g'),
        MESSAGES('m'),
        ALL('x');
        
        private char value;

        private WebAppParam(char flag) {
            this.value = flag;
        }

        public char getValue() {
            return this.value;
        }
    }

    public AppParamsInterceptor(WebView webView) {
        this.webView = webView;
    }

    public boolean handleUrl(String url) {
        AppParamsManager manager = AppParamsManagerImpl.getInstance();
        if (manager.isEmpty() || TextUtils.isEmpty(url)) {
            return false;
        }
        if (url.indexOf(63) != -1) {
            url = url + "&app.params=" + manager.popAppParams();
        } else {
            url = url + "?app.params=" + manager.popAppParams();
        }
        manager.clear();
        this.webView.loadUrl(url);
        Logger.m172d("add web param url: " + url);
        return true;
    }

    public static String manageUrl(String url) {
        AppParamsManager manager = AppParamsManagerImpl.getInstance();
        if (manager.isEmpty() || TextUtils.isEmpty(url)) {
            return url;
        }
        String newUrl;
        if (url.indexOf(63) != -1) {
            newUrl = url + "&app.params=" + manager.popAppParams();
        } else {
            newUrl = url + "?app.params=" + manager.popAppParams();
        }
        manager.clear();
        return newUrl;
    }
}
