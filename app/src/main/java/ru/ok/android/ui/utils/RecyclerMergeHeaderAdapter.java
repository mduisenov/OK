package ru.ok.android.ui.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.ui.utils.StickyHeaderItemDecorator.HeaderViewProvider;
import ru.ok.android.ui.utils.StickyHeaderItemDecorator.ViewHolderHeader;

public final class RecyclerMergeHeaderAdapter<TAdapter extends Adapter & AdapterItemViewTypeMaxValueProvider> extends RecyclerMergeAdapter<TAdapter> implements HeaderViewProvider {
    private HeaderViewProvider adapterSectionHeaderPrivider;
    private final boolean isSmall;

    public interface HeaderTextProvider {
        CharSequence getHeaderName();
    }

    /* renamed from: ru.ok.android.ui.utils.RecyclerMergeHeaderAdapter.1 */
    class C13481 implements HeaderViewProvider {
        C13481() {
        }

        public CharSequence getHeader(int position) {
            if (RecyclerMergeHeaderAdapter.this.findAdapter(position) && (RecyclerMergeHeaderAdapter.this.chosenAdapter instanceof HeaderTextProvider)) {
                return ((HeaderTextProvider) RecyclerMergeHeaderAdapter.this.chosenAdapter).getHeaderName();
            }
            return null;
        }

        public ViewHolderHeader newHeaderView(int position, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(2130903213, parent, false);
            view.setBackgroundColor(parent.getContext().getResources().getColor(RecyclerMergeHeaderAdapter.this.isSmall ? 2131493041 : 2131493132));
            return new ViewHolderHeader(view);
        }

        public int getHeaderViewType(int position) {
            return 2131624358;
        }

        public void bindHeaderView(ViewHolderHeader view, int position) {
            ((TextView) view.view).setText(getHeader(position));
        }

        public int getAnchorViewId(int position) {
            return 0;
        }
    }

    public RecyclerMergeHeaderAdapter(boolean hasStableIds, boolean isSmall) {
        super(hasStableIds);
        this.isSmall = isSmall;
    }

    public RecyclerMergeHeaderAdapter addAdapter(@NonNull TAdapter adapter) {
        super.addAdapter(adapter);
        return this;
    }

    boolean findAdapterHeader(int position) {
        if (findAdapter(position)) {
            return this.chosenAdapter instanceof HeaderViewProvider;
        }
        return false;
    }

    public CharSequence getHeader(int position) {
        if (findAdapterHeader(position)) {
            return ((HeaderViewProvider) this.chosenAdapter).getHeader(this.indexInAdapter);
        }
        return null;
    }

    public ViewHolderHeader newHeaderView(int position, ViewGroup parent) {
        if (findAdapterHeader(position)) {
            return ((HeaderViewProvider) this.chosenAdapter).newHeaderView(this.indexInAdapter, parent);
        }
        return null;
    }

    public int getHeaderViewType(int position) {
        if (findAdapterHeader(position)) {
            return ((HeaderViewProvider) this.chosenAdapter).getHeaderViewType(this.indexInAdapter);
        }
        return 0;
    }

    public void bindHeaderView(ViewHolderHeader view, int position) {
        if (findAdapterHeader(position)) {
            ((HeaderViewProvider) this.chosenAdapter).bindHeaderView(view, this.indexInAdapter);
        }
    }

    public int getAnchorViewId(int position) {
        if (findAdapterHeader(position)) {
            return ((HeaderViewProvider) this.chosenAdapter).getAnchorViewId(position);
        }
        return 0;
    }

    public HeaderViewProvider getAdapterSectionHeaderPrivider() {
        if (this.adapterSectionHeaderPrivider == null) {
            this.adapterSectionHeaderPrivider = new C13481();
        }
        return this.adapterSectionHeaderPrivider;
    }
}
