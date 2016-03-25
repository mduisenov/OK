package ru.ok.android.ui.custom.scroll;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import java.util.HashSet;
import java.util.Set;

public final class ScrollListenerRecyclerSet extends OnScrollListener {
    private final Set<OnScrollListener> listeners;

    public ScrollListenerRecyclerSet() {
        this.listeners = new HashSet();
    }

    public ScrollListenerRecyclerSet addListener(@NonNull OnScrollListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
        return this;
    }

    public void removeListener(@NonNull OnScrollListener listener) {
        if (listener != null) {
            this.listeners.remove(listener);
        }
    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        for (OnScrollListener listener : this.listeners) {
            listener.onScrolled(recyclerView, dx, dy);
        }
    }

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        for (OnScrollListener listener : this.listeners) {
            listener.onScrollStateChanged(recyclerView, newState);
        }
    }
}
