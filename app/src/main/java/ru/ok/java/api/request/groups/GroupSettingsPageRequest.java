package ru.ok.java.api.request.groups;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasTargetUrl = true)
public class GroupSettingsPageRequest extends BaseRequest implements TargetUrlGetter {
    private String groupId;
    private String url;

    public GroupSettingsPageRequest(String baseWebUrl, String groupId) {
        this.url = baseWebUrl;
        this.groupId = groupId;
    }

    public String getMethodName() {
        return "api/group_settings";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME);
        serializer.add(SerializeParamName.GID, this.groupId);
    }

    public String getTargetUrl() {
        return this.url;
    }
}
