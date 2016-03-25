package ru.ok.model.mediatopics;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class MediaItemVideoBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, MediaItemVideoBuilder item) throws IOException {
        out.writeInt(1);
        out.writeStringList(item.videoRefs);
        MediaItemBuilderSerializer.write(out, item);
    }

    public static MediaItemVideoBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        MediaItemVideoBuilder item = new MediaItemVideoBuilder(in.readStringArrayList());
        MediaItemBuilderSerializer.read(in, item);
        return item;
    }
}
