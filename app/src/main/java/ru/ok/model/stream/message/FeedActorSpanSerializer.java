package ru.ok.model.stream.message;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedActorSpanSerializer {
    public static void write(SimpleSerialOutputStream out, FeedActorSpan span) throws IOException {
        out.writeInt(1);
        FeedMessageSpanSerializer.write(out, span);
    }

    public static FeedActorSpan read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedActorSpan span = new FeedActorSpan();
        FeedMessageSpanSerializer.read(in, span);
        return span;
    }
}
