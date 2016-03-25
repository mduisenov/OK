package ru.ok.android.http.impl.nio.pool;

import java.io.IOException;
import javax.net.ssl.SSLContext;
import ru.ok.android.http.HttpHost;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.HttpResponseFactory;
import ru.ok.android.http.config.ConnectionConfig;
import ru.ok.android.http.impl.DefaultHttpResponseFactory;
import ru.ok.android.http.impl.nio.DefaultNHttpClientConnectionFactory;
import ru.ok.android.http.impl.nio.SSLNHttpClientConnectionFactory;
import ru.ok.android.http.nio.NHttpClientConnection;
import ru.ok.android.http.nio.NHttpConnectionFactory;
import ru.ok.android.http.nio.NHttpMessageParserFactory;
import ru.ok.android.http.nio.NHttpMessageWriterFactory;
import ru.ok.android.http.nio.pool.NIOConnFactory;
import ru.ok.android.http.nio.reactor.IOSession;
import ru.ok.android.http.nio.reactor.ssl.SSLSetupHandler;
import ru.ok.android.http.nio.util.ByteBufferAllocator;
import ru.ok.android.http.nio.util.HeapByteBufferAllocator;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;

public class BasicNIOConnFactory implements NIOConnFactory<HttpHost, NHttpClientConnection> {
    private final NHttpConnectionFactory<? extends NHttpClientConnection> plainFactory;
    private final NHttpConnectionFactory<? extends NHttpClientConnection> sslFactory;

    public BasicNIOConnFactory(NHttpConnectionFactory<? extends NHttpClientConnection> plainFactory, NHttpConnectionFactory<? extends NHttpClientConnection> sslFactory) {
        Args.notNull(plainFactory, "Plain HTTP client connection factory");
        this.plainFactory = plainFactory;
        this.sslFactory = sslFactory;
    }

    public BasicNIOConnFactory(NHttpConnectionFactory<? extends NHttpClientConnection> plainFactory) {
        this(plainFactory, null);
    }

    @Deprecated
    public BasicNIOConnFactory(SSLContext sslcontext, SSLSetupHandler sslHandler, HttpResponseFactory responseFactory, ByteBufferAllocator allocator, HttpParams params) {
        this(new DefaultNHttpClientConnectionFactory(responseFactory, allocator, params), new SSLNHttpClientConnectionFactory(sslcontext, sslHandler, responseFactory, allocator, params));
    }

    @Deprecated
    public BasicNIOConnFactory(SSLContext sslcontext, SSLSetupHandler sslHandler, HttpParams params) {
        this(sslcontext, sslHandler, DefaultHttpResponseFactory.INSTANCE, HeapByteBufferAllocator.INSTANCE, params);
    }

    @Deprecated
    public BasicNIOConnFactory(HttpParams params) {
        this(null, null, params);
    }

    public BasicNIOConnFactory(SSLContext sslcontext, SSLSetupHandler sslHandler, NHttpMessageParserFactory<HttpResponse> responseParserFactory, NHttpMessageWriterFactory<HttpRequest> requestWriterFactory, ByteBufferAllocator allocator, ConnectionConfig config) {
        this(new DefaultNHttpClientConnectionFactory(responseParserFactory, requestWriterFactory, allocator, config), new SSLNHttpClientConnectionFactory(sslcontext, sslHandler, responseParserFactory, requestWriterFactory, allocator, config));
    }

    public BasicNIOConnFactory(SSLContext sslcontext, SSLSetupHandler sslHandler, ConnectionConfig config) {
        this(sslcontext, sslHandler, null, null, null, config);
    }

    public BasicNIOConnFactory(ConnectionConfig config) {
        this(new DefaultNHttpClientConnectionFactory(config), null);
    }

    public NHttpClientConnection create(HttpHost route, IOSession session) throws IOException {
        NHttpClientConnection conn;
        if (!route.getSchemeName().equalsIgnoreCase("https")) {
            conn = (NHttpClientConnection) this.plainFactory.createConnection(session);
        } else if (this.sslFactory == null) {
            throw new IOException("SSL not supported");
        } else {
            conn = (NHttpClientConnection) this.sslFactory.createConnection(session);
        }
        session.setAttribute("http.connection", conn);
        return conn;
    }
}
