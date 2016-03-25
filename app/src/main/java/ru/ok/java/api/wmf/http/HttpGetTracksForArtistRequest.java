package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetTracksForArtistRequest extends BaseRequestWmf {
    private long artistId;

    public HttpGetTracksForArtistRequest(long artistId, String url) {
        super(url);
        this.artistId = artistId;
    }

    public String getMethodName() {
        return "/artist";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.ARTIST_ID, this.artistId).add(SerializeWmfParamName.CLIENT, "android");
    }
}
