package ru.ok.android.ui;

import android.os.Handler;
import android.support.v7.widget.SearchView.OnQueryTextListener;

public abstract class SearchQueryTextHandler implements OnQueryTextListener, Runnable {
    private long delayValue;
    private Handler handler;
    private String text;

    protected abstract void onSearchQueryChange(String str);

    protected SearchQueryTextHandler(long delayValue) {
        this.handler = new Handler();
        this.delayValue = delayValue;
    }

    public boolean onQueryTextSubmit(String s) {
        onQuery(s);
        return true;
    }

    public void run() {
        onSearchQueryChange(this.text);
    }

    public boolean onQueryTextChange(String s) {
        onQuery(s);
        return true;
    }

    private void onQuery(String s) {
        if (!s.equals(this.text) && s.length() >= 0 && this.handler != null) {
            this.handler.removeCallbacks(this);
            this.text = s;
            this.handler.postDelayed(this, this.delayValue);
        }
    }
}
