package ru.ok.android.services.processors.geo;

import android.os.Bundle;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.json.places.JsonPlaceCategoryParser;
import ru.ok.java.api.request.geo.HttpGetCategoriesRequest;
import ru.ok.model.places.PlaceCategory;

public final class GetCategoriesProcessor {
    @Subscribe(on = 2131623944, to = 2131624028)
    public void reverseGeoCode(BusEvent event) {
        try {
            JSONObject object = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetCategoriesRequest()).getResultAsObject();
            ArrayList<PlaceCategory> categoriesList = new ArrayList();
            JSONArray categoriesArray = object.getJSONArray("categories");
            for (int i = 0; i < categoriesArray.length(); i++) {
                PlaceCategory category = new JsonPlaceCategoryParser(categoriesArray.getJSONObject(i)).parse();
                if (category != null) {
                    categoriesList.add(category);
                }
            }
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("key_categories_list_result", categoriesList);
            GlobalBus.send(2131624203, new BusEvent(event.bundleInput, bundle, -1));
        } catch (Exception e) {
            Bundle errorBundle = new Bundle();
            errorBundle.putSerializable("key_exception_get_categories_result", e);
            GlobalBus.send(2131624203, new BusEvent(event.bundleInput, errorBundle, -2));
        }
    }
}
