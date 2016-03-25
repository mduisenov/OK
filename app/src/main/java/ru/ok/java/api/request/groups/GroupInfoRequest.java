package ru.ok.java.api.request.groups;

import android.text.TextUtils;
import java.util.Collection;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.param.BaseRequestParam;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class GroupInfoRequest extends BaseRequest {
    private final String fields;
    private final Collection<String> groupIds;
    private final BaseRequestParam groupIdsParam;
    private final boolean online;

    public GroupInfoRequest(Collection<String> groupIds, String fields) {
        this(groupIds, fields, true, null);
    }

    public GroupInfoRequest(Collection<String> groupIds, String fields, boolean online) {
        this(groupIds, fields, online, null);
    }

    public GroupInfoRequest(Collection<String> groupIds, String fields, boolean online, BaseRequestParam param) {
        this.groupIds = groupIds;
        this.fields = fields;
        this.online = online;
        this.groupIdsParam = param;
    }

    public String getUserIdsSupplier() {
        return getMethodName() + ".admin_ids";
    }

    public String getScopeSupplier() {
        return getMethodName() + ".scope_id";
    }

    public String getSubcategorySupplier() {
        return getMethodName() + ".subcategory_id";
    }

    public String getMethodName() {
        return "group.getInfo";
    }

    public boolean isMakeUserOnline() {
        return this.online;
    }

    public void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        if (this.groupIdsParam != null) {
            serializer.add(SerializeParamName.USER_IDS, this.groupIdsParam);
        } else {
            serializer.add(SerializeParamName.USER_IDS, TextUtils.join(",", this.groupIds));
        }
        if (this.fields != null) {
            serializer.add(SerializeParamName.FIELDS, this.fields);
        }
    }
}
