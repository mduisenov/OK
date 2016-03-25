package ru.ok.model.stream.message;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedMessageSerializer {
    public static void write(SimpleSerialOutputStream out, FeedMessage message) throws IOException {
        out.writeInt(1);
        out.writeString(message.text);
        out.writeList(message.spans);
    }

    public static FeedMessage read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedMessage message = new FeedMessage();
        message.text = in.readString();
        message.spans = in.readArrayList();
        return message;
    }
}
