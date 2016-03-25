package ru.ok.android.ui.stream.list;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.ui.stream.BaseStreamListFragment.AdapterWithPromoLinks;
import ru.ok.android.ui.stream.StreamListFragment.AdapterWithAppPolls;
import ru.ok.android.ui.stream.StreamListFragment.AdapterWithPhotoRoll;
import ru.ok.android.ui.stream.suggestions.PymkPreviewHolder;
import ru.ok.android.ui.stream.suggestions.SearchSuggestionsHolder;
import ru.ok.java.api.json.users.JsonPymkParser;
import ru.ok.model.UserInfo;
import ru.ok.onelog.search.FeedSuggestionType;
import ru.ok.onelog.search.FeedSuggestionsDisplayFactory;

public class StreamHeaderRecyclerAdapter extends Adapter implements AdapterWithPromoLinks, AdapterWithAppPolls, AdapterWithPhotoRoll {
    private Activity activity;
    private AppPollHolder appPollHolder;
    private int extraMargin;
    private Fragment fragment;
    private LayoutInflater inflater;
    private boolean isInitialized;
    private ArrayList<Integer> items;
    private ViewGroup parent;
    private PhotoRollViewHolder photoRollViewHolder;
    private PromoLinkViewHolder promoLinkViewHolder;
    private int pymkPosition;
    private PymkPreviewHolder pymkPreviewHolder;
    private ArrayList<UserInfo> pymkUsers;
    private SearchSuggestionsHolder searchSuggestionsHolder;

    public StreamHeaderRecyclerAdapter() {
        this.pymkUsers = new ArrayList();
        this.isInitialized = false;
        this.pymkPosition = 0;
        this.items = new ArrayList();
        GlobalBus.register(this);
    }

    public StreamHeaderRecyclerAdapter(Activity activity, Fragment fragment, LayoutInflater inflater, ViewGroup parent) {
        this();
        this.activity = activity;
        this.inflater = inflater;
        this.parent = parent;
        this.fragment = fragment;
        this.pymkUsers = new ArrayList();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 2131624293:
                return this.appPollHolder;
            case 2131624295:
                return this.photoRollViewHolder;
            case 2131624296:
                return this.promoLinkViewHolder;
            case 2131624297:
                if (this.pymkPreviewHolder == null) {
                    this.pymkPreviewHolder = new PymkPreviewHolder(PymkPreviewHolder.createView(this.inflater, parent), this.pymkUsers, this.activity);
                }
                checkExtraMargin();
                return this.pymkPreviewHolder;
            case 2131624298:
                if (this.searchSuggestionsHolder == null) {
                    this.searchSuggestionsHolder = new SearchSuggestionsHolder(SearchSuggestionsHolder.createView(this.inflater, parent), this.activity, this.fragment);
                }
                checkExtraMargin();
                return this.searchSuggestionsHolder;
            default:
                return null;
        }
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
    }

    public int getItemViewType(int position) {
        return ((Integer) this.items.get(position)).intValue();
    }

    public int getItemCount() {
        return this.items.size();
    }

    public void setPromoLinksHolder(PromoLinkViewHolder holder) {
        this.promoLinkViewHolder = holder;
        addItem(2131624296);
    }

    public void setAppPollHolder(AppPollHolder holder) {
        this.appPollHolder = holder;
        addItem(2131624293);
    }

    @Subscribe(on = 2131623946, to = 2131624166)
    public void checkPymkResponse(BusEvent event) {
        ArrayList<UserInfo> users = event.bundleOutput.getParcelableArrayList(JsonPymkParser.KEY_USERS);
        if (users != null && users.size() > 0) {
            this.pymkUsers = users;
            if (this.pymkPreviewHolder != null) {
                this.pymkPreviewHolder.updatePymkUsers(this.pymkUsers);
            } else {
                addItem(this.pymkPosition, 2131624297);
                OneLog.log(FeedSuggestionsDisplayFactory.get(FeedSuggestionType.pymk));
                notifyDataSetChanged();
            }
        }
        GlobalBus.unregister(this);
    }

    public void addPymkPreview() {
        if (this.pymkPreviewHolder != null) {
            addItem(2131624297);
            return;
        }
        GlobalBus.send(2131623989, new BusEvent());
        this.pymkPosition = this.items.size();
    }

    public void addItem(int viewType) {
        this.items.add(Integer.valueOf(viewType));
    }

    public void addItem(int position, int viewType) {
        this.items.add(position, Integer.valueOf(viewType));
    }

    public void removeItemsForEmptyFeed() {
        removeItem(2131624298);
        removeItem(2131624297);
    }

    public void removeItem(int viewType) {
        for (int i = this.items.size() - 1; i >= 0; i--) {
            if (((Integer) this.items.get(i)).intValue() == viewType) {
                this.items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public boolean isInitialized() {
        return this.isInitialized;
    }

    public void setIsInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }

    protected void applyExtraMarginToElementPadding(View view, int extraPadding) {
        view.setPadding(extraPadding, view.getPaddingTop(), extraPadding, view.getPaddingBottom());
    }

    protected void checkExtraMargin() {
        if (this.pymkPreviewHolder != null) {
            applyExtraMarginToElementPadding(this.pymkPreviewHolder.itemView, this.extraMargin);
        }
        if (this.searchSuggestionsHolder != null) {
            applyExtraMarginToElementPadding(this.searchSuggestionsHolder.itemView, this.extraMargin);
        }
    }

    public void updateLandscapeMargins(StreamLayoutConfig layoutConfig) {
        this.extraMargin = layoutConfig.getExtraMarginForLandscapeAsInPortrait(true);
        checkExtraMargin();
    }

    public void setPhotoRollViewHolder(@NonNull PhotoRollViewHolder holder) {
        this.photoRollViewHolder = holder;
        addItem(2131624295);
    }
}
