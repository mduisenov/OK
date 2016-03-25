package ru.ok.java.api.request.groups;

import android.text.TextUtils;
import java.util.Collection;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class GetCategoriesGroupsRequest extends BaseRequest {
    private final String anchor;
    private final Collection<String> categoryIds;
    private final int count;
    private final String direction;
    private final String fields;
    private final int friendMembersLimit;
    private final boolean loadMembersCounters;
    private final boolean loadOwnGroup;
    private final boolean online;
    private final int tagsLimit;

    public GetCategoriesGroupsRequest(Collection<String> categoryIds, String anchor, String direction, int count, int friendMembersLimit) {
        this(categoryIds, true, true, friendMembersLimit, 0, "group.*,group_photo.pic128x128,group_photo.pic240min,group_photo.pic320min,group_photo.pic640x480,user.*", anchor, direction, count, true);
    }

    public GetCategoriesGroupsRequest(Collection<String> categoryIds, boolean loadOwnGroup, boolean loadMembersCounters, int friendMembersLimit, int tagsLimit, String fields, String anchor, String direction, int count, boolean online) {
        this.categoryIds = categoryIds;
        this.loadOwnGroup = loadOwnGroup;
        this.loadMembersCounters = loadMembersCounters;
        this.friendMembersLimit = friendMembersLimit;
        this.tagsLimit = tagsLimit;
        this.fields = fields;
        this.anchor = anchor;
        this.direction = direction;
        this.count = count;
        this.online = online;
    }

    public String getMethodName() {
        return "group.getCategoriesGroups";
    }

    public void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        if (this.categoryIds != null) {
            serializer.add(SerializeParamName.CATEGORY_IDS, TextUtils.join(",", this.categoryIds));
        }
        serializer.add(SerializeParamName.LOAD_OWN_GROUP, this.loadOwnGroup).add(SerializeParamName.LOAD_MEMBERS_COUNTERS, this.loadMembersCounters).add(SerializeParamName.FIELDS, this.fields).add(SerializeParamName.FRIEND_MEMBERS_LIMIT, this.friendMembersLimit).add(SerializeParamName.TAGS_LIMIT, this.tagsLimit).add(SerializeParamName.ANCHOR, this.anchor).add(SerializeParamName.DIRECTION, this.direction).add(SerializeParamName.COUNT, this.count);
        if (!this.online) {
            serializer.add(SerializeParamName.SET_ONLINE, this.online);
        }
    }
}
