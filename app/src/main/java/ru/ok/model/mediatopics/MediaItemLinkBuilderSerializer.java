package ru.ok.model.mediatopics;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class MediaItemLinkBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, MediaItemLinkBuilder item) throws IOException {
        out.writeInt(1);
        MediaItemBuilderSerializer.write(out, item);
        out.writeString(item.title);
        out.writeString(item.description);
        out.writeString(item.url);
        out.writeList(item.imageUrls);
    }

    public static MediaItemLinkBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        MediaItemLinkBuilder item = new MediaItemLinkBuilder();
        MediaItemBuilderSerializer.read(in, item);
        item.title = in.readString();
        item.description = in.readString();
        item.url = in.readString();
        in.readArrayList(item.imageUrls);
        return item;
    }
}
