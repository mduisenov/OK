package ru.ok.android.ui.search.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.adapters.friends.ItemClickListenerControllerProvider;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.DividerBlockItem;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.DividerItem;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.FooterCardItem;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.UserCardItem;
import ru.ok.android.ui.custom.cards.listcard.CardViewHolder;
import ru.ok.android.ui.custom.cards.search.CommunityViewsHolder;
import ru.ok.android.ui.custom.cards.search.ExpandViewsHolder;
import ru.ok.android.ui.custom.cards.search.GroupViewsHolder;
import ru.ok.android.ui.custom.cards.search.HeaderTitleViewsHolder;
import ru.ok.android.ui.custom.cards.search.UserViewsHolder;
import ru.ok.android.ui.utils.AdapterItemViewTypeMaxValueProvider;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.GroupInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.search.SearchResult;
import ru.ok.model.search.SearchResult.SearchScope;
import ru.ok.model.search.SearchResultCommunity;
import ru.ok.model.search.SearchResultGroup;
import ru.ok.model.search.SearchResultUser;
import ru.ok.model.search.SearchType;

public abstract class SearchResultsAdapter extends Adapter<CardViewHolder> implements ItemClickListenerControllerProvider, AdapterItemViewTypeMaxValueProvider {
    private Context context;
    private final RecyclerItemClickListenerController itemClickListenerController;
    protected final ArrayList<SearchAdapterItem> items;
    private OnAdapterItemClickListener onAdapterItemClickListener;
    protected final SearchAdapterItemsRecycler recycler;
    protected final ArrayList<SearchResult> searchResults;
    protected final ArrayList<SearchResultsGroup> searchResultsGroups;

    public interface OnAdapterItemClickListener {
        void onCommunityClicked(GroupInfo groupInfo);

        void onExpandClicked(SearchType searchType);

        void onGroupClicked(GroupInfo groupInfo);

        void onUserClicked(UserInfo userInfo);
    }

    protected static final class SearchResultsGroup {
        public boolean expandable;
        public ArrayList<SearchResult> results;
        public SearchScope scope;
        public SearchType type;

        protected SearchResultsGroup() {
            this.results = new ArrayList();
            this.expandable = false;
        }
    }

    protected abstract void splitInGroups(ArrayList<SearchResult> arrayList, ArrayList<SearchResultsGroup> arrayList2);

    public SearchResultsAdapter(Context context, List<SearchResult> searchResults) {
        this.itemClickListenerController = new RecyclerItemClickListenerController();
        this.items = new ArrayList();
        this.searchResults = new ArrayList();
        this.searchResultsGroups = new ArrayList();
        this.recycler = new SearchAdapterItemsRecycler();
        this.context = context.getApplicationContext();
        addResults(searchResults);
    }

    public int getItemCount() {
        return this.items.size();
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public int getItemViewType(int position) {
        return getItemType(getItemInternal(position));
    }

    public int getItemViewTypeMaxValue() {
        return 7;
    }

    public int getItemType(SearchAdapterItem item) {
        if (item.getType() == UserItem.TYPE) {
            return 0;
        }
        if (item.getType() == GroupItem.TYPE) {
            return 1;
        }
        if (item.getType() == HeaderTitleItem.TYPE) {
            return 2;
        }
        if (item.getType() == FooterItem.TYPE) {
            return 3;
        }
        if (item.getType() == ExpandItem.TYPE) {
            return 4;
        }
        if (item.getType() == BlocksDividerItem.TYPE) {
            return 5;
        }
        if (item.getType() == DividerItem.TYPE) {
            return 6;
        }
        if (item.getType() == CommunityItem.TYPE) {
            return 7;
        }
        return -1;
    }

    private SearchAdapterItem getItemInternal(int position) {
        return (SearchAdapterItem) this.items.get(position);
    }

    public CardViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        switch (type) {
            case RECEIVED_VALUE:
                return new UserViewsHolder(UserCardItem.newViewTypeSearch(parent));
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return new GroupViewsHolder(GroupItem.newView(parent));
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return new HeaderTitleViewsHolder(HeaderTitleItem.newView(parent));
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return new CardViewHolder(FooterCardItem.newView(parent));
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return new ExpandViewsHolder(ExpandItem.newView(parent));
            case Message.UUID_FIELD_NUMBER /*5*/:
                return new CardViewHolder(DividerBlockItem.newView(parent));
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return new CardViewHolder(DividerItem.newView(parent));
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                return new CommunityViewsHolder(CommunityItem.newView(parent));
            default:
                return null;
        }
    }

    public void onBindViewHolder(CardViewHolder holder, int position) {
        ((SearchAdapterItem) this.items.get(position)).bindViewHolder(holder, this);
        this.itemClickListenerController.onBindViewHolder(holder, position);
    }

    public void addResults(List<SearchResult> results) {
        if (results != null && !results.isEmpty()) {
            this.searchResults.addAll(results);
            recycle();
            this.items.clear();
            this.searchResultsGroups.clear();
            splitInGroups(this.searchResults, this.searchResultsGroups);
            fillAdapterItems();
            this.recycler.clear();
            notifyDataSetChanged();
        }
    }

    public void clear() {
        this.recycler.clear();
        this.items.clear();
        this.searchResults.clear();
        this.searchResultsGroups.clear();
        notifyDataSetChanged();
    }

    private void recycle() {
        if (this.items != null && !this.items.isEmpty()) {
            Iterator i$ = this.items.iterator();
            while (i$.hasNext()) {
                this.recycler.recycle((SearchAdapterItem) i$.next());
            }
        }
    }

    private void fillAdapterItems() {
        Iterator i$ = this.searchResultsGroups.iterator();
        while (i$.hasNext()) {
            fillGroup((SearchResultsGroup) i$.next());
        }
    }

    private void fillGroup(SearchResultsGroup group) {
        this.items.add(this.recycler.getHeaderTitleItem(getTitleForResultsGroup(group)));
        int lastResultIdx = group.results.size() - 1;
        for (int i = 0; i < group.results.size(); i++) {
            SearchResult result = (SearchResult) group.results.get(i);
            if (result.getType() == SearchType.USER) {
                this.items.add(this.recycler.getUserItem((SearchResultUser) result));
            } else if (result.getType() == SearchType.GROUP) {
                this.items.add(this.recycler.getGroupItem((SearchResultGroup) result));
            } else if (result.getType() == SearchType.COMMUNITY) {
                this.items.add(this.recycler.getCommunityItem((SearchResultCommunity) result));
            }
            if (i != lastResultIdx) {
                this.items.add(this.recycler.getDividerItem());
            }
        }
        if (group.expandable) {
            this.items.add(this.recycler.getExpandItemItem(LocalizationManager.getString(getContext(), 2131166564), group.type));
        }
        this.items.add(this.recycler.getFooterItem());
    }

    protected String getTitleForResultsGroup(SearchResultsGroup resultsGroup) {
        if (resultsGroup.type == SearchType.USER) {
            if (resultsGroup.scope == SearchScope.OWN) {
                return LocalizationManager.getString(getContext(), 2131166244);
            }
            return LocalizationManager.getString(getContext(), 2131166805);
        } else if (resultsGroup.type == SearchType.GROUP) {
            if (resultsGroup.scope == SearchScope.OWN) {
                return LocalizationManager.getString(getContext(), 2131166245);
            }
            return LocalizationManager.getString(getContext(), 2131165961);
        } else if (resultsGroup.type != SearchType.COMMUNITY) {
            return null;
        } else {
            if (resultsGroup.scope == SearchScope.OWN) {
                return LocalizationManager.getString(getContext(), 2131166243);
            }
            return LocalizationManager.getString(getContext(), 2131165614);
        }
    }

    public void triggerAdapterItemClick(int position) {
        if (this.onAdapterItemClickListener != null) {
            SearchAdapterItem item = (SearchAdapterItem) this.items.get(position);
            int type = getItemType(item);
            if (type == 0) {
                this.onAdapterItemClickListener.onUserClicked(((UserItem) item).userSearchResult.getUserInfo());
            } else if (type == 1) {
                this.onAdapterItemClickListener.onGroupClicked(((GroupItem) item).groupSearchResult.getGroupInfo());
            } else if (type == 4) {
                this.onAdapterItemClickListener.onExpandClicked(((ExpandItem) item).type);
            } else if (type == 7) {
                this.onAdapterItemClickListener.onCommunityClicked(((CommunityItem) item).searchResult.getGroupInfo());
            }
        }
    }

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener onAdapterItemClickListener) {
        this.onAdapterItemClickListener = onAdapterItemClickListener;
    }

    public Context getContext() {
        return this.context;
    }

    public RecyclerItemClickListenerController getItemClickListenerController() {
        return this.itemClickListenerController;
    }

    protected boolean shouldLoadMore(int position) {
        return getItemCount() - position == 6;
    }
}
