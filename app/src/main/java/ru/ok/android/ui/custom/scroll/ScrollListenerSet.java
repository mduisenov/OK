package ru.ok.android.ui.custom.scroll;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import java.util.HashSet;
import java.util.Set;

public final class ScrollListenerSet implements OnScrollListener {
    private final Set<OnScrollListener> listeners;

    public ScrollListenerSet() {
        this.listeners = new HashSet();
    }

    public ScrollListenerSet addListener(OnScrollListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
        return this;
    }

    public void removeAllListeners() {
        this.listeners.clear();
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        for (OnScrollListener listener : this.listeners) {
            listener.onScrollStateChanged(view, scrollState);
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        for (OnScrollListener listener : this.listeners) {
            listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}
