package ru.ok.android.ui.utils;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Pair;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ru.ok.android.utils.Logger;

public class RecyclerMergeAdapter<TAdapter extends Adapter & AdapterItemViewTypeMaxValueProvider> extends Adapter<ViewHolder> implements AdapterItemViewTypeMaxValueProvider {
    private final List<TAdapter> adapters;
    private final List<Integer> adaptersViewTypeOffset;
    protected int choosenAdapterItemViewTypeOffset;
    protected Adapter chosenAdapter;
    private int count;
    protected int indexInAdapter;
    private final Map<Integer, Pair<Adapter, Integer>> viewTypeAdapterMap;

    /* renamed from: ru.ok.android.ui.utils.RecyclerMergeAdapter.1 */
    class C13471 extends ItemCountChangedDataObserver {
        C13471() {
        }

        public void onItemCountMayChange() {
            RecyclerMergeAdapter.this.count = -1;
        }
    }

    private class RecyclerMergeAdapterDataObserver extends ItemCountChangedDataObserver {
        final int adapterPosition;

        public RecyclerMergeAdapterDataObserver(int adapterPosition) {
            this.adapterPosition = adapterPosition;
        }

        public void onItemCountMayChange() {
            RecyclerMergeAdapter.this.count = -1;
        }

        public void onChanged() {
            super.onChanged();
            RecyclerMergeAdapter.this.viewTypeAdapterMap.clear();
            RecyclerMergeAdapter.this.notifyDataSetChanged();
        }

        private int getPositionOffset() {
            int ret = 0;
            for (int i = 0; i < this.adapterPosition; i++) {
                ret += ((Adapter) RecyclerMergeAdapter.this.adapters.get(i)).getItemCount();
            }
            return ret;
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            RecyclerMergeAdapter.this.notifyItemRangeChanged(getPositionOffset() + positionStart, itemCount);
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            RecyclerMergeAdapter.this.notifyItemRangeInserted(getPositionOffset() + positionStart, itemCount);
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            RecyclerMergeAdapter.this.notifyItemRangeRemoved(getPositionOffset() + positionStart, itemCount);
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            int offset = getPositionOffset();
            for (int i = 0; i < itemCount; i++) {
                RecyclerMergeAdapter.this.notifyItemMoved((offset + fromPosition) + i, (offset + toPosition) + i);
            }
        }
    }

    public RecyclerMergeAdapter() {
        this(false);
    }

    public RecyclerMergeAdapter(boolean hasStableIds) {
        this.adapters = new ArrayList();
        this.viewTypeAdapterMap = new ArrayMap();
        this.adaptersViewTypeOffset = new ArrayList();
        this.count = -1;
        setHasStableIds(hasStableIds);
        registerAdapterDataObserver(new C13471());
    }

    public RecyclerMergeAdapter addAdapter(@NonNull TAdapter adapter) {
        this.adapters.add(adapter);
        int adapterPosition = this.adapters.size() - 1;
        if (adapterPosition == 0) {
            this.adaptersViewTypeOffset.add(Integer.valueOf(0));
        } else {
            this.adaptersViewTypeOffset.add(Integer.valueOf(((AdapterItemViewTypeMaxValueProvider) adapter).getItemViewTypeMaxValue() + ((Integer) this.adaptersViewTypeOffset.get(adapterPosition - 1)).intValue()));
        }
        adapter.registerAdapterDataObserver(new RecyclerMergeAdapterDataObserver(adapterPosition));
        return this;
    }

    protected final boolean findAdapter(int position) {
        for (int index = 0; index < this.adapters.size(); index++) {
            Adapter adapter = (Adapter) this.adapters.get(index);
            int count = adapter.getItemCount();
            if (position < count) {
                this.indexInAdapter = position;
                this.chosenAdapter = adapter;
                this.choosenAdapterItemViewTypeOffset = ((Integer) this.adaptersViewTypeOffset.get(index)).intValue();
                return true;
            }
            position -= count;
        }
        return false;
    }

    public int getItemViewType(int position) {
        if (findAdapter(position)) {
            int itemViewType = this.chosenAdapter.getItemViewType(this.indexInAdapter);
            if (itemViewType == -1) {
                Logger.m176e(this.chosenAdapter + " uses -1 view type == RecyclerView.INVALID_TYPE");
            }
            int resultItemViewType = this.choosenAdapterItemViewTypeOffset + itemViewType;
            this.viewTypeAdapterMap.put(Integer.valueOf(resultItemViewType), new Pair(this.chosenAdapter, Integer.valueOf(this.choosenAdapterItemViewTypeOffset)));
            return resultItemViewType;
        }
        throw new IllegalStateException("Can't find adapter for position: " + position);
    }

    public long getItemId(int position) {
        if (findAdapter(position)) {
            return this.chosenAdapter.getItemId(this.indexInAdapter);
        }
        return 0;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Pair<Adapter, Integer> p = (Pair) this.viewTypeAdapterMap.get(Integer.valueOf(viewType));
        Adapter adapter = p.first;
        if (adapter == null) {
            Logger.m177e("viewType: %d", Integer.valueOf(viewType));
        }
        return adapter.onCreateViewHolder(parent, viewType - ((Integer) p.second).intValue());
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (findAdapter(position)) {
            this.chosenAdapter.onBindViewHolder(holder, this.indexInAdapter);
        }
    }

    public int getItemCount() {
        if (this.count == -1) {
            this.count = 0;
            for (int i = 0; i < this.adapters.size(); i++) {
                this.count = ((Adapter) this.adapters.get(i)).getItemCount() + this.count;
            }
        }
        return this.count;
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        int size = this.adapters.size();
        for (int index = 0; index < size; index++) {
            ((Adapter) this.adapters.get(index)).onAttachedToRecyclerView(recyclerView);
        }
    }

    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        int size = this.adapters.size();
        for (int index = 0; index < size; index++) {
            ((Adapter) this.adapters.get(index)).onDetachedFromRecyclerView(recyclerView);
        }
    }

    public int getRecyclerPositionByAdapterPosition(Adapter adapter, int adapterPosition) {
        int sum = 0;
        for (Adapter adapter1 : this.adapters) {
            if (adapter == adapter1) {
                return sum + adapterPosition;
            }
            sum += adapter1.getItemCount();
        }
        return -1;
    }

    public int getAdapterPositionByRecyclerPosition(Adapter adapter, int recyclerPosition) {
        findAdapter(recyclerPosition);
        if (this.chosenAdapter != adapter) {
            return -1;
        }
        return this.indexInAdapter;
    }

    public int getItemViewTypeMaxValue() {
        int size = this.adaptersViewTypeOffset.size();
        return size == 0 ? 0 : ((Integer) this.adaptersViewTypeOffset.get(size - 1)).intValue();
    }
}
