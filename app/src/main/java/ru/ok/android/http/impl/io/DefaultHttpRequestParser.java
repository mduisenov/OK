package ru.ok.android.http.impl.io;

import android.support.v4.app.NotificationCompat;
import java.io.IOException;
import ru.ok.android.http.ConnectionClosedException;
import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.HttpRequestFactory;
import ru.ok.android.http.ParseException;
import ru.ok.android.http.config.MessageConstraints;
import ru.ok.android.http.impl.DefaultHttpRequestFactory;
import ru.ok.android.http.io.SessionInputBuffer;
import ru.ok.android.http.message.LineParser;
import ru.ok.android.http.message.ParserCursor;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharArrayBuffer;

public class DefaultHttpRequestParser extends AbstractMessageParser<HttpRequest> {
    private final CharArrayBuffer lineBuf;
    private final HttpRequestFactory requestFactory;

    @Deprecated
    public DefaultHttpRequestParser(SessionInputBuffer buffer, LineParser lineParser, HttpRequestFactory requestFactory, HttpParams params) {
        super(buffer, lineParser, params);
        this.requestFactory = (HttpRequestFactory) Args.notNull(requestFactory, "Request factory");
        this.lineBuf = new CharArrayBuffer(NotificationCompat.FLAG_HIGH_PRIORITY);
    }

    public DefaultHttpRequestParser(SessionInputBuffer buffer, LineParser lineParser, HttpRequestFactory requestFactory, MessageConstraints constraints) {
        super(buffer, lineParser, constraints);
        if (requestFactory == null) {
            requestFactory = DefaultHttpRequestFactory.INSTANCE;
        }
        this.requestFactory = requestFactory;
        this.lineBuf = new CharArrayBuffer(NotificationCompat.FLAG_HIGH_PRIORITY);
    }

    public DefaultHttpRequestParser(SessionInputBuffer buffer, MessageConstraints constraints) {
        this(buffer, null, null, constraints);
    }

    public DefaultHttpRequestParser(SessionInputBuffer buffer) {
        this(buffer, null, null, MessageConstraints.DEFAULT);
    }

    protected HttpRequest parseHead(SessionInputBuffer sessionBuffer) throws IOException, HttpException, ParseException {
        this.lineBuf.clear();
        if (sessionBuffer.readLine(this.lineBuf) == -1) {
            throw new ConnectionClosedException("Client closed connection");
        }
        return this.requestFactory.newHttpRequest(this.lineParser.parseRequestLine(this.lineBuf, new ParserCursor(0, this.lineBuf.length())));
    }
}
