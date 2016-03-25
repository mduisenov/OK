package ru.ok.android.http.impl.nio.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import ru.ok.android.http.ConnectionClosedException;
import ru.ok.android.http.ConnectionReuseStrategy;
import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpHost;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.client.config.RequestConfig;
import ru.ok.android.http.client.methods.HttpRequestWrapper;
import ru.ok.android.http.client.protocol.HttpClientContext;
import ru.ok.android.http.commons.logging.Log;
import ru.ok.android.http.concurrent.BasicFuture;
import ru.ok.android.http.conn.ConnectionKeepAliveStrategy;
import ru.ok.android.http.conn.routing.HttpRoute;
import ru.ok.android.http.nio.ContentDecoder;
import ru.ok.android.http.nio.ContentEncoder;
import ru.ok.android.http.nio.IOControl;
import ru.ok.android.http.nio.NHttpClientConnection;
import ru.ok.android.http.nio.conn.NHttpClientConnectionManager;
import ru.ok.android.http.nio.protocol.HttpAsyncRequestProducer;
import ru.ok.android.http.nio.protocol.HttpAsyncResponseConsumer;
import ru.ok.android.http.nio.protocol.Pipelined;
import ru.ok.android.http.protocol.HttpProcessor;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.Asserts;

@Pipelined
class PipeliningClientExchangeHandlerImpl<T> extends AbstractClientExchangeHandler {
    private final HttpProcessor httpProcessor;
    private final HttpClientContext localContext;
    private final Queue<HttpAsyncRequestProducer> requestProducerQueue;
    private final AtomicReference<HttpAsyncRequestProducer> requestProducerRef;
    private final Queue<HttpRequest> requestQueue;
    private final Queue<HttpAsyncResponseConsumer<T>> responseConsumerQueue;
    private final AtomicReference<HttpAsyncResponseConsumer<T>> responseConsumerRef;
    private final BasicFuture<List<T>> resultFuture;
    private final Queue<T> resultQueue;
    private final HttpHost target;

    public PipeliningClientExchangeHandlerImpl(Log log, HttpHost target, List<? extends HttpAsyncRequestProducer> requestProducers, List<? extends HttpAsyncResponseConsumer<T>> responseConsumers, HttpClientContext localContext, BasicFuture<List<T>> resultFuture, NHttpClientConnectionManager connmgr, HttpProcessor httpProcessor, ConnectionReuseStrategy connReuseStrategy, ConnectionKeepAliveStrategy keepaliveStrategy) {
        super(log, localContext, resultFuture, connmgr, connReuseStrategy, keepaliveStrategy);
        Args.notNull(target, "HTTP target");
        Args.notEmpty(requestProducers, "Request producer list");
        Args.notEmpty(responseConsumers, "Response consumer list");
        Args.check(requestProducers.size() == responseConsumers.size(), "Number of request producers does not match that of response consumers");
        this.target = target;
        this.requestProducerQueue = new ConcurrentLinkedQueue(requestProducers);
        this.responseConsumerQueue = new ConcurrentLinkedQueue(responseConsumers);
        this.requestQueue = new ConcurrentLinkedQueue();
        this.resultQueue = new ConcurrentLinkedQueue();
        this.localContext = localContext;
        this.resultFuture = resultFuture;
        this.httpProcessor = httpProcessor;
        this.requestProducerRef = new AtomicReference(null);
        this.responseConsumerRef = new AtomicReference(null);
    }

    private void closeProducer(HttpAsyncRequestProducer requestProducer) {
        if (requestProducer != null) {
            try {
                requestProducer.close();
            } catch (IOException ex) {
                this.log.debug("I/O error closing request producer", ex);
            }
        }
    }

    private void closeConsumer(HttpAsyncResponseConsumer<?> responseConsumer) {
        if (responseConsumer != null) {
            try {
                responseConsumer.close();
            } catch (IOException ex) {
                this.log.debug("I/O error closing response consumer", ex);
            }
        }
    }

    void releaseResources() {
        closeProducer((HttpAsyncRequestProducer) this.requestProducerRef.getAndSet(null));
        closeConsumer((HttpAsyncResponseConsumer) this.responseConsumerRef.getAndSet(null));
        while (!this.requestProducerQueue.isEmpty()) {
            closeProducer((HttpAsyncRequestProducer) this.requestProducerQueue.remove());
        }
        while (!this.responseConsumerQueue.isEmpty()) {
            closeConsumer((HttpAsyncResponseConsumer) this.responseConsumerQueue.remove());
        }
        this.requestQueue.clear();
        this.resultQueue.clear();
    }

    void executionFailed(Exception ex) {
        HttpAsyncRequestProducer requestProducer = (HttpAsyncRequestProducer) this.requestProducerRef.get();
        if (requestProducer != null) {
            requestProducer.failed(ex);
        }
        HttpAsyncResponseConsumer<T> responseConsumer = (HttpAsyncResponseConsumer) this.responseConsumerRef.get();
        if (responseConsumer != null) {
            responseConsumer.failed(ex);
        }
        for (HttpAsyncResponseConsumer<T> cancellable : this.responseConsumerQueue) {
            cancellable.cancel();
        }
    }

    boolean executionCancelled() {
        HttpAsyncResponseConsumer<T> responseConsumer = (HttpAsyncResponseConsumer) this.responseConsumerRef.get();
        boolean cancelled = responseConsumer != null && responseConsumer.cancel();
        this.resultFuture.cancel();
        return cancelled;
    }

    public void start() throws HttpException, IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + getId() + "] start execution");
        }
        HttpRoute route = new HttpRoute(this.target);
        setRoute(route);
        this.localContext.setAttribute("http.target_host", this.target);
        this.localContext.setAttribute("http.route", route);
        requestConnection();
    }

    public HttpRequest generateRequest() throws IOException, HttpException {
        verifytRoute();
        if (!isRouteEstablished()) {
            onRouteToTarget();
            onRouteComplete();
        }
        NHttpClientConnection localConn = getConnection();
        this.localContext.setAttribute("http.connection", localConn);
        Asserts.check(this.requestProducerRef.get() == null, "Inconsistent state: currentRequest producer is not null");
        HttpAsyncRequestProducer requestProducer = (HttpAsyncRequestProducer) this.requestProducerQueue.poll();
        if (requestProducer == null) {
            return null;
        }
        this.requestProducerRef.set(requestProducer);
        HttpRequest currentRequest = HttpRequestWrapper.wrap(requestProducer.generateRequest());
        RequestConfig config = this.localContext.getRequestConfig();
        if (config.getSocketTimeout() > 0) {
            localConn.setSocketTimeout(config.getSocketTimeout());
        }
        this.httpProcessor.process(currentRequest, this.localContext);
        this.requestQueue.add(currentRequest);
        setCurrentRequest(currentRequest);
        return currentRequest;
    }

    public void produceContent(ContentEncoder encoder, IOControl ioctrl) throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + getId() + "] produce content");
        }
        HttpAsyncRequestProducer requestProducer = (HttpAsyncRequestProducer) this.requestProducerRef.get();
        Asserts.check(requestProducer != null, "Inconsistent state: request producer is null");
        requestProducer.produceContent(encoder, ioctrl);
        if (encoder.isCompleted()) {
            requestProducer.resetRequest();
        }
    }

    public void requestCompleted() {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + getId() + "] Request completed");
        }
        HttpAsyncRequestProducer requestProducer = (HttpAsyncRequestProducer) this.requestProducerRef.getAndSet(null);
        Asserts.check(requestProducer != null, "Inconsistent state: request producer is null");
        requestProducer.requestCompleted(this.localContext);
        try {
            requestProducer.close();
        } catch (IOException ioex) {
            this.log.debug(ioex.getMessage(), ioex);
        }
    }

    public void responseReceived(HttpResponse response) throws IOException, HttpException {
        boolean z;
        boolean z2 = true;
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + getId() + "] Response received " + response.getStatusLine());
        }
        if (this.responseConsumerRef.get() == null) {
            z = true;
        } else {
            z = false;
        }
        Asserts.check(z, "Inconsistent state: response consumer is not null");
        HttpAsyncResponseConsumer<T> responseConsumer = (HttpAsyncResponseConsumer) this.responseConsumerQueue.poll();
        if (responseConsumer != null) {
            z = true;
        } else {
            z = false;
        }
        Asserts.check(z, "Inconsistent state: response consumer queue is empty");
        this.responseConsumerRef.set(responseConsumer);
        HttpRequest request = (HttpRequest) this.requestQueue.poll();
        if (request == null) {
            z2 = false;
        }
        Asserts.check(z2, "Inconsistent state: request queue is empty");
        this.localContext.setAttribute("http.request", request);
        this.localContext.setAttribute("http.response", response);
        this.httpProcessor.process(response, this.localContext);
        responseConsumer.responseReceived(response);
        setCurrentResponse(response);
    }

    public void consumeContent(ContentDecoder decoder, IOControl ioctrl) throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + getId() + "] Consume content");
        }
        HttpAsyncResponseConsumer<T> responseConsumer = (HttpAsyncResponseConsumer) this.responseConsumerRef.get();
        Asserts.check(responseConsumer != null, "Inconsistent state: response consumer is null");
        responseConsumer.consumeContent(decoder, ioctrl);
    }

    public void responseCompleted() throws IOException, HttpException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + getId() + "] Response processed");
        }
        boolean keepAlive = manageConnectionPersistence();
        HttpAsyncResponseConsumer<T> responseConsumer = (HttpAsyncResponseConsumer) this.responseConsumerRef.getAndSet(null);
        Asserts.check(responseConsumer != null, "Inconsistent state: response consumer is null");
        Exception ex;
        try {
            responseConsumer.responseCompleted(this.localContext);
            T result = responseConsumer.getResult();
            ex = responseConsumer.getException();
            try {
                responseConsumer.close();
            } catch (IOException ioex) {
                this.log.debug(ioex.getMessage(), ioex);
            }
            if (result != null) {
                this.resultQueue.add(result);
            } else {
                failed(ex);
            }
            if (!this.resultFuture.isDone() && this.responseConsumerQueue.isEmpty()) {
                this.resultFuture.completed(new ArrayList(this.resultQueue));
                this.resultQueue.clear();
            }
            if (this.resultFuture.isDone()) {
                close();
            } else if (keepAlive) {
                NHttpClientConnection localConn = getConnection();
                if (localConn != null) {
                    localConn.requestOutput();
                } else {
                    requestConnection();
                }
            } else {
                failed(new ConnectionClosedException("Connection closed"));
            }
        } catch (Exception ex2) {
            failed(ex2);
            throw ex2;
        }
    }

    public void inputTerminated() {
        failed(new ConnectionClosedException("Connection closed"));
    }

    public void abortConnection() {
        discardConnection();
    }
}
