package ru.ok.android.ui.places.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import ru.ok.android.services.utils.users.LocationUtils;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.model.Address;
import ru.ok.model.Location;

public final class PlaceLocationFragment extends BaseFragment implements OnClickListener {
    private GoogleMapViewAdapter adapter;
    private View addressPanel;
    private SupportMapFragment fragmentMap;
    private TextView textAddress;

    public static Bundle newArguments(Location location, Address address, String placeName) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("extra_location", location);
        bundle.putParcelable("extra_address", address);
        bundle.putString("place_name", placeName);
        return bundle;
    }

    private Location getLocation() {
        return (Location) getArguments().getParcelable("extra_location");
    }

    private Address getAddress() {
        return (Address) getArguments().getParcelable("extra_address");
    }

    private String getPlaceName() {
        return getArguments().getString("place_name");
    }

    private String getAnyPlaceName() {
        Address address = getAddress();
        if (address != null) {
            String stringAddress = address.getStringAddress();
            if (!TextUtils.isEmpty(stringAddress)) {
                return stringAddress;
            }
        }
        return getPlaceName();
    }

    protected int getLayoutId() {
        return 2130903389;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View mainView = inflater.inflate(getLayoutId(), null);
        this.fragmentMap = (SupportMapFragment) getChildFragmentManager().findFragmentById(2131624596);
        this.textAddress = (TextView) mainView.findViewById(2131624995);
        this.addressPanel = mainView.findViewById(2131625224);
        this.textAddress.setText(getAnyPlaceName());
        this.addressPanel.setOnClickListener(this);
        return mainView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.adapter = new GoogleMapViewAdapter(getContext());
        this.adapter.applyMapView(this.fragmentMap.getMap());
        this.adapter.showMyLocationButton(true);
        this.adapter.showZoomControl(false);
    }

    public void onClick(View v) {
        if (getLocation() != null) {
            this.adapter.positionToLocation();
        }
    }

    public void onStart() {
        super.onStart();
        if (getLocation() == null) {
            android.location.Location lastLocation = LocationUtils.getLastLocation(getActivity());
            if (lastLocation != null) {
                this.adapter.positionToPoint(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
                return;
            }
            return;
        }
        notifyLocation();
    }

    private void notifyLocation() {
        this.adapter.setLocation(getLocation());
        this.adapter.markLocation();
        this.adapter.positionToLocation();
    }
}
