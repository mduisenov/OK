package ru.ok.android.ui.adapters;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;

public final class ScrollLoadBlocker implements OnScrollListener, HandleBlocker {
    private final List<Integer> idleStates;
    private boolean isScrolling;
    private Collection<Runnable> tasks;

    public static ScrollLoadBlocker forIdleOnly() {
        return new ScrollLoadBlocker(Arrays.asList(new Integer[]{Integer.valueOf(0)}));
    }

    public static ScrollLoadBlocker forIdleAndTouchIdle() {
        return new ScrollLoadBlocker(Arrays.asList(new Integer[]{Integer.valueOf(0), Integer.valueOf(1)}));
    }

    private ScrollLoadBlocker(List<Integer> idleStates) {
        this.isScrolling = false;
        this.tasks = new LinkedList();
        this.idleStates = idleStates;
    }

    public boolean isBlocking() {
        return this.isScrolling;
    }

    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        this.isScrolling = !this.idleStates.contains(Integer.valueOf(scrollState));
        if (!isBlocking()) {
            for (Runnable runnable : this.tasks) {
                runnable.run();
            }
            this.tasks.clear();
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }
}
