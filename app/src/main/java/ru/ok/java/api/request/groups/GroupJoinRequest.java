package ru.ok.java.api.request.groups;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class GroupJoinRequest extends BaseRequest {
    private final String groupId;
    private final boolean maybe;

    public GroupJoinRequest(String groupId, boolean maybe) {
        this.groupId = groupId;
        this.maybe = maybe;
    }

    public String getMethodName() {
        return "group.join";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.GROUP_ID, this.groupId);
        serializer.add(SerializeParamName.GROUP_MAYBE, this.maybe);
    }
}
