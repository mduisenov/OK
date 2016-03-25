package ru.ok.model.stream;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.Discussion;

public final class DiscussionSummarySerializer {
    public static void write(SimpleSerialOutputStream out, DiscussionSummary ds) throws IOException {
        out.writeInt(1);
        out.writeObject(ds.discussion);
        out.writeInt(ds.commentsCount);
    }

    public static DiscussionSummary read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version == 1) {
            return new DiscussionSummary((Discussion) in.readObject(), in.readInt());
        }
        throw new SimpleSerialException("Unsupported serial version: " + version);
    }
}
