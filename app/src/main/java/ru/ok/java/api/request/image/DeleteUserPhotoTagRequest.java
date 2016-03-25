package ru.ok.java.api.request.image;

import android.text.TextUtils;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class DeleteUserPhotoTagRequest extends BaseRequest {
    private final String[] pids;

    public DeleteUserPhotoTagRequest(String[] pids) {
        this.pids = pids;
    }

    public String getMethodName() {
        return "photos.deleteUserTags";
    }

    public void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.PHOTO_IDS, TextUtils.join(",", this.pids));
    }
}
