package ru.ok.java.api.request.groups;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasTargetUrl = true, hasUserId = true)
public final class GetGroupPageRequest extends BaseRequest implements TargetUrlGetter {
    private String groupUid;
    private String url;

    public GetGroupPageRequest(String baseWebUrl, String groupUid) {
        this.url = baseWebUrl;
        this.groupUid = groupUid;
    }

    public String getMethodName() {
        return "api/group";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.GID, this.groupUid).add(SerializeParamName.CLIENT, Api.CLIENT_NAME).add(SerializeParamName.TYPE, "UNKNOWN");
    }

    public String getTargetUrl() {
        return this.url;
    }
}
