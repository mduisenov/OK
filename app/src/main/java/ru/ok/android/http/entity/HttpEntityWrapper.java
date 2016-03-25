package ru.ok.android.http.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import ru.ok.android.http.Header;
import ru.ok.android.http.HttpEntity;
import ru.ok.android.http.util.Args;

public class HttpEntityWrapper implements HttpEntity {
    protected HttpEntity wrappedEntity;

    public HttpEntityWrapper(HttpEntity wrappedEntity) {
        this.wrappedEntity = (HttpEntity) Args.notNull(wrappedEntity, "Wrapped entity");
    }

    public boolean isRepeatable() {
        return this.wrappedEntity.isRepeatable();
    }

    public boolean isChunked() {
        return this.wrappedEntity.isChunked();
    }

    public long getContentLength() {
        return this.wrappedEntity.getContentLength();
    }

    public Header getContentType() {
        return this.wrappedEntity.getContentType();
    }

    public Header getContentEncoding() {
        return this.wrappedEntity.getContentEncoding();
    }

    public InputStream getContent() throws IOException {
        return this.wrappedEntity.getContent();
    }

    public void writeTo(OutputStream outstream) throws IOException {
        this.wrappedEntity.writeTo(outstream);
    }

    public boolean isStreaming() {
        return this.wrappedEntity.isStreaming();
    }

    @Deprecated
    public void consumeContent() throws IOException {
        this.wrappedEntity.consumeContent();
    }
}
