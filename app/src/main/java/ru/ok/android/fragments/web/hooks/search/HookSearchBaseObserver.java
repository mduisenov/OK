package ru.ok.android.fragments.web.hooks.search;

import android.net.Uri;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;
import ru.ok.model.search.SearchType;

public abstract class HookSearchBaseObserver extends HookBaseProcessor {
    private OnSearchListener onSearchListener;

    public interface OnSearchListener {
        void onSearch(String str, SearchType searchType);
    }

    protected abstract SearchType getSearchType();

    public HookSearchBaseObserver(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    protected void onHookExecute(Uri uri) {
        String query = uri.getQueryParameter(DiscoverInfo.ELEMENT);
        if (query != null) {
            query = query.replace("+", " ");
        }
        if (this.onSearchListener != null) {
            this.onSearchListener.onSearch(query, getSearchType());
        }
    }
}
