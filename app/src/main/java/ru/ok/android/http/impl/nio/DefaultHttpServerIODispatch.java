package ru.ok.android.http.impl.nio;

import java.io.IOException;
import javax.net.ssl.SSLContext;
import ru.ok.android.http.config.ConnectionConfig;
import ru.ok.android.http.impl.nio.reactor.AbstractIODispatch;
import ru.ok.android.http.nio.NHttpConnectionFactory;
import ru.ok.android.http.nio.NHttpServerEventHandler;
import ru.ok.android.http.nio.reactor.IOSession;
import ru.ok.android.http.nio.reactor.ssl.SSLSetupHandler;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;

public class DefaultHttpServerIODispatch extends AbstractIODispatch<DefaultNHttpServerConnection> {
    private final NHttpConnectionFactory<? extends DefaultNHttpServerConnection> connFactory;
    private final NHttpServerEventHandler handler;

    public DefaultHttpServerIODispatch(NHttpServerEventHandler handler, NHttpConnectionFactory<? extends DefaultNHttpServerConnection> connFactory) {
        this.handler = (NHttpServerEventHandler) Args.notNull(handler, "HTTP client handler");
        this.connFactory = (NHttpConnectionFactory) Args.notNull(connFactory, "HTTP server connection factory");
    }

    @Deprecated
    public DefaultHttpServerIODispatch(NHttpServerEventHandler handler, HttpParams params) {
        this(handler, new DefaultNHttpServerConnectionFactory(params));
    }

    @Deprecated
    public DefaultHttpServerIODispatch(NHttpServerEventHandler handler, SSLContext sslcontext, SSLSetupHandler sslHandler, HttpParams params) {
        this(handler, new SSLNHttpServerConnectionFactory(sslcontext, sslHandler, params));
    }

    @Deprecated
    public DefaultHttpServerIODispatch(NHttpServerEventHandler handler, SSLContext sslcontext, HttpParams params) {
        this(handler, sslcontext, null, params);
    }

    public DefaultHttpServerIODispatch(NHttpServerEventHandler handler, ConnectionConfig config) {
        this(handler, new DefaultNHttpServerConnectionFactory(config));
    }

    public DefaultHttpServerIODispatch(NHttpServerEventHandler handler, SSLContext sslcontext, SSLSetupHandler sslHandler, ConnectionConfig config) {
        this(handler, new SSLNHttpServerConnectionFactory(sslcontext, sslHandler, config));
    }

    public DefaultHttpServerIODispatch(NHttpServerEventHandler handler, SSLContext sslcontext, ConnectionConfig config) {
        this(handler, new SSLNHttpServerConnectionFactory(sslcontext, null, config));
    }

    protected DefaultNHttpServerConnection createConnection(IOSession session) {
        return (DefaultNHttpServerConnection) this.connFactory.createConnection(session);
    }

    protected void onConnected(DefaultNHttpServerConnection conn) {
        try {
            this.handler.connected(conn);
        } catch (Exception ex) {
            this.handler.exception(conn, ex);
        }
    }

    protected void onClosed(DefaultNHttpServerConnection conn) {
        this.handler.closed(conn);
    }

    protected void onException(DefaultNHttpServerConnection conn, IOException ex) {
        this.handler.exception(conn, ex);
    }

    protected void onInputReady(DefaultNHttpServerConnection conn) {
        conn.consumeInput(this.handler);
    }

    protected void onOutputReady(DefaultNHttpServerConnection conn) {
        conn.produceOutput(this.handler);
    }

    protected void onTimeout(DefaultNHttpServerConnection conn) {
        try {
            this.handler.timeout(conn);
        } catch (Exception ex) {
            this.handler.exception(conn, ex);
        }
    }
}
