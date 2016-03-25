package ru.ok.android.http.impl;

import java.io.IOException;
import java.net.Socket;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;

@Deprecated
public class DefaultHttpServerConnection extends SocketHttpServerConnection {
    public void bind(Socket socket, HttpParams params) throws IOException {
        boolean z = true;
        Args.notNull(socket, "Socket");
        Args.notNull(params, "HTTP parameters");
        assertNotOpen();
        socket.setTcpNoDelay(params.getBooleanParameter("http.tcp.nodelay", true));
        socket.setSoTimeout(params.getIntParameter("http.socket.timeout", 0));
        socket.setKeepAlive(params.getBooleanParameter("http.socket.keepalive", false));
        int linger = params.getIntParameter("http.socket.linger", -1);
        if (linger >= 0) {
            socket.setSoLinger(linger > 0, linger);
        }
        if (linger >= 0) {
            if (linger <= 0) {
                z = false;
            }
            socket.setSoLinger(z, linger);
        }
        super.bind(socket, params);
    }
}
