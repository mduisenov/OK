package ru.ok.android.fragments.marks;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import ru.ok.android.fragments.web.WebBaseFragment.DefaultWebViewClient;
import ru.ok.android.fragments.web.WebFragment;
import ru.ok.android.fragments.web.client.interceptor.stcmd.DefaultStCmdUrlBridge;
import ru.ok.android.fragments.web.client.interceptor.stcmd.StCmdUrlInterceptor;
import ru.ok.android.utils.WebUrlCreator;
import ru.ok.java.api.exceptions.NotSessionKeyException;
import ru.ok.model.events.OdnkEvent;
import ru.ok.model.events.OdnkEvent.EventType;

public class MarksWebFragment extends WebFragment {
    protected OdnkEvent lastEvent;

    class MarksWebViewClient extends DefaultWebViewClient {
        protected boolean mLoaded;

        public void clearLoadedState() {
            this.mLoaded = false;
        }

        public MarksWebViewClient(Context context) {
            super(context);
            this.mLoaded = false;
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!StCmdUrlInterceptor.isMarksPageFinishUrl(Uri.parse(url))) {
                return super.shouldOverrideUrlLoading(view, url);
            }
            this.mLoaded = true;
            return false;
        }

        public boolean isExternalUrl(String url) {
            String stCmd = Uri.parse(url).getQueryParameter("st.cmd");
            if (TextUtils.equals(stCmd, "userMarks") || TextUtils.equals(stCmd, "clientRedirect")) {
                return false;
            }
            return this.mLoaded;
        }
    }

    class StCmdCallBack extends DefaultStCmdUrlBridge {
        public StCmdCallBack(Activity activity) {
            super(activity);
        }

        public boolean onGoMarksPage() {
            return false;
        }
    }

    public String getStartUrl() {
        try {
            return WebUrlCreator.getGuestsMarksUrl();
        } catch (NotSessionKeyException e) {
            onSessionFailForUrl(null);
            return "";
        }
    }

    public void reloadUrl() {
        try {
            loadUrl(WebUrlCreator.getGuestsMarksUrl());
        } catch (NotSessionKeyException e) {
            onSessionFailForUrl(null);
        }
    }

    protected void onShowFragment() {
        reloadUrl();
    }

    protected DefaultStCmdUrlBridge createStCmdUrlBridge() {
        return new StCmdCallBack(getActivity());
    }

    public DefaultWebViewClient createWebViewClient() {
        return new MarksWebViewClient(getContext());
    }

    public void loadUrl(String url, Map<String, String> map) {
        ((MarksWebViewClient) this.webViewClient).clearLoadedState();
        super.loadUrl(url, map);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getWebView().saveState(outState);
    }

    protected void onGetNewEvents(ArrayList<OdnkEvent> events) {
        super.onGetNewEvents(events);
        Iterator i$ = events.iterator();
        while (i$.hasNext()) {
            OdnkEvent event = (OdnkEvent) i$.next();
            if (event.type == EventType.MARKS) {
                if (event.getValueInt() > 0 && event.lastId > 0 && this.lastEvent != null && event.lastId != this.lastEvent.lastId) {
                    this.lastEvent = event;
                    showRefreshingView();
                } else if (this.lastEvent == null) {
                    this.lastEvent = event;
                }
            }
        }
    }

    protected void resetNotifications() {
    }

    protected int getTitleResId() {
        return 2131166069;
    }
}
