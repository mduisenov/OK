package ru.ok.android.ui.custom.loadmore;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import ru.ok.android.ui.custom.loadmore.LoadMoreController.LoadMoreStateChangedListener;

public class LoadMoreAdapter extends BaseAdapter implements LoadMoreStateChangedListener {
    private final BaseAdapter baseAdapter;
    private final Context context;
    private final LoadMoreController controller;
    @NonNull
    private final LoadMoreViewProvider loadMoreViewProvider;

    /* renamed from: ru.ok.android.ui.custom.loadmore.LoadMoreAdapter.1 */
    class C06671 implements LoadMoreConditionCallback {
        C06671() {
        }

        public boolean isTimeToLoadTop(int position, int count) {
            return position == 0;
        }

        public boolean isTimeToLoadBottom(int position, int count) {
            return position == count + -1;
        }
    }

    public LoadMoreAdapter(Context context, BaseAdapter baseAdapter, LoadMoreAdapterListener listener, LoadMoreMode mode, @Nullable LoadMoreViewProvider loadMoreViewProvider) {
        this.context = context;
        this.baseAdapter = baseAdapter;
        if (loadMoreViewProvider == null) {
            loadMoreViewProvider = new DefaultLoadMoreViewProvider();
        }
        this.loadMoreViewProvider = loadMoreViewProvider;
        this.controller = new LoadMoreController(listener, mode, new C06671(), this);
    }

    public int getCount() {
        return this.baseAdapter.getCount() + this.controller.getLoadMoreAdditionalCount();
    }

    public Object getItem(int position) {
        if (this.controller.isTopView(position)) {
            return this.controller.topViewData;
        }
        if (this.controller.isBottomView(position, getCount())) {
            return this.controller.bottomViewData;
        }
        return this.baseAdapter.getItem(this.controller.getDataPosition(position));
    }

    public long getItemId(int position) {
        if (this.controller.isTopView(position)) {
            return (long) this.controller.topViewData.hashCode();
        }
        if (this.controller.isBottomView(position, getCount())) {
            return (long) this.controller.bottomViewData.hashCode();
        }
        return this.baseAdapter.getItemId(this.controller.getDataPosition(position));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        int count = getCount();
        this.controller.onBindViewHolder(position, count);
        if (!this.controller.isTopOrBottomView(position, count)) {
            return this.baseAdapter.getView(this.controller.getDataPosition(position), convertView, parent);
        }
        LoadMoreView loadMoreView;
        boolean isTop = this.controller.isTopView(position);
        if (convertView instanceof LoadMoreView) {
            loadMoreView = (LoadMoreView) convertView;
        } else {
            loadMoreView = this.loadMoreViewProvider.createLoadMoreView(this.context, isTop, parent);
        }
        loadMoreView.bind(isTop ? this.controller.topViewData : this.controller.bottomViewData);
        return loadMoreView;
    }

    public int getItemViewType(int position) {
        if (this.controller.isTopView(position)) {
            return 0;
        }
        if (this.controller.isBottomView(position, getCount())) {
            return 1;
        }
        return this.baseAdapter.getItemViewType(this.controller.getDataPosition(position)) + 2;
    }

    public int getViewTypeCount() {
        return this.baseAdapter.getViewTypeCount() + 2;
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
        this.baseAdapter.registerDataSetObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        super.unregisterDataSetObserver(observer);
        this.baseAdapter.unregisterDataSetObserver(observer);
    }

    public boolean processItemClick(int position) {
        if (this.controller.isTopView(position)) {
            this.controller.startTopLoading();
            return true;
        } else if (!this.controller.isBottomView(position, getCount())) {
            return false;
        } else {
            this.controller.startBottomLoading();
            return true;
        }
    }

    public boolean isEmpty() {
        return this.controller.isEmpty(this.baseAdapter.getCount());
    }

    public boolean isEnabled(int position) {
        int i = 0;
        if (this.controller.isTopOrBottomView(position, getCount())) {
            return false;
        }
        BaseAdapter baseAdapter = this.baseAdapter;
        if (this.controller.mode.hasTopAdditionalView) {
            i = 1;
        }
        return baseAdapter.isEnabled(position - i);
    }

    public boolean hasStableIds() {
        return this.baseAdapter.hasStableIds();
    }

    public void onTopAutoLoadChanged() {
        notifyDataSetChanged();
    }

    public void onBottomAutoLoadChanged() {
        notifyDataSetChanged();
    }

    public LoadMoreController getController() {
        return this.controller;
    }
}
