package ru.ok.android.http.impl.io;

import android.support.v4.app.NotificationCompat;
import java.io.IOException;
import ru.ok.android.http.HeaderIterator;
import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpMessage;
import ru.ok.android.http.io.HttpMessageWriter;
import ru.ok.android.http.io.SessionOutputBuffer;
import ru.ok.android.http.message.BasicLineFormatter;
import ru.ok.android.http.message.LineFormatter;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharArrayBuffer;

public abstract class AbstractMessageWriter<T extends HttpMessage> implements HttpMessageWriter<T> {
    protected final CharArrayBuffer lineBuf;
    protected final LineFormatter lineFormatter;
    protected final SessionOutputBuffer sessionBuffer;

    protected abstract void writeHeadLine(T t) throws IOException;

    @Deprecated
    public AbstractMessageWriter(SessionOutputBuffer buffer, LineFormatter formatter, HttpParams params) {
        Args.notNull(buffer, "Session input buffer");
        this.sessionBuffer = buffer;
        this.lineBuf = new CharArrayBuffer(NotificationCompat.FLAG_HIGH_PRIORITY);
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
        this.lineBuf = new CharArrayBuffer(NotificationCompat.FLAG_HIGH_PRIORITY);
    }

    public void write(T message) throws IOException, HttpException {
        Args.notNull(message, "HTTP message");
        writeHeadLine(message);
        HeaderIterator it = message.headerIterator();
        while (it.hasNext()) {
            this.sessionBuffer.writeLine(this.lineFormatter.formatHeader(this.lineBuf, it.nextHeader()));
        }
        this.lineBuf.clear();
        this.sessionBuffer.writeLine(this.lineBuf);
    }
}
