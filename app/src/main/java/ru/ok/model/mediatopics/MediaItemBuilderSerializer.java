package ru.ok.model.mediatopics;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

final class MediaItemBuilderSerializer {
    static void write(SimpleSerialOutputStream out, MediaItemBuilder item) throws IOException {
        out.writeInt(1);
        out.writeStringList(item.reshareOwnerRefs);
        out.writeBoolean(item.isReshare);
    }

    static void read(SimpleSerialInputStream in, MediaItemBuilder item) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        item.reshareOwnerRefs = in.readStringArrayList();
        item.isReshare = in.readBoolean();
    }
}
