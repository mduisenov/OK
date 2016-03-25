package ru.ok.android.ui.search.adapters;

import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import android.widget.Filterable;
import java.util.ArrayList;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.utils.Logger;

public abstract class SearchBaseAdapter<T> extends BaseAdapter implements Filterable {
    protected SearchErrorListener errorListener;
    protected ArrayList<T> items;

    public interface SearchErrorListener {
        void onSearchError(ErrorType errorType);
    }

    protected class SearchFilter extends Filter {

        protected class SearchFilterResults extends FilterResults {
            public ErrorType errorType;

            protected SearchFilterResults() {
            }
        }

        protected SearchFilter() {
        }

        protected ru.ok.android.ui.search.adapters.SearchBaseAdapter$ru.ok.android.ui.search.adapters.SearchBaseAdapter$SearchFilter.ru.ok.android.ui.search.adapters.SearchBaseAdapter.SearchFilter.SearchFilterResults performFiltering(CharSequence constraint) {
            ru.ok.android.ui.search.adapters.SearchBaseAdapter$SearchFilter.SearchFilterResults filterResults = new SearchFilterResults();
            try {
                ArrayList<T> values = SearchBaseAdapter.this.performFiltering(constraint);
                filterResults.values = values;
                filterResults.count = values.size();
            } catch (Throwable e) {
                Logger.m178e(e);
                filterResults.values = null;
                filterResults.errorType = ErrorType.fromException(e);
            }
            return filterResults;
        }

        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results == null || results.values == null) {
                if (results instanceof SearchFilterResults) {
                    SearchFilterResults searchFilterResults = (SearchFilterResults) results;
                    if (!(searchFilterResults.errorType == null || SearchBaseAdapter.this.errorListener == null)) {
                        SearchBaseAdapter.this.errorListener.onSearchError(searchFilterResults.errorType);
                    }
                }
                SearchBaseAdapter.this.notifyDataSetInvalidated();
                return;
            }
            SearchBaseAdapter.this.items = (ArrayList) results.values;
            SearchBaseAdapter.this.notifyDataSetChanged();
        }
    }

    protected abstract ArrayList<T> performFiltering(CharSequence charSequence) throws Exception;

    public void setSearchErrorListener(SearchErrorListener searchErrorListener) {
        this.errorListener = searchErrorListener;
    }

    public int getCount() {
        if (this.items == null) {
            return 0;
        }
        return this.items.size();
    }

    public T getItem(int position) {
        return this.items.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public Filter getFilter() {
        return new SearchFilter();
    }
}
