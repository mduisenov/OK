package ru.ok.android.http.impl.nio.codecs;

import java.io.IOException;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.message.LineFormatter;
import ru.ok.android.http.nio.reactor.SessionOutputBuffer;
import ru.ok.android.http.params.HttpParams;

public class DefaultHttpRequestWriter extends AbstractMessageWriter<HttpRequest> {
    @Deprecated
    public DefaultHttpRequestWriter(SessionOutputBuffer buffer, LineFormatter formatter, HttpParams params) {
        super(buffer, formatter, params);
    }

    public DefaultHttpRequestWriter(SessionOutputBuffer buffer, LineFormatter formatter) {
        super(buffer, formatter);
    }

    public DefaultHttpRequestWriter(SessionOutputBuffer buffer) {
        super(buffer, null);
    }

    protected void writeHeadLine(HttpRequest message) throws IOException {
        this.sessionBuffer.writeLine(this.lineFormatter.formatRequestLine(this.lineBuf, message.getRequestLine()));
    }
}
