package ru.ok.android.http.impl.nio.codecs;

import java.io.IOException;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.message.LineFormatter;
import ru.ok.android.http.nio.reactor.SessionOutputBuffer;
import ru.ok.android.http.params.HttpParams;

public class DefaultHttpResponseWriter extends AbstractMessageWriter<HttpResponse> {
    @Deprecated
    public DefaultHttpResponseWriter(SessionOutputBuffer buffer, LineFormatter formatter, HttpParams params) {
        super(buffer, formatter, params);
    }

    public DefaultHttpResponseWriter(SessionOutputBuffer buffer, LineFormatter formatter) {
        super(buffer, formatter);
    }

    public DefaultHttpResponseWriter(SessionOutputBuffer buffer) {
        super(buffer, null);
    }

    protected void writeHeadLine(HttpResponse message) throws IOException {
        this.sessionBuffer.writeLine(this.lineFormatter.formatStatusLine(this.lineBuf, message.getStatusLine()));
    }
}
