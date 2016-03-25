package ru.ok.model.stream.message;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedTargetSpanSerializer {
    public static void write(SimpleSerialOutputStream out, FeedTargetSpan span) throws IOException {
        out.writeInt(1);
        FeedMessageSpanSerializer.write(out, span);
    }

    public static FeedTargetSpan read(SimpleSerialInputStream in) throws IOException {
        FeedTargetSpan span = new FeedTargetSpan();
        read(in, span);
        return span;
    }

    static FeedTargetSpan read(SimpleSerialInputStream in, FeedTargetSpan span) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedMessageSpanSerializer.read(in, span);
        return span;
    }
}
