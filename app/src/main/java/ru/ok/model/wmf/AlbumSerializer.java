package ru.ok.model.wmf;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class AlbumSerializer {
    public static void write(SimpleSerialOutputStream out, Album album) throws IOException {
        out.writeInt(1);
        out.writeLong(album.id);
        out.writeString(album.name);
        out.writeString(album.imageUrl);
        out.writeString(album.ensemble);
    }

    public static Album read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version == 1) {
            return new Album(in.readLong(), in.readString(), in.readString(), in.readString());
        }
        throw new SimpleSerialException("Unsupported serial version: " + version);
    }
}
