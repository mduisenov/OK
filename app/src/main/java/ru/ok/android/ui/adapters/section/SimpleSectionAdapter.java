package ru.ok.android.ui.adapters.section;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public final class SimpleSectionAdapter<T extends BaseAdapter> extends BaseAdapter {
    static final String TAG;
    private final DataSetObserver dataSetObserver;
    private boolean finalInitCalled;
    private final Context mContext;
    private final T mListAdapter;
    private final int mSectionHeaderLayoutId;
    private final int mSectionTitleTextViewId;
    private final Sectionizer<T> mSectionizer;
    private final LinkedHashMap<String, Integer> mSections;

    /* renamed from: ru.ok.android.ui.adapters.section.SimpleSectionAdapter.1 */
    class C05971 extends DataSetObserver {
        C05971() {
        }

        public void onChanged() {
            super.onChanged();
            SimpleSectionAdapter.this.findSections();
        }

        public void onInvalidated() {
            super.onInvalidated();
            SimpleSectionAdapter.this.findSections();
        }
    }

    static class SectionHolder {
        public TextView titleTextView;

        SectionHolder() {
        }
    }

    static {
        TAG = SimpleSectionAdapter.class.getSimpleName();
    }

    public SimpleSectionAdapter(Context context, T listAdapter, int sectionHeaderLayoutId, int sectionTitleTextViewId, Sectionizer<T> sectionizer) {
        this.mSections = new LinkedHashMap();
        this.dataSetObserver = new C05971();
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
            findSections();
        } else {
            throw new IllegalArgumentException("sectionTitleTextViewId should be a TextView.");
        }
    }

    public void finalInit() {
        this.finalInitCalled = true;
        this.mListAdapter.registerDataSetObserver(this.dataSetObserver);
    }

    private boolean isTextView(Context context, int layoutId, int textViewId) {
        return View.inflate(context, layoutId, null).findViewById(textViewId) instanceof TextView;
    }

    public int getCount() {
        return this.mListAdapter.getCount() + this.mSections.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (this.finalInitCalled) {
            View view = convertView;
            SectionHolder sectionHolder = null;
            switch (getItemViewType(position)) {
                case RECEIVED_VALUE:
                    if (view != null) {
                        sectionHolder = (SectionHolder) view.getTag();
                        break;
                    }
                    view = View.inflate(this.mContext, this.mSectionHeaderLayoutId, null);
                    sectionHolder = new SectionHolder();
                    sectionHolder.titleTextView = (TextView) view.findViewById(this.mSectionTitleTextViewId);
                    view.setTag(sectionHolder);
                    break;
                default:
                    view = this.mListAdapter.getView(getDataIndexForPosition(position), convertView, parent);
                    break;
            }
            if (sectionHolder != null) {
                sectionHolder.titleTextView.setText(sectionTitleForPosition(position));
            }
            return view;
        }
        throw new IllegalStateException("You must call finalInit method");
    }

    public boolean areAllItemsEnabled() {
        return this.mListAdapter.areAllItemsEnabled() && this.mSections.size() == 0;
    }

    public int getItemViewType(int position) {
        return this.mSections.values().contains(Integer.valueOf(position)) ? 0 : this.mListAdapter.getItemViewType(getDataIndexForPosition(position)) + 1;
    }

    public int getViewTypeCount() {
        return this.mListAdapter.getViewTypeCount() + 1;
    }

    public boolean isEnabled(int position) {
        return this.mSections.values().contains(Integer.valueOf(position)) ? false : this.mListAdapter.isEnabled(getDataIndexForPosition(position));
    }

    public boolean isSectionHeader(int position) {
        return this.mSections.values().contains(Integer.valueOf(position));
    }

    public Object getItem(int position) {
        return this.mListAdapter.getItem(getDataIndexForPosition(position));
    }

    public long getItemId(int position) {
        if (isSectionHeader(position)) {
            return (long) sectionTitleForPosition(position).hashCode();
        }
        return this.mListAdapter.getItemId(getDataIndexForPosition(position));
    }

    public void notifyDataSetChanged() {
        this.mListAdapter.notifyDataSetChanged();
        findSections();
        super.notifyDataSetChanged();
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
        int n = this.mListAdapter.getCount();
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

    public String sectionTitleForDataPosition(int position) {
        position += getSectionsCountPriorDataPosition(position);
        for (Entry<String, Integer> entry : this.mSections.entrySet()) {
            if (((Integer) entry.getValue()).intValue() >= position) {
                return (String) entry.getKey();
            }
        }
        return null;
    }

    public String sectionTitleForPosition(int position) {
        for (Entry<String, Integer> entry : this.mSections.entrySet()) {
            if (((Integer) entry.getValue()).intValue() == position) {
                return (String) entry.getKey();
            }
        }
        return null;
    }

    public boolean hasStableIds() {
        return this.mListAdapter.hasStableIds();
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        this.mListAdapter.registerDataSetObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.mListAdapter.unregisterDataSetObserver(observer);
    }
}
