package ru.ok.java.api.request.presents;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;

public final class SendPresentRequest extends BaseRequest {
    private final String holidayId;
    private final String message;
    private final String presentId;
    private final String token;
    private final String type;
    private final String userId;

    public SendPresentRequest(@NonNull String userId, @NonNull String presentId, @Nullable String message, @Nullable String token, @Nullable String holidayId, @NonNull String type) {
        this.userId = userId;
        this.presentId = presentId;
        this.type = type;
        this.message = message;
        this.token = token;
        this.holidayId = holidayId;
    }

    @NonNull
    public String getMethodName() {
        return "presents.send";
    }

    public void serializeInternal(@NonNull RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.FRIEND_ID, this.userId);
        serializer.add(SerializeParamName.SEND_PRESENT_ID, this.presentId);
        serializer.add(SerializeParamName.PRESENT_VISIBILITY, this.type);
        if (this.holidayId != null) {
            serializer.add(SerializeParamName.SEND_PRESENT_HOLIDAY_ID, this.holidayId);
        }
        if (this.message != null) {
            serializer.add(SerializeParamName.MESSAGE, this.message);
        }
        if (this.token != null) {
            serializer.add(SerializeParamName.TOKEN, this.token);
        }
    }
}
