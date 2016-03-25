package ru.ok.android.fragments.web.hooks.search;

import ru.ok.android.fragments.web.hooks.search.HookSearchBaseObserver.OnSearchListener;
import ru.ok.model.search.SearchType;

public class HookSearchAllObserver extends HookSearchBaseObserver {
    public HookSearchAllObserver(OnSearchListener onSearchListener) {
        super(onSearchListener);
    }

    protected SearchType getSearchType() {
        return null;
    }

    protected String getHookName() {
        return "/apphook/searchAll";
    }
}
