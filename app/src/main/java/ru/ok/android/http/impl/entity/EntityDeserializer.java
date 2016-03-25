package ru.ok.android.http.impl.entity;

import java.io.IOException;
import ru.ok.android.http.Header;
import ru.ok.android.http.HttpEntity;
import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpMessage;
import ru.ok.android.http.entity.BasicHttpEntity;
import ru.ok.android.http.entity.ContentLengthStrategy;
import ru.ok.android.http.impl.io.ChunkedInputStream;
import ru.ok.android.http.impl.io.ContentLengthInputStream;
import ru.ok.android.http.impl.io.IdentityInputStream;
import ru.ok.android.http.io.SessionInputBuffer;
import ru.ok.android.http.util.Args;

@Deprecated
public class EntityDeserializer {
    private final ContentLengthStrategy lenStrategy;

    public EntityDeserializer(ContentLengthStrategy lenStrategy) {
        this.lenStrategy = (ContentLengthStrategy) Args.notNull(lenStrategy, "Content length strategy");
    }

    protected BasicHttpEntity doDeserialize(SessionInputBuffer inbuffer, HttpMessage message) throws HttpException, IOException {
        BasicHttpEntity entity = new BasicHttpEntity();
        long len = this.lenStrategy.determineLength(message);
        if (len == -2) {
            entity.setChunked(true);
            entity.setContentLength(-1);
            entity.setContent(new ChunkedInputStream(inbuffer));
        } else if (len == -1) {
            entity.setChunked(false);
            entity.setContentLength(-1);
            entity.setContent(new IdentityInputStream(inbuffer));
        } else {
            entity.setChunked(false);
            entity.setContentLength(len);
            entity.setContent(new ContentLengthInputStream(inbuffer, len));
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

    public HttpEntity deserialize(SessionInputBuffer inbuffer, HttpMessage message) throws HttpException, IOException {
        Args.notNull(inbuffer, "Session input buffer");
        Args.notNull(message, "HTTP message");
        return doDeserialize(inbuffer, message);
    }
}
