package ru.ok.android.ui.search.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.ui.custom.cards.SuggestionsListView;
import ru.ok.android.ui.custom.cards.SuggestionsListView.OnSuggestionClickListener;
import ru.ok.android.ui.custom.cards.SuggestionsListView.OnSuggestionLongClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.custom.indicator.PagerSlidingTabStrip;
import ru.ok.android.ui.custom.search.KeyboardRelativeLayout;
import ru.ok.android.ui.search.fragment.SearchFragment.OnUserActionListener;
import ru.ok.android.ui.search.fragment.SuggestionsViewController;
import ru.ok.android.ui.search.util.SuggestionsStore;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.GroupInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.search.SearchType;

public class SearchActivity extends BaseActivity implements OnActionExpandListener, OnPageChangeListener, OnCloseListener, OnQueryTextListener, OnSuggestionClickListener, OnSuggestionLongClickListener, OnUserActionListener {
    private SmartEmptyViewAnimated emptyView;
    private ViewPager pagerView;
    private String query;
    private KeyboardRelativeLayout rootView;
    private SearchHandler searchHandler;
    private SearchPagerAdapter searchPagerAdapter;
    private SearchView searchView;
    private PagerSlidingTabStrip stripIndicatorView;
    private SuggestionsViewController suggestionsController;
    private SuggestionsListView suggestionsView;

    /* renamed from: ru.ok.android.ui.search.activity.SearchActivity.1 */
    class C11801 implements OnPreDrawListener {
        C11801() {
        }

        public boolean onPreDraw() {
            SearchActivity.this.pagerView.getViewTreeObserver().removeOnPreDrawListener(this);
            SearchActivity.this.onQueryChanged(SearchActivity.this.query, true);
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.search.activity.SearchActivity.2 */
    class C11812 implements OnClickListener {
        final /* synthetic */ String val$suggestion;

        C11812(String str) {
            this.val$suggestion = str;
        }

        public void onClick(DialogInterface dialog, int which) {
            SuggestionsStore store = SuggestionsStore.getInstance(SearchActivity.this);
            store.removeSuggestion(this.val$suggestion);
            SearchActivity.this.suggestionsController.setSuggestions(store.getSuggestions());
            SearchActivity.this.updateSuggestionsState();
        }
    }

    public SearchActivity() {
        this.searchHandler = new SearchHandler(this);
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        setContentView(2130903087);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.rootView = (KeyboardRelativeLayout) findViewById(2131624575);
        this.emptyView = (SmartEmptyViewAnimated) findViewById(C0263R.id.empty_view);
        this.emptyView.setState(State.LOADED);
        this.emptyView.setType(Type.SEARCH_GLOBAL);
        this.suggestionsView = (SuggestionsListView) findViewById(2131624578);
        this.suggestionsController = new SuggestionsViewController(this.suggestionsView);
        this.suggestionsView.setOnSuggestionLongClickListener(this);
        this.suggestionsView.setOnSuggestionClickListener(this);
        this.pagerView = (ViewPager) findViewById(C0263R.id.pager);
        this.searchPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
        this.pagerView.setAdapter(this.searchPagerAdapter);
        this.stripIndicatorView = (PagerSlidingTabStrip) findViewById(C0263R.id.indicator);
        this.stripIndicatorView.setViewPager(this.pagerView);
        this.stripIndicatorView.setOnPageChangeListener(this);
        this.query = getIntent().getStringExtra("saquery");
        int tabPosition = 0;
        SearchType searchType = (SearchType) getIntent().getParcelableExtra("satype");
        if (searchType == SearchType.COMMUNITY || searchType == SearchType.GROUP) {
            tabPosition = 2;
        } else if (searchType == SearchType.USER) {
            tabPosition = 1;
        }
        if (savedInstanceState != null) {
            this.query = savedInstanceState.getString("saquery");
            tabPosition = savedInstanceState.getInt("saslctdtb");
        }
        if (!isQueryEmpty(this.query)) {
            this.stripIndicatorView.setVisibility(0);
        }
        this.pagerView.setCurrentItem(tabPosition, false);
        if (TextUtils.isEmpty(this.query)) {
            updateSearch(this.query);
        } else {
            this.pagerView.getViewTreeObserver().addOnPreDrawListener(new C11801());
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("saquery", this.query);
        outState.putInt("saslctdtb", this.pagerView.getCurrentItem());
    }

    public void onPageScrolled(int i, float v, int i2) {
    }

    public void onPageSelected(int position) {
        Logger.m173d("%d search tab selected with query \"%s\"", Integer.valueOf(position), this.query);
        if (!TextUtils.isEmpty(this.query)) {
            this.searchPagerAdapter.searchOnPosition(position, this.query, false);
            SuggestionsStore.getInstance(this).addSuggestion(this.query);
            KeyBoardUtils.hideKeyBoard(this);
        }
    }

    public void onPageScrollStateChanged(int position) {
    }

    public void onClick(SuggestionsListView view, View suggestionView, int position, String suggestion) {
        onQueryChanged(suggestion, true);
    }

    public void onLongClick(SuggestionsListView view, View suggestionView, int position, String suggestion) {
        new Builder(this).setMessage(getStringLocalized(2131165696)).setNegativeButton(getStringLocalized(2131165476), null).setPositiveButton(getStringLocalized(2131165671), new C11812(suggestion)).show();
    }

    public void onUserRequested(UserInfo userInfo) {
        SuggestionsStore.getInstance(this).addSuggestion(this.query);
        NavigationHelper.showUserInfo(this, userInfo.uid);
    }

    public void onGroupRequested(GroupInfo groupInfo) {
        SuggestionsStore.getInstance(this).addSuggestion(this.query);
        NavigationHelper.showGroupInfo(this, groupInfo.getId());
    }

    public void onCommunityRequested(GroupInfo groupInfo) {
        SuggestionsStore.getInstance(this).addSuggestion(this.query);
        NavigationHelper.showGroupInfoWeb(this, groupInfo.getId());
    }

    public void onShowContexted(SearchType type) {
        if (type == SearchType.GROUP) {
            this.pagerView.setCurrentItem(2, true);
        } else if (type == SearchType.USER) {
            this.pagerView.setCurrentItem(1, true);
        }
    }

    public void onQueryChanged(String suggestion, boolean submit) {
        Logger.m173d("Query changed to \"%s\" by suggestion. Submit: %b", suggestion, Boolean.valueOf(submit));
        if (this.searchView != null) {
            this.searchView.setQuery(suggestion, false);
            updateSearch(suggestion);
        }
    }

    public boolean onQueryTextSubmit(String query) {
        Logger.m173d("Query text submit: \"%s\"", query);
        this.searchHandler.removeQueuedUpdates();
        updateSearch(query);
        this.query = query;
        KeyBoardUtils.hideKeyBoard(this);
        return false;
    }

    public boolean onQueryTextChange(String query) {
        Logger.m173d("Query text changed to \"%s\"", query);
        this.searchHandler.removeQueuedUpdates();
        if (isQueryEmpty(query)) {
            updateSearch(query);
        } else {
            this.searchHandler.queueSearchUpdate(query);
        }
        this.query = query;
        return false;
    }

    void updateSearch(String query) {
        Logger.m173d("Search update for query \"%s\" requested", query);
        if (isQueryEmpty(query)) {
            Logger.m172d("Switching to idle state");
            this.stripIndicatorView.setVisibility(8);
            this.pagerView.setVisibility(4);
            updateSuggestionsState();
            this.emptyView.setVisibility(0);
        } else {
            Logger.m172d("Switching to search results state");
            this.stripIndicatorView.setVisibility(0);
            this.pagerView.setVisibility(0);
            this.emptyView.setVisibility(8);
            this.suggestionsController.setVisible(false, true);
        }
        this.searchPagerAdapter.searchOnPosition(this.pagerView.getCurrentItem(), query, true);
    }

    protected final void updateSuggestionsState() {
        SuggestionsStore sstore = SuggestionsStore.getInstance(this);
        if (sstore.getSuggestions().isEmpty()) {
            Logger.m172d("No suggestions to show");
            this.suggestionsController.setVisible(false, false);
            return;
        }
        Logger.m172d("Showing suggestions");
        this.suggestionsController.setSuggestions(sstore.getSuggestions());
        this.suggestionsController.setVisible(true, true);
    }

    protected boolean isQueryEmpty(String query) {
        return query == null || query.length() == 0 || TextUtils.isEmpty(query.trim());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        LocalizationManager.inflate((Context) this, getMenuInflater(), 2131689523, menu);
        MenuItem item = menu.findItem(2131625441);
        if (item != null) {
            MenuItemCompat.setOnActionExpandListener(item, this);
            this.searchView = (SearchView) MenuItemCompat.getActionView(item);
            this.searchView.setOnQueryTextListener(this);
            this.searchView.setQueryHint(getStringLocalized(2131166482));
            this.searchView.setIconified(false);
            this.searchView.setOnCloseListener(this);
            onQueryChanged(this.query, false);
        }
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() != 4 || event.getAction() != 1) {
            return super.dispatchKeyEvent(event);
        }
        finish();
        return true;
    }

    protected void onPause() {
        super.onPause();
        SuggestionsStore.getInstance(this).save(this);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (!TextUtils.isEmpty(this.query)) {
            SuggestionsStore.getInstance(this).addSuggestion(this.query);
            SuggestionsStore.getInstance(this).save(this);
        }
        SuggestionsStore.destroyInstance();
    }

    public boolean onClose() {
        finish();
        return false;
    }

    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        finish();
        return false;
    }

    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        return false;
    }

    protected boolean isToolbarTitleEnabled() {
        return false;
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}
