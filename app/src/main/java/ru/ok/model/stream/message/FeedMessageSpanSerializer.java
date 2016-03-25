package ru.ok.model.stream.message;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedMessageSpanSerializer {
    public static void write(SimpleSerialOutputStream out, FeedMessageSpan span) throws IOException {
        out.writeInt(1);
        out.writeInt(span.startIndex);
        out.writeInt(span.endIndex);
    }

    public static void read(SimpleSerialInputStream in, FeedMessageSpan span) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unexpected serial version: " + version);
        }
        span.startIndex = in.readInt();
        span.endIndex = in.readInt();
    }
}
