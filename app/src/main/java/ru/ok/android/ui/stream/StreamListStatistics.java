package ru.ok.android.ui.stream;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.statistics.stream.StreamStats;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItem;
import ru.ok.android.ui.stream.list.StreamItemAdapter;

public final class StreamListStatistics {
    private final AttachListener attachListener;
    private final LongSparseArray<FeedStatus> currentFeedStatuses;
    private final List<FeedStatus> feedStatusesCache;
    private final Handler handler;
    private final RecyclerScrollListener scrollListener;
    private final LongSparseArray<FeedVisibilityInfo> startTimes;

    private final class AttachListener implements OnAttachStateChangeListener {
        private AttachListener() {
        }

        public void onViewAttachedToWindow(View v) {
            v.removeOnAttachStateChangeListener(this);
            if (v.getParent() instanceof RecyclerView) {
                StreamListStatistics.this.postProcessStatistics((RecyclerView) v.getParent());
            }
        }

        public void onViewDetachedFromWindow(View v) {
        }
    }

    private final class Callback implements android.os.Handler.Callback {
        private Callback() {
        }

        public boolean handleMessage(@NonNull Message msg) {
            StreamListStatistics.this.processStatistics((RecyclerView) msg.obj);
            return true;
        }
    }

    static class FeedStatus {
        int bottomY;
        int feedPosition;
        String feedStat;
        boolean reportVisibility;
        int topY;

        FeedStatus() {
        }
    }

    static class FeedVisibilityInfo {
        final String feedStat;
        final int position;
        final long visibilityStartTime;

        FeedVisibilityInfo(int position, long visibilityStartTime, String feedStat) {
            this.position = position;
            this.visibilityStartTime = visibilityStartTime;
            this.feedStat = feedStat;
        }
    }

    private final class RecyclerScrollListener extends OnScrollListener {
        private RecyclerScrollListener() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            StreamListStatistics.this.postProcessStatistics(recyclerView);
        }
    }

    public StreamListStatistics() {
        this.attachListener = new AttachListener();
        this.scrollListener = new RecyclerScrollListener();
        this.startTimes = new LongSparseArray();
        this.currentFeedStatuses = new LongSparseArray();
        this.feedStatusesCache = new ArrayList();
        this.handler = new Handler(Looper.getMainLooper(), new Callback());
    }

    public OnAttachStateChangeListener getAttachListener() {
        return this.attachListener;
    }

    public void postProcessStatistics(@NonNull View view) {
        if (!this.handler.hasMessages(0, view)) {
            Message.obtain(this.handler, 0, view).sendToTarget();
        }
    }

    private void processStatistics(@NonNull RecyclerView recyclerView) {
        LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
        Adapter adapter = recyclerView.getAdapter();
        int firstVisiblePosition = llm.findFirstVisibleItemPosition();
        int childCount = recyclerView.getChildCount();
        int recyclerHeight = recyclerView.getHeight();
        int i = 0;
        while (i < childCount && firstVisiblePosition + i < adapter.getItemCount()) {
            View childView = recyclerView.getChildAt(i);
            ViewHolder viewHolder = recyclerView.getChildViewHolder(childView);
            if (viewHolder instanceof StreamItemAdapter.ViewHolder) {
                StreamItem streamItem = ((StreamItemAdapter.ViewHolder) viewHolder).item;
                if (streamItem != null) {
                    FeedWithState feed = streamItem.feedWithState;
                    long feedId = feed.feed.getId();
                    FeedStatus status = (FeedStatus) this.currentFeedStatuses.get(feedId);
                    if (status == null) {
                        status = peekFeedStatus(feed.position, feed.feed.getFeedStatInfo(), recyclerHeight);
                        this.currentFeedStatuses.put(feedId, status);
                    }
                    if (!status.reportVisibility) {
                        boolean first = streamItem.isFirstInFeed();
                        boolean last = streamItem.isLastInFeed();
                        int childTop = childView.getTop();
                        int childBottom = childView.getBottom();
                        if (first && childTop < recyclerHeight / 2 && childTop >= 0) {
                            status.reportVisibility = true;
                        } else if (!last || childBottom <= recyclerHeight / 2 || childBottom >= recyclerHeight) {
                            status.topY = Math.min(status.topY, Math.max(childTop, 0));
                            status.bottomY = Math.max(status.bottomY, Math.min(childBottom, recyclerHeight));
                            if ((status.bottomY - status.topY) + 1 >= recyclerHeight / 2) {
                                status.reportVisibility = true;
                            }
                        } else {
                            status.reportVisibility = true;
                        }
                    }
                }
            }
            i++;
        }
        long time = SystemClock.uptimeMillis();
        processDisappearedItems(time);
        processVisibleItems(time);
        cacheFeedStatuses();
    }

    private void cacheFeedStatuses() {
        for (int i = 0; i < this.currentFeedStatuses.size(); i++) {
            this.feedStatusesCache.add(this.currentFeedStatuses.valueAt(i));
        }
        this.currentFeedStatuses.clear();
    }

    @NonNull
    private FeedStatus peekFeedStatus(int position, String feedStat, int recyclerHeight) {
        FeedStatus result;
        if (this.feedStatusesCache.isEmpty()) {
            result = new FeedStatus();
        } else {
            result = (FeedStatus) this.feedStatusesCache.remove(this.feedStatusesCache.size() - 1);
            result.reportVisibility = false;
        }
        result.feedPosition = position;
        result.feedStat = feedStat;
        result.topY = recyclerHeight;
        result.bottomY = 0;
        return result;
    }

    private void onFeedVisible(int position, long duration, String statInfo) {
        if (duration >= 500) {
            StreamStats.showFull(position, duration, statInfo);
        }
    }

    private void processDisappearedItems(long time) {
        int i = 0;
        while (i < this.startTimes.size()) {
            FeedStatus status = (FeedStatus) this.currentFeedStatuses.get(this.startTimes.keyAt(i));
            if (status == null || !status.reportVisibility) {
                FeedVisibilityInfo info = (FeedVisibilityInfo) this.startTimes.valueAt(i);
                onFeedVisible(info.position, time - info.visibilityStartTime, info.feedStat);
                this.startTimes.removeAt(i);
                i--;
            }
            i++;
        }
    }

    private void processVisibleItems(long time) {
        for (int i = 0; i < this.currentFeedStatuses.size(); i++) {
            long feedId = this.currentFeedStatuses.keyAt(i);
            FeedStatus feedStatus = (FeedStatus) this.currentFeedStatuses.valueAt(i);
            boolean wasFullVisible = ((FeedVisibilityInfo) this.startTimes.get(feedId)) != null;
            boolean isFullVisible = feedStatus.reportVisibility;
            if (!wasFullVisible && isFullVisible) {
                this.startTimes.put(feedId, new FeedVisibilityInfo(feedStatus.feedPosition, time, feedStatus.feedStat));
            }
        }
    }

    public OnScrollListener getScrollListener() {
        return this.scrollListener;
    }
}
