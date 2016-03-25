package ru.ok.android.http.nio.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import ru.ok.android.http.entity.AbstractHttpEntity;
import ru.ok.android.http.entity.ContentType;
import ru.ok.android.http.nio.ContentEncoder;
import ru.ok.android.http.nio.IOControl;
import ru.ok.android.http.protocol.HTTP;
import ru.ok.android.http.util.Args;

public class NStringEntity extends AbstractHttpEntity implements HttpAsyncContentProducer, ProducingNHttpEntity {
    private final byte[] f68b;
    private final ByteBuffer buf;
    @Deprecated
    protected final ByteBuffer buffer;
    @Deprecated
    protected final byte[] content;

    public NStringEntity(String s, ContentType contentType) {
        Args.notNull(s, "Source string");
        Charset charset = contentType != null ? contentType.getCharset() : null;
        if (charset == null) {
            charset = HTTP.DEF_CONTENT_CHARSET;
        }
        this.f68b = s.getBytes(charset);
        this.buf = ByteBuffer.wrap(this.f68b);
        this.content = this.f68b;
        this.buffer = this.buf;
        if (contentType != null) {
            setContentType(contentType.toString());
        }
    }

    public boolean isRepeatable() {
        return true;
    }

    public long getContentLength() {
        return (long) this.f68b.length;
    }

    public void close() {
        this.buf.rewind();
    }

    public void produceContent(ContentEncoder encoder, IOControl ioctrl) throws IOException {
        encoder.write(this.buf);
        if (!this.buf.hasRemaining()) {
            encoder.complete();
        }
    }

    public boolean isStreaming() {
        return false;
    }

    public InputStream getContent() {
        return new ByteArrayInputStream(this.f68b);
    }

    public void writeTo(OutputStream outstream) throws IOException {
        Args.notNull(outstream, "Output stream");
        outstream.write(this.f68b);
        outstream.flush();
    }
}
