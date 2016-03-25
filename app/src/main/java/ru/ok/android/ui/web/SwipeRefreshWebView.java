package ru.ok.android.ui.web;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.AttributeSet;
import ru.ok.android.ui.swiperefresh.OkSwipeRefreshLayout;
import ru.ok.android.ui.web.HTML5WebView.ProgressCompletedListener;

public class SwipeRefreshWebView extends OkSwipeRefreshLayout implements OnRefreshListener, ProgressCompletedListener {
    private HTML5WebView html5WebView;

    public SwipeRefreshWebView(Context context) {
        this(context, null);
    }

    public SwipeRefreshWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.html5WebView = new HTML5WebView(context);
        addView(this.html5WebView, -1, -1);
        setEnabled(true);
        ViewCompat.setNestedScrollingEnabled(this, true);
        setOnRefreshListener(this);
        this.html5WebView.setProgressCompletedListener(this);
        ViewCompat.setNestedScrollingEnabled(this, true);
    }

    public void onRefresh() {
        this.html5WebView.startReload();
    }

    public void onProgressCompleted() {
        setRefreshing(false);
    }

    public HTML5WebView getWebView() {
        return this.html5WebView;
    }
}
