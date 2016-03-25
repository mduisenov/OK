package ru.ok.java.api.request;

import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;

@HttpPreamble(hasTargetUrl = true)
public class MakePresentRequest extends BaseRequest implements TargetUrlGetter {
    private final String holidayId;
    private final String presentId;
    private final String url;
    private final String userId;

    public MakePresentRequest(String baseWebUrl, String userId, String presentId, String holidayId) {
        this.url = baseWebUrl;
        this.userId = userId;
        this.presentId = presentId;
        this.holidayId = holidayId;
    }

    public String getMethodName() {
        return "api/make_present";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.PRESENT_ID, this.presentId);
        if (this.userId != null) {
            serializer.add(SerializeParamName.REF_USERS_ID, this.userId);
        }
        if (this.holidayId != null) {
            serializer.add(SerializeParamName.HOLIDAY_ID, this.holidayId);
        }
    }

    public String getTargetUrl() {
        return this.url;
    }
}
