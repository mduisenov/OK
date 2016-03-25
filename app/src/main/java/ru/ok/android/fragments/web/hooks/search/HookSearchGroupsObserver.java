package ru.ok.android.fragments.web.hooks.search;

import ru.ok.android.fragments.web.hooks.search.HookSearchBaseObserver.OnSearchListener;
import ru.ok.model.search.SearchType;

public class HookSearchGroupsObserver extends HookSearchBaseObserver {
    public HookSearchGroupsObserver(OnSearchListener onSearchListener) {
        super(onSearchListener);
    }

    protected SearchType getSearchType() {
        return SearchType.GROUP;
    }

    protected String getHookName() {
        return "/apphook/searchGroups";
    }
}
