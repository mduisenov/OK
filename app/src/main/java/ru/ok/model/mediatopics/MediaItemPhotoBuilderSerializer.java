package ru.ok.model.mediatopics;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class MediaItemPhotoBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, MediaItemPhotoBuilder item) throws IOException {
        out.writeInt(1);
        out.writeStringList(item.photoRefs);
        MediaItemBuilderSerializer.write(out, item);
    }

    public static MediaItemPhotoBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        MediaItemPhotoBuilder item = new MediaItemPhotoBuilder(in.readStringArrayList());
        MediaItemBuilderSerializer.read(in, item);
        return item;
    }
}
