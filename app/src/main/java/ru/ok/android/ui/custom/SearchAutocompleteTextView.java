package ru.ok.android.ui.custom;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import ru.ok.android.ui.utils.SearchBaseHandler;

public class SearchAutocompleteTextView extends AutoCompleteTextView {
    private ProgressBar progressBar;
    private SearchBaseHandler searchHandler;

    protected CharSequence convertSelectionToString(Object selectedItem) {
        return super.convertSelectionToString(selectedItem);
    }

    public SearchAutocompleteTextView(Context context) {
        this(context, null);
    }

    public SearchAutocompleteTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchAutocompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSearchHandler(SearchBaseHandler searchHandler) {
        this.searchHandler = searchHandler;
    }

    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(null);
    }

    protected void performFiltering(CharSequence text, int keyCode) {
        if (this.searchHandler != null) {
            this.searchHandler.removeQueuedUpdates();
            this.searchHandler.queueSearchUpdate(text.toString());
        }
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public ProgressBar getProgressBar() {
        return this.progressBar;
    }
}
