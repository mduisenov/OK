package ru.ok.android.http.impl.nio;

import java.io.IOException;
import javax.net.ssl.SSLContext;
import ru.ok.android.http.config.ConnectionConfig;
import ru.ok.android.http.impl.nio.reactor.AbstractIODispatch;
import ru.ok.android.http.nio.NHttpClientEventHandler;
import ru.ok.android.http.nio.NHttpConnectionFactory;
import ru.ok.android.http.nio.reactor.IOSession;
import ru.ok.android.http.nio.reactor.ssl.SSLSetupHandler;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;

public class DefaultHttpClientIODispatch extends AbstractIODispatch<DefaultNHttpClientConnection> {
    private final NHttpConnectionFactory<DefaultNHttpClientConnection> connFactory;
    private final NHttpClientEventHandler handler;

    public DefaultHttpClientIODispatch(NHttpClientEventHandler handler, NHttpConnectionFactory<DefaultNHttpClientConnection> connFactory) {
        this.handler = (NHttpClientEventHandler) Args.notNull(handler, "HTTP client handler");
        this.connFactory = (NHttpConnectionFactory) Args.notNull(connFactory, "HTTP client connection factory");
    }

    @Deprecated
    public DefaultHttpClientIODispatch(NHttpClientEventHandler handler, HttpParams params) {
        this(handler, new DefaultNHttpClientConnectionFactory(params));
    }

    @Deprecated
    public DefaultHttpClientIODispatch(NHttpClientEventHandler handler, SSLContext sslcontext, SSLSetupHandler sslHandler, HttpParams params) {
        this(handler, new SSLNHttpClientConnectionFactory(sslcontext, sslHandler, params));
    }

    @Deprecated
    public DefaultHttpClientIODispatch(NHttpClientEventHandler handler, SSLContext sslcontext, HttpParams params) {
        this(handler, sslcontext, null, params);
    }

    public DefaultHttpClientIODispatch(NHttpClientEventHandler handler, ConnectionConfig config) {
        this(handler, new DefaultNHttpClientConnectionFactory(config));
    }

    public DefaultHttpClientIODispatch(NHttpClientEventHandler handler, SSLContext sslcontext, SSLSetupHandler sslHandler, ConnectionConfig config) {
        this(handler, new SSLNHttpClientConnectionFactory(sslcontext, sslHandler, config));
    }

    public DefaultHttpClientIODispatch(NHttpClientEventHandler handler, SSLContext sslcontext, ConnectionConfig config) {
        this(handler, new SSLNHttpClientConnectionFactory(sslcontext, null, config));
    }

    protected DefaultNHttpClientConnection createConnection(IOSession session) {
        return (DefaultNHttpClientConnection) this.connFactory.createConnection(session);
    }

    protected void onConnected(DefaultNHttpClientConnection conn) {
        try {
            this.handler.connected(conn, conn.getContext().getAttribute("http.session.attachment"));
        } catch (Exception ex) {
            this.handler.exception(conn, ex);
        }
    }

    protected void onClosed(DefaultNHttpClientConnection conn) {
        this.handler.closed(conn);
    }

    protected void onException(DefaultNHttpClientConnection conn, IOException ex) {
        this.handler.exception(conn, ex);
    }

    protected void onInputReady(DefaultNHttpClientConnection conn) {
        conn.consumeInput(this.handler);
    }

    protected void onOutputReady(DefaultNHttpClientConnection conn) {
        conn.produceOutput(this.handler);
    }

    protected void onTimeout(DefaultNHttpClientConnection conn) {
        try {
            this.handler.timeout(conn);
        } catch (Exception ex) {
            this.handler.exception(conn, ex);
        }
    }
}
