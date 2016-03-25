package ru.ok.android.http.impl;

import java.io.IOException;
import java.net.SocketTimeoutException;
import ru.ok.android.http.HttpClientConnection;
import ru.ok.android.http.HttpConnectionMetrics;
import ru.ok.android.http.HttpEntityEnclosingRequest;
import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.HttpResponseFactory;
import ru.ok.android.http.impl.entity.EntityDeserializer;
import ru.ok.android.http.impl.entity.EntitySerializer;
import ru.ok.android.http.impl.entity.LaxContentLengthStrategy;
import ru.ok.android.http.impl.entity.StrictContentLengthStrategy;
import ru.ok.android.http.impl.io.DefaultHttpResponseParser;
import ru.ok.android.http.impl.io.HttpRequestWriter;
import ru.ok.android.http.io.EofSensor;
import ru.ok.android.http.io.HttpMessageParser;
import ru.ok.android.http.io.HttpMessageWriter;
import ru.ok.android.http.io.HttpTransportMetrics;
import ru.ok.android.http.io.SessionInputBuffer;
import ru.ok.android.http.io.SessionOutputBuffer;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;

@Deprecated
public abstract class AbstractHttpClientConnection implements HttpClientConnection {
    private final EntityDeserializer entitydeserializer;
    private final EntitySerializer entityserializer;
    private EofSensor eofSensor;
    private SessionInputBuffer inbuffer;
    private HttpConnectionMetricsImpl metrics;
    private SessionOutputBuffer outbuffer;
    private HttpMessageWriter<HttpRequest> requestWriter;
    private HttpMessageParser<HttpResponse> responseParser;

    protected abstract void assertOpen() throws IllegalStateException;

    public AbstractHttpClientConnection() {
        this.inbuffer = null;
        this.outbuffer = null;
        this.eofSensor = null;
        this.responseParser = null;
        this.requestWriter = null;
        this.metrics = null;
        this.entityserializer = createEntitySerializer();
        this.entitydeserializer = createEntityDeserializer();
    }

    protected EntityDeserializer createEntityDeserializer() {
        return new EntityDeserializer(new LaxContentLengthStrategy());
    }

    protected EntitySerializer createEntitySerializer() {
        return new EntitySerializer(new StrictContentLengthStrategy());
    }

    protected HttpResponseFactory createHttpResponseFactory() {
        return DefaultHttpResponseFactory.INSTANCE;
    }

    protected HttpMessageParser<HttpResponse> createResponseParser(SessionInputBuffer buffer, HttpResponseFactory responseFactory, HttpParams params) {
        return new DefaultHttpResponseParser(buffer, null, responseFactory, params);
    }

    protected HttpMessageWriter<HttpRequest> createRequestWriter(SessionOutputBuffer buffer, HttpParams params) {
        return new HttpRequestWriter(buffer, null, params);
    }

    protected HttpConnectionMetricsImpl createConnectionMetrics(HttpTransportMetrics inTransportMetric, HttpTransportMetrics outTransportMetric) {
        return new HttpConnectionMetricsImpl(inTransportMetric, outTransportMetric);
    }

    protected void init(SessionInputBuffer inbuffer, SessionOutputBuffer outbuffer, HttpParams params) {
        this.inbuffer = (SessionInputBuffer) Args.notNull(inbuffer, "Input session buffer");
        this.outbuffer = (SessionOutputBuffer) Args.notNull(outbuffer, "Output session buffer");
        if (inbuffer instanceof EofSensor) {
            this.eofSensor = (EofSensor) inbuffer;
        }
        this.responseParser = createResponseParser(inbuffer, createHttpResponseFactory(), params);
        this.requestWriter = createRequestWriter(outbuffer, params);
        this.metrics = createConnectionMetrics(inbuffer.getMetrics(), outbuffer.getMetrics());
    }

    public boolean isResponseAvailable(int timeout) throws IOException {
        assertOpen();
        try {
            return this.inbuffer.isDataAvailable(timeout);
        } catch (SocketTimeoutException e) {
            return false;
        }
    }

    public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        assertOpen();
        this.requestWriter.write(request);
        this.metrics.incrementRequestCount();
    }

    public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        assertOpen();
        if (request.getEntity() != null) {
            this.entityserializer.serialize(this.outbuffer, request, request.getEntity());
        }
    }

    protected void doFlush() throws IOException {
        this.outbuffer.flush();
    }

    public void flush() throws IOException {
        assertOpen();
        doFlush();
    }

    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        assertOpen();
        HttpResponse response = (HttpResponse) this.responseParser.parse();
        if (response.getStatusLine().getStatusCode() >= 200) {
            this.metrics.incrementResponseCount();
        }
        return response;
    }

    public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        assertOpen();
        response.setEntity(this.entitydeserializer.deserialize(this.inbuffer, response));
    }

    protected boolean isEof() {
        return this.eofSensor != null && this.eofSensor.isEof();
    }

    public boolean isStale() {
        boolean z = true;
        if (!isOpen() || isEof()) {
            return z;
        }
        try {
            this.inbuffer.isDataAvailable(1);
            return isEof();
        } catch (SocketTimeoutException e) {
            return false;
        } catch (IOException e2) {
            return z;
        }
    }

    public HttpConnectionMetrics getMetrics() {
        return this.metrics;
    }
}
