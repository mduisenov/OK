package ru.ok.java.api.request.mediatopic;

import android.text.TextUtils;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class MediaTopicGetByPhotoRequest extends BaseRequest {
    private final String pid;

    public MediaTopicGetByPhotoRequest(String pid) {
        this.pid = pid;
    }

    public String getMethodName() {
        return "mediatopic.getByPhoto";
    }

    public void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        if (!TextUtils.isEmpty(this.pid)) {
            serializer.add(SerializeParamName.PHOTO_ID, this.pid);
        }
    }

    public String toString() {
        return "MediaTopicGetByPhotoRequest{pid='" + this.pid + '\'' + '}';
    }
}
