package ru.ok.android.http.impl.io;

import android.support.v4.app.NotificationCompat;
import java.io.IOException;
import ru.ok.android.http.ConnectionClosedException;
import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpMessage;
import ru.ok.android.http.HttpRequestFactory;
import ru.ok.android.http.ParseException;
import ru.ok.android.http.io.SessionInputBuffer;
import ru.ok.android.http.message.LineParser;
import ru.ok.android.http.message.ParserCursor;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharArrayBuffer;

@Deprecated
public class HttpRequestParser extends AbstractMessageParser<HttpMessage> {
    private final CharArrayBuffer lineBuf;
    private final HttpRequestFactory requestFactory;

    public HttpRequestParser(SessionInputBuffer buffer, LineParser parser, HttpRequestFactory requestFactory, HttpParams params) {
        super(buffer, parser, params);
        this.requestFactory = (HttpRequestFactory) Args.notNull(requestFactory, "Request factory");
        this.lineBuf = new CharArrayBuffer(NotificationCompat.FLAG_HIGH_PRIORITY);
    }

    protected HttpMessage parseHead(SessionInputBuffer sessionBuffer) throws IOException, HttpException, ParseException {
        this.lineBuf.clear();
        if (sessionBuffer.readLine(this.lineBuf) == -1) {
            throw new ConnectionClosedException("Client closed connection");
        }
        return this.requestFactory.newHttpRequest(this.lineParser.parseRequestLine(this.lineBuf, new ParserCursor(0, this.lineBuf.length())));
    }
}
