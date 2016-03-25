package ru.ok.model.mediatopics;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.stream.StreamSerialInputStream;
import ru.ok.android.storage.serializer.stream.StreamSerialOutputStream;

public class MediaItemStubBuilderSerializer {
    public static void write(StreamSerialOutputStream outputStream, MediaItemStubBuilder builder) throws IOException {
        outputStream.writeInt(1);
        outputStream.writeString(builder.text);
    }

    public static MediaItemStubBuilder read(StreamSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version == 1) {
            return new MediaItemStubBuilder().withText(in.readString());
        }
        throw new SimpleSerialException("Unsupported serial version: " + version);
    }
}
