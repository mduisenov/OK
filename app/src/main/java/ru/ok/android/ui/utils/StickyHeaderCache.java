package ru.ok.android.ui.utils;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.util.SparseArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import java.util.LinkedList;
import java.util.Map;
import ru.ok.android.ui.utils.StickyHeaderItemDecorator.HeaderViewProvider;
import ru.ok.android.ui.utils.StickyHeaderItemDecorator.ViewHolderHeader;

public final class StickyHeaderCache {
    final SparseArray<LinkedList<ViewHolderHeader>> freeViews;
    final Map<Integer, ViewHolderHeader> headerViews;
    final HeaderViewProvider headersProvider;
    final RecyclerView recyclerView;

    /* renamed from: ru.ok.android.ui.utils.StickyHeaderCache.1 */
    class C13491 extends AdapterDataObserver {
        C13491() {
        }

        public void onChanged() {
            super.onChanged();
            StickyHeaderCache.this.freeAllViews();
        }
    }

    public StickyHeaderCache(RecyclerView recyclerView, HeaderViewProvider headersProvider) {
        this.headerViews = new ArrayMap();
        this.freeViews = new SparseArray();
        this.recyclerView = recyclerView;
        this.headersProvider = headersProvider;
        recyclerView.getAdapter().registerAdapterDataObserver(new C13491());
    }

    @NonNull
    public ViewHolderHeader getHeaderView(int position) {
        ViewHolderHeader holder = (ViewHolderHeader) this.headerViews.get(Integer.valueOf(position));
        if (holder != null) {
            return holder;
        }
        LinkedList<ViewHolderHeader> views = (LinkedList) this.freeViews.get(this.headersProvider.getHeaderViewType(position));
        if (!(views == null || views.isEmpty())) {
            holder = (ViewHolderHeader) views.pop();
        }
        if (holder == null) {
            holder = this.headersProvider.newHeaderView(position, this.recyclerView);
        }
        this.headerViews.put(Integer.valueOf(position), holder);
        this.headersProvider.bindHeaderView(holder, position);
        View view = holder.view;
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        view.measure(lp.width == -1 ? MeasureSpec.makeMeasureSpec(this.recyclerView.getMeasuredWidth(), 1073741824) : ViewGroup.getChildMeasureSpec(this.recyclerView.getMeasuredWidthAndState(), 0, lp.width), ViewGroup.getChildMeasureSpec(this.recyclerView.getMeasuredHeightAndState(), 0, lp.height));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        holder.paddingLeft = lp.leftMargin;
        holder.paddingTop = lp.topMargin;
        return holder;
    }

    void freeAllViews() {
        for (ViewHolderHeader holderHeader : this.headerViews.values()) {
            LinkedList<ViewHolderHeader> views = (LinkedList) this.freeViews.get(holderHeader.viewType);
            if (views == null) {
                views = new LinkedList();
            }
            views.add(holderHeader);
        }
        this.headerViews.clear();
    }
}
