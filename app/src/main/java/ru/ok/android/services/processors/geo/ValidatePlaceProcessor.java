package ru.ok.android.services.processors.geo;

import android.os.Bundle;
import android.text.TextUtils;
import java.util.ArrayList;
import org.json.JSONArray;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.geo.HttpValidatePlaceRequest;

public final class ValidatePlaceProcessor {
    @Subscribe(on = 2131623944, to = 2131624037)
    public void validatePlace(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        try {
            BaseRequest request = new HttpValidatePlaceRequest(bundleInput.getDouble("lat"), bundleInput.getDouble("lng"), bundleInput.getString("country_code"), bundleInput.getString("city_id"), bundleInput.getString("city_name"), bundleInput.getString("street"), bundleInput.getString("house"), bundleInput.getString("place_name"), bundleInput.getString("category_id"));
            JSONArray errors = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(request).getResultAsObject().getJSONArray("validation_errors");
            ArrayList<String> errorsList = new ArrayList();
            if (errors.length() > 0) {
                for (int i = 0; i < errors.length(); i++) {
                    String msg = errors.getJSONObject(i).getString("error_message");
                    if (!TextUtils.isEmpty(msg)) {
                        errorsList.add(msg);
                    }
                }
            }
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("key_extra_messages_errors", errorsList);
            GlobalBus.send(2131624212, new BusEvent(event.bundleInput, bundle, -1));
        } catch (Exception e) {
            Bundle errorBundle = new Bundle();
            errorBundle.putSerializable("key_exception_validate_place_result", e);
            GlobalBus.send(2131624212, new BusEvent(event.bundleInput, errorBundle, -2));
        }
    }
}
