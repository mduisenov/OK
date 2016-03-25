package ru.ok.java.api.request.groups;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.param.BaseRequestParam;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class GetGroupsUserStatusRequest extends BaseRequest {
    private final String groupId;
    private final BaseRequestParam uids;

    public GetGroupsUserStatusRequest(BaseRequestParam uids, String groupId) {
        this.uids = uids;
        this.groupId = groupId;
    }

    public String getMethodName() {
        return "group.getUserGroupsByIds";
    }

    public void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.GROUP_ID, this.groupId);
        serializer.add(SerializeParamName.USER_IDS, this.uids);
    }
}
