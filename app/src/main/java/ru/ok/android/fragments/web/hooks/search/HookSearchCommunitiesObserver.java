package ru.ok.android.fragments.web.hooks.search;

import ru.ok.android.fragments.web.hooks.search.HookSearchBaseObserver.OnSearchListener;
import ru.ok.model.search.SearchType;

public class HookSearchCommunitiesObserver extends HookSearchBaseObserver {
    public HookSearchCommunitiesObserver(OnSearchListener onSearchListener) {
        super(onSearchListener);
    }

    protected SearchType getSearchType() {
        return SearchType.COMMUNITY;
    }

    protected String getHookName() {
        return "/apphook/searchCommunities";
    }
}
