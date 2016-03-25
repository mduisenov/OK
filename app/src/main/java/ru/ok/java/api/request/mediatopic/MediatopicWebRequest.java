package ru.ok.java.api.request.mediatopic;

import android.text.TextUtils;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasTargetUrl = true, hasUserId = true)
public final class MediatopicWebRequest extends BaseRequest implements TargetUrlGetter {
    private final String baseWebUrl;
    private final String cityId;
    private final String groupId;
    private final String topicId;
    private final String userId;

    public MediatopicWebRequest(String baseWebUrl, String topicId, String userId, String groupId, String cityId) {
        this.baseWebUrl = baseWebUrl;
        this.topicId = topicId;
        this.userId = userId;
        this.groupId = groupId;
        this.cityId = cityId;
    }

    public String getMethodName() {
        return "api/mediatopic";
    }

    public void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.TID, this.topicId).add(SerializeParamName.CLIENT, Api.CLIENT_NAME).add(SerializeParamName.RENDER_AS, "widget");
        if (!TextUtils.isEmpty(this.groupId)) {
            serializer.add(SerializeParamName.GID, this.groupId);
        }
        if (!TextUtils.isEmpty(this.userId)) {
            serializer.add(SerializeParamName.FRIEND_ID, this.userId);
        }
        if (!TextUtils.isEmpty(this.cityId)) {
            serializer.add(SerializeParamName.CITY_ID, this.cityId);
        }
    }

    public String getTargetUrl() {
        return this.baseWebUrl;
    }
}
