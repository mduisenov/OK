package ru.ok.java.api.request.groups;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasTargetUrl = true)
public class InviteGroupsPageRequest extends BaseRequest implements TargetUrlGetter {
    private String uid;
    private String url;

    public InviteGroupsPageRequest(String baseWebUrl, String uid) {
        this.url = baseWebUrl;
        this.uid = uid;
    }

    public String getMethodName() {
        return "api/user_invite_to_group";
    }

    public void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.FRIEND_ID, this.uid);
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME);
    }

    public String getTargetUrl() {
        return this.url;
    }
}
