package ru.ok.android.http.nio;

import java.io.IOException;
import ru.ok.android.http.HttpException;

@Deprecated
public interface NHttpClientHandler {
    void closed(NHttpClientConnection nHttpClientConnection);

    void connected(NHttpClientConnection nHttpClientConnection, Object obj);

    void exception(NHttpClientConnection nHttpClientConnection, IOException iOException);

    void exception(NHttpClientConnection nHttpClientConnection, HttpException httpException);

    void inputReady(NHttpClientConnection nHttpClientConnection, ContentDecoder contentDecoder);

    void outputReady(NHttpClientConnection nHttpClientConnection, ContentEncoder contentEncoder);

    void requestReady(NHttpClientConnection nHttpClientConnection);

    void responseReceived(NHttpClientConnection nHttpClientConnection);

    void timeout(NHttpClientConnection nHttpClientConnection);
}
