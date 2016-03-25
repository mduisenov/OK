package ru.ok.android.ui.users.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.android.gms.plus.PlusShare;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.fragments.RefreshableListFragmentServiceHelper;
import ru.ok.android.fragments.RefreshableRecyclerFragmentHelper;
import ru.ok.android.fragments.UsersListFragment;
import ru.ok.android.ui.SearchQueryTextHandler;
import ru.ok.android.ui.adapters.friends.UserInfosController.SelectionsMode;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.users.UsersSelectionParams;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.filter.TranslateNormalizer;

public class FriendsListFilteredFragment extends UsersListFragment {
    private MenuItem doneItem;
    private String userQuery;

    /* renamed from: ru.ok.android.ui.users.fragments.FriendsListFilteredFragment.1 */
    class C13041 extends SearchQueryTextHandler {
        C13041(long x0) {
            super(x0);
        }

        protected void onSearchQueryChange(String s) {
            FriendsListFilteredFragment.this.filterFriends(s);
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.FriendsListFilteredFragment.2 */
    class C13052 implements OnActionExpandListener {
        C13052() {
        }

        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;
        }

        public boolean onMenuItemActionCollapse(MenuItem item) {
            NavigationHelper.finishActivity(FriendsListFilteredFragment.this.getActivity());
            return false;
        }
    }

    private static void initArguments(Bundle arguments, boolean doShowSelection, String selectedUserId, SelectionsMode selectionsMode, UsersSelectionParams selectionParams, int titleId) {
        UsersListFragment.initArguments(arguments, doShowSelection, selectedUserId, selectionsMode, selectionParams);
        arguments.putInt(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, titleId);
    }

    public static Bundle newArguments(boolean doShowSelection, String selectedUserId, SelectionsMode selectionsMode, UsersSelectionParams selectionParams, int titleId) {
        Bundle args = new Bundle();
        initArguments(args, doShowSelection, selectedUserId, selectionsMode, selectionParams, titleId);
        return args;
    }

    public static Fragment newInstance(boolean showSelection, String selectedUserId) {
        Fragment result = new FriendsListFilteredFragment();
        result.setArguments(newArguments(showSelection, selectedUserId, SelectionsMode.SINGLE, null, 0));
        return result;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(getParentFragment() == null);
    }

    protected CharSequence getTitle() {
        return getStringLocalized(getTitleId());
    }

    private int getTitleId() {
        return getArguments().getInt(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, 2131165394);
    }

    protected RefreshableRecyclerFragmentHelper createRefreshHelper() {
        return new RefreshableListFragmentServiceHelper(this, getActivity(), "friends_update_time", 2131166268, 2131624119, getClass().getSimpleName());
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isFilteringActive()) {
            inflater.inflate(2131689490, menu);
            MenuItem item = menu.findItem(2131625463);
            MenuItemCompat.expandActionView(item);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
            searchView.setQueryHint(getStringLocalized(2131165887));
            searchView.setOnQueryTextListener(new C13041(200));
            if (getArguments() != null) {
                String filter = getArguments().getString("filter");
                if (filter != null) {
                    searchView.setQuery(filter, true);
                }
            }
            MenuItemCompat.setOnActionExpandListener(item, new C13052());
        }
        if (isDoneButtonActive() && inflateMenuLocalized(2131689526, menu)) {
            this.doneItem = menu.findItem(2131625470);
            if (this.doneItem != null && getSelectionsMode() == SelectionsMode.MEDIA_TOPICS) {
                this.doneItem.setEnabled(true);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        this.userQuery = bundle == null ? null : bundle.getString("filter");
        String filter = this.userQuery;
        if (filter == null) {
            filter = "";
        }
        filter = (TranslateNormalizer.normalizeText4Sorting(filter.toUpperCase()) + "%") + "%";
        return new CursorLoader(getActivity(), OdklProvider.friendsUri(), null, "user_n_first_name LIKE ? OR user_n_last_name LIKE ?", new String[]{filter, filter}, "user_n_first_name, user_n_last_name");
    }

    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && cursor.getCount() == 0) {
            if (TextUtils.isEmpty(this.userQuery)) {
                this.emptyView.setType(Type.FRIENDS_LIST_CONVERSATIONS);
            } else {
                this.emptyView.setType(Type.SEARCH);
            }
        }
        super.onLoadFinished((Loader) cursorLoader, cursor);
    }

    protected void filterFriends(String filter) {
        getArguments().putString("filter", filter);
        getLoaderManager().restartLoader(0, getArguments(), this);
    }

    protected boolean isFilteringActive() {
        return true;
    }

    protected boolean isDoneButtonActive() {
        return true;
    }
}
