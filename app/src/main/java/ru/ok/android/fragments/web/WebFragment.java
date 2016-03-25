package ru.ok.android.fragments.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fragments.web.hooks.ShortLinkUtils;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.activity.StartVideoUploadActivity;
import ru.ok.android.ui.web.HTML5WebView;
import ru.ok.android.ui.web.HTML5WebView.WebPageRefreshListener;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.java.api.utils.Constants.Api;
import ru.ok.model.events.OdnkEvent;

public abstract class WebFragment extends WebBaseFragment implements WebPageRefreshListener {

    public enum RootFragmentType {
        HOME,
        DISCUSSIONS,
        FEED,
        GUEST,
        MARKS,
        MESSAGES
    }

    public abstract String getStartUrl();

    @SuppressLint({"AddJavascriptInterface"})
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getWebView().addJavascriptInterface(new EventsJSInterface(this.appHooksBridge), EventsJSInterface.JS_HOOK_NAME);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            getWebView().restoreState(savedInstanceState);
        } else {
            loadUrl(getStartUrl(), getParams());
        }
        return view;
    }

    protected void retainWebLoadState() {
        if (this.loadingState == WebState.PAGE_LOADING_ABORT) {
            onWebPageRefresh(getWebView());
        }
    }

    public void loadUrl(String url, Map<String, String> map) {
        Context context = getActivity();
        if (context != null) {
            List<String> domains = WebViewUtil.getOkCookieDomainUrls();
            Cookie[] cookies = new Cookie[domains.size()];
            for (int i = 0; i < cookies.length; i++) {
                cookies[i] = new Cookie((String) domains.get(i), "APPCAPS", Api.CLIENT_NAME);
            }
            WebBaseFragment.setCookie(context, cookies);
        }
        super.loadUrl(url, map);
    }

    public void onWebPageRefresh(HTML5WebView webView) {
        super.onWebPageRefresh(webView);
        if (webView.getUrl() == null) {
            loadUrl(getStartUrl());
        } else {
            reloadUrl();
        }
    }

    public void onLoadUrlFinish(String url) {
        super.onLoadUrlFinish(url);
        executeJSFunction(EventsJSInterface.getJSFunction());
    }

    @Subscribe(on = 2131623946, to = 2131624234)
    public void onGetNewEvent(BusEvent event) {
        onGetNewEvents(EventsManager.getEventsFromBusEvent(event));
    }

    protected boolean onAddMovie(String groupId) {
        StartVideoUploadActivity.startVideoUpload(getContext(), groupId);
        StatisticManager.getInstance().addStatisticEvent("video-upload-clicked", Pair.create("type", "web"));
        return true;
    }

    protected void onGetNewEvents(ArrayList<OdnkEvent> arrayList) {
        Logger.m172d("get new events");
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflateMenuLocalized(2131689522, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131625510:
                performRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void performRefresh() {
        StatisticManager.getInstance().addStatisticEvent("refresh_menu", new Pair[0]);
        reloadUrl();
    }

    protected void onHideFragment() {
        super.onHideFragment();
        resetNotifications();
    }

    protected void resetNotifications() {
        Logger.m172d("reset notifications");
    }

    @Subscribe(on = 2131623946, to = 2131624226)
    public void onGroupTopicLoad(BusEvent event) {
        if (isVisible() && !TextUtils.isEmpty(getUrl()) && getUrl().contains("st.groupId")) {
            String gid = event.bundleInput.getString("group_id");
            String url = getUrl();
            Uri uri = TextUtils.isEmpty(url) ? null : Uri.parse(url);
            if (uri != null && gid != null) {
                String xorValue = String.valueOf(Long.parseLong(gid) ^ 265224201205L);
                if (uri.getQueryParameter("st.groupId") != null && uri.getQueryParameter("st.groupId").equals(xorValue)) {
                    reloadUrl();
                }
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624227)
    public void onUserTopicLoad(BusEvent event) {
        if (isVisible() && ShortLinkUtils.isCurrentUserNotesShortLink(getUrl())) {
            Logger.m172d("We are on 'Notes' page, refreshing...");
            reloadUrl();
            return;
        }
        Logger.m172d("We are NOT on 'Notes' page, not refreshing.");
    }
}
