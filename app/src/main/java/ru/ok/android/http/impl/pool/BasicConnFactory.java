package ru.ok.android.http.impl.pool;

import android.support.v4.app.FragmentTransaction;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import ru.ok.android.http.HttpClientConnection;
import ru.ok.android.http.HttpConnectionFactory;
import ru.ok.android.http.HttpHost;
import ru.ok.android.http.config.ConnectionConfig;
import ru.ok.android.http.config.SocketConfig;
import ru.ok.android.http.impl.DefaultBHttpClientConnection;
import ru.ok.android.http.impl.DefaultBHttpClientConnectionFactory;
import ru.ok.android.http.params.HttpParamConfig;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.pool.ConnFactory;
import ru.ok.android.http.util.Args;

public class BasicConnFactory implements ConnFactory<HttpHost, HttpClientConnection> {
    private final HttpConnectionFactory<? extends HttpClientConnection> connFactory;
    private final int connectTimeout;
    private final SocketFactory plainfactory;
    private final SocketConfig sconfig;
    private final SSLSocketFactory sslfactory;

    @Deprecated
    public BasicConnFactory(SSLSocketFactory sslfactory, HttpParams params) {
        Args.notNull(params, "HTTP params");
        this.plainfactory = null;
        this.sslfactory = sslfactory;
        this.connectTimeout = params.getIntParameter("http.connection.timeout", 0);
        this.sconfig = HttpParamConfig.getSocketConfig(params);
        this.connFactory = new DefaultBHttpClientConnectionFactory(HttpParamConfig.getConnectionConfig(params));
    }

    @Deprecated
    public BasicConnFactory(HttpParams params) {
        this(null, params);
    }

    public BasicConnFactory(SocketFactory plainfactory, SSLSocketFactory sslfactory, int connectTimeout, SocketConfig sconfig, ConnectionConfig cconfig) {
        this.plainfactory = plainfactory;
        this.sslfactory = sslfactory;
        this.connectTimeout = connectTimeout;
        if (sconfig == null) {
            sconfig = SocketConfig.DEFAULT;
        }
        this.sconfig = sconfig;
        if (cconfig == null) {
            cconfig = ConnectionConfig.DEFAULT;
        }
        this.connFactory = new DefaultBHttpClientConnectionFactory(cconfig);
    }

    public BasicConnFactory(int connectTimeout, SocketConfig sconfig, ConnectionConfig cconfig) {
        this(null, null, connectTimeout, sconfig, cconfig);
    }

    public BasicConnFactory(SocketConfig sconfig, ConnectionConfig cconfig) {
        this(null, null, 0, sconfig, cconfig);
    }

    public BasicConnFactory() {
        this(null, null, 0, SocketConfig.DEFAULT, ConnectionConfig.DEFAULT);
    }

    @Deprecated
    protected HttpClientConnection create(Socket socket, HttpParams params) throws IOException {
        DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(params.getIntParameter("http.socket.buffer-size", FragmentTransaction.TRANSIT_EXIT_MASK));
        conn.bind(socket);
        return conn;
    }

    public HttpClientConnection create(HttpHost host) throws IOException {
        String scheme = host.getSchemeName();
        Socket socket = null;
        if ("http".equalsIgnoreCase(scheme)) {
            socket = this.plainfactory != null ? this.plainfactory.createSocket() : new Socket();
        }
        if ("https".equalsIgnoreCase(scheme)) {
            socket = (this.sslfactory != null ? this.sslfactory : SSLSocketFactory.getDefault()).createSocket();
        }
        if (socket == null) {
            throw new IOException(scheme + " scheme is not supported");
        }
        String hostname = host.getHostName();
        int port = host.getPort();
        if (port == -1) {
            if (host.getSchemeName().equalsIgnoreCase("http")) {
                port = 80;
            } else if (host.getSchemeName().equalsIgnoreCase("https")) {
                port = 443;
            }
        }
        socket.setSoTimeout(this.sconfig.getSoTimeout());
        if (this.sconfig.getSndBufSize() > 0) {
            socket.setSendBufferSize(this.sconfig.getSndBufSize());
        }
        if (this.sconfig.getRcvBufSize() > 0) {
            socket.setReceiveBufferSize(this.sconfig.getRcvBufSize());
        }
        socket.setTcpNoDelay(this.sconfig.isTcpNoDelay());
        int linger = this.sconfig.getSoLinger();
        if (linger >= 0) {
            socket.setSoLinger(true, linger);
        }
        socket.setKeepAlive(this.sconfig.isSoKeepAlive());
        socket.connect(new InetSocketAddress(hostname, port), this.connectTimeout);
        return (HttpClientConnection) this.connFactory.createConnection(socket);
    }
}
