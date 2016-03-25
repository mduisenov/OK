package ru.ok.android.ui.groups.search;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.loadmore.LoadMoreRecyclerAdapter;
import ru.ok.android.ui.groups.adapters.GroupsSearchVerticalAdapter;
import ru.ok.android.ui.groups.adapters.GroupsVerticalAdapter;
import ru.ok.android.ui.groups.fragments.GroupsFragment;
import ru.ok.android.ui.groups.fragments.GroupsFragment.GroupsVerticalSpanSizeLookup;
import ru.ok.android.ui.groups.loaders.BaseGroupsPageLoader;
import ru.ok.android.ui.groups.loaders.GroupsLoaderResult;
import ru.ok.android.ui.groups.loaders.GroupsSearchLoader;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.paging.PagingDirection;

public class GroupsSearchFragment extends GroupsFragment {
    private String query;

    private class GroupsSearchCallback implements LoaderCallbacks<GroupsLoaderResult> {
        private GroupsSearchCallback() {
        }

        public Loader<GroupsLoaderResult> onCreateLoader(int id, Bundle args) {
            return new GroupsSearchLoader(GroupsSearchFragment.this.getContext(), GroupsSearchFragment.this.query, null, PagingDirection.FORWARD, 40);
        }

        public void onLoadFinished(Loader<GroupsLoaderResult> loader, GroupsLoaderResult result) {
            GroupsSearchFragment.this.processGroupLoaderResult(result);
        }

        public void onLoaderReset(Loader<GroupsLoaderResult> loader) {
        }
    }

    public static class GroupsSearchSpanSizeLookup extends GroupsVerticalSpanSizeLookup {
        public GroupsSearchSpanSizeLookup(RecyclerView recyclerView, LoadMoreRecyclerAdapter loadMoreRecyclerAdapter, boolean hasHeader) {
            super(recyclerView, loadMoreRecyclerAdapter, hasHeader);
        }

        public int getSpanSize(int position) {
            if (getGridLayoutManager().getSpanCount() == 2) {
                Adapter adapter = this.recyclerView.getAdapter();
                if (adapter != null) {
                    if (adapter.getItemViewType(position) == 2131624361) {
                        return 2;
                    }
                    if (position == adapter.getItemCount() - 1 && this.loadMoreRecyclerAdapter.getController().isBottomViewAdded()) {
                        return 2;
                    }
                }
            }
            return 1;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.query = savedInstanceState.getString(DiscoverInfo.ELEMENT);
        }
    }

    public void setQuery(String query) {
        this.query = query;
        if (getActivity() != null) {
            GroupsSearchLoader groupsSearchLoader = (GroupsSearchLoader) getGroupsLoader();
            if (groupsSearchLoader != null) {
                groupsSearchLoader.setQuery(query);
                groupsSearchLoader.setAnchor(null);
                groupsSearchLoader.forceLoad();
                this.emptyView.setState(State.LOADING);
            }
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected void initLoaders() {
        getLoaderManager().initLoader(2131624278, null, new GroupsSearchCallback());
        getGroupsLoader().forceLoad();
        this.emptyView.setState(State.LOADING);
    }

    protected void init() {
        this.title = LocalizationManager.getString(getContext(), 2131165950);
        this.subtitle = null;
        setTitleIfVisible(this.title);
        setSubTitleIfVisible(this.subtitle);
        setHasOptionsMenu(false);
    }

    protected BaseGroupsPageLoader getGroupsLoader() {
        return (GroupsSearchLoader) getLoaderManager().getLoader(2131624278);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    protected GroupsVerticalAdapter getGroupsAdapter() {
        return new GroupsSearchVerticalAdapter(getContext(), false);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DiscoverInfo.ELEMENT, this.query);
    }

    protected GroupsVerticalSpanSizeLookup getSpanSizeLookup() {
        return new GroupsSearchSpanSizeLookup(this.recyclerView, this.loadMoreAdapter, false);
    }
}
