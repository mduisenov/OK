package ru.ok.java.api.request.geo;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.utils.Constants.Api;
import ru.ok.model.places.ComplaintPlaceType;

@HttpPreamble(hasSessionKey = true)
public class HttpComplaintPlaceRequest extends BaseRequest {
    private String id;
    private ComplaintPlaceType type;

    public HttpComplaintPlaceRequest(String placeId, ComplaintPlaceType type) {
        this.id = placeId;
        this.type = type;
    }

    public String getMethodName() {
        return "places.registerComplaint";
    }

    public void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.PLACE_ID, this.id);
        if (this.type != null) {
            serializer.add(SerializeParamName.COMPLAINT_TYPE, this.type.getValue());
        }
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME);
    }
}
