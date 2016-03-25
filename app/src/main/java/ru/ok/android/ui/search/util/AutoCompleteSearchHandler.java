package ru.ok.android.ui.search.util;

import android.widget.Filter.FilterListener;
import android.widget.ProgressBar;
import ru.ok.android.ui.search.adapters.SearchBaseAdapter;
import ru.ok.android.ui.utils.SearchBaseHandler;

public class AutoCompleteSearchHandler extends SearchBaseHandler {
    private SearchBaseAdapter adapter;
    private final ProgressBar progressBar;

    /* renamed from: ru.ok.android.ui.search.util.AutoCompleteSearchHandler.1 */
    class C12001 implements FilterListener {
        C12001() {
        }

        public void onFilterComplete(int count) {
            if (AutoCompleteSearchHandler.this.progressBar != null) {
                AutoCompleteSearchHandler.this.progressBar.setVisibility(4);
            }
        }
    }

    public AutoCompleteSearchHandler(SearchBaseAdapter adapter, ProgressBar progressBar) {
        this.adapter = adapter;
        this.progressBar = progressBar;
    }

    public void onSearchHandle(String query) {
        if (this.progressBar != null) {
            this.progressBar.setVisibility(0);
        }
        this.adapter.getFilter().filter(query, new C12001());
    }

    public int getSearchUpdateDelay() {
        return 850;
    }
}
