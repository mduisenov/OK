package ru.ok.android.fragments.web.hooks.search;

import ru.ok.android.fragments.web.hooks.search.HookSearchBaseObserver.OnSearchListener;
import ru.ok.model.search.SearchType;

public class HookSearchUsersObserver extends HookSearchBaseObserver {
    public HookSearchUsersObserver(OnSearchListener onSearchListener) {
        super(onSearchListener);
    }

    protected SearchType getSearchType() {
        return SearchType.USER;
    }

    protected String getHookName() {
        return "/apphook/searchUsers";
    }
}
