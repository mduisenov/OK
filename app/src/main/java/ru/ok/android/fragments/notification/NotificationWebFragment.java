package ru.ok.android.fragments.notification;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import ru.ok.android.fragments.web.WebBaseFragment.DefaultWebViewClient;
import ru.ok.android.fragments.web.WebFragment;
import ru.ok.android.fragments.web.client.interceptor.stcmd.DefaultStCmdUrlBridge;
import ru.ok.android.fragments.web.client.interceptor.stcmd.StCmdUrlInterceptor;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.utils.NetUtils;
import ru.ok.android.utils.WebUrlCreator;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.model.events.OdnkEvent;
import ru.ok.model.events.OdnkEvent.EventType;

public final class NotificationWebFragment extends WebFragment {
    private boolean hasNotifications;
    private boolean reloadData;

    class EventsWebViewClient extends DefaultWebViewClient {
        protected boolean mLoaded;

        public void clearLoadedState() {
            this.mLoaded = false;
        }

        public EventsWebViewClient(Context context) {
            super(context);
            this.mLoaded = false;
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!StCmdUrlInterceptor.isEventsPageFinishUrl(Uri.parse(url))) {
                return super.shouldOverrideUrlLoading(view, url);
            }
            this.mLoaded = true;
            return false;
        }

        protected void processExternalUrl(String url) {
            super.processExternalUrl(url);
            NotificationWebFragment.this.reloadData = true;
            EventsManager.getInstance().updateNow();
        }

        public boolean isExternalUrl(String url) {
            if (StCmdUrlInterceptor.isEventsPageFinishUrl(Uri.parse(url))) {
                return false;
            }
            return this.mLoaded;
        }
    }

    private static class StCmdCallBack extends DefaultStCmdUrlBridge {
        public StCmdCallBack(Activity activity) {
            super(activity);
        }

        public boolean onGoHomePage(String url) {
            EventsManager.getInstance().setEmptyValue(EventType.EVENTS);
            EventsManager.getInstance().sendActualValue();
            return super.onGoHomePage(url);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public String getStartUrl() {
        return WebUrlCreator.getNotificationPageUrl();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getWebView().saveState(outState);
    }

    public void loadUrl(String url, Map<String, String> map) {
        ((EventsWebViewClient) this.webViewClient).clearLoadedState();
        super.loadUrl(url, map);
        this.reloadData = false;
    }

    public void onStart() {
        super.onStart();
        if (this.reloadData) {
            this.reloadData = false;
            reloadUrl();
        }
    }

    public DefaultWebViewClient createWebViewClient() {
        return new EventsWebViewClient(getContext());
    }

    protected DefaultStCmdUrlBridge createStCmdUrlBridge() {
        return new StCmdCallBack(getActivity());
    }

    public void onResume() {
        super.onResume();
        removeExistingNotification();
    }

    private void removeExistingNotification() {
        if (isFragmentVisible()) {
            ((NotificationManager) getActivity().getSystemService("notification")).cancel(6);
        }
    }

    public void showError() {
        SmartEmptyViewAnimated emptyView = getEmptyView();
        Type type = NetUtils.isConnectionAvailable(getContext(), false) ? this.hasNotifications ? Type.ERROR : Type.NOTIFICATIONS : Type.NO_INTERNET;
        emptyView.setType(type);
        emptyView.setState(State.LOADED);
        emptyView.setVisibility(0);
    }

    public void hideError() {
        if (this.hasNotifications) {
            super.hideError();
        }
    }

    protected void onGetNewEvents(ArrayList<OdnkEvent> events) {
        int total = 0;
        Iterator i$ = events.iterator();
        while (i$.hasNext()) {
            OdnkEvent event = (OdnkEvent) i$.next();
            if (event.type == EventType.EVENTS_TOTAL) {
                total = event.getValueInt();
            }
        }
        this.hasNotifications = total > 0;
        if (this.hasNotifications) {
            SmartEmptyViewAnimated emptyView = getEmptyView();
            if (emptyView.getVisibility() == 0 && emptyView.getState() == State.LOADED && emptyView.getType() == Type.NOTIFICATIONS) {
                reloadUrl();
                return;
            }
            return;
        }
        showError();
    }

    protected int getTitleResId() {
        return 2131166724;
    }
}
