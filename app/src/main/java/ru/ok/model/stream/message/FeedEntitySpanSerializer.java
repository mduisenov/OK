package ru.ok.model.stream.message;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedEntitySpanSerializer {
    public static void write(SimpleSerialOutputStream out, FeedEntitySpan span) throws IOException {
        out.writeInt(1);
        FeedMessageSpanSerializer.write(out, span);
        out.writeInt(span.entityType);
        out.writeString(span.entityId);
        out.writeString(span.ref);
    }

    public static FeedEntitySpan read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedEntitySpan span = new FeedEntitySpan();
        FeedMessageSpanSerializer.read(in, span);
        span.entityType = in.readInt();
        span.entityId = in.readString();
        span.ref = in.readString();
        return span;
    }
}
