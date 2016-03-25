package ru.ok.android.http.impl.io;

import android.support.v4.app.NotificationCompat;
import java.io.IOException;
import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.HttpResponseFactory;
import ru.ok.android.http.NoHttpResponseException;
import ru.ok.android.http.ParseException;
import ru.ok.android.http.config.MessageConstraints;
import ru.ok.android.http.impl.DefaultHttpResponseFactory;
import ru.ok.android.http.io.SessionInputBuffer;
import ru.ok.android.http.message.LineParser;
import ru.ok.android.http.message.ParserCursor;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharArrayBuffer;

public class DefaultHttpResponseParser extends AbstractMessageParser<HttpResponse> {
    private final CharArrayBuffer lineBuf;
    private final HttpResponseFactory responseFactory;

    @Deprecated
    public DefaultHttpResponseParser(SessionInputBuffer buffer, LineParser lineParser, HttpResponseFactory responseFactory, HttpParams params) {
        super(buffer, lineParser, params);
        this.responseFactory = (HttpResponseFactory) Args.notNull(responseFactory, "Response factory");
        this.lineBuf = new CharArrayBuffer(NotificationCompat.FLAG_HIGH_PRIORITY);
    }

    public DefaultHttpResponseParser(SessionInputBuffer buffer, LineParser lineParser, HttpResponseFactory responseFactory, MessageConstraints constraints) {
        super(buffer, lineParser, constraints);
        if (responseFactory == null) {
            responseFactory = DefaultHttpResponseFactory.INSTANCE;
        }
        this.responseFactory = responseFactory;
        this.lineBuf = new CharArrayBuffer(NotificationCompat.FLAG_HIGH_PRIORITY);
    }

    public DefaultHttpResponseParser(SessionInputBuffer buffer, MessageConstraints constraints) {
        this(buffer, null, null, constraints);
    }

    public DefaultHttpResponseParser(SessionInputBuffer buffer) {
        this(buffer, null, null, MessageConstraints.DEFAULT);
    }

    protected HttpResponse parseHead(SessionInputBuffer sessionBuffer) throws IOException, HttpException, ParseException {
        this.lineBuf.clear();
        if (sessionBuffer.readLine(this.lineBuf) == -1) {
            throw new NoHttpResponseException("The target server failed to respond");
        }
        return this.responseFactory.newHttpResponse(this.lineParser.parseStatusLine(this.lineBuf, new ParserCursor(0, this.lineBuf.length())), null);
    }
}
