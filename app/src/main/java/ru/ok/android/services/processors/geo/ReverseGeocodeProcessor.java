package ru.ok.android.services.processors.geo;

import android.os.Bundle;
import org.json.JSONObject;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.request.geo.HttpReverseGeocodeRequest;

public final class ReverseGeocodeProcessor {
    @Subscribe(on = 2131623944, to = 2131624035)
    public void reverseGeoCode(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        try {
            HttpReverseGeocodeRequest httpReverseGeocodeRequest = new HttpReverseGeocodeRequest(bundleInput.getDouble("lat"), bundleInput.getDouble("lng"));
            JSONObject object = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(httpReverseGeocodeRequest).getResultAsObject();
            String countryCode = object.getString("country_code");
            String cityName = object.getString("city_name");
            String cityId = object.getString("city_id");
            Bundle bundle = new Bundle();
            bundle.putString("key_country_code_result", countryCode);
            bundle.putString("key_city_id_result", cityId);
            bundle.putString("key_city_name_result", cityName);
            GlobalBus.send(2131624209, new BusEvent(event.bundleInput, bundle, -1));
        } catch (Exception e) {
            Bundle errorBundle = new Bundle();
            errorBundle.putSerializable("key_exception_reverse_result", e);
            GlobalBus.send(2131624209, new BusEvent(event.bundleInput, errorBundle, -2));
        }
    }
}
