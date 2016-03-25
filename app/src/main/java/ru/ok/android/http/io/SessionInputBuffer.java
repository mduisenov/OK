package ru.ok.android.http.io;

import java.io.IOException;
import ru.ok.android.http.util.CharArrayBuffer;

public interface SessionInputBuffer {
    HttpTransportMetrics getMetrics();

    @Deprecated
    boolean isDataAvailable(int i) throws IOException;

    int read() throws IOException;

    int read(byte[] bArr, int i, int i2) throws IOException;

    int readLine(CharArrayBuffer charArrayBuffer) throws IOException;
}
