package ru.ok.android.fragments.discussions;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import ru.ok.android.fragments.web.AppParamsManagerImpl;
import ru.ok.android.fragments.web.JSFunction;
import ru.ok.android.fragments.web.WebBaseFragment.DefaultWebViewClient;
import ru.ok.android.fragments.web.WebFragment;
import ru.ok.android.fragments.web.client.interceptor.appparams.AppParamsInterceptor.WebAppParam;
import ru.ok.android.fragments.web.client.interceptor.stcmd.DefaultStCmdUrlBridge;
import ru.ok.android.fragments.web.client.interceptor.stcmd.StCmdUrlInterceptor;
import ru.ok.android.utils.WebUrlCreator;
import ru.ok.android.utils.controls.discussions.DiscussionsControl;
import ru.ok.android.utils.controls.discussions.DiscussionsControl.OnMarkAsReadListener;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.java.api.exceptions.NotSessionKeyException;
import ru.ok.model.events.DiscussionOdklEvent;
import ru.ok.model.events.OdnkEvent;
import ru.ok.model.events.OdnkEvent.EventType;

public class DiscussionsWebFragment extends WebFragment implements OnMarkAsReadListener {
    private boolean atLeastOnePageLoaded;
    private MenuItem markAsReadMenuItem;
    private boolean shouldShowEvents;
    private int totalEventsCount;

    class DiscussionsWebViewClient extends DefaultWebViewClient {
        protected boolean mLoaded;

        public void clearLoadedState() {
            this.mLoaded = false;
        }

        public DiscussionsWebViewClient(Context context) {
            super(context);
            this.mLoaded = false;
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!StCmdUrlInterceptor.isDscPageFinishUrl(Uri.parse(url))) {
                return super.shouldOverrideUrlLoading(view, url);
            }
            this.mLoaded = true;
            return false;
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            DiscussionsWebFragment.this.getWebView().clearHistory();
            DiscussionsWebFragment.this.getWebView().clearCache(true);
            if (!DiscussionsWebFragment.this.atLeastOnePageLoaded && DiscussionsWebFragment.this.shouldShowEvents) {
                DiscussionsWebFragment.this.updateEventsCount();
            }
            DiscussionsWebFragment.this.atLeastOnePageLoaded = true;
        }

        public boolean isExternalUrl(String url) {
            Uri uri = Uri.parse(url);
            if (StCmdUrlInterceptor.isDscPageFinishUrl(uri) || uri.getPath().equals("api/discussions")) {
                return false;
            }
            return this.mLoaded;
        }
    }

    private class MarkAsReadJSFunction extends JSFunction {
        public MarkAsReadJSFunction() {
            super("OK", "markAsRead");
        }
    }

    class StCmdCallBack extends DefaultStCmdUrlBridge {
        public StCmdCallBack(Activity activity) {
            super(activity);
        }

        public boolean onGoProfilePage() {
            return false;
        }
    }

    public DiscussionsWebFragment() {
        this.totalEventsCount = 0;
    }

    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.atLeastOnePageLoaded = savedInstanceState.getBoolean("at_least_one_page_loaded", false);
            this.shouldShowEvents = savedInstanceState.getBoolean("shuld_show_events", false);
        }
        super.onCreate(savedInstanceState);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.atLeastOnePageLoaded && this.shouldShowEvents) {
            updateEventsCount();
        }
    }

    private void updateEventsCount() {
        if (this.scrollTopView != null) {
            OdnkEvent event = EventsManager.getInstance().getDiscussionsLastEvent();
            if (event != null) {
                this.scrollTopView.setNewEventCount(event.getValueInt());
            }
        }
    }

    public String getStartUrl() {
        try {
            return WebUrlCreator.getDiscussionsUrl(true);
        } catch (NotSessionKeyException e) {
            onSessionFailForUrl(null);
            return "";
        }
    }

    public void reloadUrl() {
        try {
            loadUrl(WebUrlCreator.getDiscussionsUrl(false));
        } catch (NotSessionKeyException e) {
            onSessionFailForUrl(null);
        }
    }

    protected DefaultStCmdUrlBridge createStCmdUrlBridge() {
        return new StCmdCallBack(getActivity());
    }

    public DefaultWebViewClient createWebViewClient() {
        return new DiscussionsWebViewClient(getContext());
    }

    public void loadUrl(String url, Map<String, String> map) {
        ((DiscussionsWebViewClient) this.webViewClient).clearLoadedState();
        super.loadUrl(url, map);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getWebView().saveState(outState);
        outState.putBoolean("at_least_one_page_loaded", this.atLeastOnePageLoaded);
        outState.putBoolean("shuld_show_events", this.shouldShowEvents);
    }

    public void onError(String url) {
        super.onError(url);
    }

    protected void onGetNewEvents(ArrayList<OdnkEvent> events) {
        super.onGetNewEvents(events);
        Iterator i$ = events.iterator();
        while (i$.hasNext()) {
            OdnkEvent event = (OdnkEvent) i$.next();
            if (event.type == EventType.DISCUSSIONS && (event instanceof DiscussionOdklEvent)) {
                this.totalEventsCount = (((DiscussionOdklEvent) event).getIntValueLike() + ((DiscussionOdklEvent) event).getIntValueReply()) + event.getValueInt();
                if (this.markAsReadMenuItem != null) {
                    this.markAsReadMenuItem.setVisible(this.totalEventsCount > 0);
                }
                this.shouldShowEvents = false;
                if (this.totalEventsCount > 0 && event.lastId > 0 && EventsManager.getInstance().getDiscussionsLastEvent() != null && event.lastId > EventsManager.getInstance().getDiscussionsLastEvent().lastId) {
                    EventsManager.getInstance().setDiscussionsLastEvent(event);
                    if (this.atLeastOnePageLoaded) {
                        updateEventsCount();
                    }
                    this.shouldShowEvents = true;
                } else if (EventsManager.getInstance().getDiscussionsLastEvent() == null && event.lastId > 0) {
                    EventsManager.getInstance().setDiscussionsLastEvent(event);
                }
            }
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (inflateMenuLocalized(2131689485, menu)) {
            this.markAsReadMenuItem = menu.findItem(2131625455);
            this.markAsReadMenuItem.setVisible(this.totalEventsCount > 0);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131625455:
                markAsRead();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void markAsRead() {
        executeJSFunction(new MarkAsReadJSFunction());
        AppParamsManagerImpl.getInstance().pushAppParam(WebAppParam.ALL);
        DiscussionsControl discussionsControl = new DiscussionsControl(getActivity());
        discussionsControl.setListener(this);
        discussionsControl.sendMarkAsRead();
    }

    public void onMarkAsReadSuccessful() {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), getStringLocalized(2131165594), 0).show();
            this.markAsReadMenuItem.setVisible(false);
            this.totalEventsCount = 0;
            EventsManager.getInstance().clearDiscussionEvents();
        }
    }

    public void onMarkAsReadError() {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), getStringLocalized(2131166065), 0).show();
            this.markAsReadMenuItem.setVisible(true);
        }
    }

    protected void resetNotifications() {
        AppParamsManagerImpl.getInstance().pushAppParam(WebAppParam.ALL);
    }

    protected int getTitleResId() {
        return 2131165715;
    }
}
