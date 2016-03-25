package ru.ok.android.http.impl.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.http.Header;
import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpMessage;
import ru.ok.android.http.ParseException;
import ru.ok.android.http.ProtocolException;
import ru.ok.android.http.config.MessageConstraints;
import ru.ok.android.http.io.HttpMessageParser;
import ru.ok.android.http.io.SessionInputBuffer;
import ru.ok.android.http.message.BasicLineParser;
import ru.ok.android.http.message.LineParser;
import ru.ok.android.http.params.HttpParamConfig;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharArrayBuffer;

public abstract class AbstractMessageParser<T extends HttpMessage> implements HttpMessageParser<T> {
    private static final int HEADERS = 1;
    private static final int HEAD_LINE = 0;
    private final List<CharArrayBuffer> headerLines;
    protected final LineParser lineParser;
    private T message;
    private final MessageConstraints messageConstraints;
    private final SessionInputBuffer sessionBuffer;
    private int state;

    protected abstract T parseHead(SessionInputBuffer sessionInputBuffer) throws IOException, HttpException, ParseException;

    @Deprecated
    public AbstractMessageParser(SessionInputBuffer buffer, LineParser parser, HttpParams params) {
        Args.notNull(buffer, "Session input buffer");
        Args.notNull(params, "HTTP parameters");
        this.sessionBuffer = buffer;
        this.messageConstraints = HttpParamConfig.getMessageConstraints(params);
        if (parser == null) {
            parser = BasicLineParser.INSTANCE;
        }
        this.lineParser = parser;
        this.headerLines = new ArrayList();
        this.state = 0;
    }

    public AbstractMessageParser(SessionInputBuffer buffer, LineParser lineParser, MessageConstraints constraints) {
        this.sessionBuffer = (SessionInputBuffer) Args.notNull(buffer, "Session input buffer");
        if (lineParser == null) {
            lineParser = BasicLineParser.INSTANCE;
        }
        this.lineParser = lineParser;
        if (constraints == null) {
            constraints = MessageConstraints.DEFAULT;
        }
        this.messageConstraints = constraints;
        this.headerLines = new ArrayList();
        this.state = 0;
    }

    public static Header[] parseHeaders(SessionInputBuffer inbuffer, int maxHeaderCount, int maxLineLen, LineParser parser) throws HttpException, IOException {
        List<CharArrayBuffer> headerLines = new ArrayList();
        if (parser == null) {
            parser = BasicLineParser.INSTANCE;
        }
        return parseHeaders(inbuffer, maxHeaderCount, maxLineLen, parser, headerLines);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static ru.ok.android.http.Header[] parseHeaders(ru.ok.android.http.io.SessionInputBuffer r10, int r11, int r12, ru.ok.android.http.message.LineParser r13, java.util.List<ru.ok.android.http.util.CharArrayBuffer> r14) throws ru.ok.android.http.HttpException, java.io.IOException {
        /*
        r8 = "Session input buffer";
        ru.ok.android.http.util.Args.notNull(r10, r8);
        r8 = "Line parser";
        ru.ok.android.http.util.Args.notNull(r13, r8);
        r8 = "Header line list";
        ru.ok.android.http.util.Args.notNull(r14, r8);
        r2 = 0;
        r7 = 0;
    L_0x0014:
        if (r2 != 0) goto L_0x0047;
    L_0x0016:
        r2 = new ru.ok.android.http.util.CharArrayBuffer;
        r8 = 64;
        r2.<init>(r8);
    L_0x001d:
        r6 = r10.readLine(r2);
        r8 = -1;
        if (r6 == r8) goto L_0x002b;
    L_0x0024:
        r8 = r2.length();
        r9 = 1;
        if (r8 >= r9) goto L_0x004b;
    L_0x002b:
        r8 = r14.size();
        r4 = new ru.ok.android.http.Header[r8];
        r5 = 0;
    L_0x0032:
        r8 = r14.size();
        if (r5 >= r8) goto L_0x00bd;
    L_0x0038:
        r0 = r14.get(r5);
        r0 = (ru.ok.android.http.util.CharArrayBuffer) r0;
        r8 = r13.parseHeader(r0);	 Catch:{ ParseException -> 0x00b2 }
        r4[r5] = r8;	 Catch:{ ParseException -> 0x00b2 }
        r5 = r5 + 1;
        goto L_0x0032;
    L_0x0047:
        r2.clear();
        goto L_0x001d;
    L_0x004b:
        r8 = 0;
        r8 = r2.charAt(r8);
        r9 = 32;
        if (r8 == r9) goto L_0x005d;
    L_0x0054:
        r8 = 0;
        r8 = r2.charAt(r8);
        r9 = 9;
        if (r8 != r9) goto L_0x00ac;
    L_0x005d:
        if (r7 == 0) goto L_0x00ac;
    L_0x005f:
        r5 = 0;
    L_0x0060:
        r8 = r2.length();
        if (r5 >= r8) goto L_0x0072;
    L_0x0066:
        r1 = r2.charAt(r5);
        r8 = 32;
        if (r1 == r8) goto L_0x008b;
    L_0x006e:
        r8 = 9;
        if (r1 == r8) goto L_0x008b;
    L_0x0072:
        if (r12 <= 0) goto L_0x008e;
    L_0x0074:
        r8 = r7.length();
        r8 = r8 + 1;
        r9 = r2.length();
        r8 = r8 + r9;
        r8 = r8 - r5;
        if (r8 <= r12) goto L_0x008e;
    L_0x0082:
        r8 = new ru.ok.android.http.MessageConstraintException;
        r9 = "Maximum line length limit exceeded";
        r8.<init>(r9);
        throw r8;
    L_0x008b:
        r5 = r5 + 1;
        goto L_0x0060;
    L_0x008e:
        r8 = 32;
        r7.append(r8);
        r8 = r2.length();
        r8 = r8 - r5;
        r7.append(r2, r5, r8);
    L_0x009b:
        if (r11 <= 0) goto L_0x0014;
    L_0x009d:
        r8 = r14.size();
        if (r8 < r11) goto L_0x0014;
    L_0x00a3:
        r8 = new ru.ok.android.http.MessageConstraintException;
        r9 = "Maximum header count exceeded";
        r8.<init>(r9);
        throw r8;
    L_0x00ac:
        r14.add(r2);
        r7 = r2;
        r2 = 0;
        goto L_0x009b;
    L_0x00b2:
        r3 = move-exception;
        r8 = new ru.ok.android.http.ProtocolException;
        r9 = r3.getMessage();
        r8.<init>(r9);
        throw r8;
    L_0x00bd:
        return r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.http.impl.io.AbstractMessageParser.parseHeaders(ru.ok.android.http.io.SessionInputBuffer, int, int, ru.ok.android.http.message.LineParser, java.util.List):ru.ok.android.http.Header[]");
    }

    public T parse() throws IOException, HttpException {
        switch (this.state) {
            case RECEIVED_VALUE:
                try {
                    this.message = parseHead(this.sessionBuffer);
                    this.state = HEADERS;
                    break;
                } catch (ParseException px) {
                    throw new ProtocolException(px.getMessage(), px);
                }
            case HEADERS /*1*/:
                break;
            default:
                throw new IllegalStateException("Inconsistent parser state");
        }
        this.message.setHeaders(parseHeaders(this.sessionBuffer, this.messageConstraints.getMaxHeaderCount(), this.messageConstraints.getMaxLineLength(), this.lineParser, this.headerLines));
        T result = this.message;
        this.message = null;
        this.headerLines.clear();
        this.state = 0;
        return result;
    }
}
