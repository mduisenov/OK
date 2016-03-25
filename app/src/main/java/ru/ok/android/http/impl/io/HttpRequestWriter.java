package ru.ok.android.http.impl.io;

import java.io.IOException;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.io.SessionOutputBuffer;
import ru.ok.android.http.message.LineFormatter;
import ru.ok.android.http.params.HttpParams;

@Deprecated
public class HttpRequestWriter extends AbstractMessageWriter<HttpRequest> {
    public HttpRequestWriter(SessionOutputBuffer buffer, LineFormatter formatter, HttpParams params) {
        super(buffer, formatter, params);
    }

    protected void writeHeadLine(HttpRequest message) throws IOException {
        this.lineFormatter.formatRequestLine(this.lineBuf, message.getRequestLine());
        this.sessionBuffer.writeLine(this.lineBuf);
    }
}
