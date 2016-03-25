package ru.ok.android.http.impl.io;

import java.io.IOException;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.io.SessionOutputBuffer;
import ru.ok.android.http.message.LineFormatter;
import ru.ok.android.http.params.HttpParams;

@Deprecated
public class HttpResponseWriter extends AbstractMessageWriter<HttpResponse> {
    public HttpResponseWriter(SessionOutputBuffer buffer, LineFormatter formatter, HttpParams params) {
        super(buffer, formatter, params);
    }

    protected void writeHeadLine(HttpResponse message) throws IOException {
        this.lineFormatter.formatStatusLine(this.lineBuf, message.getStatusLine());
        this.sessionBuffer.writeLine(this.lineBuf);
    }
}
