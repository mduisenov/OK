package ru.ok.android.http.impl.nio.codecs;

import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.HttpRequestFactory;
import ru.ok.android.http.ParseException;
import ru.ok.android.http.config.MessageConstraints;
import ru.ok.android.http.impl.DefaultHttpRequestFactory;
import ru.ok.android.http.message.LineParser;
import ru.ok.android.http.message.ParserCursor;
import ru.ok.android.http.nio.reactor.SessionInputBuffer;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharArrayBuffer;

public class DefaultHttpRequestParser extends AbstractMessageParser<HttpRequest> {
    private final HttpRequestFactory requestFactory;

    @Deprecated
    public DefaultHttpRequestParser(SessionInputBuffer buffer, LineParser parser, HttpRequestFactory requestFactory, HttpParams params) {
        super(buffer, parser, params);
        Args.notNull(requestFactory, "Request factory");
        this.requestFactory = requestFactory;
    }

    public DefaultHttpRequestParser(SessionInputBuffer buffer, LineParser parser, HttpRequestFactory requestFactory, MessageConstraints constraints) {
        super(buffer, parser, constraints);
        if (requestFactory == null) {
            requestFactory = DefaultHttpRequestFactory.INSTANCE;
        }
        this.requestFactory = requestFactory;
    }

    public DefaultHttpRequestParser(SessionInputBuffer buffer, MessageConstraints constraints) {
        this(buffer, null, null, constraints);
    }

    public DefaultHttpRequestParser(SessionInputBuffer buffer) {
        this(buffer, null);
    }

    protected HttpRequest createMessage(CharArrayBuffer buffer) throws HttpException, ParseException {
        return this.requestFactory.newHttpRequest(this.lineParser.parseRequestLine(buffer, new ParserCursor(0, buffer.length())));
    }
}
