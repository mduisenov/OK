package ru.ok.android.http.impl.nio.codecs;

import java.io.IOException;
import java.util.Iterator;
import ru.ok.android.http.Header;
import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpMessage;
import ru.ok.android.http.message.BasicLineFormatter;
import ru.ok.android.http.message.LineFormatter;
import ru.ok.android.http.nio.NHttpMessageWriter;
import ru.ok.android.http.nio.reactor.SessionOutputBuffer;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharArrayBuffer;

public abstract class AbstractMessageWriter<T extends HttpMessage> implements NHttpMessageWriter<T> {
    protected final CharArrayBuffer lineBuf;
    protected final LineFormatter lineFormatter;
    protected final SessionOutputBuffer sessionBuffer;

    protected abstract void writeHeadLine(T t) throws IOException;

    @Deprecated
    public AbstractMessageWriter(SessionOutputBuffer buffer, LineFormatter formatter, HttpParams params) {
        Args.notNull(buffer, "Session input buffer");
        this.sessionBuffer = buffer;
        this.lineBuf = new CharArrayBuffer(64);
        if (formatter == null) {
            formatter = BasicLineFormatter.INSTANCE;
        }
        this.lineFormatter = formatter;
    }

    public AbstractMessageWriter(SessionOutputBuffer buffer, LineFormatter formatter) {
        this.sessionBuffer = (SessionOutputBuffer) Args.notNull(buffer, "Session input buffer");
        if (formatter == null) {
            formatter = BasicLineFormatter.INSTANCE;
        }
        this.lineFormatter = formatter;
        this.lineBuf = new CharArrayBuffer(64);
    }

    public void reset() {
    }

    public void write(T message) throws IOException, HttpException {
        Args.notNull(message, "HTTP message");
        writeHeadLine(message);
        Iterator<?> it = message.headerIterator();
        while (it.hasNext()) {
            this.sessionBuffer.writeLine(this.lineFormatter.formatHeader(this.lineBuf, (Header) it.next()));
        }
        this.lineBuf.clear();
        this.sessionBuffer.writeLine(this.lineBuf);
    }
}
