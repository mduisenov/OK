package ru.ok.android.ui.places.loaders;

import android.content.Context;
import android.location.Geocoder;
import android.support.v4.content.AsyncTaskLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.utils.Logger;
import ru.ok.model.Address;

public class GeocodeLoader extends AsyncTaskLoader<List<Address>> {
    public final double latitude;
    public final double longitude;

    public GeocodeLoader(Context context, double latitude, double longitude) {
        super(context);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public List<Address> loadInBackground() {
        List<Address> myAddresses = new ArrayList();
        try {
            List<android.location.Address> addresses = new Geocoder(OdnoklassnikiApplication.getContext(), Locale.getDefault()).getFromLocation(this.latitude, this.longitude, 10);
            if (addresses.isEmpty()) {
                return null;
            }
            for (android.location.Address sAddress : addresses) {
                myAddresses.add(Address.createFromSystemAddress(sAddress));
            }
            return myAddresses;
        } catch (Exception e) {
            Logger.m172d("Error add addresses");
            return myAddresses;
        }
    }
}
