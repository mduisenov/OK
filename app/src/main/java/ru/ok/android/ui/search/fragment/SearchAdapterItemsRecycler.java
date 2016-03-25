package ru.ok.android.ui.search.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ru.ok.model.search.SearchResultCommunity;
import ru.ok.model.search.SearchResultGroup;
import ru.ok.model.search.SearchResultUser;
import ru.ok.model.search.SearchType;

public final class SearchAdapterItemsRecycler {
    protected final HashMap<String, List<SearchAdapterItem>> dump;

    public SearchAdapterItemsRecycler() {
        this.dump = new HashMap();
    }

    public void recycle(SearchAdapterItem item) {
        String type = item.getClass().getSimpleName();
        List<SearchAdapterItem> bucket = (List) this.dump.get(type);
        if (bucket == null) {
            bucket = new ArrayList();
            this.dump.put(type, bucket);
        }
        bucket.add(item);
    }

    public void clear() {
        this.dump.clear();
    }

    public <T extends SearchAdapterItem> T salvage(Class<T> type) {
        List<SearchAdapterItem> bucket = (List) this.dump.get(type.getSimpleName());
        if (bucket == null || bucket.isEmpty()) {
            return null;
        }
        return (SearchAdapterItem) bucket.remove(0);
    }

    protected HeaderTitleItem getHeaderTitleItem(String title) {
        HeaderTitleItem headerItem = (HeaderTitleItem) salvage(HeaderTitleItem.class);
        if (headerItem == null) {
            headerItem = new HeaderTitleItem();
        }
        headerItem.title = title;
        return headerItem;
    }

    protected DividerItem getDividerItem() {
        DividerItem dividerItem = (DividerItem) salvage(DividerItem.class);
        if (dividerItem == null) {
            return new DividerItem();
        }
        return dividerItem;
    }

    protected FooterItem getFooterItem() {
        FooterItem footerItem = (FooterItem) salvage(FooterItem.class);
        if (footerItem == null) {
            return new FooterItem();
        }
        return footerItem;
    }

    protected GroupItem getGroupItem(SearchResultGroup result) {
        GroupItem groupItem = (GroupItem) salvage(GroupItem.class);
        if (groupItem == null) {
            groupItem = new GroupItem();
        }
        groupItem.setGroupSearchResult(result);
        return groupItem;
    }

    protected CommunityItem getCommunityItem(SearchResultCommunity result) {
        CommunityItem communityItem = (CommunityItem) salvage(CommunityItem.class);
        if (communityItem == null) {
            communityItem = new CommunityItem();
        }
        communityItem.setCommunitySearchResult(result);
        return communityItem;
    }

    protected UserItem getUserItem(SearchResultUser result) {
        UserItem userItem = (UserItem) salvage(UserItem.class);
        if (userItem == null) {
            userItem = new UserItem();
        }
        userItem.setUserSearchResult(result);
        return userItem;
    }

    protected ExpandItem getExpandItemItem(String text, SearchType type) {
        ExpandItem expandItem = (ExpandItem) salvage(ExpandItem.class);
        if (expandItem == null) {
            expandItem = new ExpandItem();
        }
        expandItem.text = text;
        expandItem.type = type;
        return expandItem;
    }
}
