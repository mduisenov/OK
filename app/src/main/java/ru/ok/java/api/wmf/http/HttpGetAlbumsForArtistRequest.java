package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.model.wmf.Artist;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetAlbumsForArtistRequest extends BaseRequestWmf {
    private Artist artist;

    public HttpGetAlbumsForArtistRequest(Artist artist, String url) {
        super(url);
        this.artist = artist;
    }

    public String getMethodName() {
        return "/artist";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.CLIENT, "android");
        serializer.add(SerializeWmfParamName.ARTIST_ID, this.artist.id);
    }
}
