package ru.ok.java.api.request.geo;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasSessionKey = true)
public class HttpGetPlacesRequest extends BaseRequest {
    private String anchor;
    private int count;
    private PagingDirection direction;
    private double lat;
    private double lng;
    private String query;
    private String searchProfile;

    public HttpGetPlacesRequest(String query, double lat, double lng, String searchProfile, String anchor, PagingDirection direction, int count) {
        this.query = null;
        this.query = query;
        this.lat = lat;
        this.lng = lng;
        this.searchProfile = searchProfile;
        this.anchor = anchor;
        this.direction = direction;
        this.count = count;
    }

    public HttpGetPlacesRequest(String query, String searchProfile, String anchor, PagingDirection direction, int count) {
        this(query, 0.0d, 0.0d, searchProfile, anchor, direction, count);
    }

    public String getMethodName() {
        return "places.search";
    }

    public void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.QUERY, this.query);
        if (!(this.lat == 0.0d || this.lng == 0.0d)) {
            serializer.add(SerializeParamName.LAT, this.lat);
            serializer.add(SerializeParamName.LNG, this.lng);
        }
        serializer.add(SerializeParamName.SEARCH_PROFILE, this.searchProfile);
        serializer.add(SerializeParamName.ANCHOR, this.anchor);
        serializer.add(SerializeParamName.DIRECTION, this.direction.getValue());
        serializer.add(SerializeParamName.COUNT, this.count);
        serializer.add(SerializeParamName.FIELDS, "place.*");
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME);
    }
}
