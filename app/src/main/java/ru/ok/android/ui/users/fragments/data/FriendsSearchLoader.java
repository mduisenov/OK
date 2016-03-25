package ru.ok.android.ui.users.fragments.data;

import android.content.Context;
import android.support.v4.content.GeneralDataLoader;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.services.processors.SearchQuickProcessor;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.model.UserInfo;
import ru.ok.model.search.SearchResult;
import ru.ok.model.search.SearchResult.SearchScope;
import ru.ok.model.search.SearchResultUser;
import ru.ok.model.search.SearchResults;
import ru.ok.model.search.SearchResults.SearchContext;
import ru.ok.model.search.SearchType;

public final class FriendsSearchLoader extends GeneralDataLoader<FriendsSearchBundle> {
    private String anchor;
    private final List<UserInfoExtended> friends;
    private boolean hasMore;
    public final String query;
    private final List<UserInfo> users;

    public FriendsSearchLoader(Context context, String query) {
        super(context);
        this.friends = new ArrayList();
        this.users = new ArrayList();
        this.query = query;
    }

    protected FriendsSearchBundle loadData() {
        try {
            SearchResults results = SearchQuickProcessor.performSearch(this.query, new SearchType[]{SearchType.USER}, SearchContext.USER, this.anchor, PagingDirection.FORWARD, 20);
            List<SearchResult> found = results.getFound();
            if (!(found == null || found.isEmpty())) {
                for (SearchResult searchResult : found) {
                    if (searchResult.getType() == SearchType.USER) {
                        UserInfo userInfo = ((SearchResultUser) searchResult).getUserInfo();
                        if (searchResult.getScope() == SearchScope.OWN) {
                            this.friends.add(new UserInfoExtended(userInfo));
                        } else {
                            this.users.add(userInfo);
                        }
                    }
                }
            }
            this.anchor = results.getAnchor();
            this.hasMore = results.isHasMore();
            return new FriendsSearchBundle(this.friends, this.users);
        } catch (Throwable e) {
            Logger.m178e(e);
            return null;
        }
    }

    public boolean isHasMore() {
        return this.hasMore;
    }

    public List<UserInfoExtended> getFriends() {
        return new ArrayList(this.friends);
    }
}
