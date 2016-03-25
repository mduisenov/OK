package ru.ok.model.mediatopics;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class MediaItemMusicBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, MediaItemMusicBuilder item) throws IOException {
        out.writeInt(1);
        out.writeStringList(item.trackRefs);
        MediaItemBuilderSerializer.write(out, item);
    }

    public static MediaItemMusicBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        MediaItemMusicBuilder item = new MediaItemMusicBuilder(in.readStringArrayList());
        MediaItemBuilderSerializer.read(in, item);
        return item;
    }
}
