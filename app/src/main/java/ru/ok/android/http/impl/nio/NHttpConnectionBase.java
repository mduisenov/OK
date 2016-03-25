package ru.ok.android.http.impl.nio;

import android.support.v4.app.FragmentTransaction;
import com.google.android.gms.ads.AdRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import ru.ok.android.http.ConnectionClosedException;
import ru.ok.android.http.Consts;
import ru.ok.android.http.Header;
import ru.ok.android.http.HttpConnectionMetrics;
import ru.ok.android.http.HttpEntity;
import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpInetConnection;
import ru.ok.android.http.HttpMessage;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.config.MessageConstraints;
import ru.ok.android.http.entity.BasicHttpEntity;
import ru.ok.android.http.entity.ContentLengthStrategy;
import ru.ok.android.http.impl.HttpConnectionMetricsImpl;
import ru.ok.android.http.impl.entity.LaxContentLengthStrategy;
import ru.ok.android.http.impl.entity.StrictContentLengthStrategy;
import ru.ok.android.http.impl.io.HttpTransportMetricsImpl;
import ru.ok.android.http.impl.nio.codecs.ChunkDecoder;
import ru.ok.android.http.impl.nio.codecs.ChunkEncoder;
import ru.ok.android.http.impl.nio.codecs.IdentityDecoder;
import ru.ok.android.http.impl.nio.codecs.IdentityEncoder;
import ru.ok.android.http.impl.nio.codecs.LengthDelimitedDecoder;
import ru.ok.android.http.impl.nio.codecs.LengthDelimitedEncoder;
import ru.ok.android.http.impl.nio.reactor.SessionInputBufferImpl;
import ru.ok.android.http.impl.nio.reactor.SessionOutputBufferImpl;
import ru.ok.android.http.io.HttpTransportMetrics;
import ru.ok.android.http.nio.ContentDecoder;
import ru.ok.android.http.nio.ContentEncoder;
import ru.ok.android.http.nio.NHttpConnection;
import ru.ok.android.http.nio.reactor.IOSession;
import ru.ok.android.http.nio.reactor.SessionBufferStatus;
import ru.ok.android.http.nio.reactor.SessionInputBuffer;
import ru.ok.android.http.nio.reactor.SessionOutputBuffer;
import ru.ok.android.http.nio.reactor.SocketAccessor;
import ru.ok.android.http.nio.util.ByteBufferAllocator;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.protocol.HttpContext;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharsetUtils;
import ru.ok.android.http.util.NetUtils;

public class NHttpConnectionBase implements HttpInetConnection, NHttpConnection, SessionBufferStatus, SocketAccessor {
    protected final HttpConnectionMetricsImpl connMetrics;
    private final MessageConstraints constraints;
    protected volatile ContentDecoder contentDecoder;
    protected volatile ContentEncoder contentEncoder;
    protected HttpContext context;
    private final int fragmentSizeHint;
    protected volatile boolean hasBufferedInput;
    protected volatile boolean hasBufferedOutput;
    protected final HttpTransportMetricsImpl inTransportMetrics;
    protected final SessionInputBufferImpl inbuf;
    protected final ContentLengthStrategy incomingContentStrategy;
    protected final HttpTransportMetricsImpl outTransportMetrics;
    protected final SessionOutputBufferImpl outbuf;
    protected final ContentLengthStrategy outgoingContentStrategy;
    protected SocketAddress remote;
    protected volatile HttpRequest request;
    protected volatile HttpResponse response;
    protected IOSession session;
    protected volatile int status;

    @Deprecated
    public NHttpConnectionBase(IOSession session, ByteBufferAllocator allocator, HttpParams params) {
        Args.notNull(session, "I/O session");
        Args.notNull(params, "HTTP params");
        int buffersize = params.getIntParameter("http.socket.buffer-size", -1);
        if (buffersize <= 0) {
            buffersize = FragmentTransaction.TRANSIT_ENTER_MASK;
        }
        int linebuffersize = buffersize;
        if (linebuffersize > AdRequest.MAX_CONTENT_URL_LENGTH) {
            linebuffersize = AdRequest.MAX_CONTENT_URL_LENGTH;
        }
        CharsetDecoder decoder = null;
        CharsetEncoder encoder = null;
        if (CharsetUtils.lookup((String) params.getParameter("http.protocol.element-charset")) != null) {
            Charset charset = Consts.ASCII;
            decoder = charset.newDecoder();
            encoder = charset.newEncoder();
            CodingErrorAction malformedCharAction = (CodingErrorAction) params.getParameter("http.malformed.input.action");
            CodingErrorAction unmappableCharAction = (CodingErrorAction) params.getParameter("http.unmappable.input.action");
            decoder.onMalformedInput(malformedCharAction).onUnmappableCharacter(unmappableCharAction);
            encoder.onMalformedInput(malformedCharAction).onUnmappableCharacter(unmappableCharAction);
        }
        this.inbuf = new SessionInputBufferImpl(buffersize, linebuffersize, decoder, allocator);
        this.outbuf = new SessionOutputBufferImpl(buffersize, linebuffersize, encoder, allocator);
        this.fragmentSizeHint = buffersize;
        this.constraints = MessageConstraints.DEFAULT;
        this.incomingContentStrategy = createIncomingContentStrategy();
        this.outgoingContentStrategy = createOutgoingContentStrategy();
        this.inTransportMetrics = createTransportMetrics();
        this.outTransportMetrics = createTransportMetrics();
        this.connMetrics = createConnectionMetrics(this.inTransportMetrics, this.outTransportMetrics);
        setSession(session);
        this.status = 0;
    }

    protected NHttpConnectionBase(IOSession session, int buffersize, int fragmentSizeHint, ByteBufferAllocator allocator, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy) {
        Args.notNull(session, "I/O session");
        Args.positive(buffersize, "Buffer size");
        int linebuffersize = buffersize;
        if (linebuffersize > AdRequest.MAX_CONTENT_URL_LENGTH) {
            linebuffersize = AdRequest.MAX_CONTENT_URL_LENGTH;
        }
        this.inbuf = new SessionInputBufferImpl(buffersize, linebuffersize, chardecoder, allocator);
        this.outbuf = new SessionOutputBufferImpl(buffersize, linebuffersize, charencoder, allocator);
        if (fragmentSizeHint < 0) {
            fragmentSizeHint = buffersize;
        }
        this.fragmentSizeHint = fragmentSizeHint;
        this.inTransportMetrics = new HttpTransportMetricsImpl();
        this.outTransportMetrics = new HttpTransportMetricsImpl();
        this.connMetrics = new HttpConnectionMetricsImpl(this.inTransportMetrics, this.outTransportMetrics);
        if (constraints == null) {
            constraints = MessageConstraints.DEFAULT;
        }
        this.constraints = constraints;
        if (incomingContentStrategy == null) {
            incomingContentStrategy = LaxContentLengthStrategy.INSTANCE;
        }
        this.incomingContentStrategy = incomingContentStrategy;
        if (outgoingContentStrategy == null) {
            outgoingContentStrategy = StrictContentLengthStrategy.INSTANCE;
        }
        this.outgoingContentStrategy = outgoingContentStrategy;
        setSession(session);
        this.status = 0;
    }

    protected NHttpConnectionBase(IOSession session, int buffersize, int fragmentSizeHint, ByteBufferAllocator allocator, CharsetDecoder chardecoder, CharsetEncoder charencoder, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy) {
        this(session, buffersize, fragmentSizeHint, allocator, chardecoder, charencoder, null, incomingContentStrategy, outgoingContentStrategy);
    }

    private void setSession(IOSession session) {
        this.session = session;
        this.context = new SessionHttpContext(this.session);
        this.session.setBufferStatus(this);
        this.remote = this.session.getRemoteAddress();
    }

    protected void bind(IOSession session) {
        Args.notNull(session, "I/O session");
        setSession(session);
    }

    @Deprecated
    protected ContentLengthStrategy createIncomingContentStrategy() {
        return new LaxContentLengthStrategy();
    }

    @Deprecated
    protected ContentLengthStrategy createOutgoingContentStrategy() {
        return new StrictContentLengthStrategy();
    }

    @Deprecated
    protected HttpTransportMetricsImpl createTransportMetrics() {
        return new HttpTransportMetricsImpl();
    }

    @Deprecated
    protected HttpConnectionMetricsImpl createConnectionMetrics(HttpTransportMetrics inTransportMetric, HttpTransportMetrics outTransportMetric) {
        return new HttpConnectionMetricsImpl(inTransportMetric, outTransportMetric);
    }

    public int getStatus() {
        return this.status;
    }

    public HttpContext getContext() {
        return this.context;
    }

    public HttpRequest getHttpRequest() {
        return this.request;
    }

    public HttpResponse getHttpResponse() {
        return this.response;
    }

    public void requestInput() {
        this.session.setEvent(1);
    }

    public void requestOutput() {
        this.session.setEvent(4);
    }

    public void suspendInput() {
        this.session.clearEvent(1);
    }

    public void suspendOutput() {
        this.session.clearEvent(4);
    }

    protected HttpEntity prepareDecoder(HttpMessage message) throws HttpException {
        BasicHttpEntity entity = new BasicHttpEntity();
        long len = this.incomingContentStrategy.determineLength(message);
        this.contentDecoder = createContentDecoder(len, this.session.channel(), this.inbuf, this.inTransportMetrics);
        if (len == -2) {
            entity.setChunked(true);
            entity.setContentLength(-1);
        } else if (len == -1) {
            entity.setChunked(false);
            entity.setContentLength(-1);
        } else {
            entity.setChunked(false);
            entity.setContentLength(len);
        }
        Header contentTypeHeader = message.getFirstHeader("Content-Type");
        if (contentTypeHeader != null) {
            entity.setContentType(contentTypeHeader);
        }
        Header contentEncodingHeader = message.getFirstHeader("Content-Encoding");
        if (contentEncodingHeader != null) {
            entity.setContentEncoding(contentEncodingHeader);
        }
        return entity;
    }

    protected ContentDecoder createContentDecoder(long len, ReadableByteChannel channel, SessionInputBuffer buffer, HttpTransportMetricsImpl metrics) {
        if (len == -2) {
            return new ChunkDecoder(channel, buffer, this.constraints, metrics);
        }
        if (len == -1) {
            return new IdentityDecoder(channel, buffer, metrics);
        }
        return new LengthDelimitedDecoder(channel, buffer, metrics, len);
    }

    protected void prepareEncoder(HttpMessage message) throws HttpException {
        this.contentEncoder = createContentEncoder(this.outgoingContentStrategy.determineLength(message), this.session.channel(), this.outbuf, this.outTransportMetrics);
    }

    protected ContentEncoder createContentEncoder(long len, WritableByteChannel channel, SessionOutputBuffer buffer, HttpTransportMetricsImpl metrics) {
        if (len == -2) {
            return new ChunkEncoder(channel, buffer, metrics, this.fragmentSizeHint);
        }
        if (len == -1) {
            return new IdentityEncoder(channel, buffer, metrics, this.fragmentSizeHint);
        }
        return new LengthDelimitedEncoder(channel, buffer, metrics, len, this.fragmentSizeHint);
    }

    public boolean hasBufferedInput() {
        return this.hasBufferedInput;
    }

    public boolean hasBufferedOutput() {
        return this.hasBufferedOutput;
    }

    protected void assertNotClosed() throws ConnectionClosedException {
        if (this.status != 0) {
            throw new ConnectionClosedException("Connection is closed");
        }
    }

    public void close() throws IOException {
        if (this.status == 0) {
            this.status = 1;
            if (this.outbuf.hasData()) {
                this.session.setEvent(4);
                return;
            }
            this.session.close();
            this.status = 2;
        }
    }

    public boolean isOpen() {
        return this.status == 0 && !this.session.isClosed();
    }

    public boolean isStale() {
        return this.session.isClosed();
    }

    public InetAddress getLocalAddress() {
        SocketAddress address = this.session.getLocalAddress();
        if (address instanceof InetSocketAddress) {
            return ((InetSocketAddress) address).getAddress();
        }
        return null;
    }

    public int getLocalPort() {
        SocketAddress address = this.session.getLocalAddress();
        if (address instanceof InetSocketAddress) {
            return ((InetSocketAddress) address).getPort();
        }
        return -1;
    }

    public InetAddress getRemoteAddress() {
        SocketAddress address = this.session.getRemoteAddress();
        if (address instanceof InetSocketAddress) {
            return ((InetSocketAddress) address).getAddress();
        }
        return null;
    }

    public int getRemotePort() {
        SocketAddress address = this.session.getRemoteAddress();
        if (address instanceof InetSocketAddress) {
            return ((InetSocketAddress) address).getPort();
        }
        return -1;
    }

    public void setSocketTimeout(int timeout) {
        this.session.setSocketTimeout(timeout);
    }

    public int getSocketTimeout() {
        return this.session.getSocketTimeout();
    }

    public void shutdown() throws IOException {
        this.status = 2;
        this.session.shutdown();
    }

    public HttpConnectionMetrics getMetrics() {
        return this.connMetrics;
    }

    public String toString() {
        SocketAddress remoteAddress = this.session.getRemoteAddress();
        SocketAddress localAddress = this.session.getLocalAddress();
        if (remoteAddress == null || localAddress == null) {
            return "[Not bound]";
        }
        StringBuilder buffer = new StringBuilder();
        NetUtils.formatAddress(buffer, localAddress);
        buffer.append("<->");
        NetUtils.formatAddress(buffer, remoteAddress);
        return buffer.toString();
    }

    public Socket getSocket() {
        if (this.session instanceof SocketAccessor) {
            return ((SocketAccessor) this.session).getSocket();
        }
        return null;
    }
}
