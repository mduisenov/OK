package ru.ok.model.mediatopics;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class MediaItemTextBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, MediaItemTextBuilder item) throws IOException {
        out.writeInt(1);
        MediaItemBuilderSerializer.write(out, item);
        out.writeString(item.textTokens);
    }

    public static MediaItemTextBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        MediaItemTextBuilder item = new MediaItemTextBuilder();
        MediaItemBuilderSerializer.read(in, item);
        item.textTokens = in.readString();
        return item;
    }
}
