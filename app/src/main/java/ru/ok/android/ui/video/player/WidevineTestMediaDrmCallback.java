package ru.ok.android.ui.video.player;

import android.annotation.TargetApi;
import android.media.MediaDrm.KeyRequest;
import android.media.MediaDrm.ProvisionRequest;
import android.text.TextUtils;
import com.google.android.exoplayer.drm.MediaDrmCallback;
import java.io.IOException;
import java.util.UUID;

@TargetApi(18)
public class WidevineTestMediaDrmCallback implements MediaDrmCallback {
    private final String defaultUri;

    public WidevineTestMediaDrmCallback(String videoId) {
        this.defaultUri = "http://wv-staging-proxy.appspot.com/proxy?provider=YouTube&video_id=" + videoId;
    }

    public byte[] executeProvisionRequest(UUID uuid, ProvisionRequest request) throws IOException {
        return PlayerUtil.executePost(request.getDefaultUrl() + "&signedRequest=" + new String(request.getData()), null, null);
    }

    public byte[] executeKeyRequest(UUID uuid, KeyRequest request) throws IOException {
        String url = request.getDefaultUrl();
        if (TextUtils.isEmpty(url)) {
            url = this.defaultUri;
        }
        return PlayerUtil.executePost(url, request.getData(), null);
    }
}
