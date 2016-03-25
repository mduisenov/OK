package ru.ok.android.ui.stream;

import android.support.v4.util.LongSparseArray;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import com.noundla.centerviewpagersample.comps.CenterLockViewPager;
import ru.ok.android.statistics.stream.StreamStats;
import ru.ok.android.ui.stream.data.FeedWithState;

public final class ViewPagerStateHolder {
    private final LongSparseArray<ViewPagerState> viewPagerStates;

    private class PageChangeSaver extends SimpleOnPageChangeListener {
        private final FeedWithState feed;

        public PageChangeSaver(FeedWithState feed) {
            this.feed = feed;
        }

        public void onPageSelected(int position) {
            ViewPagerState state = (ViewPagerState) ViewPagerStateHolder.this.viewPagerStates.get(this.feed.feed.getId());
            state.pageIndex = position;
            if (!state.statsSent) {
                StreamStats.clickMore(this.feed.position, this.feed.feed);
                state.statsSent = true;
            }
        }
    }

    private static class ViewPagerState {
        int pageIndex;
        boolean statsSent;

        private ViewPagerState() {
        }
    }

    public ViewPagerStateHolder() {
        this.viewPagerStates = new LongSparseArray();
    }

    public int watchViewPager(FeedWithState feedWithState, CenterLockViewPager viewPager) {
        viewPager.setOnPageChangeListener(new PageChangeSaver(feedWithState));
        long feedId = feedWithState.feed.getId();
        ViewPagerState state = (ViewPagerState) this.viewPagerStates.get(feedId);
        if (state == null) {
            state = new ViewPagerState();
            this.viewPagerStates.put(feedId, state);
        }
        return state.pageIndex;
    }
}
