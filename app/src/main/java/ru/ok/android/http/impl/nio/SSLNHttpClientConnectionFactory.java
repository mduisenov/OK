package ru.ok.android.http.impl.nio;

import javax.net.ssl.SSLContext;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.HttpResponseFactory;
import ru.ok.android.http.config.ConnectionConfig;
import ru.ok.android.http.entity.ContentLengthStrategy;
import ru.ok.android.http.impl.ConnSupport;
import ru.ok.android.http.impl.DefaultHttpResponseFactory;
import ru.ok.android.http.impl.nio.codecs.DefaultHttpResponseParserFactory;
import ru.ok.android.http.nio.NHttpConnectionFactory;
import ru.ok.android.http.nio.NHttpMessageParserFactory;
import ru.ok.android.http.nio.NHttpMessageWriterFactory;
import ru.ok.android.http.nio.reactor.IOSession;
import ru.ok.android.http.nio.reactor.ssl.SSLIOSession;
import ru.ok.android.http.nio.reactor.ssl.SSLMode;
import ru.ok.android.http.nio.reactor.ssl.SSLSetupHandler;
import ru.ok.android.http.nio.util.ByteBufferAllocator;
import ru.ok.android.http.nio.util.HeapByteBufferAllocator;
import ru.ok.android.http.params.HttpParamConfig;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.ssl.SSLContexts;
import ru.ok.android.http.util.Args;

public class SSLNHttpClientConnectionFactory implements NHttpConnectionFactory<DefaultNHttpClientConnection> {
    public static final SSLNHttpClientConnectionFactory INSTANCE;
    private final ByteBufferAllocator allocator;
    private final ConnectionConfig cconfig;
    private final ContentLengthStrategy incomingContentStrategy;
    private final ContentLengthStrategy outgoingContentStrategy;
    private final NHttpMessageWriterFactory<HttpRequest> requestWriterFactory;
    private final NHttpMessageParserFactory<HttpResponse> responseParserFactory;
    private final SSLSetupHandler sslHandler;
    private final SSLContext sslcontext;

    static {
        INSTANCE = new SSLNHttpClientConnectionFactory();
    }

    @Deprecated
    public SSLNHttpClientConnectionFactory(SSLContext sslcontext, SSLSetupHandler sslHandler, HttpResponseFactory responseFactory, ByteBufferAllocator allocator, HttpParams params) {
        Args.notNull(responseFactory, "HTTP response factory");
        Args.notNull(allocator, "Byte buffer allocator");
        Args.notNull(params, "HTTP parameters");
        if (sslcontext == null) {
            sslcontext = SSLContexts.createSystemDefault();
        }
        this.sslcontext = sslcontext;
        this.sslHandler = sslHandler;
        this.allocator = allocator;
        this.incomingContentStrategy = null;
        this.outgoingContentStrategy = null;
        this.responseParserFactory = new DefaultHttpResponseParserFactory(null, responseFactory);
        this.requestWriterFactory = null;
        this.cconfig = HttpParamConfig.getConnectionConfig(params);
    }

    @Deprecated
    public SSLNHttpClientConnectionFactory(SSLContext sslcontext, SSLSetupHandler sslHandler, HttpParams params) {
        this(sslcontext, sslHandler, DefaultHttpResponseFactory.INSTANCE, HeapByteBufferAllocator.INSTANCE, params);
    }

    @Deprecated
    public SSLNHttpClientConnectionFactory(HttpParams params) {
        this(null, null, params);
    }

    public SSLNHttpClientConnectionFactory(SSLContext sslcontext, SSLSetupHandler sslHandler, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, NHttpMessageParserFactory<HttpResponse> responseParserFactory, NHttpMessageWriterFactory<HttpRequest> requestWriterFactory, ByteBufferAllocator allocator, ConnectionConfig cconfig) {
        if (sslcontext == null) {
            sslcontext = SSLContexts.createSystemDefault();
        }
        this.sslcontext = sslcontext;
        this.sslHandler = sslHandler;
        this.incomingContentStrategy = incomingContentStrategy;
        this.outgoingContentStrategy = outgoingContentStrategy;
        this.responseParserFactory = responseParserFactory;
        this.requestWriterFactory = requestWriterFactory;
        this.allocator = allocator;
        if (cconfig == null) {
            cconfig = ConnectionConfig.DEFAULT;
        }
        this.cconfig = cconfig;
    }

    public SSLNHttpClientConnectionFactory(SSLContext sslcontext, SSLSetupHandler sslHandler, NHttpMessageParserFactory<HttpResponse> responseParserFactory, NHttpMessageWriterFactory<HttpRequest> requestWriterFactory, ByteBufferAllocator allocator, ConnectionConfig cconfig) {
        this(sslcontext, sslHandler, null, null, responseParserFactory, requestWriterFactory, allocator, cconfig);
    }

    public SSLNHttpClientConnectionFactory(SSLContext sslcontext, SSLSetupHandler sslHandler, NHttpMessageParserFactory<HttpResponse> responseParserFactory, NHttpMessageWriterFactory<HttpRequest> requestWriterFactory, ConnectionConfig cconfig) {
        this(sslcontext, sslHandler, null, null, responseParserFactory, requestWriterFactory, null, cconfig);
    }

    public SSLNHttpClientConnectionFactory(SSLContext sslcontext, SSLSetupHandler sslHandler, ConnectionConfig config) {
        this(sslcontext, sslHandler, null, null, null, null, null, config);
    }

    public SSLNHttpClientConnectionFactory(ConnectionConfig config) {
        this(null, null, null, null, null, null, null, config);
    }

    public SSLNHttpClientConnectionFactory() {
        this(null, null, null, null, null, null);
    }

    @Deprecated
    protected DefaultNHttpClientConnection createConnection(IOSession session, HttpResponseFactory responseFactory, ByteBufferAllocator allocator, HttpParams params) {
        return new DefaultNHttpClientConnection(session, responseFactory, allocator, params);
    }

    protected SSLIOSession createSSLIOSession(IOSession iosession, SSLContext sslcontext, SSLSetupHandler sslHandler) {
        return new SSLIOSession(iosession, SSLMode.CLIENT, sslcontext, sslHandler);
    }

    public DefaultNHttpClientConnection createConnection(IOSession iosession) {
        SSLIOSession ssliosession = createSSLIOSession(iosession, this.sslcontext, this.sslHandler);
        iosession.setAttribute("http.session.ssl", ssliosession);
        return new DefaultNHttpClientConnection(ssliosession, this.cconfig.getBufferSize(), this.cconfig.getFragmentSizeHint(), this.allocator, ConnSupport.createDecoder(this.cconfig), ConnSupport.createEncoder(this.cconfig), this.cconfig.getMessageConstraints(), this.incomingContentStrategy, this.outgoingContentStrategy, this.requestWriterFactory, this.responseParserFactory);
    }
}
