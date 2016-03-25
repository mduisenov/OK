package ru.ok.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;

public final class ScrollLoadRecyclerViewBlocker extends OnScrollListener implements HandleBlocker {
    private final List<Integer> idleStates;
    private boolean isScrolling;
    private Collection<Runnable> tasks;

    public static ScrollLoadRecyclerViewBlocker forIdleOnly() {
        return new ScrollLoadRecyclerViewBlocker(Arrays.asList(new Integer[]{Integer.valueOf(0)}));
    }

    public static ScrollLoadRecyclerViewBlocker forIdleAndTouchIdle() {
        return new ScrollLoadRecyclerViewBlocker(Arrays.asList(new Integer[]{Integer.valueOf(0), Integer.valueOf(1)}));
    }

    private ScrollLoadRecyclerViewBlocker(List<Integer> idleStates) {
        this.isScrolling = false;
        this.tasks = new LinkedList();
        this.idleStates = idleStates;
    }

    public boolean isBlocking() {
        return this.isScrolling;
    }

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        this.isScrolling = !this.idleStates.contains(Integer.valueOf(newState));
        if (!isBlocking()) {
            for (Runnable runnable : this.tasks) {
                runnable.run();
            }
            this.tasks.clear();
        }
    }
}
