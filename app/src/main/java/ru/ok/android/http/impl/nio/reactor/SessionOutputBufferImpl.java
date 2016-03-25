package ru.ok.android.http.impl.nio.reactor;

import android.support.v4.app.NotificationCompat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import ru.ok.android.http.nio.reactor.SessionOutputBuffer;
import ru.ok.android.http.nio.util.ByteBufferAllocator;
import ru.ok.android.http.nio.util.ExpandableBuffer;
import ru.ok.android.http.nio.util.HeapByteBufferAllocator;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharArrayBuffer;
import ru.ok.android.http.util.CharsetUtils;

public class SessionOutputBufferImpl extends ExpandableBuffer implements SessionOutputBuffer {
    private static final byte[] CRLF;
    private CharBuffer charbuffer;
    private final CharsetEncoder charencoder;
    private final int lineBuffersize;

    static {
        CRLF = new byte[]{(byte) 13, (byte) 10};
    }

    public SessionOutputBufferImpl(int buffersize, int lineBuffersize, CharsetEncoder charencoder, ByteBufferAllocator allocator) {
        if (allocator == null) {
            allocator = HeapByteBufferAllocator.INSTANCE;
        }
        super(buffersize, allocator);
        this.lineBuffersize = Args.positive(lineBuffersize, "Line buffer size");
        this.charencoder = charencoder;
    }

    @Deprecated
    public SessionOutputBufferImpl(int buffersize, int lineBuffersize, ByteBufferAllocator allocator, HttpParams params) {
        super(buffersize, allocator);
        this.lineBuffersize = Args.positive(lineBuffersize, "Line buffer size");
        Charset charset = CharsetUtils.lookup((String) params.getParameter("http.protocol.element-charset"));
        if (charset != null) {
            this.charencoder = charset.newEncoder();
            CodingErrorAction a1 = (CodingErrorAction) params.getParameter("http.malformed.input.action");
            CharsetEncoder charsetEncoder = this.charencoder;
            if (a1 == null) {
                a1 = CodingErrorAction.REPORT;
            }
            charsetEncoder.onMalformedInput(a1);
            CodingErrorAction a2 = (CodingErrorAction) params.getParameter("http.unmappable.input.action");
            charsetEncoder = this.charencoder;
            if (a2 == null) {
                a2 = CodingErrorAction.REPORT;
            }
            charsetEncoder.onUnmappableCharacter(a2);
            return;
        }
        this.charencoder = null;
    }

    @Deprecated
    public SessionOutputBufferImpl(int buffersize, int linebuffersize, HttpParams params) {
        this(buffersize, linebuffersize, HeapByteBufferAllocator.INSTANCE, params);
    }

    public SessionOutputBufferImpl(int buffersize) {
        this(buffersize, (int) NotificationCompat.FLAG_LOCAL_ONLY, null, HeapByteBufferAllocator.INSTANCE);
    }

    public SessionOutputBufferImpl(int buffersize, int linebuffersize, Charset charset) {
        this(buffersize, linebuffersize, charset != null ? charset.newEncoder() : null, HeapByteBufferAllocator.INSTANCE);
    }

    public SessionOutputBufferImpl(int buffersize, int linebuffersize) {
        this(buffersize, linebuffersize, null, HeapByteBufferAllocator.INSTANCE);
    }

    public void reset(HttpParams params) {
        clear();
    }

    public int flush(WritableByteChannel channel) throws IOException {
        Args.notNull(channel, "Channel");
        setOutputMode();
        return channel.write(this.buffer);
    }

    public void write(ByteBuffer src) {
        if (src != null) {
            setInputMode();
            ensureCapacity(this.buffer.position() + src.remaining());
            this.buffer.put(src);
        }
    }

    public void write(ReadableByteChannel src) throws IOException {
        if (src != null) {
            setInputMode();
            src.read(this.buffer);
        }
    }

    private void write(byte[] b) {
        if (b != null) {
            setInputMode();
            int len = b.length;
            ensureCapacity(this.buffer.position() + len);
            this.buffer.put(b, 0, len);
        }
    }

    private void writeCRLF() {
        write(CRLF);
    }

    public void writeLine(CharArrayBuffer linebuffer) throws CharacterCodingException {
        if (linebuffer != null) {
            setInputMode();
            if (linebuffer.length() > 0) {
                if (this.charencoder == null) {
                    ensureCapacity(this.buffer.position() + linebuffer.length());
                    int i;
                    if (this.buffer.hasArray()) {
                        byte[] b = this.buffer.array();
                        int len = linebuffer.length();
                        int off = this.buffer.position();
                        for (i = 0; i < len; i++) {
                            b[off + i] = (byte) linebuffer.charAt(i);
                        }
                        this.buffer.position(off + len);
                    } else {
                        for (i = 0; i < linebuffer.length(); i++) {
                            this.buffer.put((byte) linebuffer.charAt(i));
                        }
                    }
                } else {
                    boolean retry;
                    CoderResult result;
                    if (this.charbuffer == null) {
                        this.charbuffer = CharBuffer.allocate(this.lineBuffersize);
                    }
                    this.charencoder.reset();
                    int remaining = linebuffer.length();
                    int offset = 0;
                    while (remaining > 0) {
                        int l = this.charbuffer.remaining();
                        boolean eol = false;
                        if (remaining <= l) {
                            l = remaining;
                            eol = true;
                        }
                        this.charbuffer.put(linebuffer.buffer(), offset, l);
                        this.charbuffer.flip();
                        retry = true;
                        while (retry) {
                            result = this.charencoder.encode(this.charbuffer, this.buffer, eol);
                            if (result.isError()) {
                                result.throwException();
                            }
                            if (result.isOverflow()) {
                                expand();
                            }
                            retry = !result.isUnderflow();
                        }
                        this.charbuffer.compact();
                        offset += l;
                        remaining -= l;
                    }
                    retry = true;
                    while (retry) {
                        result = this.charencoder.flush(this.buffer);
                        if (result.isError()) {
                            result.throwException();
                        }
                        if (result.isOverflow()) {
                            expand();
                        }
                        retry = !result.isUnderflow();
                    }
                }
            }
            writeCRLF();
        }
    }

    public void writeLine(String s) throws IOException {
        if (s != null) {
            if (s.length() > 0) {
                CharArrayBuffer tmp = new CharArrayBuffer(s.length());
                tmp.append(s);
                writeLine(tmp);
                return;
            }
            write(CRLF);
        }
    }
}
