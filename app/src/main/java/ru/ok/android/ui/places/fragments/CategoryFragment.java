package ru.ok.android.ui.places.fragments;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.adapters.places.CategoriesAdapter;
import ru.ok.android.ui.adapters.section.Sectionizer;
import ru.ok.android.ui.adapters.section.SimpleSectionAdapter;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView.WebState;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.places.CategoryActivity;
import ru.ok.android.ui.utils.SearchBaseHandler;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.Location;
import ru.ok.model.places.PlaceCategory;

public final class CategoryFragment extends BaseFragment implements OnNavigationListener, OnQueryTextListener, OnItemClickListener {
    private CategoriesAdapter adapter;
    private CategoriesSpinnerAdapter categoriesSpinnerAdapter;
    private SmartEmptyView emptyView;
    private ListView listView;
    private SearchHandler searchHandler;
    private SearchView searchView;
    private SimpleSectionAdapter<CategoriesAdapter> sectionAdapter;
    private final CategorySectionizer sectionizer;

    /* renamed from: ru.ok.android.ui.places.fragments.CategoryFragment.1 */
    class C11251 implements OnActionExpandListener {
        C11251() {
        }

        public boolean onMenuItemActionExpand(MenuItem item) {
            CategoryFragment.this.getSupportActionBar().setSelectedNavigationItem(0);
            return true;
        }

        public boolean onMenuItemActionCollapse(MenuItem item) {
            CategoryFragment.this.searchHandler.removeQueuedUpdates();
            CategoryFragment.this.searchHandler.queueSearchUpdate("");
            return true;
        }
    }

    private class CategorySectionizer implements Sectionizer<CategoriesAdapter> {
        private Map<String, PlaceCategory> mapData;

        private CategorySectionizer() {
            this.mapData = new HashMap();
        }

        public String getSectionTitleForItem(CategoriesAdapter adapter, int index) {
            String value = ((PlaceCategory) this.mapData.get(((PlaceCategory) adapter.getItem(index)).id)).text;
            if (TextUtils.isEmpty(value)) {
                return "default";
            }
            return value;
        }

        public void setData(List<PlaceCategory> data) {
            this.mapData.clear();
            for (PlaceCategory category : data) {
                for (PlaceCategory subCategory : category.subCategories) {
                    this.mapData.put(subCategory.id, category);
                }
            }
        }
    }

    class SearchHandler extends SearchBaseHandler {
        SearchHandler() {
        }

        public void onSearchHandle(String queryNew) {
            CategoryFragment.this.adapter.getFilter().filter(queryNew);
        }

        public int getSearchUpdateDelay() {
            return 850;
        }
    }

    public CategoryFragment() {
        this.searchHandler = new SearchHandler();
        this.sectionizer = new CategorySectionizer();
    }

    public static Bundle getArguments(Location location) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("extra_input_location", location);
        return bundle;
    }

    protected CharSequence getTitle() {
        return "";
    }

    protected int getLayoutId() {
        return 2130903388;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View mainView = LocalizationManager.inflate(getContext(), getLayoutId(), null, false);
        this.listView = (ListView) mainView.findViewById(2131624731);
        this.adapter = new CategoriesAdapter(getContext());
        this.sectionAdapter = new SimpleSectionAdapter(getContext(), this.adapter, 2130903430, C0263R.id.text, this.sectionizer);
        this.listView.setAdapter(this.sectionAdapter);
        this.listView.setOnItemClickListener(this);
        this.emptyView = (SmartEmptyView) mainView.findViewById(C0263R.id.empty_view);
        this.listView.setEmptyView(this.emptyView);
        this.sectionAdapter.finalInit();
        this.categoriesSpinnerAdapter = new CategoriesSpinnerAdapter(getContext());
        getSupportActionBar().setNavigationMode(1);
        getSupportActionBar().setListNavigationCallbacks(this.categoriesSpinnerAdapter, this);
        return mainView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestCategories();
    }

    private void requestCategories() {
        GlobalBus.send(2131624028, new BusEvent(new Bundle()));
        this.emptyView.setWebState(WebState.PROGRESS);
    }

    @Subscribe(on = 2131623946, to = 2131624203)
    public final void onGetCategories(BusEvent busEvent) {
        Bundle bundle = busEvent.bundleOutput;
        if (bundle == null) {
            return;
        }
        if (busEvent.resultCode == -2) {
            onGetCategoryError();
        } else {
            onGetCategories(bundle.getParcelableArrayList("key_categories_list_result"));
        }
    }

    private void onGetCategoryError() {
        this.emptyView.setWebState(WebState.ERROR);
    }

    private void onGetCategories(ArrayList<PlaceCategory> categoriesList) {
        this.emptyView.setWebState(WebState.EMPTY);
        this.sectionizer.setData(categoriesList);
        this.adapter.setCategories(categoriesList);
        this.adapter.notifyDataSetChanged();
        this.categoriesSpinnerAdapter.setData(categoriesList);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        onCategorySelect((PlaceCategory) this.sectionAdapter.getItem(position));
    }

    private void onCategorySelect(PlaceCategory category) {
        if (getActivity() != null && (getActivity() instanceof CategoryActivity)) {
            ((CategoryActivity) getActivity()).onCategorySelect(category);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LocalizationManager.inflate(getActivity(), inflater, 2131689478, menu);
        MenuItem item = menu.findItem(2131625441);
        if (item != null) {
            this.searchView = (SearchView) MenuItemCompat.getActionView(item);
            this.searchView.setOnQueryTextListener(this);
            this.searchView.setQueryHint(getStringLocalized(2131165489));
        }
        MenuItemCompat.setOnActionExpandListener(item, new C11251());
    }

    public boolean onQueryTextSubmit(String query) {
        this.searchHandler.removeQueuedUpdates();
        this.searchHandler.queueSearchUpdate(query);
        KeyBoardUtils.hideKeyBoard(getActivity());
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        this.searchHandler.removeQueuedUpdates();
        this.searchHandler.queueSearchUpdate(newText);
        return false;
    }

    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (itemPosition == 0) {
            this.adapter.clearFilter();
            this.adapter.notifyDataSetChanged();
        } else {
            this.adapter.byParentCategory((PlaceCategory) this.categoriesSpinnerAdapter.getItem(itemPosition - 1));
            this.adapter.notifyDataSetChanged();
        }
        return false;
    }
}
