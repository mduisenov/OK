package ru.ok.java.api.request.friends;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class FriendsFilterRequest extends BaseRequest {
    private final FriendsFilter filter;

    public FriendsFilterRequest(FriendsFilter filter) {
        this.filter = filter;
    }

    public String getMethodName() {
        return "friends.filter";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        if (this.filter != null) {
            serializer.add(SerializeParamName.FILTER, this.filter.name());
        }
    }
}
