package ru.ok.android.onelog.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import ru.ok.android.http.Header;
import ru.ok.android.http.HttpEntity;
import ru.ok.android.http.message.BasicHeader;

final class ApiStreamingEntity implements HttpEntity {
    private final Header CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED;
    private final ApiRequest request;

    public ApiStreamingEntity(ApiRequest request) {
        this.CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED = new BasicHeader("Content-Type", "application/x-www-form-urlencoded");
        this.request = request;
    }

    public boolean isRepeatable() {
        return false;
    }

    public boolean isChunked() {
        return true;
    }

    public boolean isStreaming() {
        return true;
    }

    @Deprecated
    public void consumeContent() throws IOException {
    }

    public long getContentLength() {
        return -1;
    }

    public Header getContentType() {
        return this.CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED;
    }

    public Header getContentEncoding() {
        return null;
    }

    public InputStream getContent() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public void writeTo(OutputStream out) throws IOException {
        this.request.writeTo(out);
    }
}
