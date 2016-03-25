package ru.ok.model.stream.message;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedTargetGroupSpanSerializer {
    public static void write(SimpleSerialOutputStream out, FeedTargetGroupSpan span) throws IOException {
        out.writeInt(1);
        FeedTargetSpanSerializer.write(out, span);
    }

    public static FeedTargetGroupSpan read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedTargetGroupSpan span = new FeedTargetGroupSpan();
        FeedTargetSpanSerializer.read(in, span);
        return span;
    }
}
