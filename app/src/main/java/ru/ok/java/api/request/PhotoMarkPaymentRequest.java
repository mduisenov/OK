package ru.ok.java.api.request;

import android.text.TextUtils;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasTargetUrl = true, hasUserId = true)
public final class PhotoMarkPaymentRequest extends BaseRequest implements TargetUrlGetter {
    private String aid;
    private String fid;
    private String pid;
    private String url;

    public PhotoMarkPaymentRequest(String baseWebUrl, String aid, String pid, String fid) {
        this.url = baseWebUrl;
        this.aid = aid;
        this.pid = pid;
        this.fid = fid;
    }

    public String getMethodName() {
        return "api/show-payment";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        if (!TextUtils.isEmpty(this.aid)) {
            serializer.add(SerializeParamName.ALBUM_ID, this.aid);
        }
        serializer.add(SerializeParamName.PHOTO_ID, this.pid);
        serializer.add(SerializeParamName.FRIEND_ID, this.fid);
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME);
        serializer.add(SerializeParamName.SRV_ID, 1);
    }

    public String getTargetUrl() {
        return this.url;
    }
}
