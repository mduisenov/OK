package ru.ok.android.http.impl;

import java.io.IOException;
import ru.ok.android.http.HttpConnectionMetrics;
import ru.ok.android.http.HttpEntityEnclosingRequest;
import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.HttpRequestFactory;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.HttpServerConnection;
import ru.ok.android.http.impl.entity.DisallowIdentityContentLengthStrategy;
import ru.ok.android.http.impl.entity.EntityDeserializer;
import ru.ok.android.http.impl.entity.EntitySerializer;
import ru.ok.android.http.impl.entity.LaxContentLengthStrategy;
import ru.ok.android.http.impl.entity.StrictContentLengthStrategy;
import ru.ok.android.http.impl.io.DefaultHttpRequestParser;
import ru.ok.android.http.impl.io.HttpResponseWriter;
import ru.ok.android.http.io.EofSensor;
import ru.ok.android.http.io.HttpMessageParser;
import ru.ok.android.http.io.HttpMessageWriter;
import ru.ok.android.http.io.HttpTransportMetrics;
import ru.ok.android.http.io.SessionInputBuffer;
import ru.ok.android.http.io.SessionOutputBuffer;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;

@Deprecated
public abstract class AbstractHttpServerConnection implements HttpServerConnection {
    private final EntityDeserializer entitydeserializer;
    private final EntitySerializer entityserializer;
    private EofSensor eofSensor;
    private SessionInputBuffer inbuffer;
    private HttpConnectionMetricsImpl metrics;
    private SessionOutputBuffer outbuffer;
    private HttpMessageParser<HttpRequest> requestParser;
    private HttpMessageWriter<HttpResponse> responseWriter;

    protected abstract void assertOpen() throws IllegalStateException;

    public AbstractHttpServerConnection() {
        this.inbuffer = null;
        this.outbuffer = null;
        this.eofSensor = null;
        this.requestParser = null;
        this.responseWriter = null;
        this.metrics = null;
        this.entityserializer = createEntitySerializer();
        this.entitydeserializer = createEntityDeserializer();
    }

    protected EntityDeserializer createEntityDeserializer() {
        return new EntityDeserializer(new DisallowIdentityContentLengthStrategy(new LaxContentLengthStrategy(0)));
    }

    protected EntitySerializer createEntitySerializer() {
        return new EntitySerializer(new StrictContentLengthStrategy());
    }

    protected HttpRequestFactory createHttpRequestFactory() {
        return DefaultHttpRequestFactory.INSTANCE;
    }

    protected HttpMessageParser<HttpRequest> createRequestParser(SessionInputBuffer buffer, HttpRequestFactory requestFactory, HttpParams params) {
        return new DefaultHttpRequestParser(buffer, null, requestFactory, params);
    }

    protected HttpMessageWriter<HttpResponse> createResponseWriter(SessionOutputBuffer buffer, HttpParams params) {
        return new HttpResponseWriter(buffer, null, params);
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
        this.requestParser = createRequestParser(inbuffer, createHttpRequestFactory(), params);
        this.responseWriter = createResponseWriter(outbuffer, params);
        this.metrics = createConnectionMetrics(inbuffer.getMetrics(), outbuffer.getMetrics());
    }

    public HttpRequest receiveRequestHeader() throws HttpException, IOException {
        assertOpen();
        HttpRequest request = (HttpRequest) this.requestParser.parse();
        this.metrics.incrementRequestCount();
        return request;
    }

    public void receiveRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        assertOpen();
        request.setEntity(this.entitydeserializer.deserialize(this.inbuffer, request));
    }

    protected void doFlush() throws IOException {
        this.outbuffer.flush();
    }

    public void flush() throws IOException {
        assertOpen();
        doFlush();
    }

    public void sendResponseHeader(HttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        assertOpen();
        this.responseWriter.write(response);
        if (response.getStatusLine().getStatusCode() >= 200) {
            this.metrics.incrementResponseCount();
        }
    }

    public void sendResponseEntity(HttpResponse response) throws HttpException, IOException {
        if (response.getEntity() != null) {
            this.entityserializer.serialize(this.outbuffer, response, response.getEntity());
        }
    }

    protected boolean isEof() {
        return this.eofSensor != null && this.eofSensor.isEof();
    }

    public boolean isStale() {
        boolean z = true;
        if (isOpen() && !isEof()) {
            try {
                this.inbuffer.isDataAvailable(1);
                z = isEof();
            } catch (IOException e) {
            }
        }
        return z;
    }

    public HttpConnectionMetrics getMetrics() {
        return this.metrics;
    }
}
