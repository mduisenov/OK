package ru.ok.android.http.impl.nio;

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
import ru.ok.android.http.nio.util.ByteBufferAllocator;
import ru.ok.android.http.nio.util.HeapByteBufferAllocator;
import ru.ok.android.http.params.HttpParamConfig;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;

public class DefaultNHttpClientConnectionFactory implements NHttpConnectionFactory<DefaultNHttpClientConnection> {
    public static final DefaultNHttpClientConnectionFactory INSTANCE;
    private final ByteBufferAllocator allocator;
    private final ConnectionConfig cconfig;
    private final ContentLengthStrategy incomingContentStrategy;
    private final ContentLengthStrategy outgoingContentStrategy;
    private final NHttpMessageWriterFactory<HttpRequest> requestWriterFactory;
    private final NHttpMessageParserFactory<HttpResponse> responseParserFactory;

    static {
        INSTANCE = new DefaultNHttpClientConnectionFactory();
    }

    @Deprecated
    public DefaultNHttpClientConnectionFactory(HttpResponseFactory responseFactory, ByteBufferAllocator allocator, HttpParams params) {
        Args.notNull(responseFactory, "HTTP response factory");
        Args.notNull(allocator, "Byte buffer allocator");
        Args.notNull(params, "HTTP parameters");
        this.allocator = allocator;
        this.incomingContentStrategy = null;
        this.outgoingContentStrategy = null;
        this.responseParserFactory = new DefaultHttpResponseParserFactory(null, responseFactory);
        this.requestWriterFactory = null;
        this.cconfig = HttpParamConfig.getConnectionConfig(params);
    }

    @Deprecated
    public DefaultNHttpClientConnectionFactory(HttpParams params) {
        this(DefaultHttpResponseFactory.INSTANCE, HeapByteBufferAllocator.INSTANCE, params);
    }

    public DefaultNHttpClientConnectionFactory(ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, NHttpMessageParserFactory<HttpResponse> responseParserFactory, NHttpMessageWriterFactory<HttpRequest> requestWriterFactory, ByteBufferAllocator allocator, ConnectionConfig cconfig) {
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

    public DefaultNHttpClientConnectionFactory(NHttpMessageParserFactory<HttpResponse> responseParserFactory, NHttpMessageWriterFactory<HttpRequest> requestWriterFactory, ByteBufferAllocator allocator, ConnectionConfig cconfig) {
        this(null, null, responseParserFactory, requestWriterFactory, allocator, cconfig);
    }

    public DefaultNHttpClientConnectionFactory(NHttpMessageParserFactory<HttpResponse> responseParserFactory, NHttpMessageWriterFactory<HttpRequest> requestWriterFactory, ConnectionConfig cconfig) {
        this(null, null, responseParserFactory, requestWriterFactory, null, cconfig);
    }

    public DefaultNHttpClientConnectionFactory(ConnectionConfig cconfig) {
        this(null, null, null, null, null, cconfig);
    }

    public DefaultNHttpClientConnectionFactory() {
        this(null, null, null, null, null, null);
    }

    @Deprecated
    protected DefaultNHttpClientConnection createConnection(IOSession session, HttpResponseFactory responseFactory, ByteBufferAllocator allocator, HttpParams params) {
        return new DefaultNHttpClientConnection(session, responseFactory, allocator, params);
    }

    public DefaultNHttpClientConnection createConnection(IOSession session) {
        return new DefaultNHttpClientConnection(session, this.cconfig.getBufferSize(), this.cconfig.getFragmentSizeHint(), this.allocator, ConnSupport.createDecoder(this.cconfig), ConnSupport.createEncoder(this.cconfig), this.cconfig.getMessageConstraints(), this.incomingContentStrategy, this.outgoingContentStrategy, this.requestWriterFactory, this.responseParserFactory);
    }
}
