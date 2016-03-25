package ru.ok.android.http.impl.io;

import java.io.IOException;
import java.net.Socket;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;

@Deprecated
public class SocketOutputBuffer extends AbstractSessionOutputBuffer {
    public SocketOutputBuffer(Socket socket, int buffersize, HttpParams params) throws IOException {
        Args.notNull(socket, "Socket");
        int n = buffersize;
        if (n < 0) {
            n = socket.getSendBufferSize();
        }
        if (n < 1024) {
            n = 1024;
        }
        init(socket.getOutputStream(), n, params);
    }
}
