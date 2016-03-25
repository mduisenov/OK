package ru.ok.java.api.request.friends;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class MutualFriendsRequest extends BaseRequest {
    private final String source_id;
    private final String target_id;

    public MutualFriendsRequest(String source_id, String target_id) {
        this.source_id = source_id;
        this.target_id = target_id;
    }

    public String getMethodName() {
        return "friends.getMutualFriends";
    }

    public void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.TARGET_ID, this.target_id);
    }

    public static String getSupplierId() {
        return "friends.getMutualFriends.uids";
    }
}
