package ru.ok.android.http.impl.entity;

import java.io.IOException;
import java.io.OutputStream;
import ru.ok.android.http.HttpEntity;
import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpMessage;
import ru.ok.android.http.entity.ContentLengthStrategy;
import ru.ok.android.http.impl.io.ChunkedOutputStream;
import ru.ok.android.http.impl.io.ContentLengthOutputStream;
import ru.ok.android.http.impl.io.IdentityOutputStream;
import ru.ok.android.http.io.SessionOutputBuffer;
import ru.ok.android.http.util.Args;

@Deprecated
public class EntitySerializer {
    private final ContentLengthStrategy lenStrategy;

    public EntitySerializer(ContentLengthStrategy lenStrategy) {
        this.lenStrategy = (ContentLengthStrategy) Args.notNull(lenStrategy, "Content length strategy");
    }

    protected OutputStream doSerialize(SessionOutputBuffer outbuffer, HttpMessage message) throws HttpException, IOException {
        long len = this.lenStrategy.determineLength(message);
        if (len == -2) {
            return new ChunkedOutputStream(outbuffer);
        }
        if (len == -1) {
            return new IdentityOutputStream(outbuffer);
        }
        return new ContentLengthOutputStream(outbuffer, len);
    }

    public void serialize(SessionOutputBuffer outbuffer, HttpMessage message, HttpEntity entity) throws HttpException, IOException {
        Args.notNull(outbuffer, "Session output buffer");
        Args.notNull(message, "HTTP message");
        Args.notNull(entity, "HTTP entity");
        OutputStream outstream = doSerialize(outbuffer, message);
        entity.writeTo(outstream);
        outstream.close();
    }
}
