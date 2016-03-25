package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetSimilarTracksForArtistRequest extends BaseRequestWmf {
    private long artistId;

    public HttpGetSimilarTracksForArtistRequest(long artistId, String url) {
        super(url);
        this.artistId = artistId;
    }

    public String getMethodName() {
        return "/similarTracksForArtist";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.ARTIST_ID, this.artistId).add(SerializeWmfParamName.CLIENT, "android");
    }
}
