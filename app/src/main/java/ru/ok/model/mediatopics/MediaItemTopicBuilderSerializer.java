package ru.ok.model.mediatopics;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class MediaItemTopicBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, MediaItemTopicBuilder item) throws IOException {
        out.writeInt(1);
        MediaItemBuilderSerializer.write(out, item);
        out.writeStringList(item.mediaTopicRefs);
    }

    public static MediaItemTopicBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        MediaItemTopicBuilder item = new MediaItemTopicBuilder();
        MediaItemBuilderSerializer.read(in, item);
        item.mediaTopicRefs = in.readStringArrayList();
        return item;
    }
}
