package ru.ok.android.http.impl.nio;

import java.io.IOException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import ru.ok.android.http.HttpEntityEnclosingRequest;
import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.HttpResponseFactory;
import ru.ok.android.http.config.MessageConstraints;
import ru.ok.android.http.entity.ContentLengthStrategy;
import ru.ok.android.http.impl.nio.codecs.DefaultHttpRequestWriter;
import ru.ok.android.http.impl.nio.codecs.DefaultHttpRequestWriterFactory;
import ru.ok.android.http.impl.nio.codecs.DefaultHttpResponseParser;
import ru.ok.android.http.impl.nio.codecs.DefaultHttpResponseParserFactory;
import ru.ok.android.http.nio.NHttpClientHandler;
import ru.ok.android.http.nio.NHttpClientIOTarget;
import ru.ok.android.http.nio.NHttpMessageParser;
import ru.ok.android.http.nio.NHttpMessageParserFactory;
import ru.ok.android.http.nio.NHttpMessageWriter;
import ru.ok.android.http.nio.NHttpMessageWriterFactory;
import ru.ok.android.http.nio.reactor.IOSession;
import ru.ok.android.http.nio.reactor.SessionInputBuffer;
import ru.ok.android.http.nio.reactor.SessionOutputBuffer;
import ru.ok.android.http.nio.util.ByteBufferAllocator;
import ru.ok.android.http.params.HttpParamConfig;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;

public class DefaultNHttpClientConnection extends NHttpConnectionBase implements NHttpClientIOTarget {
    protected final NHttpMessageWriter<HttpRequest> requestWriter;
    protected final NHttpMessageParser<HttpResponse> responseParser;

    @Deprecated
    public DefaultNHttpClientConnection(IOSession session, HttpResponseFactory responseFactory, ByteBufferAllocator allocator, HttpParams params) {
        super(session, allocator, params);
        Args.notNull(responseFactory, "Response factory");
        this.responseParser = createResponseParser(this.inbuf, responseFactory, params);
        this.requestWriter = createRequestWriter(this.outbuf, params);
        this.hasBufferedInput = false;
        this.hasBufferedOutput = false;
        this.session.setBufferStatus(this);
    }

    public DefaultNHttpClientConnection(IOSession session, int buffersize, int fragmentSizeHint, ByteBufferAllocator allocator, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, NHttpMessageWriterFactory<HttpRequest> requestWriterFactory, NHttpMessageParserFactory<HttpResponse> responseParserFactory) {
        NHttpMessageWriterFactory requestWriterFactory2;
        NHttpMessageParserFactory responseParserFactory2;
        super(session, buffersize, fragmentSizeHint, allocator, chardecoder, charencoder, constraints, incomingContentStrategy, outgoingContentStrategy);
        if (requestWriterFactory == null) {
            requestWriterFactory2 = DefaultHttpRequestWriterFactory.INSTANCE;
        }
        this.requestWriter = requestWriterFactory2.create(this.outbuf);
        if (responseParserFactory == null) {
            responseParserFactory2 = DefaultHttpResponseParserFactory.INSTANCE;
        }
        this.responseParser = responseParserFactory2.create(this.inbuf, constraints);
    }

    public DefaultNHttpClientConnection(IOSession session, int buffersize, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints) {
        this(session, buffersize, buffersize, null, chardecoder, charencoder, constraints, null, null, null, null);
    }

    public DefaultNHttpClientConnection(IOSession session, int buffersize) {
        this(session, buffersize, buffersize, null, null, null, null, null, null, null, null);
    }

    @Deprecated
    protected NHttpMessageParser<HttpResponse> createResponseParser(SessionInputBuffer buffer, HttpResponseFactory responseFactory, HttpParams params) {
        return new DefaultHttpResponseParser(buffer, null, responseFactory, HttpParamConfig.getMessageConstraints(params));
    }

    @Deprecated
    protected NHttpMessageWriter<HttpRequest> createRequestWriter(SessionOutputBuffer buffer, HttpParams params) {
        return new DefaultHttpRequestWriter(buffer, null);
    }

    protected void onResponseReceived(HttpResponse response) {
    }

    protected void onRequestSubmitted(HttpRequest request) {
    }

    public void resetInput() {
        this.response = null;
        this.contentDecoder = null;
        this.responseParser.reset();
    }

    public void resetOutput() {
        this.request = null;
        this.contentEncoder = null;
        this.requestWriter.reset();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void consumeInput(ru.ok.android.http.nio.NHttpClientEventHandler r7) {
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
        r3 = r6.response;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        if (r3 != 0) goto L_0x007a;
    L_0x000f:
        r3 = r6.responseParser;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r4 = r6.session;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r4 = r4.channel();	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r0 = r3.fillBuffer(r4);	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        if (r0 <= 0) goto L_0x0023;
    L_0x001d:
        r3 = r6.inTransportMetrics;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r4 = (long) r0;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3.incrementBytesTransferred(r4);	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
    L_0x0023:
        r3 = r6.responseParser;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3 = r3.parse();	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3 = (ru.ok.android.http.HttpResponse) r3;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r6.response = r3;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        if (r0 <= 0) goto L_0x0033;
    L_0x002f:
        r3 = r6.response;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        if (r3 == 0) goto L_0x000f;
    L_0x0033:
        r3 = r6.response;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        if (r3 == 0) goto L_0x006c;
    L_0x0037:
        r3 = r6.response;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3 = r3.getStatusLine();	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3 = r3.getStatusCode();	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r4 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r3 < r4) goto L_0x0055;
    L_0x0045:
        r3 = r6.response;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r1 = r6.prepareDecoder(r3);	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3 = r6.response;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3.setEntity(r1);	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3 = r6.connMetrics;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3.incrementResponseCount();	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
    L_0x0055:
        r3 = r6.inbuf;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3 = r3.hasData();	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r6.hasBufferedInput = r3;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3 = r6.response;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r6.onResponseReceived(r3);	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r7.responseReceived(r6);	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3 = r6.contentDecoder;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        if (r3 != 0) goto L_0x006c;
    L_0x0069:
        r6.resetInput();	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
    L_0x006c:
        r3 = -1;
        if (r0 != r3) goto L_0x007a;
    L_0x006f:
        r3 = r6.inbuf;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3 = r3.hasData();	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        if (r3 != 0) goto L_0x007a;
    L_0x0077:
        r7.endOfInput(r6);	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
    L_0x007a:
        r3 = r6.contentDecoder;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        if (r3 == 0) goto L_0x0098;
    L_0x007e:
        r3 = r6.session;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3 = r3.getEventMask();	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3 = r3 & 1;
        if (r3 <= 0) goto L_0x0098;
    L_0x0088:
        r3 = r6.contentDecoder;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r7.inputReady(r6, r3);	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3 = r6.contentDecoder;	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        r3 = r3.isCompleted();	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
        if (r3 == 0) goto L_0x0098;
    L_0x0095:
        r6.resetInput();	 Catch:{ HttpException -> 0x00a2, Exception -> 0x00b3 }
    L_0x0098:
        r3 = r6.inbuf;
        r3 = r3.hasData();
        r6.hasBufferedInput = r3;
        goto L_0x000a;
    L_0x00a2:
        r2 = move-exception;
        r6.resetInput();	 Catch:{ all -> 0x00c1 }
        r7.exception(r6, r2);	 Catch:{ all -> 0x00c1 }
        r3 = r6.inbuf;
        r3 = r3.hasData();
        r6.hasBufferedInput = r3;
        goto L_0x000a;
    L_0x00b3:
        r2 = move-exception;
        r7.exception(r6, r2);	 Catch:{ all -> 0x00c1 }
        r3 = r6.inbuf;
        r3 = r3.hasData();
        r6.hasBufferedInput = r3;
        goto L_0x000a;
    L_0x00c1:
        r3 = move-exception;
        r4 = r6.inbuf;
        r4 = r4.hasData();
        r6.hasBufferedInput = r4;
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.http.impl.nio.DefaultNHttpClientConnection.consumeInput(ru.ok.android.http.nio.NHttpClientEventHandler):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void produceOutput(ru.ok.android.http.nio.NHttpClientEventHandler r8) {
        /*
        r7 = this;
        r6 = 2;
        r2 = r7.status;	 Catch:{ Exception -> 0x006b }
        if (r2 != 0) goto L_0x0020;
    L_0x0005:
        r2 = r7.contentEncoder;	 Catch:{ Exception -> 0x006b }
        if (r2 != 0) goto L_0x000c;
    L_0x0009:
        r8.requestReady(r7);	 Catch:{ Exception -> 0x006b }
    L_0x000c:
        r2 = r7.contentEncoder;	 Catch:{ Exception -> 0x006b }
        if (r2 == 0) goto L_0x0020;
    L_0x0010:
        r2 = r7.contentEncoder;	 Catch:{ Exception -> 0x006b }
        r8.outputReady(r7, r2);	 Catch:{ Exception -> 0x006b }
        r2 = r7.contentEncoder;	 Catch:{ Exception -> 0x006b }
        r2 = r2.isCompleted();	 Catch:{ Exception -> 0x006b }
        if (r2 == 0) goto L_0x0020;
    L_0x001d:
        r7.resetOutput();	 Catch:{ Exception -> 0x006b }
    L_0x0020:
        r2 = r7.outbuf;	 Catch:{ Exception -> 0x006b }
        r2 = r2.hasData();	 Catch:{ Exception -> 0x006b }
        if (r2 == 0) goto L_0x003c;
    L_0x0028:
        r2 = r7.outbuf;	 Catch:{ Exception -> 0x006b }
        r3 = r7.session;	 Catch:{ Exception -> 0x006b }
        r3 = r3.channel();	 Catch:{ Exception -> 0x006b }
        r0 = r2.flush(r3);	 Catch:{ Exception -> 0x006b }
        if (r0 <= 0) goto L_0x003c;
    L_0x0036:
        r2 = r7.outTransportMetrics;	 Catch:{ Exception -> 0x006b }
        r4 = (long) r0;	 Catch:{ Exception -> 0x006b }
        r2.incrementBytesTransferred(r4);	 Catch:{ Exception -> 0x006b }
    L_0x003c:
        r2 = r7.outbuf;	 Catch:{ Exception -> 0x006b }
        r2 = r2.hasData();	 Catch:{ Exception -> 0x006b }
        if (r2 != 0) goto L_0x0062;
    L_0x0044:
        r2 = r7.status;	 Catch:{ Exception -> 0x006b }
        r3 = 1;
        if (r2 != r3) goto L_0x0054;
    L_0x0049:
        r2 = r7.session;	 Catch:{ Exception -> 0x006b }
        r2.close();	 Catch:{ Exception -> 0x006b }
        r2 = 2;
        r7.status = r2;	 Catch:{ Exception -> 0x006b }
        r7.resetOutput();	 Catch:{ Exception -> 0x006b }
    L_0x0054:
        r2 = r7.contentEncoder;	 Catch:{ Exception -> 0x006b }
        if (r2 != 0) goto L_0x0062;
    L_0x0058:
        r2 = r7.status;	 Catch:{ Exception -> 0x006b }
        if (r2 == r6) goto L_0x0062;
    L_0x005c:
        r2 = r7.session;	 Catch:{ Exception -> 0x006b }
        r3 = 4;
        r2.clearEvent(r3);	 Catch:{ Exception -> 0x006b }
    L_0x0062:
        r2 = r7.outbuf;
        r2 = r2.hasData();
        r7.hasBufferedOutput = r2;
    L_0x006a:
        return;
    L_0x006b:
        r1 = move-exception;
        r8.exception(r7, r1);	 Catch:{ all -> 0x0078 }
        r2 = r7.outbuf;
        r2 = r2.hasData();
        r7.hasBufferedOutput = r2;
        goto L_0x006a;
    L_0x0078:
        r2 = move-exception;
        r3 = r7.outbuf;
        r3 = r3.hasData();
        r7.hasBufferedOutput = r3;
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.http.impl.nio.DefaultNHttpClientConnection.produceOutput(ru.ok.android.http.nio.NHttpClientEventHandler):void");
    }

    public void submitRequest(HttpRequest request) throws IOException, HttpException {
        Args.notNull(request, "HTTP request");
        assertNotClosed();
        if (this.request != null) {
            throw new HttpException("Request already submitted");
        }
        onRequestSubmitted(request);
        this.requestWriter.write(request);
        this.hasBufferedOutput = this.outbuf.hasData();
        if ((request instanceof HttpEntityEnclosingRequest) && ((HttpEntityEnclosingRequest) request).getEntity() != null) {
            prepareEncoder(request);
            this.request = request;
        }
        this.connMetrics.incrementRequestCount();
        this.session.setEvent(4);
    }

    public boolean isRequestSubmitted() {
        return this.request != null;
    }

    public void consumeInput(NHttpClientHandler handler) {
        consumeInput(new NHttpClientEventHandlerAdaptor(handler));
    }

    public void produceOutput(NHttpClientHandler handler) {
        produceOutput(new NHttpClientEventHandlerAdaptor(handler));
    }
}
