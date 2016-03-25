package ru.ok.android.ui.places.fragments;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.utils.users.LocationUtils;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.places.AddPlaceActivity;
import ru.ok.android.ui.places.CategoryActivity;
import ru.ok.android.ui.places.loaders.GeocodeLoader;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.Address;
import ru.ok.model.Location;
import ru.ok.model.places.Place;
import ru.ok.model.places.PlaceCategory;

public final class AddPlaceFragment extends BaseFragment implements LoaderCallbacks<List<Address>>, TextWatcher, OnClickListener, OnMapClickListener {
    private GoogleMapViewAdapter adapter;
    private MenuItem addItem;
    private Button addItemButton;
    private Address address;
    private View addressPanel;
    private final AddressTextHandler addressTextHandler;
    private PlaceCategory category;
    private SupportMapFragment fragmentMap;
    private View infoPanel;
    private Location location;
    private TextView textAddress;
    private EditText textCategory;
    private EditText textCity;
    private EditText textCountry;
    private EditText textHouse;
    private EditText textName;
    private EditText textStreet;

    /* renamed from: ru.ok.android.ui.places.fragments.AddPlaceFragment.1 */
    class C11241 implements OnClickListener {
        C11241() {
        }

        public void onClick(View v) {
            AddPlaceFragment.this.onAddPlace();
        }
    }

    class AddressTextHandler implements TextWatcher {
        private volatile AtomicBoolean block;

        AddressTextHandler() {
            this.block = new AtomicBoolean(false);
        }

        public void block() {
            this.block.set(true);
        }

        public void unBlock() {
            this.block.set(false);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!this.block.get()) {
                updateAddress();
            }
        }

        public void afterTextChanged(Editable s) {
        }

        public void updateAddress() {
            AddPlaceFragment.this.address.country = AddPlaceFragment.this.textCountry.getText().toString();
            AddPlaceFragment.this.address.city = AddPlaceFragment.this.textCity.getText().toString();
            AddPlaceFragment.this.address.street = AddPlaceFragment.this.textStreet.getText().toString();
            AddPlaceFragment.this.address.house = AddPlaceFragment.this.textHouse.getText().toString();
            AddPlaceFragment.this.textAddress.setText(AddPlaceFragment.this.address.getStringAddress());
        }
    }

    public AddPlaceFragment() {
        this.addressTextHandler = new AddressTextHandler();
        this.location = null;
        this.category = null;
        this.address = new Address();
    }

    protected int getLayoutId() {
        return 2130903096;
    }

    protected CharSequence getTitle() {
        return getStringLocalized(2131165350);
    }

    protected String getDefText() {
        return getArguments().getString("extra_text");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View mainView = inflater.inflate(getLayoutId(), null);
        this.fragmentMap = (SupportMapFragment) getChildFragmentManager().findFragmentById(2131624596);
        this.textName = (EditText) mainView.findViewById(2131624601);
        this.textCategory = (EditText) mainView.findViewById(2131624603);
        this.textAddress = (TextView) mainView.findViewById(2131624599);
        this.textCategory.setOnClickListener(this);
        this.addressPanel = mainView.findViewById(2131624604);
        this.infoPanel = mainView.findViewById(2131624597);
        this.textName.addTextChangedListener(this);
        this.textCategory.addTextChangedListener(this);
        this.textAddress.setOnClickListener(this);
        this.textCountry = (EditText) mainView.findViewById(2131624606);
        this.textCity = (EditText) mainView.findViewById(2131624607);
        this.textStreet = (EditText) mainView.findViewById(2131624608);
        this.textHouse = (EditText) mainView.findViewById(2131624609);
        this.textCountry.addTextChangedListener(this.addressTextHandler);
        this.textCity.addTextChangedListener(this.addressTextHandler);
        this.textStreet.addTextChangedListener(this.addressTextHandler);
        this.textHouse.addTextChangedListener(this.addressTextHandler);
        this.textName.setText(getDefText());
        if (!(savedInstanceState == null || savedInstanceState.getParcelable("extra_location") == null)) {
            this.location = (Location) savedInstanceState.getParcelable("extra_location");
        }
        return mainView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.adapter = new GoogleMapViewAdapter(getContext());
        GoogleMap map = this.fragmentMap.getMap();
        if (map != null) {
            map.setOnMapClickListener(this);
            this.adapter.applyMapView(map);
        }
        this.adapter.showMyLocationButton(true);
        this.adapter.showZoomControl(false);
    }

    public void onStart() {
        super.onStart();
        if (this.location == null) {
            updateWithLastLocation();
        } else {
            notifyLocation();
        }
    }

    private void updateWithLastLocation() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            android.location.Location location = LocationUtils.getLastLocationIfPermitted(activity);
            if (location == null) {
                return;
            }
            if (this.location == null || location.getLongitude() != this.location.getLongitude() || location.getLatitude() != this.location.getLatitude()) {
                this.location = new Location(location);
                notifyLocation();
                this.adapter.positionToLocation();
                Logger.m172d("New location :" + this.location.getLatitude() + "; " + this.location.getLongitude());
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.location != null) {
            outState.putParcelable("extra_location", this.location);
        }
        outState.putBoolean("extra_address", this.addressPanel.isShown());
    }

    private void notifyLocation() {
        this.adapter.setLocation(this.location);
        this.adapter.markLocation();
        notifyMapPosition();
        Bundle bundle = new Bundle();
        bundle.putDouble("LAT", this.location.getLatitude());
        bundle.putDouble("LNG", this.location.getLongitude());
        if (getLoaderManager().getLoader(0) == null) {
            getLoaderManager().initLoader(0, bundle, this).forceLoad();
        } else {
            getLoaderManager().restartLoader(0, bundle, this).forceLoad();
        }
    }

    public Loader<List<Address>> onCreateLoader(int id, Bundle args) {
        return new GeocodeLoader(getContext(), args.getDouble("LAT"), args.getDouble("LNG"));
    }

    public void onLoadFinished(Loader<List<Address>> loader, List<Address> addresses) {
        if (addresses != null && addresses.size() > 0 && this.location != null && ((GeocodeLoader) loader).latitude == this.location.getLatitude() && ((GeocodeLoader) loader).longitude == this.location.getLongitude()) {
            Address sAddress = (Address) addresses.get(0);
            this.address.city = sAddress.city;
            this.address.countryISO = sAddress.countryISO;
            this.address.country = sAddress.country;
            this.address.house = sAddress.house;
            this.address.street = sAddress.street;
            notifyAddress();
        }
    }

    public void onLoaderReset(Loader<List<Address>> loader) {
    }

    private void notifyMapPosition() {
        GoogleMap map = this.fragmentMap.getMap();
        if (map != null) {
            Point point = map.getProjection().toScreenLocation(new LatLng(this.location.getLatitude(), this.location.getLongitude()));
            this.adapter.scrollBy((float) (point.x - (this.fragmentMap.getView().getWidth() / 2)), (float) (point.y - ((this.fragmentMap.getView().getHeight() - (this.infoPanel.getHeight() - (!this.addressPanel.isShown() ? this.addressPanel.getHeight() : 0))) / 2)));
        }
    }

    protected void notifyAddress() {
        this.textAddress.setText(this.address.getStringAddress());
        this.addressTextHandler.block();
        if (!TextUtils.isEmpty(this.address.country)) {
            this.textCountry.setText(this.address.country);
        }
        if (!TextUtils.isEmpty(this.address.city)) {
            this.textCity.setText(this.address.city);
        }
        if (!TextUtils.isEmpty(this.address.street)) {
            this.textStreet.setText(this.address.street);
        }
        if (!TextUtils.isEmpty(this.address.house)) {
            this.textHouse.setText(this.address.house);
        }
        this.addressTextHandler.unBlock();
    }

    public void onMapClick(LatLng latLng) {
        this.location = new Location(Double.valueOf(latLng.latitude), Double.valueOf(latLng.longitude));
        notifyLocation();
        reverseGeoCode();
    }

    private void reverseGeoCode() {
        Bundle input = new Bundle();
        input.putDouble("lat", this.location.getLatitude());
        input.putDouble("lng", this.location.getLongitude());
        GlobalBus.send(2131624035, new BusEvent(input));
    }

    @Subscribe(on = 2131623946, to = 2131624209)
    public final void onReverseGeoCode(BusEvent busEvent) {
        Bundle bundleInput = busEvent.bundleInput;
        if (bundleInput != null) {
            double lat = bundleInput.getDouble("lat", 0.0d);
            double lng = bundleInput.getDouble("lng", 0.0d);
            if (this.location.getLatitude() == lat && this.location.getLongitude() == lng) {
                Bundle bundle = busEvent.bundleOutput;
                String countryCode = bundle.getString("key_country_code_result");
                String cityId = bundle.getString("key_city_id_result");
                String cityName = bundle.getString("key_city_name_result");
                this.address.cityId = cityId;
                this.address.countryISO = countryCode;
                this.address.city = cityName;
            }
        }
    }

    public void onClick(View v) {
        if (v == this.textCategory && getActivity() != null) {
            Intent selectCategoryIntent = new Intent(getActivity(), CategoryActivity.class);
            selectCategoryIntent.putExtra("location_input", this.location);
            startActivityForResult(selectCategoryIntent, 6000);
            getActivity().overridePendingTransition(2130968586, 2130968587);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6000 && resultCode == -1) {
            this.category = (PlaceCategory) data.getParcelableExtra("category_result");
            this.textCategory.setText(this.category.text);
            notifyTextChange();
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflateMenuLocalized(2131689474, menu);
        this.addItem = menu.findItem(2131625439);
        this.addItemButton = (Button) MenuItemCompat.getActionView(this.addItem);
        this.addItemButton.setText(LocalizationManager.getString(getActivity(), 2131165349));
        this.addItemButton.setEnabled(false);
        this.addItemButton.setOnClickListener(new C11241());
    }

    private void onAddPlace() {
        if (this.location == null || this.category == null) {
            showTimedToastIfVisible(2131166364, 1);
            return;
        }
        Bundle output = new Bundle();
        output.putDouble("lat", this.location.getLatitude());
        output.putDouble("lng", this.location.getLongitude());
        output.putString("place_name", this.textName.getText().toString());
        output.putString("category_id", this.category.id);
        output.putString("city_id", this.address.cityId);
        output.putString("city_name", this.address.city);
        output.putString("country_code", this.address.countryISO);
        output.putString("street", this.address.street);
        output.putString("house", this.address.house);
        GlobalBus.send(2131624037, new BusEvent(output));
    }

    @Subscribe(on = 2131623946, to = 2131624212)
    public final void onValidatePlace(BusEvent busEvent) {
        if (getActivity() != null) {
            Bundle bundle = busEvent.bundleOutput;
            if (((Exception) bundle.getSerializable("key_exception_validate_place_result")) != null) {
                showTimedToastIfVisible(2131166364, 1);
                return;
            }
            ArrayList<String> errors = bundle.getStringArrayList("key_extra_messages_errors");
            if (errors.size() > 0 && isResumed() && isVisible()) {
                Toast.makeText(getContext(), (CharSequence) errors.get(0), 1).show();
            } else {
                onPlaceValidationOk();
            }
        }
    }

    private void onPlaceValidationOk() {
        Place place = new Place("");
        place.location = this.location;
        place.name = this.textName.getText().toString();
        place.category = this.category;
        place.address = this.address;
        if (getActivity() instanceof AddPlaceActivity) {
            ((AddPlaceActivity) getActivity()).onNewPlace(place);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131625439:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        notifyTextChange();
    }

    private void notifyTextChange() {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Logger.m172d("onTextChanged");
    }

    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(this.textName.getText()) || TextUtils.isEmpty(this.textCategory.getText())) {
            if (this.addItemButton != null) {
                this.addItemButton.setEnabled(false);
            }
        } else if (this.addItemButton != null) {
            this.addItemButton.setEnabled(true);
        }
    }
}
