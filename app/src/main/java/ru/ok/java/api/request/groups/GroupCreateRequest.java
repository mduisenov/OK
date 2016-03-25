package ru.ok.java.api.request.groups;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class GroupCreateRequest extends BaseRequest {
    private final String description;
    private final boolean isOpen;
    private final String name;
    private final GroupCreateType type;

    public GroupCreateRequest(GroupCreateType type, String name, String description, boolean isOpen) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.isOpen = isOpen;
    }

    public String getMethodName() {
        return "group.createGroup";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.GROUP_TYPE, this.type.getValue());
        serializer.add(SerializeParamName.GROUP_NAME, this.name);
        serializer.add(SerializeParamName.GROUP_DESCRIPTION, this.description);
        serializer.add(SerializeParamName.GROUP_OPEN, this.isOpen);
    }
}
