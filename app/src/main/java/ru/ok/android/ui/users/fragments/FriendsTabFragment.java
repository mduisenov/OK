package ru.ok.android.ui.users.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.internal.app.ToolbarActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.SearchView.OnSuggestionListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FilterQueryProvider;
import java.util.List;
import ru.ok.android.db.FriendsSuggestTable;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.custom.indicator.PagerSlidingTabStrip;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.tabbar.manager.BaseTabbarManager;
import ru.ok.android.ui.users.fragments.FriendsFragmentNew.RelationsListener;
import ru.ok.android.ui.users.fragments.data.FriendsRelationsAdapter;
import ru.ok.android.ui.users.fragments.data.FriendsRelationsAdapter.RelationItem;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.widget.ViewPagerDisable;
import ru.ok.java.api.request.relatives.RelativesType;

public final class FriendsTabFragment extends BaseFragment implements OnPageChangeListener, OnNavigationListener, OnQueryTextListener, OnSuggestionListener, RelationsListener {
    private PagerSlidingTabStrip indicator;
    private MenuItem itemSearch;
    private FriendsPagerAdapter pagerAdapter;
    String query;
    private FriendsRelationsAdapter relationsAdapter;
    private SearchView searchView;
    RelativesType type;
    private ViewPagerDisable viewPager;

    /* renamed from: ru.ok.android.ui.users.fragments.FriendsTabFragment.1 */
    class C13081 extends AsyncTask<String, Void, Void> {
        C13081() {
        }

        protected Void doInBackground(String... params) {
            Activity activity = FriendsTabFragment.this.getActivity();
            if (activity != null) {
                Uri result = activity.getContentResolver().insert(OdklProvider.friendsSuggest(), FriendsSuggestTable.fillValues(params[0]));
            }
            return null;
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.FriendsTabFragment.2 */
    class C13092 implements FilterQueryProvider {
        C13092() {
        }

        public Cursor runQuery(CharSequence constraint) {
            Context context = FriendsTabFragment.this.getContext();
            if (context == null) {
                return null;
            }
            return context.getContentResolver().query(OdklProvider.friendsSuggest(), new String[]{"suggestion", "_id"}, "suggestion LIKE ?", new String[]{constraint + "%"}, null);
        }
    }

    final class FriendsPagerAdapter extends FragmentPagerAdapter {
        FriendsFragmentNew fragmentFriends;
        FriendsOnlineFragmentNew onlineUsersFragment;

        public FriendsPagerAdapter() {
            super(FriendsTabFragment.this.getChildFragmentManager());
        }

        private FriendsFragmentNew getFriendsFragment() {
            if (this.fragmentFriends == null) {
                this.fragmentFriends = new FriendsFragmentNew();
                configureFriendsFragment();
            }
            return this.fragmentFriends;
        }

        private void configureFriendsFragment() {
            this.fragmentFriends.setRelationsListener(FriendsTabFragment.this);
            if (FriendsTabFragment.this.type != null) {
                this.fragmentFriends.setRelationType(FriendsTabFragment.this.type);
            }
            if (!TextUtils.isEmpty(FriendsTabFragment.this.query)) {
                this.fragmentFriends.setQuery(FriendsTabFragment.this.query, true);
            }
        }

        private FriendsOnlineFragmentNew getOnlineFragment() {
            if (this.onlineUsersFragment == null) {
                this.onlineUsersFragment = new FriendsOnlineFragmentNew();
                configureOnlineFragment();
            }
            return this.onlineUsersFragment;
        }

        private void configureOnlineFragment() {
            if (FriendsTabFragment.this.type != null) {
                this.onlineUsersFragment.setRelationType(FriendsTabFragment.this.type);
            }
            if (!TextUtils.isEmpty(FriendsTabFragment.this.query)) {
                this.onlineUsersFragment.setQuery(FriendsTabFragment.this.query);
            }
        }

        public CharSequence getPageTitle(int position) {
            int titleResId;
            switch (position) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    titleResId = 2131166316;
                    break;
                default:
                    titleResId = 2131165378;
                    break;
            }
            return LocalizationManager.getString(FriendsTabFragment.this.getContext(), titleResId);
        }

        public Fragment getItem(int position) {
            switch (position) {
                case RECEIVED_VALUE:
                    return getFriendsFragment();
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    return getOnlineFragment();
                default:
                    return null;
            }
        }

        public int getCount() {
            return 2;
        }

        public void setRelationType(RelativesType type) {
            if (this.fragmentFriends != null) {
                this.fragmentFriends.setRelationType(type);
            }
            if (this.onlineUsersFragment != null) {
                this.onlineUsersFragment.setRelationType(type);
            }
        }

        public void setQuery(String newText, boolean runImmediately) {
            if (this.fragmentFriends != null) {
                this.fragmentFriends.setQuery(newText, runImmediately);
            }
            if (this.onlineUsersFragment != null) {
                this.onlineUsersFragment.setQuery(newText);
            }
        }

        public void restoreState() {
            List<Fragment> fragments = FriendsTabFragment.this.getChildFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment != null) {
                        if (fragment instanceof FriendsFragmentNew) {
                            this.fragmentFriends = (FriendsFragmentNew) fragment;
                            configureFriendsFragment();
                        } else if (fragment instanceof FriendsOnlineFragmentNew) {
                            this.onlineUsersFragment = (FriendsOnlineFragmentNew) fragment;
                            configureOnlineFragment();
                        }
                    }
                }
            }
        }
    }

    public boolean onQueryTextSubmit(String query) {
        this.pagerAdapter.setQuery(query, true);
        new C13081().execute(new String[]{query});
        return true;
    }

    public boolean onQueryTextChange(String newText) {
        FriendsPagerAdapter friendsPagerAdapter = this.pagerAdapter;
        this.query = newText;
        friendsPagerAdapter.setQuery(newText, false);
        return true;
    }

    protected CharSequence getTitle() {
        return null;
    }

    public boolean onSuggestionSelect(int position) {
        CursorAdapter adapter = this.searchView.getSuggestionsAdapter();
        this.searchView.setQuery(adapter.convertToString((Cursor) adapter.getItem(position)), false);
        return true;
    }

    public boolean onSuggestionClick(int position) {
        CursorAdapter adapter = this.searchView.getSuggestionsAdapter();
        this.searchView.setQuery(adapter.convertToString((Cursor) adapter.getItem(position)), false);
        return true;
    }

    protected int getLayoutId() {
        return 2130903220;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (inflateMenuLocalized(2131689505, menu)) {
            this.itemSearch = menu.findItem(2131625451);
            if (this.itemSearch != null) {
                this.searchView = (SearchView) MenuItemCompat.getActionView(this.itemSearch);
                this.searchView.setQueryHint(getStringLocalized(2131165887));
                this.searchView.setOnQueryTextListener(this);
                this.searchView.setOnSuggestionListener(this);
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), 17367050, null, new String[]{"suggestion"}, new int[]{16908308}, 0);
                adapter.setStringConversionColumn(0);
                adapter.setFilterQueryProvider(new C13092());
                this.searchView.setSuggestionsAdapter(adapter);
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = LocalizationManager.inflate(getContext(), getLayoutId(), container, false);
        this.viewPager = (ViewPagerDisable) view.findViewById(2131624881);
        this.indicator = (PagerSlidingTabStrip) view.findViewById(C0263R.id.indicator);
        this.pagerAdapter = new FriendsPagerAdapter();
        this.pagerAdapter.restoreState();
        this.viewPager.setOffscreenPageLimit(1);
        this.viewPager.setAdapter(this.pagerAdapter);
        this.viewPager.setCurrentItem(0);
        this.indicator.setViewPager(this.viewPager);
        this.indicator.setOnPageChangeListener(this);
        this.relationsAdapter = new FriendsRelationsAdapter(getActivity());
        ToolbarActionBar toolbar = (ToolbarActionBar) getSupportActionBar();
        toolbar.setNavigationMode(1);
        toolbar.setListNavigationCallbacks(this.relationsAdapter, this);
        return view;
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageSelected(int position) {
        if (position == 1) {
            if (this.pagerAdapter.onlineUsersFragment != null && this.pagerAdapter.onlineUsersFragment.isEmpty()) {
                appBarExpand();
            }
        } else if (position == 0 && this.pagerAdapter.fragmentFriends != null && this.pagerAdapter.fragmentFriends.emptyView != null && this.pagerAdapter.fragmentFriends.emptyView.getVisibility() == 0) {
            appBarExpand();
        }
    }

    public void onPageScrollStateChanged(int state) {
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (BaseCompatToolbarActivity.isUseTabbar(getContext())) {
            ((BaseTabbarManager) getContext()).showTabbar(true);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        FriendsPagerAdapter friendsPagerAdapter = this.pagerAdapter;
        RelativesType relativesType = this.relationsAdapter.getItem(itemPosition).type;
        this.type = relativesType;
        friendsPagerAdapter.setRelationType(relativesType);
        return true;
    }

    public void updateRelations(List<RelationItem> relations) {
        this.relationsAdapter.updateRelations(relations);
    }

    public void selectSearch() {
        MenuItemCompat.expandActionView(this.itemSearch);
    }
}
