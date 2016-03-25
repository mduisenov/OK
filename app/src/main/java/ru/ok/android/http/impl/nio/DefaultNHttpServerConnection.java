package ru.ok.android.http.impl.nio;

import java.io.IOException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.HttpRequestFactory;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.config.MessageConstraints;
import ru.ok.android.http.entity.ContentLengthStrategy;
import ru.ok.android.http.impl.entity.DisallowIdentityContentLengthStrategy;
import ru.ok.android.http.impl.entity.LaxContentLengthStrategy;
import ru.ok.android.http.impl.entity.StrictContentLengthStrategy;
import ru.ok.android.http.impl.nio.codecs.DefaultHttpRequestParser;
import ru.ok.android.http.impl.nio.codecs.DefaultHttpRequestParserFactory;
import ru.ok.android.http.impl.nio.codecs.DefaultHttpResponseWriter;
import ru.ok.android.http.impl.nio.codecs.DefaultHttpResponseWriterFactory;
import ru.ok.android.http.nio.NHttpMessageParser;
import ru.ok.android.http.nio.NHttpMessageParserFactory;
import ru.ok.android.http.nio.NHttpMessageWriter;
import ru.ok.android.http.nio.NHttpMessageWriterFactory;
import ru.ok.android.http.nio.NHttpServerIOTarget;
import ru.ok.android.http.nio.NHttpServiceHandler;
import ru.ok.android.http.nio.reactor.IOSession;
import ru.ok.android.http.nio.reactor.SessionInputBuffer;
import ru.ok.android.http.nio.reactor.SessionOutputBuffer;
import ru.ok.android.http.nio.util.ByteBufferAllocator;
import ru.ok.android.http.params.HttpParamConfig;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;

public class DefaultNHttpServerConnection extends NHttpConnectionBase implements NHttpServerIOTarget {
    protected final NHttpMessageParser<HttpRequest> requestParser;
    protected final NHttpMessageWriter<HttpResponse> responseWriter;

    @Deprecated
    public DefaultNHttpServerConnection(IOSession session, HttpRequestFactory requestFactory, ByteBufferAllocator allocator, HttpParams params) {
        super(session, allocator, params);
        Args.notNull(requestFactory, "Request factory");
        this.requestParser = createRequestParser(this.inbuf, requestFactory, params);
        this.responseWriter = createResponseWriter(this.outbuf, params);
    }

    public DefaultNHttpServerConnection(IOSession session, int buffersize, int fragmentSizeHint, ByteBufferAllocator allocator, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, NHttpMessageParserFactory<HttpRequest> requestParserFactory, NHttpMessageWriterFactory<HttpResponse> responseWriterFactory) {
        super(session, buffersize, fragmentSizeHint, allocator, chardecoder, charencoder, constraints, incomingContentStrategy != null ? incomingContentStrategy : DisallowIdentityContentLengthStrategy.INSTANCE, outgoingContentStrategy != null ? outgoingContentStrategy : StrictContentLengthStrategy.INSTANCE);
        if (requestParserFactory == null) {
            requestParserFactory = DefaultHttpRequestParserFactory.INSTANCE;
        }
        this.requestParser = requestParserFactory.create(this.inbuf, constraints);
        if (responseWriterFactory == null) {
            responseWriterFactory = DefaultHttpResponseWriterFactory.INSTANCE;
        }
        this.responseWriter = responseWriterFactory.create(this.outbuf);
    }

    public DefaultNHttpServerConnection(IOSession session, int buffersize, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints) {
        this(session, buffersize, buffersize, null, chardecoder, charencoder, constraints, null, null, null, null);
    }

    public DefaultNHttpServerConnection(IOSession session, int buffersize) {
        this(session, buffersize, buffersize, null, null, null, null, null, null, null, null);
    }

    @Deprecated
    protected ContentLengthStrategy createIncomingContentStrategy() {
        return new DisallowIdentityContentLengthStrategy(new LaxContentLengthStrategy(0));
    }

    @Deprecated
    protected NHttpMessageParser<HttpRequest> createRequestParser(SessionInputBuffer buffer, HttpRequestFactory requestFactory, HttpParams params) {
        return new DefaultHttpRequestParser(buffer, null, requestFactory, HttpParamConfig.getMessageConstraints(params));
    }

    @Deprecated
    protected NHttpMessageWriter<HttpResponse> createResponseWriter(SessionOutputBuffer buffer, HttpParams params) {
        return new DefaultHttpResponseWriter(buffer, null);
    }

    protected void onRequestReceived(HttpRequest request) {
    }

    protected void onResponseSubmitted(HttpResponse response) {
    }

    public void resetInput() {
        this.request = null;
        this.contentDecoder = null;
        this.requestParser.reset();
    }

    public void resetOutput() {
        this.response = null;
        this.contentEncoder = null;
        this.responseWriter.reset();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void consumeInput(ru.ok.android.http.nio.NHttpServerEventHandler r7) {
        /*
        r6 = this;
        r3 = r6.status;
        if (r3 == 0) goto L_0x000b;
    L_0x0004:
        r3 = r6.session;
        r4 = 1;
        r3.clearEvent(r4);
    L_0x000a:
        return;
    L_0x000b:
        r3 = r6.request;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        if (r3 != 0) goto L_0x0074;
    L_0x000f:
        r3 = r6.requestParser;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r4 = r6.session;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r4 = r4.channel();	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r0 = r3.fillBuffer(r4);	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        if (r0 <= 0) goto L_0x0023;
    L_0x001d:
        r3 = r6.inTransportMetrics;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r4 = (long) r0;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3.incrementBytesTransferred(r4);	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
    L_0x0023:
        r3 = r6.requestParser;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3 = r3.parse();	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3 = (ru.ok.android.http.HttpRequest) r3;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r6.request = r3;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        if (r0 <= 0) goto L_0x0033;
    L_0x002f:
        r3 = r6.request;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        if (r3 == 0) goto L_0x000f;
    L_0x0033:
        r3 = r6.request;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        if (r3 == 0) goto L_0x0066;
    L_0x0037:
        r3 = r6.request;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3 = r3 instanceof ru.ok.android.http.HttpEntityEnclosingRequest;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        if (r3 == 0) goto L_0x004a;
    L_0x003d:
        r3 = r6.request;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r1 = r6.prepareDecoder(r3);	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3 = r6.request;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3 = (ru.ok.android.http.HttpEntityEnclosingRequest) r3;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3.setEntity(r1);	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
    L_0x004a:
        r3 = r6.connMetrics;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3.incrementRequestCount();	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3 = r6.inbuf;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3 = r3.hasData();	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r6.hasBufferedInput = r3;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3 = r6.request;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r6.onRequestReceived(r3);	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r7.requestReceived(r6);	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3 = r6.contentDecoder;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        if (r3 != 0) goto L_0x0066;
    L_0x0063:
        r6.resetInput();	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
    L_0x0066:
        r3 = -1;
        if (r0 != r3) goto L_0x0074;
    L_0x0069:
        r3 = r6.inbuf;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3 = r3.hasData();	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        if (r3 != 0) goto L_0x0074;
    L_0x0071:
        r7.endOfInput(r6);	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
    L_0x0074:
        r3 = r6.contentDecoder;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        if (r3 == 0) goto L_0x0092;
    L_0x0078:
        r3 = r6.session;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3 = r3.getEventMask();	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3 = r3 & 1;
        if (r3 <= 0) goto L_0x0092;
    L_0x0082:
        r3 = r6.contentDecoder;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r7.inputReady(r6, r3);	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3 = r6.contentDecoder;	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        r3 = r3.isCompleted();	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
        if (r3 == 0) goto L_0x0092;
    L_0x008f:
        r6.resetInput();	 Catch:{ HttpException -> 0x009c, Exception -> 0x00ad }
    L_0x0092:
        r3 = r6.inbuf;
        r3 = r3.hasData();
        r6.hasBufferedInput = r3;
        goto L_0x000a;
    L_0x009c:
        r2 = move-exception;
        r6.resetInput();	 Catch:{ all -> 0x00bb }
        r7.exception(r6, r2);	 Catch:{ all -> 0x00bb }
        r3 = r6.inbuf;
        r3 = r3.hasData();
        r6.hasBufferedInput = r3;
        goto L_0x000a;
    L_0x00ad:
        r2 = move-exception;
        r7.exception(r6, r2);	 Catch:{ all -> 0x00bb }
        r3 = r6.inbuf;
        r3 = r3.hasData();
        r6.hasBufferedInput = r3;
        goto L_0x000a;
    L_0x00bb:
        r3 = move-exception;
        r4 = r6.inbuf;
        r4 = r4.hasData();
        r6.hasBufferedInput = r4;
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.http.impl.nio.DefaultNHttpServerConnection.consumeInput(ru.ok.android.http.nio.NHttpServerEventHandler):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void produceOutput(ru.ok.android.http.nio.NHttpServerEventHandler r7) {
        /*
        r6 = this;
        r2 = r6.status;	 Catch:{ Exception -> 0x005c }
        if (r2 != 0) goto L_0x001f;
    L_0x0004:
        r2 = r6.contentEncoder;	 Catch:{ Exception -> 0x005c }
        if (r2 != 0) goto L_0x000b;
    L_0x0008:
        r7.responseReady(r6);	 Catch:{ Exception -> 0x005c }
    L_0x000b:
        r2 = r6.contentEncoder;	 Catch:{ Exception -> 0x005c }
        if (r2 == 0) goto L_0x001f;
    L_0x000f:
        r2 = r6.contentEncoder;	 Catch:{ Exception -> 0x005c }
        r7.outputReady(r6, r2);	 Catch:{ Exception -> 0x005c }
        r2 = r6.contentEncoder;	 Catch:{ Exception -> 0x005c }
        r2 = r2.isCompleted();	 Catch:{ Exception -> 0x005c }
        if (r2 == 0) goto L_0x001f;
    L_0x001c:
        r6.resetOutput();	 Catch:{ Exception -> 0x005c }
    L_0x001f:
        r2 = r6.outbuf;	 Catch:{ Exception -> 0x005c }
        r2 = r2.hasData();	 Catch:{ Exception -> 0x005c }
        if (r2 == 0) goto L_0x003b;
    L_0x0027:
        r2 = r6.outbuf;	 Catch:{ Exception -> 0x005c }
        r3 = r6.session;	 Catch:{ Exception -> 0x005c }
        r3 = r3.channel();	 Catch:{ Exception -> 0x005c }
        r0 = r2.flush(r3);	 Catch:{ Exception -> 0x005c }
        if (r0 <= 0) goto L_0x003b;
    L_0x0035:
        r2 = r6.outTransportMetrics;	 Catch:{ Exception -> 0x005c }
        r4 = (long) r0;	 Catch:{ Exception -> 0x005c }
        r2.incrementBytesTransferred(r4);	 Catch:{ Exception -> 0x005c }
    L_0x003b:
        r2 = r6.outbuf;	 Catch:{ Exception -> 0x005c }
        r2 = r2.hasData();	 Catch:{ Exception -> 0x005c }
        if (r2 != 0) goto L_0x0053;
    L_0x0043:
        r2 = r6.status;	 Catch:{ Exception -> 0x005c }
        r3 = 1;
        if (r2 != r3) goto L_0x0053;
    L_0x0048:
        r2 = r6.session;	 Catch:{ Exception -> 0x005c }
        r2.close();	 Catch:{ Exception -> 0x005c }
        r2 = 2;
        r6.status = r2;	 Catch:{ Exception -> 0x005c }
        r6.resetOutput();	 Catch:{ Exception -> 0x005c }
    L_0x0053:
        r2 = r6.outbuf;
        r2 = r2.hasData();
        r6.hasBufferedOutput = r2;
    L_0x005b:
        return;
    L_0x005c:
        r1 = move-exception;
        r7.exception(r6, r1);	 Catch:{ all -> 0x0069 }
        r2 = r6.outbuf;
        r2 = r2.hasData();
        r6.hasBufferedOutput = r2;
        goto L_0x005b;
    L_0x0069:
        r2 = move-exception;
        r3 = r6.outbuf;
        r3 = r3.hasData();
        r6.hasBufferedOutput = r3;
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.http.impl.nio.DefaultNHttpServerConnection.produceOutput(ru.ok.android.http.nio.NHttpServerEventHandler):void");
    }

    public void submitResponse(HttpResponse response) throws IOException, HttpException {
        Args.notNull(response, "HTTP response");
        assertNotClosed();
        if (this.response != null) {
            throw new HttpException("Response already submitted");
        }
        onResponseSubmitted(response);
        this.responseWriter.write(response);
        this.hasBufferedOutput = this.outbuf.hasData();
        if (response.getStatusLine().getStatusCode() >= 200) {
            this.connMetrics.incrementResponseCount();
            if (response.getEntity() != null) {
                this.response = response;
                prepareEncoder(response);
            }
        }
        this.session.setEvent(4);
    }

    public boolean isResponseSubmitted() {
        return this.response != null;
    }

    public void consumeInput(NHttpServiceHandler handler) {
        consumeInput(new NHttpServerEventHandlerAdaptor(handler));
    }

    public void produceOutput(NHttpServiceHandler handler) {
        produceOutput(new NHttpServerEventHandlerAdaptor(handler));
    }
}
