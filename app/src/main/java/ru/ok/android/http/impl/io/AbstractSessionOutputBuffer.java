package ru.ok.android.http.impl.io;

import com.google.android.gms.ads.AdRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import ru.ok.android.http.Consts;
import ru.ok.android.http.io.BufferInfo;
import ru.ok.android.http.io.HttpTransportMetrics;
import ru.ok.android.http.io.SessionOutputBuffer;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.ByteArrayBuffer;
import ru.ok.android.http.util.CharArrayBuffer;

@Deprecated
public abstract class AbstractSessionOutputBuffer implements BufferInfo, SessionOutputBuffer {
    private static final byte[] CRLF;
    private boolean ascii;
    private ByteBuffer bbuf;
    private ByteArrayBuffer buffer;
    private Charset charset;
    private CharsetEncoder encoder;
    private HttpTransportMetricsImpl metrics;
    private int minChunkLimit;
    private CodingErrorAction onMalformedCharAction;
    private CodingErrorAction onUnmappableCharAction;
    private OutputStream outstream;

    static {
        CRLF = new byte[]{(byte) 13, (byte) 10};
    }

    protected AbstractSessionOutputBuffer(OutputStream outstream, int buffersize, Charset charset, int minChunkLimit, CodingErrorAction malformedCharAction, CodingErrorAction unmappableCharAction) {
        Args.notNull(outstream, "Input stream");
        Args.notNegative(buffersize, "Buffer size");
        this.outstream = outstream;
        this.buffer = new ByteArrayBuffer(buffersize);
        if (charset == null) {
            charset = Consts.ASCII;
        }
        this.charset = charset;
        this.ascii = this.charset.equals(Consts.ASCII);
        this.encoder = null;
        if (minChunkLimit < 0) {
            minChunkLimit = AdRequest.MAX_CONTENT_URL_LENGTH;
        }
        this.minChunkLimit = minChunkLimit;
        this.metrics = createTransportMetrics();
        if (malformedCharAction == null) {
            malformedCharAction = CodingErrorAction.REPORT;
        }
        this.onMalformedCharAction = malformedCharAction;
        if (unmappableCharAction == null) {
            unmappableCharAction = CodingErrorAction.REPORT;
        }
        this.onUnmappableCharAction = unmappableCharAction;
    }

    protected void init(OutputStream outstream, int buffersize, HttpParams params) {
        Args.notNull(outstream, "Input stream");
        Args.notNegative(buffersize, "Buffer size");
        Args.notNull(params, "HTTP parameters");
        this.outstream = outstream;
        this.buffer = new ByteArrayBuffer(buffersize);
        String charset = (String) params.getParameter("http.protocol.element-charset");
        this.charset = charset != null ? Charset.forName(charset) : Consts.ASCII;
        this.ascii = this.charset.equals(Consts.ASCII);
        this.encoder = null;
        this.minChunkLimit = params.getIntParameter("http.connection.min-chunk-limit", AdRequest.MAX_CONTENT_URL_LENGTH);
        this.metrics = createTransportMetrics();
        CodingErrorAction a1 = (CodingErrorAction) params.getParameter("http.malformed.input.action");
        if (a1 == null) {
            a1 = CodingErrorAction.REPORT;
        }
        this.onMalformedCharAction = a1;
        CodingErrorAction a2 = (CodingErrorAction) params.getParameter("http.unmappable.input.action");
        if (a2 == null) {
            a2 = CodingErrorAction.REPORT;
        }
        this.onUnmappableCharAction = a2;
    }

    protected HttpTransportMetricsImpl createTransportMetrics() {
        return new HttpTransportMetricsImpl();
    }

    public int capacity() {
        return this.buffer.capacity();
    }

    public int length() {
        return this.buffer.length();
    }

    public int available() {
        return capacity() - length();
    }

    protected void flushBuffer() throws IOException {
        int len = this.buffer.length();
        if (len > 0) {
            this.outstream.write(this.buffer.buffer(), 0, len);
            this.buffer.clear();
            this.metrics.incrementBytesTransferred((long) len);
        }
    }

    public void flush() throws IOException {
        flushBuffer();
        this.outstream.flush();
    }

    public void write(byte[] b, int off, int len) throws IOException {
        if (b != null) {
            if (len > this.minChunkLimit || len > this.buffer.capacity()) {
                flushBuffer();
                this.outstream.write(b, off, len);
                this.metrics.incrementBytesTransferred((long) len);
                return;
            }
            if (len > this.buffer.capacity() - this.buffer.length()) {
                flushBuffer();
            }
            this.buffer.append(b, off, len);
        }
    }

    public void write(byte[] b) throws IOException {
        if (b != null) {
            write(b, 0, b.length);
        }
    }

    public void write(int b) throws IOException {
        if (this.buffer.isFull()) {
            flushBuffer();
        }
        this.buffer.append(b);
    }

    public void writeLine(String s) throws IOException {
        if (s != null) {
            if (s.length() > 0) {
                if (this.ascii) {
                    for (int i = 0; i < s.length(); i++) {
                        write(s.charAt(i));
                    }
                } else {
                    writeEncoded(CharBuffer.wrap(s));
                }
            }
            write(CRLF);
        }
    }

    public void writeLine(CharArrayBuffer charbuffer) throws IOException {
        if (charbuffer != null) {
            if (this.ascii) {
                int off = 0;
                int remaining = charbuffer.length();
                while (remaining > 0) {
                    int chunk = Math.min(this.buffer.capacity() - this.buffer.length(), remaining);
                    if (chunk > 0) {
                        this.buffer.append(charbuffer, off, chunk);
                    }
                    if (this.buffer.isFull()) {
                        flushBuffer();
                    }
                    off += chunk;
                    remaining -= chunk;
                }
            } else {
                writeEncoded(CharBuffer.wrap(charbuffer.buffer(), 0, charbuffer.length()));
            }
            write(CRLF);
        }
    }

    private void writeEncoded(CharBuffer cbuf) throws IOException {
        if (cbuf.hasRemaining()) {
            if (this.encoder == null) {
                this.encoder = this.charset.newEncoder();
                this.encoder.onMalformedInput(this.onMalformedCharAction);
                this.encoder.onUnmappableCharacter(this.onUnmappableCharAction);
            }
            if (this.bbuf == null) {
                this.bbuf = ByteBuffer.allocate(1024);
            }
            this.encoder.reset();
            while (cbuf.hasRemaining()) {
                handleEncodingResult(this.encoder.encode(cbuf, this.bbuf, true));
            }
            handleEncodingResult(this.encoder.flush(this.bbuf));
            this.bbuf.clear();
        }
    }

    private void handleEncodingResult(CoderResult result) throws IOException {
        if (result.isError()) {
            result.throwException();
        }
        this.bbuf.flip();
        while (this.bbuf.hasRemaining()) {
            write(this.bbuf.get());
        }
        this.bbuf.compact();
    }

    public HttpTransportMetrics getMetrics() {
        return this.metrics;
    }
}
