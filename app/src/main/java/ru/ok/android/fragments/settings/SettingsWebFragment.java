package ru.ok.android.fragments.settings;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.webkit.WebView;
import ru.ok.android.fragments.web.WebBaseFragment.DefaultWebViewClient;
import ru.ok.android.fragments.web.WebFragment;
import ru.ok.android.fragments.web.client.interceptor.stcmd.DefaultStCmdUrlBridge;
import ru.ok.android.utils.WebUrlCreator;

public class SettingsWebFragment extends WebFragment {

    class SettingsWebViewClient extends DefaultWebViewClient {
        public SettingsWebViewClient(Context context) {
            super(context);
        }

        public boolean isExternalUrl(String url) {
            return false;
        }

        public void onPageFinished(WebView webView, String url) {
            super.onPageFinished(webView, url);
            SettingsWebFragment.this.clearHistoryOnRoot(webView, url);
        }
    }

    class StCmdCallBack extends DefaultStCmdUrlBridge {
        public StCmdCallBack(Activity activity) {
            super(activity);
        }

        public boolean onGoHomePage(String url) {
            if (Uri.parse(url).getQueryParameter("st.hid") != null) {
                return false;
            }
            return super.onGoHomePage(url);
        }
    }

    public String getStartUrl() {
        return WebUrlCreator.getUserSettingsUrl();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getWebView().saveState(outState);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflateMenuLocalized(2131689531, menu);
    }

    protected DefaultStCmdUrlBridge createStCmdUrlBridge() {
        return new StCmdCallBack(getActivity());
    }

    public DefaultWebViewClient createWebViewClient() {
        return new SettingsWebViewClient(getContext());
    }

    private void clearHistoryOnRoot(WebView webView, String url) {
        Uri uri = Uri.parse(url);
        String stCmd = uri.isHierarchical() ? uri.getQueryParameter("st.cmd") : null;
        if (stCmd != null && stCmd.equals("userSettings")) {
            webView.clearHistory();
        }
    }

    protected int getTitleResId() {
        return 2131166410;
    }
}
