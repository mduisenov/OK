package ru.ok.android.http.impl.nio.reactor;

import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MotionEventCompat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import ru.ok.android.http.MessageConstraintException;
import ru.ok.android.http.config.MessageConstraints;
import ru.ok.android.http.nio.reactor.SessionInputBuffer;
import ru.ok.android.http.nio.util.ByteBufferAllocator;
import ru.ok.android.http.nio.util.ExpandableBuffer;
import ru.ok.android.http.nio.util.HeapByteBufferAllocator;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharArrayBuffer;
import ru.ok.android.http.util.CharsetUtils;

public class SessionInputBufferImpl extends ExpandableBuffer implements SessionInputBuffer {
    private CharBuffer charbuffer;
    private final CharsetDecoder chardecoder;
    private final MessageConstraints constraints;
    private final int lineBuffersize;

    public SessionInputBufferImpl(int buffersize, int lineBuffersize, MessageConstraints constraints, CharsetDecoder chardecoder, ByteBufferAllocator allocator) {
        if (allocator == null) {
            allocator = HeapByteBufferAllocator.INSTANCE;
        }
        super(buffersize, allocator);
        this.lineBuffersize = Args.positive(lineBuffersize, "Line buffer size");
        if (constraints == null) {
            constraints = MessageConstraints.DEFAULT;
        }
        this.constraints = constraints;
        this.chardecoder = chardecoder;
    }

    public SessionInputBufferImpl(int buffersize, int lineBuffersize, CharsetDecoder chardecoder, ByteBufferAllocator allocator) {
        this(buffersize, lineBuffersize, null, chardecoder, allocator);
    }

    @Deprecated
    public SessionInputBufferImpl(int buffersize, int lineBuffersize, ByteBufferAllocator allocator, HttpParams params) {
        super(buffersize, allocator);
        this.lineBuffersize = Args.positive(lineBuffersize, "Line buffer size");
        Charset charset = CharsetUtils.lookup((String) params.getParameter("http.protocol.element-charset"));
        if (charset != null) {
            this.chardecoder = charset.newDecoder();
            CodingErrorAction a1 = (CodingErrorAction) params.getParameter("http.malformed.input.action");
            CharsetDecoder charsetDecoder = this.chardecoder;
            if (a1 == null) {
                a1 = CodingErrorAction.REPORT;
            }
            charsetDecoder.onMalformedInput(a1);
            CodingErrorAction a2 = (CodingErrorAction) params.getParameter("http.unmappable.input.action");
            charsetDecoder = this.chardecoder;
            if (a2 == null) {
                a2 = CodingErrorAction.REPORT;
            }
            charsetDecoder.onUnmappableCharacter(a2);
        } else {
            this.chardecoder = null;
        }
        this.constraints = MessageConstraints.DEFAULT;
    }

    @Deprecated
    public SessionInputBufferImpl(int buffersize, int linebuffersize, HttpParams params) {
        this(buffersize, linebuffersize, HeapByteBufferAllocator.INSTANCE, params);
    }

    public SessionInputBufferImpl(int buffersize, int lineBuffersize, Charset charset) {
        CharsetDecoder newDecoder;
        if (charset != null) {
            newDecoder = charset.newDecoder();
        } else {
            newDecoder = null;
        }
        this(buffersize, lineBuffersize, null, newDecoder, HeapByteBufferAllocator.INSTANCE);
    }

    public SessionInputBufferImpl(int buffersize, int lineBuffersize, MessageConstraints constraints, Charset charset) {
        this(buffersize, lineBuffersize, constraints, charset != null ? charset.newDecoder() : null, HeapByteBufferAllocator.INSTANCE);
    }

    public SessionInputBufferImpl(int buffersize, int lineBuffersize) {
        this(buffersize, lineBuffersize, null, null, HeapByteBufferAllocator.INSTANCE);
    }

    public SessionInputBufferImpl(int buffersize) {
        this(buffersize, NotificationCompat.FLAG_LOCAL_ONLY, null, null, HeapByteBufferAllocator.INSTANCE);
    }

    public int fill(ReadableByteChannel channel) throws IOException {
        Args.notNull(channel, "Channel");
        setInputMode();
        if (!this.buffer.hasRemaining()) {
            expand();
        }
        return channel.read(this.buffer);
    }

    public int read() {
        setOutputMode();
        return this.buffer.get() & MotionEventCompat.ACTION_MASK;
    }

    public int read(ByteBuffer dst, int maxLen) {
        if (dst == null) {
            return 0;
        }
        setOutputMode();
        int len = Math.min(dst.remaining(), maxLen);
        int chunk = Math.min(this.buffer.remaining(), len);
        if (this.buffer.remaining() > chunk) {
            int oldLimit = this.buffer.limit();
            this.buffer.limit(this.buffer.position() + chunk);
            dst.put(this.buffer);
            this.buffer.limit(oldLimit);
            return len;
        }
        dst.put(this.buffer);
        return chunk;
    }

    public int read(ByteBuffer dst) {
        if (dst == null) {
            return 0;
        }
        return read(dst, dst.remaining());
    }

    public int read(WritableByteChannel dst, int maxLen) throws IOException {
        if (dst == null) {
            return 0;
        }
        setOutputMode();
        if (this.buffer.remaining() <= maxLen) {
            return dst.write(this.buffer);
        }
        int oldLimit = this.buffer.limit();
        this.buffer.limit(oldLimit - (this.buffer.remaining() - maxLen));
        int bytesRead = dst.write(this.buffer);
        this.buffer.limit(oldLimit);
        return bytesRead;
    }

    public int read(WritableByteChannel dst) throws IOException {
        if (dst == null) {
            return 0;
        }
        setOutputMode();
        return dst.write(this.buffer);
    }

    public boolean readLine(CharArrayBuffer linebuffer, boolean endOfStream) throws CharacterCodingException {
        setOutputMode();
        int pos = -1;
        for (int i = this.buffer.position(); i < this.buffer.limit(); i++) {
            if (this.buffer.get(i) == 10) {
                pos = i + 1;
                break;
            }
        }
        int maxLineLen = this.constraints.getMaxLineLength();
        if (maxLineLen > 0) {
            if ((pos > 0 ? pos : this.buffer.limit()) - this.buffer.position() >= maxLineLen) {
                throw new MessageConstraintException("Maximum line length limit exceeded");
            }
        }
        if (pos == -1) {
            if (!endOfStream || !this.buffer.hasRemaining()) {
                return false;
            }
            pos = this.buffer.limit();
        }
        int origLimit = this.buffer.limit();
        this.buffer.limit(pos);
        linebuffer.ensureCapacity(this.buffer.limit() - this.buffer.position());
        if (this.chardecoder != null) {
            if (this.charbuffer == null) {
                this.charbuffer = CharBuffer.allocate(this.lineBuffersize);
            }
            this.chardecoder.reset();
            CoderResult result;
            do {
                result = this.chardecoder.decode(this.buffer, this.charbuffer, true);
                if (result.isError()) {
                    result.throwException();
                }
                if (result.isOverflow()) {
                    this.charbuffer.flip();
                    linebuffer.append(this.charbuffer.array(), this.charbuffer.position(), this.charbuffer.remaining());
                    this.charbuffer.clear();
                }
            } while (!result.isUnderflow());
            this.chardecoder.flush(this.charbuffer);
            this.charbuffer.flip();
            if (this.charbuffer.hasRemaining()) {
                linebuffer.append(this.charbuffer.array(), this.charbuffer.position(), this.charbuffer.remaining());
            }
        } else if (this.buffer.hasArray()) {
            byte[] b = this.buffer.array();
            int off = this.buffer.position();
            int len = this.buffer.remaining();
            linebuffer.append(b, off, len);
            this.buffer.position(off + len);
        } else {
            while (this.buffer.hasRemaining()) {
                linebuffer.append((char) (this.buffer.get() & MotionEventCompat.ACTION_MASK));
            }
        }
        this.buffer.limit(origLimit);
        int l = linebuffer.length();
        if (l > 0) {
            if (linebuffer.charAt(l - 1) == '\n') {
                l--;
                linebuffer.setLength(l);
            }
            if (l > 0) {
                if (linebuffer.charAt(l - 1) == '\r') {
                    linebuffer.setLength(l - 1);
                }
            }
        }
        return true;
    }

    public String readLine(boolean endOfStream) throws CharacterCodingException {
        CharArrayBuffer buffer = new CharArrayBuffer(64);
        if (readLine(buffer, endOfStream)) {
            return buffer.toString();
        }
        return null;
    }
}
