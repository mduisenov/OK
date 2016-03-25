package ru.ok.android.ui.search.activity;

import java.lang.ref.WeakReference;
import ru.ok.android.ui.utils.SearchBaseHandler;

final class SearchHandler extends SearchBaseHandler {
    private WeakReference<SearchActivity> searchActivityRef;

    public void onSearchHandle(String query) {
        SearchActivity searchActivity = (SearchActivity) this.searchActivityRef.get();
        if (searchActivity != null) {
            searchActivity.updateSearch(query);
        }
    }

    public int getSearchUpdateDelay() {
        return 850;
    }

    public SearchHandler(SearchActivity searchActivity) {
        this.searchActivityRef = new WeakReference(searchActivity);
    }
}
