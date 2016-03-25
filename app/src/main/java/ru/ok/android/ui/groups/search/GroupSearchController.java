package ru.ok.android.ui.groups.search;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import ru.mail.libverify.C0176R;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.localization.LocalizationManager;

public class GroupSearchController implements OnQueryTextListener {
    private final FragmentActivity activity;
    private MenuItem createMenuItem;
    private final Fragment fragment;
    private GroupsSearchFragment groupsSearchFragment;
    private boolean isIconified;
    private String previousQuery;
    private String query;
    private Handler searchHandler;
    private MenuItem searchMenuItem;
    private Runnable searchQueryPostRunnable;
    private SearchView searchView;

    /* renamed from: ru.ok.android.ui.groups.search.GroupSearchController.1 */
    class C09431 implements Runnable {
        C09431() {
        }

        public void run() {
            if ((GroupSearchController.this.previousQuery != null || !TextUtils.isEmpty(GroupSearchController.this.query)) && !TextUtils.equals(GroupSearchController.this.query, GroupSearchController.this.previousQuery)) {
                if (TextUtils.isEmpty(GroupSearchController.this.previousQuery) || !TextUtils.isEmpty(GroupSearchController.this.query)) {
                    if (GroupSearchController.this.groupsSearchFragment == null) {
                        GroupSearchController.this.groupsSearchFragment = new GroupsSearchFragment();
                        FragmentTransaction ft = GroupSearchController.this.fragment.getChildFragmentManager().beginTransaction();
                        ft.replace(2131624848, GroupSearchController.this.groupsSearchFragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                    GroupSearchController.this.groupsSearchFragment.setQuery(GroupSearchController.this.query);
                    GroupSearchController.this.searchFragmentSafeSetVisibility(0);
                } else {
                    GroupSearchController.this.searchHandler.removeCallbacks(GroupSearchController.this.searchQueryPostRunnable);
                    if (!(GroupSearchController.this.searchView.isShown() || GroupSearchController.this.groupsSearchFragment == null || GroupSearchController.this.groupsSearchFragment.getView() == null)) {
                        GroupSearchController.this.groupsSearchFragment.getView().setVisibility(8);
                    }
                }
                GroupSearchController.this.previousQuery = GroupSearchController.this.query;
            }
        }
    }

    /* renamed from: ru.ok.android.ui.groups.search.GroupSearchController.2 */
    class C09442 implements OnActionExpandListener {
        C09442() {
        }

        public boolean onMenuItemActionExpand(MenuItem item) {
            GroupSearchController.this.isIconified = false;
            GroupSearchController.this.createMenuItem.setVisible(false);
            return true;
        }

        public boolean onMenuItemActionCollapse(MenuItem item) {
            GroupSearchController.this.isIconified = true;
            GroupSearchController.this.createMenuItem.setVisible(true);
            GroupSearchController.this.hideSearchFragmentView();
            return true;
        }
    }

    private void searchFragmentSafeSetVisibility(int visibility) {
        if (this.groupsSearchFragment.getView() != null) {
            this.groupsSearchFragment.getView().setVisibility(visibility);
        }
    }

    public GroupSearchController(FragmentActivity activity, Fragment fragment) {
        this.searchHandler = new Handler();
        this.searchQueryPostRunnable = new C09431();
        this.isIconified = true;
        this.activity = activity;
        this.fragment = fragment;
    }

    public boolean onQueryTextSubmit(String query) {
        KeyBoardUtils.hideKeyBoard(this.activity);
        return false;
    }

    public boolean onQueryTextChange(String newQuery) {
        this.searchHandler.removeCallbacks(this.searchQueryPostRunnable);
        this.query = newQuery.trim();
        this.searchHandler.postDelayed(this.searchQueryPostRunnable, 1000);
        return true;
    }

    public void onCreateOptionsMenu(Menu menu) {
        this.searchMenuItem = menu.findItem(2131625441);
        this.createMenuItem = menu.findItem(2131625469);
        this.searchView = (SearchView) MenuItemCompat.getActionView(this.searchMenuItem);
        ImageView imageButton = (ImageView) this.searchView.findViewById(C0176R.id.search_button);
        if (imageButton != null) {
            imageButton.setImageResource(2130837592);
        }
        this.searchView.setQueryHint(LocalizationManager.getString(this.activity, 2131165965));
        this.searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(this.searchMenuItem, new C09442());
        if (!this.isIconified) {
            String savedQuery = this.query;
            this.searchView.setIconified(false);
            this.searchMenuItem.expandActionView();
            this.query = savedQuery;
            this.searchView.setQuery(savedQuery, false);
            this.createMenuItem.setVisible(false);
        }
    }

    public boolean handleBack() {
        if (hideSearchFragmentView()) {
            return true;
        }
        return false;
    }

    private boolean hideSearchFragmentView() {
        if (this.groupsSearchFragment == null || this.groupsSearchFragment.getView() == null || this.groupsSearchFragment.getView().getVisibility() != 0) {
            return false;
        }
        this.groupsSearchFragment.getView().setVisibility(8);
        return true;
    }

    public GroupsSearchFragment getGroupsSearchFragment() {
        return this.groupsSearchFragment;
    }

    public SearchView getSearchView() {
        return this.searchView;
    }

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean("iconified", this.searchView.isIconified());
        bundle.putString(DiscoverInfo.ELEMENT, this.query);
    }

    public void onRestoreInstanceState(@Nullable Bundle bundle) {
        if (bundle != null) {
            this.isIconified = bundle.getBoolean("iconified");
            this.query = bundle.getString(DiscoverInfo.ELEMENT);
        }
    }

    public void onPrepareOptionsMenu(Menu menu) {
    }
}
