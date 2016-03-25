package ru.ok.model.stream.message;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedTargetAppSpanSerializer {
    public static void write(SimpleSerialOutputStream out, FeedTargetAppSpan span) throws IOException {
        out.writeInt(1);
        FeedTargetSpanSerializer.write(out, span);
    }

    public static FeedTargetAppSpan read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedTargetAppSpan span = new FeedTargetAppSpan();
        FeedTargetSpanSerializer.read(in, span);
        return span;
    }
}
