package ru.ok.java.api.request.image;

import android.support.v4.view.MotionEventCompat;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public final class CommitSingleImageRequest extends BaseRequest {
    private final String mImageComment;
    private final String mImageId;
    private final String mImageToken;

    public CommitSingleImageRequest(String imageId, String imageToken, String imageComment) {
        this.mImageId = imageId;
        this.mImageToken = imageToken;
        String trimmedComment = imageComment;
        if (trimmedComment != null && trimmedComment.length() > MotionEventCompat.ACTION_MASK) {
            trimmedComment = trimmedComment.substring(0, MotionEventCompat.ACTION_MASK);
        }
        this.mImageComment = trimmedComment;
    }

    public String getMethodName() {
        return "photosV2.commit";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.TOKEN, this.mImageToken);
        serializer.add(SerializeParamName.PHOTO_ID, this.mImageId);
        if (this.mImageComment != null) {
            serializer.add(SerializeParamName.COMMENT, this.mImageComment);
        }
    }
}
