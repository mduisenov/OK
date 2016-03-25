package ru.ok.android.ui.adapters.section;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import ru.ok.android.ui.adapters.friends.BaseCursorRecyclerAdapter;

public class SimpleSectionRecyclerAdapter<T extends BaseCursorRecyclerAdapter> extends Adapter<ViewHolder> {
    private final AdapterDataObserver dataSetObserver;
    private boolean finalInitCalled;
    private final Context mContext;
    private final T mListAdapter;
    private final int mSectionHeaderLayoutId;
    private final int mSectionTitleTextViewId;
    private final RecyclerSectionizer<T> mSectionizer;
    private final LinkedHashMap<String, Integer> mSections;

    /* renamed from: ru.ok.android.ui.adapters.section.SimpleSectionRecyclerAdapter.1 */
    class C05981 extends AdapterDataObserver {
        C05981() {
        }

        public void onChanged() {
            super.onChanged();
            SimpleSectionRecyclerAdapter.this.findSections();
        }
    }

    static class SectionHolder extends ViewHolder {
        public final TextView titleTextView;

        public SectionHolder(View itemView, int id) {
            super(itemView);
            this.titleTextView = (TextView) itemView.findViewById(id);
        }
    }

    public SimpleSectionRecyclerAdapter(Context context, T listAdapter, int sectionHeaderLayoutId, int sectionTitleTextViewId, RecyclerSectionizer<T> sectionizer) {
        this.mSections = new LinkedHashMap();
        this.dataSetObserver = new C05981();
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null.");
        } else if (listAdapter == null) {
            throw new IllegalArgumentException("listAdapter cannot be null.");
        } else if (sectionizer == null) {
            throw new IllegalArgumentException("sectionizer cannot be null.");
        } else if (isTextView(context, sectionHeaderLayoutId, sectionTitleTextViewId)) {
            this.mContext = context;
            this.mListAdapter = listAdapter;
            this.mSectionHeaderLayoutId = sectionHeaderLayoutId;
            this.mSectionTitleTextViewId = sectionTitleTextViewId;
            this.mSectionizer = sectionizer;
            setHasStableIds(this.mListAdapter.hasStableIds());
            findSections();
        } else {
            throw new IllegalArgumentException("sectionTitleTextViewId should be a TextView.");
        }
    }

    public void finalInit() {
        this.finalInitCalled = true;
        this.mListAdapter.registerAdapterDataObserver(this.dataSetObserver);
    }

    private boolean isTextView(Context context, int layoutId, int textViewId) {
        return View.inflate(context, layoutId, null).findViewById(textViewId) instanceof TextView;
    }

    public int getItemCount() {
        return this.mListAdapter.getItemCount() + this.mSections.size();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 2131624294) {
            return new SectionHolder(LayoutInflater.from(parent.getContext()).inflate(this.mSectionHeaderLayoutId, parent, false), this.mSectionTitleTextViewId);
        }
        return this.mListAdapter.onCreateViewHolder(parent, viewType);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == 2131624294) {
            ((SectionHolder) holder).titleTextView.setText(sectionTitleForPosition(position));
        } else {
            this.mListAdapter.onBindViewHolder(holder, getDataIndexForPosition(position));
        }
    }

    public int getItemViewType(int position) {
        return this.mSections.values().contains(Integer.valueOf(position)) ? 2131624294 : this.mListAdapter.getItemViewType(getDataIndexForPosition(position));
    }

    public boolean isSectionHeader(int position) {
        return this.mSections.values().contains(Integer.valueOf(position));
    }

    public long getItemId(int position) {
        if (isSectionHeader(position)) {
            return (long) sectionTitleForPosition(position).hashCode();
        }
        return this.mListAdapter.getItemId(getDataIndexForPosition(position));
    }

    public int getDataIndexForPosition(int position) {
        int nSections = 0;
        for (Entry<String, Integer> entry : this.mSections.entrySet()) {
            if (((Integer) entry.getValue()).intValue() < position) {
                nSections++;
            }
        }
        return position - nSections;
    }

    public int getSectionsCountPriorDataPosition(int position) {
        int headersCount = 0;
        for (Entry<String, Integer> entry : this.mSections.entrySet()) {
            int dataRowsCount = ((Integer) entry.getValue()).intValue() - headersCount;
            headersCount++;
            if (dataRowsCount > position) {
                return headersCount - 1;
            }
        }
        return headersCount;
    }

    private void findSections() {
        int n = this.mListAdapter.getItemCount();
        int nSections = 0;
        this.mSections.clear();
        for (int i = 0; i < n; i++) {
            String sectionName = this.mSectionizer.getSectionTitleForItem(this.mListAdapter, i);
            if (!(sectionName == null || this.mSections.containsKey(sectionName))) {
                this.mSections.put(sectionName, Integer.valueOf(i + nSections));
                nSections++;
            }
        }
    }

    public String sectionTitleForPosition(int position) {
        for (Entry<String, Integer> entry : this.mSections.entrySet()) {
            if (((Integer) entry.getValue()).intValue() == position) {
                return (String) entry.getKey();
            }
        }
        return null;
    }

    public void registerAdapterDataObserver(AdapterDataObserver observer) {
        this.mListAdapter.registerAdapterDataObserver(observer);
    }

    public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
        this.mListAdapter.unregisterAdapterDataObserver(observer);
    }
}
