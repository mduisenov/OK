package ru.ok.android.ui.places.loaders;

import android.content.Context;
import android.location.Geocoder;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.utils.Logger;
import ru.ok.model.Address;
import ru.ok.model.Location;

public class ReverseGeocodeLoader extends AsyncTaskLoader<List<Pair<Address, Location>>> {
    private String text;

    public ReverseGeocodeLoader(Context context, String text) {
        super(context);
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public List<Pair<Address, Location>> loadInBackground() {
        List<Pair<Address, Location>> addressesLoc = new ArrayList();
        try {
            for (android.location.Address sAddress : new Geocoder(OdnoklassnikiApplication.getContext(), Locale.getDefault()).getFromLocationName(this.text, 2)) {
                try {
                    Location location = new Location(Double.valueOf(sAddress.getLatitude()), Double.valueOf(sAddress.getLongitude()));
                    Address address = Address.createFromSystemAddress(sAddress);
                    if (!TextUtils.isEmpty(address.street)) {
                        addressesLoc.add(new Pair(address, location));
                    }
                } catch (IllegalStateException e) {
                    Logger.m172d("Error add location");
                }
            }
        } catch (Exception e2) {
            Logger.m172d("Error create locations");
        }
        return addressesLoc;
    }
}
