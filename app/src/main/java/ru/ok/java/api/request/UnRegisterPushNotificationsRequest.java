package ru.ok.java.api.request;

import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.utils.Constants.Api;

@NoLoginNeeded
public final class UnRegisterPushNotificationsRequest extends BaseRequest {
    private final String deviceId;

    public UnRegisterPushNotificationsRequest(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMethodName() {
        return "mobile.unsubscribeFromPushNotifications";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.DEVICE_ID, this.deviceId).add(SerializeParamName.DEVICE_TYPE, "android2").add(SerializeParamName.DEVICE_VERSION, Api.CLIENT_NAME).add(SerializeParamName.CLIENT, Api.CLIENT_NAME);
    }
}
