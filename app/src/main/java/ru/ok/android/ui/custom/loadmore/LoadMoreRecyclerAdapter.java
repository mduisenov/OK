package ru.ok.android.ui.custom.loadmore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;
import ru.ok.android.ui.custom.loadmore.LoadMoreController.LoadMoreStateChangedListener;
import ru.ok.android.ui.utils.AdapterItemViewTypeMaxValueProvider;

public class LoadMoreRecyclerAdapter<TAdapter extends Adapter & AdapterItemViewTypeMaxValueProvider> extends Adapter implements LoadMoreStateChangedListener, AdapterItemViewTypeMaxValueProvider {
    private final int LOAD_MORE_TYPE_BOTTOM;
    private final int LOAD_MORE_TYPE_TOP;
    private final int TYPE_MAX_VALUE;
    private final TAdapter baseAdapter;
    private final Context context;
    private final LoadMoreController controller;
    @NonNull
    private final LoadMoreViewProvider loadMoreViewProvider;

    /* renamed from: ru.ok.android.ui.custom.loadmore.LoadMoreRecyclerAdapter.1 */
    class C06681 implements LoadMoreConditionCallback {
        C06681() {
        }

        public boolean isTimeToLoadTop(int position, int count) {
            return position == 0;
        }

        public boolean isTimeToLoadBottom(int position, int count) {
            return position == count + -3;
        }
    }

    public class BaseAdapterDataObserverTranslationObserver extends AdapterDataObserver {
        public void onChanged() {
            super.onChanged();
            LoadMoreRecyclerAdapter.this.notifyDataSetChanged();
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            LoadMoreRecyclerAdapter.this.notifyItemRangeChanged(LoadMoreRecyclerAdapter.this.controller.getExtraTopElements() + positionStart, itemCount);
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            LoadMoreRecyclerAdapter.this.notifyItemRangeInserted(LoadMoreRecyclerAdapter.this.controller.getExtraTopElements() + positionStart, itemCount);
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            LoadMoreRecyclerAdapter.this.notifyItemRangeRemoved(LoadMoreRecyclerAdapter.this.controller.getExtraTopElements() + positionStart, itemCount);
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            int offset = LoadMoreRecyclerAdapter.this.controller.getExtraTopElements();
            for (int i = 0; i < itemCount; i++) {
                LoadMoreRecyclerAdapter.this.notifyItemMoved((offset + fromPosition) + i, (offset + toPosition) + i);
            }
        }
    }

    private static class LoadMoreViewHolder extends ViewHolder {
        final LoadMoreView loadMoreView;

        public LoadMoreViewHolder(LoadMoreView itemView) {
            super(itemView);
            this.loadMoreView = itemView;
        }
    }

    public LoadMoreRecyclerAdapter(Context context, TAdapter baseAdapter, LoadMoreAdapterListener listener, LoadMoreMode mode) {
        this(context, baseAdapter, listener, mode, null);
    }

    public LoadMoreRecyclerAdapter(Context context, TAdapter baseAdapter, LoadMoreAdapterListener listener, LoadMoreMode mode, @Nullable LoadMoreViewProvider loadMoreViewProvider) {
        this.context = context;
        this.baseAdapter = baseAdapter;
        this.LOAD_MORE_TYPE_TOP = ((AdapterItemViewTypeMaxValueProvider) baseAdapter).getItemViewTypeMaxValue() + 1;
        this.LOAD_MORE_TYPE_BOTTOM = ((AdapterItemViewTypeMaxValueProvider) baseAdapter).getItemViewTypeMaxValue() + 2;
        this.TYPE_MAX_VALUE = this.LOAD_MORE_TYPE_BOTTOM;
        if (loadMoreViewProvider == null) {
            loadMoreViewProvider = new DefaultLoadMoreViewProvider();
        }
        this.loadMoreViewProvider = loadMoreViewProvider;
        setHasStableIds(baseAdapter.hasStableIds());
        this.controller = new LoadMoreController(listener, mode, new C06681(), this);
        baseAdapter.registerAdapterDataObserver(new BaseAdapterDataObserverTranslationObserver());
    }

    public int getItemCount() {
        return this.baseAdapter.getItemCount() + this.controller.getLoadMoreAdditionalCount();
    }

    public long getItemId(int position) {
        if (this.controller.isTopView(position)) {
            return (long) this.controller.topViewData.hashCode();
        }
        if (this.controller.isBottomView(position, getItemCount())) {
            return (long) this.controller.bottomViewData.hashCode();
        }
        return this.baseAdapter.getItemId(this.controller.getDataPosition(position));
    }

    public int getItemViewType(int position) {
        if (this.controller.isTopView(position)) {
            return this.LOAD_MORE_TYPE_TOP;
        }
        if (this.controller.isBottomView(position, getItemCount())) {
            return this.LOAD_MORE_TYPE_BOTTOM;
        }
        return this.baseAdapter.getItemViewType(this.controller.getDataPosition(position));
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == this.LOAD_MORE_TYPE_TOP) {
            return new LoadMoreViewHolder(this.loadMoreViewProvider.createLoadMoreView(this.context, true, parent));
        }
        if (viewType == this.LOAD_MORE_TYPE_BOTTOM) {
            return new LoadMoreViewHolder(this.loadMoreViewProvider.createLoadMoreView(this.context, false, parent));
        }
        return this.baseAdapter.onCreateViewHolder(parent, viewType);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!this.controller.onBindViewHolder(position, getItemCount())) {
            this.baseAdapter.onBindViewHolder(holder, this.controller.getDataPosition(position));
        } else if (this.controller.isTopView(position)) {
            ((LoadMoreViewHolder) holder).loadMoreView.bind(this.controller.topViewData);
        } else {
            ((LoadMoreViewHolder) holder).loadMoreView.bind(this.controller.bottomViewData);
        }
    }

    public int getItemViewTypeMaxValue() {
        return this.TYPE_MAX_VALUE;
    }

    public void registerAdapterDataObserver(AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        this.baseAdapter.registerAdapterDataObserver(observer);
    }

    public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        this.baseAdapter.unregisterAdapterDataObserver(observer);
    }

    public void onTopAutoLoadChanged() {
        if (this.controller.isTopViewAdded()) {
            notifyItemInserted(0);
        } else {
            notifyItemRemoved(0);
        }
    }

    public void onBottomAutoLoadChanged() {
        if (this.controller.isBottomViewAdded()) {
            notifyItemInserted(getItemCount() - 1);
        } else {
            notifyItemRemoved(getItemCount());
        }
    }

    public LoadMoreController getController() {
        return this.controller;
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.baseAdapter.onAttachedToRecyclerView(recyclerView);
    }

    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.baseAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (!(holder instanceof LoadMoreViewHolder)) {
            this.baseAdapter.onViewDetachedFromWindow(holder);
        }
    }

    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (!(holder instanceof LoadMoreViewHolder)) {
            this.baseAdapter.onViewAttachedToWindow(holder);
        }
    }

    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        if (!(holder instanceof LoadMoreViewHolder)) {
            this.baseAdapter.onViewRecycled(holder);
        }
    }
}
