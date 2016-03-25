package ru.ok.android.ui.stream.list;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import java.util.List;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public abstract class StreamManyInRowItem<TItem, TAdapter extends Adapter> extends StreamItem {
    private final List<TItem> items;
    private final int recyclerLayoutHeight;

    static class RecyclerViewHolder<TAdapter extends Adapter> extends ViewHolder {
        final TAdapter adapter;
        final RecyclerView recyclerView;

        public RecyclerViewHolder(View view, TAdapter adapter) {
            super(view);
            this.recyclerView = (RecyclerView) view.findViewById(2131625280);
            this.adapter = adapter;
        }
    }

    protected abstract void setData(TAdapter tAdapter, List<TItem> list);

    protected StreamManyInRowItem(int viewType, int topEdgeType, int bottomEdgeType, FeedWithState feedWithState, List<TItem> items, int recyclerLayoutHeight) {
        super(viewType, topEdgeType, bottomEdgeType, feedWithState);
        this.items = items;
        this.recyclerLayoutHeight = recyclerLayoutHeight;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903497, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        super.bindView(holder, streamItemViewController, layoutConfig);
        if (holder instanceof RecyclerViewHolder) {
            RecyclerViewHolder<TAdapter> recyclerViewHolder = (RecyclerViewHolder) holder;
            setData(recyclerViewHolder.adapter, this.items);
            LayoutParams lp = recyclerViewHolder.recyclerView.getLayoutParams();
            if (lp == null) {
                lp = new LayoutParams(-1, 0);
            }
            lp.height = this.recyclerLayoutHeight;
            recyclerViewHolder.recyclerView.setLayoutParams(lp);
        }
    }
}
