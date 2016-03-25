package ru.ok.android.ui.places.fragments;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.location.Criteria;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import ru.ok.android.C0206R;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.services.processors.geo.GetPlacesProcessor.SearchProfileType;
import ru.ok.android.services.utils.users.LocationUtils;
import ru.ok.android.ui.adapters.places.AddPlacesAdapter;
import ru.ok.android.ui.adapters.places.PlacesAdapter.MenuPlaceListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView.OnRepeatClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView.WebState;
import ru.ok.android.ui.custom.loadmore.LoadMoreAdapter;
import ru.ok.android.ui.custom.loadmore.LoadMoreAdapterListener;
import ru.ok.android.ui.custom.loadmore.LoadMoreController;
import ru.ok.android.ui.custom.loadmore.LoadMoreMode;
import ru.ok.android.ui.custom.loadmore.LoadMoreView.LoadMoreState;
import ru.ok.android.ui.custom.parallax.ParallaxListView;
import ru.ok.android.ui.dialogs.ComplaintPlaceBase.OnSelectItemDialogComplaintPlaceListener;
import ru.ok.android.ui.dialogs.actions.ComplaintPlaceActionBox;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.places.AddPlaceActivity;
import ru.ok.android.ui.places.PlacesActivity;
import ru.ok.android.ui.places.loaders.ReverseGeocodeLoader;
import ru.ok.android.ui.utils.SearchBaseHandler;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.PermissionUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.model.Address;
import ru.ok.model.Location;
import ru.ok.model.places.ComplaintPlaceType;
import ru.ok.model.places.Place;

public class PlacesFragment extends BaseFragment implements LoaderCallbacks<List<Pair<Address, Location>>>, OnQueryTextListener, OnClickListener, OnScrollListener, OnItemClickListener, OnMapClickListener, MenuPlaceListener, OnRepeatClickListener, LoadMoreAdapterListener, OnSelectItemDialogComplaintPlaceListener {
    private AddPlacesAdapter adapter;
    private View addressPanel;
    private TextView addressSearchText;
    private String anchor;
    private SmartEmptyView emptyView;
    private ParallaxListView listView;
    private LoadMoreAdapter loadMoreAdapter;
    private Location location;
    GoogleMapViewAdapter mapAdapter;
    private MapView mapView;
    private String query;
    private SearchHandler searchHandler;
    private SearchView searchView;

    /* renamed from: ru.ok.android.ui.places.fragments.PlacesFragment.1 */
    class C11261 implements OnActionExpandListener {
        C11261() {
        }

        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;
        }

        public boolean onMenuItemActionCollapse(MenuItem item) {
            PlacesFragment.this.searchHandler.removeQueuedUpdates();
            PlacesFragment.this.searchHandler.queueSearchUpdate("");
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.places.fragments.PlacesFragment.2 */
    class C11272 extends AnimatorListenerAdapter {
        C11272() {
        }

        public void onAnimationEnd(Animator animation) {
            PlacesFragment.this.emptyView.setVisibility(8);
            PlacesFragment.this.emptyView.setAlpha(1.0f);
            if (PlacesFragment.this.emptyView.getAnimation() != null) {
                PlacesFragment.this.emptyView.getAnimation().setAnimationListener(null);
            }
        }
    }

    class PanelAnimationListener implements AnimatorListener {
        private final int type;

        PanelAnimationListener(int type) {
            this.type = type;
        }

        public void onAnimationStart(Animator animation) {
            if (this.type == 0) {
                PlacesFragment.this.addressPanel.setVisibility(0);
            }
        }

        public void onAnimationEnd(Animator animation) {
            if (this.type == 1) {
                PlacesFragment.this.addressPanel.setVisibility(4);
            }
        }

        public void onAnimationCancel(Animator animation) {
        }

        public void onAnimationRepeat(Animator animation) {
        }
    }

    public class SearchHandler extends SearchBaseHandler {
        public void onSearchHandle(String queryNew) {
            if (!PlacesFragment.this.query.equals(queryNew)) {
                PlacesFragment.this.query = queryNew;
                if (TextUtils.isEmpty(PlacesFragment.this.query) && PlacesFragment.this.location == null) {
                    if (PermissionUtils.checkAnySelfPermission(PlacesFragment.this.getActivity(), "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION") == 0) {
                        android.location.Location lastLocation = LocationUtils.getLastLocation(PlacesFragment.this.getContext());
                        if (lastLocation != null) {
                            PlacesFragment.this.setLocation(lastLocation);
                            PlacesFragment.this.anchor = "";
                            PlacesFragment.this.showProcess();
                            PlacesFragment.this.requestPlaces();
                            return;
                        }
                        PlacesFragment.this.showError(2131165921);
                        return;
                    }
                    PlacesFragment.this.showError(2131166362);
                    return;
                }
                PlacesFragment.this.anchor = "";
                PlacesFragment.this.adapter.clearData();
                PlacesFragment.this.adapter.notifyDataSetChanged();
                PlacesFragment.this.showProcess();
                PlacesFragment.this.requestPlaces();
                PlacesFragment.this.reverseGeoCode();
            }
        }

        public int getSearchUpdateDelay() {
            return 850;
        }
    }

    public PlacesFragment() {
        this.searchHandler = new SearchHandler();
        this.anchor = "";
        this.location = null;
        this.query = "";
    }

    public static Bundle getArguments(Place place) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("extra_place", place);
        return bundle;
    }

    protected int getLayoutId() {
        return 2130903390;
    }

    protected CharSequence getTitle() {
        return LocalizationManager.getString(getContext(), 2131166368);
    }

    public Place getPlace() {
        return (Place) getArguments().getParcelable("extra_place");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View mainView = inflater.inflate(getLayoutId(), null);
        this.listView = (ParallaxListView) mainView.findViewById(2131624731);
        this.listView.setOnScrollListener(this);
        this.addressPanel = mainView.findViewById(2131625225);
        this.addressPanel.setOnClickListener(this);
        this.addressSearchText = (TextView) mainView.findViewById(2131625226);
        this.emptyView = (SmartEmptyView) mainView.findViewById(C0263R.id.empty_view);
        this.emptyView.setOnRepeatClickListener(this);
        new Criteria().setAccuracy(1);
        this.adapter = new AddPlacesAdapter(getContext());
        this.adapter.setPlaceMenuListener(this);
        this.loadMoreAdapter = new LoadMoreAdapter(getContext(), this.adapter, this, LoadMoreMode.BOTTOM, null);
        LoadMoreController loadMoreController = this.loadMoreAdapter.getController();
        loadMoreController.setBottomAutoLoad(true);
        loadMoreController.setBottomCurrentState(LoadMoreState.IDLE);
        if (savedInstanceState != null) {
            this.location = (Location) savedInstanceState.getParcelable("extra_location");
        }
        if (!(this.location != null || getPlace() == null || getPlace().location == null)) {
            this.location = getPlace().location;
        }
        this.mapAdapter = new GoogleMapViewAdapter(getContext(), this.location);
        View viewMap = createMapView(inflater, savedInstanceState);
        GoogleMap map = this.mapView.getMap();
        if (map != null) {
            map.setOnMapClickListener(this);
        }
        this.mapAdapter.applyMapView(map);
        this.listView.addHeaderView(viewMap, null, true);
        this.listView.setAdapter(this.loadMoreAdapter);
        this.listView.setOnItemClickListener(this);
        return mainView;
    }

    public View createMapView(LayoutInflater inflater, Bundle saveInstanceState) {
        Bundle mapBundle = null;
        View mainView = inflater.inflate(2130903391, null, false);
        this.mapView = (MapView) mainView.findViewById(2131625227);
        if (saveInstanceState != null) {
            mapBundle = (Bundle) saveInstanceState.getParcelable("map_view_bundle");
        }
        this.mapView.onCreate(mapBundle);
        return mainView;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState != 0) {
            hideAddressPanel();
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    public void onStart() {
        super.onStart();
        if (this.location == null) {
            if (PermissionUtils.checkAnySelfPermission(getActivity(), "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION") == 0) {
                updateWithLastLocation();
                return;
            }
            requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, C0206R.styleable.Theme_checkboxStyle);
        } else if (this.adapter.getCount() == 0) {
            showProcess();
            requestPlaces();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case C0206R.styleable.Theme_checkboxStyle /*101*/:
                updateWithLastLocation();
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void updateWithLastLocation() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            android.location.Location location = LocationUtils.getLastLocationIfPermitted(activity);
            if (location != null && (this.location == null || location.getLongitude() != this.location.getLongitude() || location.getLatitude() != this.location.getLatitude())) {
                setLocation(location);
                this.anchor = "";
                showProcess();
                requestPlaces();
            } else if (location == null && this.location == null) {
                this.anchor = "";
                if (TextUtils.isEmpty(this.query)) {
                    if (PermissionUtils.checkAnySelfPermission(getActivity(), "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION") != 0) {
                        showError(2131166362);
                        return;
                    } else {
                        showError(2131165921);
                        return;
                    }
                }
                showProcess();
                requestPlaces();
            }
        }
    }

    public void onRetryClick(SmartEmptyView emptyView) {
        if (this.location == null && TextUtils.isEmpty(this.query)) {
            showError(2131165921);
            return;
        }
        this.anchor = "";
        showProcess();
        requestPlaces();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.location != null) {
            outState.putParcelable("extra_location", this.location);
        }
        Bundle mapBundle = new Bundle();
        this.mapView.onSaveInstanceState(mapBundle);
        outState.putParcelable("map_view_bundle", mapBundle);
    }

    public void onResume() {
        super.onResume();
        this.mapView.onResume();
    }

    public void onPause() {
        super.onPause();
        this.mapView.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
        this.mapView.onDestroy();
    }

    public void onLowMemory() {
        super.onLowMemory();
        this.mapView.onLowMemory();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LocalizationManager.inflate(getActivity(), inflater, 2131689516, menu);
        MenuItem item = menu.findItem(2131625441);
        if (item != null) {
            this.searchView = (SearchView) MenuItemCompat.getActionView(item);
            this.searchView.setOnQueryTextListener(this);
            this.searchView.setQueryHint(getStringLocalized(2131166367));
        }
        MenuItemCompat.setOnActionExpandListener(item, new C11261());
    }

    private void requestPlaces(String query, Location location, String anchor) {
        Bundle output = new Bundle();
        output.putInt("count", 15);
        output.putString(DiscoverInfo.ELEMENT, query);
        if (location != null) {
            output.putDouble("lat", location.getLatitude());
            output.putDouble("lng", location.getLongitude());
        }
        output.putSerializable("search_profile", getSearchType(query, location));
        output.putString("anchor", anchor);
        output.putSerializable("direction", PagingDirection.FORWARD);
        GlobalBus.send(2131624029, new BusEvent(output));
    }

    private static SearchProfileType getSearchType(String query, Location location) {
        boolean emptyQuery = TextUtils.isEmpty(query);
        boolean emptyLocation = location == null;
        if (!emptyQuery && emptyLocation) {
            return SearchProfileType.NO_CORD_WITH_TEXT;
        }
        if (!emptyQuery || emptyLocation) {
            return SearchProfileType.WITH_CORD_WITH_TEXT;
        }
        return SearchProfileType.WITH_CORD_NO_TEXT;
    }

    private void requestPlaces() {
        requestPlaces(this.query, this.location, this.anchor);
    }

    private void requestPlacesNoQuery() {
        requestPlaces(null, this.location, this.anchor);
    }

    @Subscribe(on = 2131623946, to = 2131624198)
    public final void onGetComplaint(BusEvent busEvent) {
        if (getActivity() != null) {
            Bundle bundle = busEvent.bundleOutput;
            boolean isAdd = bundle.getBoolean("key_places_complaint_result");
            if (((Exception) bundle.getSerializable("key_exception_places_complaint_result")) != null) {
                showTimedToastIfVisible(2131166357, 1);
            } else if (isAdd) {
                Place place = (Place) busEvent.bundleInput.getParcelable("place");
                if (place != null && this.adapter.deletePlace(place)) {
                    this.adapter.notifyDataSetChanged();
                }
                showTimedToastIfVisible(2131166359, 1);
            } else {
                showTimedToastIfVisible(2131166358, 1);
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624204)
    public final void onGetPlaces(BusEvent busEvent) {
        Bundle bundle = busEvent.bundleOutput;
        Bundle bundleInput = busEvent.bundleInput;
        if (bundle != null) {
            if (busEvent.resultCode == -2) {
                onGetPlacesError();
            } else {
                boolean hasMore = bundle.getBoolean("key_places_has_more_result");
                this.anchor = bundle.getString("key_anchor");
                ArrayList<Place> places = bundle.getParcelableArrayList("key_places_result");
                if (TextUtils.isEmpty(bundleInput.getString("anchor"))) {
                    this.adapter.clearData();
                }
                onGetPlaces(hasMore, this.anchor, places);
            }
        }
        this.loadMoreAdapter.getController().setBottomCurrentState(LoadMoreState.IDLE);
    }

    private void onGetPlacesError() {
        showError(2131165791);
    }

    private void onGetPlaces(boolean hasMore, String anchor, ArrayList<Place> places) {
        setHasMore(hasMore);
        if (this.adapter.isShowAddItem()) {
            hideProcess(places.size() + 1);
        } else {
            hideProcess(places.size());
        }
        if (!this.anchor.equals(anchor)) {
            this.anchor = anchor;
        }
        this.adapter.addPlaces(places);
        this.adapter.notifyDataSetChanged();
    }

    private void setHasMore(boolean hasMore) {
        LoadMoreController loadMoreController = this.loadMoreAdapter.getController();
        loadMoreController.setBottomAutoLoad(hasMore);
        if (hasMore) {
            loadMoreController.setBottomPermanentState(LoadMoreState.LOAD_POSSIBLE);
            this.adapter.hideAdd();
            return;
        }
        loadMoreController.setBottomPermanentState(LoadMoreState.LOAD_IMPOSSIBLE);
        this.adapter.showAdd();
    }

    public void setLocation(android.location.Location location) {
        this.location = new Location(location);
        this.mapAdapter.setLocation(this.location);
        this.mapAdapter.positionToLocation();
    }

    public void onLoadMoreTopClicked() {
    }

    public void onLoadMoreBottomClicked() {
        requestPlaces();
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

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (this.adapter.getItemViewType(position) == 1) {
            showAddPlace();
        } else {
            onPlaceSelect((Place) this.adapter.getItem(position - this.listView.getHeaderViewsCount()));
        }
    }

    private void showAddPlace() {
        if (getActivity() != null) {
            Intent addPlaceIntent = new Intent(getActivity(), AddPlaceActivity.class);
            addPlaceIntent.putExtra("def_text", this.searchView.getQuery().toString());
            startActivityForResult(addPlaceIntent, 6001);
            getActivity().overridePendingTransition(2130968586, 2130968587);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 6001) {
            onPlaceSelect((Place) data.getParcelableExtra("place_result"));
        }
    }

    public void onPlaceMenuClick(View menuView, Place place) {
        ComplaintPlaceActionBox complaintMenu = new ComplaintPlaceActionBox(getContext(), menuView, place);
        complaintMenu.setOnSelectItemListener(this);
        complaintMenu.show();
    }

    public void onComplaintSelectedItem(Place place) {
        Bundle output = new Bundle();
        output.putParcelable("place", place);
        output.putSerializable("type", ComplaintPlaceType.ADVERTISING);
        GlobalBus.send(2131624023, new BusEvent(output));
    }

    public void onMapClick(LatLng latLng) {
        this.location = new Location(Double.valueOf(latLng.latitude), Double.valueOf(latLng.longitude));
        this.mapAdapter.setLocation(this.location);
        this.mapAdapter.markLocation();
        this.mapAdapter.positionToLocation();
        this.adapter.clearData();
        this.adapter.notifyDataSetChanged();
        this.anchor = "";
        showProcess();
        requestPlaces();
    }

    public void onClick(View v) {
        if (v == this.addressPanel && this.addressPanel.getTag() != null) {
            this.location = (Location) this.addressPanel.getTag();
            this.addressPanel.setTag(null);
            hideAddressPanel();
            this.mapAdapter.setLocation(this.location);
            this.mapAdapter.markLocation();
            this.mapAdapter.positionToLocation();
            this.adapter.clearData();
            this.adapter.notifyDataSetChanged();
            this.anchor = "";
            showProcess();
            requestPlacesNoQuery();
        }
    }

    public void onPlaceSelect(Place place) {
        if (getActivity() != null && (getActivity() instanceof PlacesActivity)) {
            ((PlacesActivity) getActivity()).onPlaceSelect(place);
        }
    }

    private void showProcess() {
        this.emptyView.setWebState(WebState.PROGRESS);
        this.emptyView.setVisibility(0);
    }

    private void hideProcess(int elemsCount) {
        this.emptyView.setWebState(WebState.EMPTY);
        if (elemsCount > 0) {
            this.emptyView.animate().alpha(0.0f).setDuration(250).setListener(new C11272());
        } else {
            this.emptyView.setVisibility(0);
        }
    }

    private void showError(int res) {
        this.emptyView.setErrorText(res);
        this.emptyView.setWebState(WebState.ERROR);
        this.emptyView.setVisibility(0);
    }

    private void reverseGeoCode() {
        if (getActivity() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("EXTRA_QUERY", this.query);
            if (getLoaderManager().getLoader(0) == null) {
                getLoaderManager().initLoader(0, bundle, this).forceLoad();
            } else {
                getLoaderManager().restartLoader(0, bundle, this).forceLoad();
            }
        }
    }

    public Loader<List<Pair<Address, Location>>> onCreateLoader(int id, Bundle args) {
        return new ReverseGeocodeLoader(getContext(), args.getString("EXTRA_QUERY", ""));
    }

    public void onLoadFinished(Loader<List<Pair<Address, Location>>> loader, List<Pair<Address, Location>> addresses) {
        if (!(loader instanceof ReverseGeocodeLoader) || !((ReverseGeocodeLoader) loader).getText().equals(this.query)) {
            return;
        }
        if (addresses.size() != 1) {
            this.addressPanel.setTag(null);
            hideAddressPanel();
        } else if (this.location == null || !(this.location == null || (((Location) ((Pair) addresses.get(0)).second).getLatitude() == this.location.getLatitude() && ((Location) ((Pair) addresses.get(0)).second).getLongitude() == this.location.getLongitude()))) {
            this.addressPanel.setTag(((Pair) addresses.get(0)).second);
            this.addressSearchText.setText(((Address) ((Pair) addresses.get(0)).first).getStringAddress());
            showAddressPanel();
        } else {
            this.addressPanel.setTag(null);
            hideAddressPanel();
        }
    }

    public void onLoaderReset(Loader<List<Pair<Address, Location>>> loader) {
        loader.reset();
    }

    private void showAddressPanel() {
        if (this.addressPanel.getVisibility() != 0) {
            ObjectAnimator animation = ObjectAnimator.ofFloat(this.addressPanel, "translationY", new float[]{0.0f, (float) this.addressPanel.getHeight()});
            animation.setDuration(200);
            animation.addListener(new PanelAnimationListener(0));
            animation.start();
        }
    }

    private void hideAddressPanel() {
        if (this.addressPanel.getVisibility() == 0) {
            ObjectAnimator animation = ObjectAnimator.ofFloat(this.addressPanel, "translationY", new float[]{(float) this.addressPanel.getHeight(), 0.0f});
            animation.setDuration(200);
            animation.addListener(new PanelAnimationListener(1));
            animation.start();
        }
    }
}
