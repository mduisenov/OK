package ru.ok.android.ui.places.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CameraPosition.Builder;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import ru.ok.android.utils.Logger;
import ru.ok.model.Location;

public class GoogleMapViewAdapter {
    private final Context context;
    private LatLng location;
    private LayoutInflater mInflater;
    private GoogleMap mapController;
    private Marker marker;

    public GoogleMapViewAdapter(Context context, Location location) {
        this(context);
        if (location != null) {
            setLocation(location);
            positionToLocation();
        }
    }

    public GoogleMapViewAdapter(Context context) {
        this.location = null;
        this.context = context;
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    public boolean showMyLocationButton(boolean value) {
        if (this.mapController == null || this.mapController.getUiSettings() == null) {
            return false;
        }
        this.mapController.getUiSettings().setMyLocationButtonEnabled(value);
        return true;
    }

    public boolean showZoomControl(boolean value) {
        if (this.mapController == null || this.mapController.getUiSettings() == null) {
            return false;
        }
        this.mapController.getUiSettings().setZoomControlsEnabled(value);
        return true;
    }

    public void applyMapView(GoogleMap map) {
        this.mapController = map;
        if (this.mapController != null) {
            this.mapController.getUiSettings().setMyLocationButtonEnabled(false);
            this.mapController.setMyLocationEnabled(true);
            this.mapController.getUiSettings().setZoomControlsEnabled(false);
            this.mapController.setMapType(1);
            try {
                MapsInitializer.initialize(this.context);
            } catch (Throwable e) {
                Logger.m178e(e);
            }
            if (this.location != null) {
                this.mapController.animateCamera(CameraUpdateFactory.newLatLngZoom(this.location, 10.0f));
            }
            if (this.location != null) {
                positionToLocation();
                markLocation();
            }
        }
    }

    public void setLocation(Location location) {
        this.location = new LatLng(location.getLatitude(), location.getLongitude());
    }

    public void positionToLocation() {
        positionToPoint(this.location);
    }

    public void positionToPoint(LatLng location) {
        CameraPosition cameraPosition = new Builder().target(location).zoom(14.0f).tilt(30.0f).build();
        if (this.mapController != null) {
            this.mapController.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void scrollBy(float xPixel, float yPixel) {
        if (this.mapController != null) {
            if (this.mapController.getCameraPosition() == null) {
                this.mapController.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
            }
            this.mapController.animateCamera(CameraUpdateFactory.scrollBy(xPixel, yPixel));
        }
    }

    public void markLocation() {
        if (this.marker != null) {
            this.marker.setPosition(this.location);
        } else if (this.mapController != null) {
            this.marker = this.mapController.addMarker(new MarkerOptions().position(this.location).icon(BitmapDescriptorFactory.fromResource(2130838168)));
        }
    }
}
