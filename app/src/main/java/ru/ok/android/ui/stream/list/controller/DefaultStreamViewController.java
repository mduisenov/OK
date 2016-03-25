package ru.ok.android.ui.stream.list.controller;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.ShownOnScrollListener;
import ru.ok.android.ui.stream.list.StreamItemAdapter.StreamAdapterListener;
import ru.ok.android.ui.utils.ViewDrawObserver;
import ru.ok.android.ui.utils.ViewDrawObserver.ViewDrawListener;

public class DefaultStreamViewController extends AbsStreamStatisticsViewController {
    private ShownOnScrollListener shownOnScrollListener;
    private ViewDrawListener viewDrawListener;
    private ViewDrawObserver viewDrawObserver;

    /* renamed from: ru.ok.android.ui.stream.list.controller.DefaultStreamViewController.1 */
    class C12511 implements ViewDrawListener {
        private final Rect visibleRect;

        C12511() {
            this.visibleRect = new Rect();
        }

        public void onViewDraw(View view) {
            view.getGlobalVisibleRect(this.visibleRect);
            if (DefaultStreamViewController.this.shownOnScrollListener != null) {
                FeedWithState feedWithState = (FeedWithState) view.getTag(2131624341);
                if (feedWithState != null && !feedWithState.shownOnScrollSent && feedWithState.feed.getStatPixels(1) != null && DefaultStreamViewController.this.shownOnScrollListener.onShownOnScroll(feedWithState.feed, this.visibleRect, view.getWidth(), view.getHeight())) {
                    feedWithState.shownOnScrollSent = true;
                    view.setTag(2131624341, null);
                }
            }
        }
    }

    public DefaultStreamViewController(Activity activity, StreamAdapterListener listener, String logContext) {
        super(activity, listener, logContext);
        this.viewDrawListener = new C12511();
    }

    public ViewDrawObserver getViewDrawObserver() {
        return this.viewDrawObserver;
    }

    public void setViewDrawObserver(ViewDrawObserver viewDrawObserver) {
        this.viewDrawObserver = viewDrawObserver;
    }

    public void setShownOnScrollListener(ShownOnScrollListener listener) {
        this.shownOnScrollListener = listener;
    }

    public ViewDrawListener getViewDrawListener() {
        return this.viewDrawListener;
    }
}
