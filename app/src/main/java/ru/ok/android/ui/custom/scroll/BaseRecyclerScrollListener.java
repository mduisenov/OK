package ru.ok.android.ui.custom.scroll;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.animation.AnimationUtils;

public class BaseRecyclerScrollListener extends OnScrollListener {
    private AdapterDataObserver dataSetObserver;
    private final DeltaListScrollListener listener;
    private long time;

    private class DataSetObserverTranslator extends AdapterDataObserver {
        public void onItemRangeChanged(int positionStart, int itemCount) {
            onChanged();
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            onChanged();
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            onChanged();
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            onChanged();
        }

        public void onChanged() {
            BaseRecyclerScrollListener.this.onChanged();
        }
    }

    public BaseRecyclerScrollListener(DeltaListScrollListener listener) {
        this.time = -1;
        this.listener = listener;
    }

    public AdapterDataObserver getDataSetObserver() {
        if (this.dataSetObserver == null) {
            this.dataSetObserver = new DataSetObserverTranslator();
        }
        return this.dataSetObserver;
    }

    public void onChanged() {
        this.time = -1;
    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        long now = AnimationUtils.currentAnimationTimeMillis();
        if (this.time != -1) {
            this.listener.onListScroll(-dy, 0, 0, 0);
        }
        this.time = now;
    }
}
