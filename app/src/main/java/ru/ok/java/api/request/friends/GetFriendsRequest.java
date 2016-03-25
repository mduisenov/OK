package ru.ok.java.api.request.friends;

import android.text.TextUtils;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasTargetUrl = false, hasUserId = false)
public final class GetFriendsRequest extends BaseRequest {
    private String fid;

    public GetFriendsRequest(String fid) {
        this.fid = fid;
    }

    public String getMethodName() {
        return "friends.get";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME);
        if (!TextUtils.isEmpty(this.fid)) {
            serializer.add(SerializeParamName.FRIEND_ID, this.fid);
        }
    }
}
