package ru.ok.android.http.impl.nio;

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
import ru.ok.android.http.nio.util.ByteBufferAllocator;
import ru.ok.android.http.nio.util.HeapByteBufferAllocator;
import ru.ok.android.http.params.HttpParamConfig;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;

public class DefaultNHttpServerConnectionFactory implements NHttpConnectionFactory<DefaultNHttpServerConnection> {
    private final ByteBufferAllocator allocator;
    private final ConnectionConfig cconfig;
    private final ContentLengthStrategy incomingContentStrategy;
    private final ContentLengthStrategy outgoingContentStrategy;
    private final NHttpMessageParserFactory<HttpRequest> requestParserFactory;
    private final NHttpMessageWriterFactory<HttpResponse> responseWriterFactory;

    @Deprecated
    public DefaultNHttpServerConnectionFactory(HttpRequestFactory requestFactory, ByteBufferAllocator allocator, HttpParams params) {
        Args.notNull(requestFactory, "HTTP request factory");
        Args.notNull(allocator, "Byte buffer allocator");
        Args.notNull(params, "HTTP parameters");
        this.incomingContentStrategy = null;
        this.outgoingContentStrategy = null;
        this.requestParserFactory = new DefaultHttpRequestParserFactory(null, requestFactory);
        this.responseWriterFactory = null;
        this.allocator = allocator;
        this.cconfig = HttpParamConfig.getConnectionConfig(params);
    }

    @Deprecated
    public DefaultNHttpServerConnectionFactory(HttpParams params) {
        this(DefaultHttpRequestFactory.INSTANCE, HeapByteBufferAllocator.INSTANCE, params);
    }

    @Deprecated
    protected DefaultNHttpServerConnection createConnection(IOSession session, HttpRequestFactory requestFactory, ByteBufferAllocator allocator, HttpParams params) {
        return new DefaultNHttpServerConnection(session, requestFactory, allocator, params);
    }

    public DefaultNHttpServerConnectionFactory(ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, NHttpMessageParserFactory<HttpRequest> requestParserFactory, NHttpMessageWriterFactory<HttpResponse> responseWriterFactory, ByteBufferAllocator allocator, ConnectionConfig cconfig) {
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

    public DefaultNHttpServerConnectionFactory(ByteBufferAllocator allocator, NHttpMessageParserFactory<HttpRequest> requestParserFactory, NHttpMessageWriterFactory<HttpResponse> responseWriterFactory, ConnectionConfig cconfig) {
        this(null, null, requestParserFactory, responseWriterFactory, allocator, cconfig);
    }

    public DefaultNHttpServerConnectionFactory(ConnectionConfig config) {
        this(null, null, null, null, null, config);
    }

    public DefaultNHttpServerConnectionFactory() {
        this(null, null, null, null, null, null);
    }

    public DefaultNHttpServerConnection createConnection(IOSession session) {
        return new DefaultNHttpServerConnection(session, this.cconfig.getBufferSize(), this.cconfig.getFragmentSizeHint(), this.allocator, ConnSupport.createDecoder(this.cconfig), ConnSupport.createEncoder(this.cconfig), this.cconfig.getMessageConstraints(), this.incomingContentStrategy, this.outgoingContentStrategy, this.requestParserFactory, this.responseWriterFactory);
    }
}
