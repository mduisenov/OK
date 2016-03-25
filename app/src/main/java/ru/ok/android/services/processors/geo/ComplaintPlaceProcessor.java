package ru.ok.android.services.processors.geo;

import android.os.Bundle;
import org.json.JSONObject;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.request.geo.HttpComplaintPlaceRequest;
import ru.ok.model.places.ComplaintPlaceType;
import ru.ok.model.places.Place;

public final class ComplaintPlaceProcessor {
    @Subscribe(on = 2131623944, to = 2131624023)
    public void complaintPlace(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        complaintPlace(event, (Place) bundleInput.getParcelable("place"), (ComplaintPlaceType) bundleInput.getSerializable("type"));
    }

    private void complaintPlace(BusEvent busEvent, Place place, ComplaintPlaceType type) {
        try {
            JSONObject responseJson = new JSONObject(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpComplaintPlaceRequest(place.id, type)).getHttpResponse());
            Bundle bundle = new Bundle();
            bundle.putBoolean("key_places_complaint_result", responseJson.getBoolean("success"));
            GlobalBus.send(2131624198, new BusEvent(busEvent.bundleInput, bundle, -1));
        } catch (Exception e) {
            Bundle errorBundle = new Bundle();
            errorBundle.putSerializable("key_exception_places_complaint_result", e);
            GlobalBus.send(2131624198, new BusEvent(busEvent.bundleInput, errorBundle, -2));
        }
    }
}
