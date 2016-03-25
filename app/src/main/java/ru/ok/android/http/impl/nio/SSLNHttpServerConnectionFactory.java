package ru.ok.android.http.impl.nio;

import javax.net.ssl.SSLContext;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.HttpRequestFactory;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.config.ConnectionConfig;
import ru.ok.android.http.entity.ContentLengthStrategy;
import ru.ok.android.http.impl.ConnSupport;
import ru.ok.android.http.impl.DefaultHttpRequestFactory;
import ru.ok.android.http.impl.nio.codecs.DefaultHttpRequestParserFactory;
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

public class SSLNHttpServerConnectionFactory implements NHttpConnectionFactory<DefaultNHttpServerConnection> {
    private final ByteBufferAllocator allocator;
    private final ConnectionConfig cconfig;
    private final ContentLengthStrategy incomingContentStrategy;
    private final ContentLengthStrategy outgoingContentStrategy;
    private final NHttpMessageParserFactory<HttpRequest> requestParserFactory;
    private final NHttpMessageWriterFactory<HttpResponse> responseWriterFactory;
    private final SSLSetupHandler sslHandler;
    private final SSLContext sslcontext;

    @Deprecated
    public SSLNHttpServerConnectionFactory(SSLContext sslcontext, SSLSetupHandler sslHandler, HttpRequestFactory requestFactory, ByteBufferAllocator allocator, HttpParams params) {
        Args.notNull(requestFactory, "HTTP request factory");
        Args.notNull(allocator, "Byte buffer allocator");
        Args.notNull(params, "HTTP parameters");
        if (sslcontext == null) {
            sslcontext = SSLContexts.createSystemDefault();
        }
        this.sslcontext = sslcontext;
        this.sslHandler = sslHandler;
        this.incomingContentStrategy = null;
        this.outgoingContentStrategy = null;
        this.requestParserFactory = new DefaultHttpRequestParserFactory(null, requestFactory);
        this.responseWriterFactory = null;
        this.allocator = allocator;
        this.cconfig = HttpParamConfig.getConnectionConfig(params);
    }

    @Deprecated
    public SSLNHttpServerConnectionFactory(SSLContext sslcontext, SSLSetupHandler sslHandler, HttpParams params) {
        this(sslcontext, sslHandler, DefaultHttpRequestFactory.INSTANCE, HeapByteBufferAllocator.INSTANCE, params);
    }

    @Deprecated
    public SSLNHttpServerConnectionFactory(HttpParams params) {
        this(null, null, params);
    }

    public SSLNHttpServerConnectionFactory(SSLContext sslcontext, SSLSetupHandler sslHandler, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, NHttpMessageParserFactory<HttpRequest> requestParserFactory, NHttpMessageWriterFactory<HttpResponse> responseWriterFactory, ByteBufferAllocator allocator, ConnectionConfig cconfig) {
        if (sslcontext == null) {
            sslcontext = SSLContexts.createSystemDefault();
        }
        this.sslcontext = sslcontext;
        this.sslHandler = sslHandler;
        this.incomingContentStrategy = incomingContentStrategy;
        this.outgoingContentStrategy = outgoingContentStrategy;
        this.requestParserFactory = requestParserFactory;
        this.responseWriterFactory = responseWriterFactory;
        this.allocator = allocator;
        if (cconfig == null) {
            cconfig = ConnectionConfig.DEFAULT;
        }
        this.cconfig = cconfig;
    }

    public SSLNHttpServerConnectionFactory(SSLContext sslcontext, SSLSetupHandler sslHandler, NHttpMessageParserFactory<HttpRequest> requestParserFactory, NHttpMessageWriterFactory<HttpResponse> responseWriterFactory, ByteBufferAllocator allocator, ConnectionConfig cconfig) {
        this(sslcontext, sslHandler, null, null, requestParserFactory, responseWriterFactory, allocator, cconfig);
    }

    public SSLNHttpServerConnectionFactory(SSLContext sslcontext, SSLSetupHandler sslHandler, NHttpMessageParserFactory<HttpRequest> requestParserFactory, NHttpMessageWriterFactory<HttpResponse> responseWriterFactory, ConnectionConfig cconfig) {
        this(sslcontext, sslHandler, null, null, requestParserFactory, responseWriterFactory, null, cconfig);
    }

    public SSLNHttpServerConnectionFactory(SSLContext sslcontext, SSLSetupHandler sslHandler, ConnectionConfig config) {
        this(sslcontext, sslHandler, null, null, null, null, null, config);
    }

    public SSLNHttpServerConnectionFactory(ConnectionConfig config) {
        this(null, null, null, null, null, null, null, config);
    }

    public SSLNHttpServerConnectionFactory() {
        this(null, null, null, null, null, null, null, null);
    }

    @Deprecated
    protected DefaultNHttpServerConnection createConnection(IOSession session, HttpRequestFactory requestFactory, ByteBufferAllocator allocator, HttpParams params) {
        return new DefaultNHttpServerConnection(session, requestFactory, allocator, params);
    }

    protected SSLIOSession createSSLIOSession(IOSession iosession, SSLContext sslcontext, SSLSetupHandler sslHandler) {
        return new SSLIOSession(iosession, SSLMode.SERVER, sslcontext, sslHandler);
    }

    public DefaultNHttpServerConnection createConnection(IOSession iosession) {
        SSLIOSession ssliosession = createSSLIOSession(iosession, this.sslcontext, this.sslHandler);
        iosession.setAttribute("http.session.ssl", ssliosession);
        return new DefaultNHttpServerConnection(ssliosession, this.cconfig.getBufferSize(), this.cconfig.getFragmentSizeHint(), this.allocator, ConnSupport.createDecoder(this.cconfig), ConnSupport.createEncoder(this.cconfig), this.cconfig.getMessageConstraints(), this.incomingContentStrategy, this.outgoingContentStrategy, this.requestParserFactory, this.responseWriterFactory);
    }
}
