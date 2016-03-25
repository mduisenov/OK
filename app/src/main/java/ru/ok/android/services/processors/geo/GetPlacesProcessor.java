package ru.ok.android.services.processors.geo;

import android.os.Bundle;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.json.JSONObject;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.json.places.JsonPlacesParser;
import ru.ok.java.api.request.geo.HttpGetPlacesRequest;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.model.places.Place;

public final class GetPlacesProcessor {

    public enum SearchProfileType {
        NO_CORD_WITH_TEXT("mob_no_coord_with_text"),
        WITH_CORD_WITH_TEXT("mob_with_coord_with_text"),
        WITH_CORD_NO_TEXT("mob_with_coord_no_text");
        
        private final String value;

        private SearchProfileType(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    @Subscribe(on = 2131623944, to = 2131624029)
    public void getPlaces(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        String query = bundleInput.getString(DiscoverInfo.ELEMENT);
        double lat = bundleInput.getDouble("lat");
        double lng = bundleInput.getDouble("lng");
        SearchProfileType searchProfile = (SearchProfileType) bundleInput.getSerializable("search_profile");
        String anchor = bundleInput.getString("anchor");
        PagingDirection direction = (PagingDirection) bundleInput.getSerializable("direction");
        int count = bundleInput.getInt("count");
        try {
            JsonHttpResult result = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetPlacesRequest(query, lat, lng, searchProfile.getValue(), anchor, direction, count));
            JSONObject object = result.getResultAsObject();
            ArrayList<Place> places = new ArrayList();
            boolean hasMore = false;
            String anchorOut = null;
            if (object.has("places")) {
                places = new JsonPlacesParser(result.getResultAsObject()).parse();
            }
            if (object.has("has_more")) {
                hasMore = object.getBoolean("has_more");
            }
            if (object.has("anchor")) {
                anchorOut = object.getString("anchor");
            }
            if (!(hasMore || places.size() >= 10 || TextUtils.isEmpty(query))) {
                JsonHttpResult resultNext = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetPlacesRequest(query, SearchProfileType.NO_CORD_WITH_TEXT.getValue(), null, direction, count));
                if (resultNext.getResultAsObject().has("places")) {
                    Iterator it = new JsonPlacesParser(resultNext.getResultAsObject()).parse().iterator();
                    while (it.hasNext()) {
                        Place placeNext = (Place) it.next();
                        boolean isAdd = true;
                        Iterator i$ = places.iterator();
                        while (i$.hasNext()) {
                            if (((Place) i$.next()).id.equals(placeNext.id)) {
                                isAdd = false;
                            }
                        }
                        if (isAdd) {
                            places.add(placeNext);
                        }
                    }
                }
            }
            Bundle bundle = new Bundle();
            bundle.putBoolean("key_places_has_more_result", hasMore);
            bundle.putParcelableArrayList("key_places_result", places);
            bundle.putString("key_anchor", anchorOut);
            GlobalBus.send(2131624204, new BusEvent(event.bundleInput, bundle, -1));
        } catch (Exception e) {
            Bundle errorBundle = new Bundle();
            errorBundle.putSerializable("key_exception_places_result", e);
            GlobalBus.send(2131624204, new BusEvent(event.bundleInput, errorBundle, -2));
        }
    }
}
