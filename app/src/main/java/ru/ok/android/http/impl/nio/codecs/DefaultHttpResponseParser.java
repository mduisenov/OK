package ru.ok.android.http.impl.nio.codecs;

import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.HttpResponseFactory;
import ru.ok.android.http.ParseException;
import ru.ok.android.http.config.MessageConstraints;
import ru.ok.android.http.impl.DefaultHttpResponseFactory;
import ru.ok.android.http.message.LineParser;
import ru.ok.android.http.message.ParserCursor;
import ru.ok.android.http.nio.reactor.SessionInputBuffer;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharArrayBuffer;

public class DefaultHttpResponseParser extends AbstractMessageParser<HttpResponse> {
    private final HttpResponseFactory responseFactory;

    @Deprecated
    public DefaultHttpResponseParser(SessionInputBuffer buffer, LineParser parser, HttpResponseFactory responseFactory, HttpParams params) {
        super(buffer, parser, params);
        Args.notNull(responseFactory, "Response factory");
        this.responseFactory = responseFactory;
    }

    public DefaultHttpResponseParser(SessionInputBuffer buffer, LineParser parser, HttpResponseFactory responseFactory, MessageConstraints constraints) {
        super(buffer, parser, constraints);
        if (responseFactory == null) {
            responseFactory = DefaultHttpResponseFactory.INSTANCE;
        }
        this.responseFactory = responseFactory;
    }

    public DefaultHttpResponseParser(SessionInputBuffer buffer, MessageConstraints constraints) {
        this(buffer, null, null, constraints);
    }

    public DefaultHttpResponseParser(SessionInputBuffer buffer) {
        this(buffer, null);
    }

    protected HttpResponse createMessage(CharArrayBuffer buffer) throws HttpException, ParseException {
        return this.responseFactory.newHttpResponse(this.lineParser.parseStatusLine(buffer, new ParserCursor(0, buffer.length())), null);
    }
}
