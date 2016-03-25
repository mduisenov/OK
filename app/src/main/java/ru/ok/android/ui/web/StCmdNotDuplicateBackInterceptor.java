package ru.ok.android.ui.web;

import android.net.Uri;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import ru.ok.android.ui.web.HTML5WebView.WebViewBackInterceptor;

public class StCmdNotDuplicateBackInterceptor implements WebViewBackInterceptor {
    private String parseStCmdParam(String url) {
        Uri uri = Uri.parse(url);
        return uri.isHierarchical() ? uri.getQueryParameter("st.cmd") : null;
    }

    public boolean onBack(WebView webView) {
        WebBackForwardList webBackForwardList = webView.copyBackForwardList();
        if (webBackForwardList == null || webBackForwardList.getSize() == 0) {
            return false;
        }
        String currentStCmd = parseStCmdParam(webView.getUrl());
        if (currentStCmd == null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        for (int i = webBackForwardList.getSize() - 1; i >= 0; i--) {
            String stCmd = parseStCmdParam(webBackForwardList.getItemAtIndex(i).getUrl());
            if (stCmd == null || !stCmd.equals(currentStCmd)) {
                int steps = (i - webBackForwardList.getSize()) + 1;
                if (steps == 0) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                        return true;
                    }
                } else if (webView.canGoBackOrForward(steps)) {
                    webView.goBackOrForward(steps);
                    return true;
                }
            }
        }
        return false;
    }
}
