package ru.ok.android.http.nio;

import java.io.IOException;
import ru.ok.android.http.HttpException;

@Deprecated
public interface NHttpServiceHandler {
    void closed(NHttpServerConnection nHttpServerConnection);

    void connected(NHttpServerConnection nHttpServerConnection);

    void exception(NHttpServerConnection nHttpServerConnection, IOException iOException);

    void exception(NHttpServerConnection nHttpServerConnection, HttpException httpException);

    void inputReady(NHttpServerConnection nHttpServerConnection, ContentDecoder contentDecoder);

    void outputReady(NHttpServerConnection nHttpServerConnection, ContentEncoder contentEncoder);

    void requestReceived(NHttpServerConnection nHttpServerConnection);

    void responseReady(NHttpServerConnection nHttpServerConnection);

    void timeout(NHttpServerConnection nHttpServerConnection);
}
