package ru.ok.android.http.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import ru.ok.android.http.util.Args;

public class ByteArrayEntity extends AbstractHttpEntity implements Cloneable {
    private final byte[] f67b;
    @Deprecated
    protected final byte[] content;
    private final int len;
    private final int off;

    public ByteArrayEntity(byte[] b, ContentType contentType) {
        Args.notNull(b, "Source byte array");
        this.content = b;
        this.f67b = b;
        this.off = 0;
        this.len = this.f67b.length;
        if (contentType != null) {
            setContentType(contentType.toString());
        }
    }

    public ByteArrayEntity(byte[] b) {
        this(b, null);
    }

    public boolean isRepeatable() {
        return true;
    }

    public long getContentLength() {
        return (long) this.len;
    }

    public InputStream getContent() {
        return new ByteArrayInputStream(this.f67b, this.off, this.len);
    }

    public void writeTo(OutputStream outstream) throws IOException {
        Args.notNull(outstream, "Output stream");
        outstream.write(this.f67b, this.off, this.len);
        outstream.flush();
    }

    public boolean isStreaming() {
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
