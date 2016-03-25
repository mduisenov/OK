package ru.ok.android.http.impl.nio.codecs;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpMessage;
import ru.ok.android.http.MessageConstraintException;
import ru.ok.android.http.ParseException;
import ru.ok.android.http.ProtocolException;
import ru.ok.android.http.config.MessageConstraints;
import ru.ok.android.http.message.BasicLineParser;
import ru.ok.android.http.message.LineParser;
import ru.ok.android.http.nio.NHttpMessageParser;
import ru.ok.android.http.nio.reactor.SessionInputBuffer;
import ru.ok.android.http.params.HttpParamConfig;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharArrayBuffer;

public abstract class AbstractMessageParser<T extends HttpMessage> implements NHttpMessageParser<T> {
    private static final int COMPLETED = 2;
    private static final int READ_HEADERS = 1;
    private static final int READ_HEAD_LINE = 0;
    private final MessageConstraints constraints;
    private boolean endOfStream;
    private final List<CharArrayBuffer> headerBufs;
    private CharArrayBuffer lineBuf;
    protected final LineParser lineParser;
    private T message;
    private final SessionInputBuffer sessionBuffer;
    private int state;

    protected abstract T createMessage(CharArrayBuffer charArrayBuffer) throws HttpException, ParseException;

    @Deprecated
    public AbstractMessageParser(SessionInputBuffer buffer, LineParser lineParser, HttpParams params) {
        Args.notNull(buffer, "Session input buffer");
        Args.notNull(params, "HTTP parameters");
        this.sessionBuffer = buffer;
        this.state = 0;
        this.endOfStream = false;
        this.headerBufs = new ArrayList();
        this.constraints = HttpParamConfig.getMessageConstraints(params);
        if (lineParser == null) {
            lineParser = BasicLineParser.INSTANCE;
        }
        this.lineParser = lineParser;
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
        this.constraints = constraints;
        this.headerBufs = new ArrayList();
        this.state = 0;
        this.endOfStream = false;
    }

    public void reset() {
        this.state = 0;
        this.endOfStream = false;
        this.headerBufs.clear();
        this.message = null;
    }

    public int fillBuffer(ReadableByteChannel channel) throws IOException {
        int bytesRead = this.sessionBuffer.fill(channel);
        if (bytesRead == -1) {
            this.endOfStream = true;
        }
        return bytesRead;
    }

    private void parseHeadLine() throws HttpException, ParseException {
        this.message = createMessage(this.lineBuf);
    }

    private void parseHeader() throws IOException {
        CharArrayBuffer current = this.lineBuf;
        int count = this.headerBufs.size();
        if ((this.lineBuf.charAt(0) == ' ' || this.lineBuf.charAt(0) == '\t') && count > 0) {
            CharArrayBuffer previous = (CharArrayBuffer) this.headerBufs.get(count - 1);
            int i = 0;
            while (i < current.length()) {
                char ch = current.charAt(i);
                if (ch != ' ' && ch != '\t') {
                    break;
                }
                i += READ_HEADERS;
            }
            int maxLineLen = this.constraints.getMaxLineLength();
            if (maxLineLen <= 0 || ((previous.length() + READ_HEADERS) + current.length()) - i <= maxLineLen) {
                previous.append(' ');
                previous.append(current, i, current.length() - i);
                return;
            }
            throw new MessageConstraintException("Maximum line length limit exceeded");
        }
        this.headerBufs.add(current);
        this.lineBuf = null;
    }

    public T parse() throws IOException, HttpException {
        while (this.state != COMPLETED) {
            if (this.lineBuf == null) {
                this.lineBuf = new CharArrayBuffer(64);
            } else {
                this.lineBuf.clear();
            }
            boolean lineComplete = this.sessionBuffer.readLine(this.lineBuf, this.endOfStream);
            int maxLineLen = this.constraints.getMaxLineLength();
            if (maxLineLen > 0 && (this.lineBuf.length() > maxLineLen || (!lineComplete && this.sessionBuffer.length() > maxLineLen))) {
                throw new MessageConstraintException("Maximum line length limit exceeded");
            } else if (lineComplete) {
                switch (this.state) {
                    case RECEIVED_VALUE:
                        try {
                            parseHeadLine();
                            this.state = READ_HEADERS;
                            break;
                        } catch (ParseException px) {
                            throw new ProtocolException(px.getMessage(), px);
                        }
                    case READ_HEADERS /*1*/:
                        if (this.lineBuf.length() <= 0) {
                            this.state = COMPLETED;
                            break;
                        }
                        int maxHeaderCount = this.constraints.getMaxHeaderCount();
                        if (maxHeaderCount <= 0 || this.headerBufs.size() < maxHeaderCount) {
                            parseHeader();
                            break;
                        }
                        throw new MessageConstraintException("Maximum header count exceeded");
                        break;
                }
                if (this.endOfStream && !this.sessionBuffer.hasData()) {
                    this.state = COMPLETED;
                }
            } else if (this.state == COMPLETED) {
                return null;
            } else {
                for (CharArrayBuffer buffer : this.headerBufs) {
                    try {
                        this.message.addHeader(this.lineParser.parseHeader(buffer));
                    } catch (ParseException ex) {
                        throw new ProtocolException(ex.getMessage(), ex);
                    }
                }
                return this.message;
            }
        }
        if (this.state == COMPLETED) {
            return null;
        }
        while (i$.hasNext()) {
            this.message.addHeader(this.lineParser.parseHeader(buffer));
        }
        return this.message;
    }
}
