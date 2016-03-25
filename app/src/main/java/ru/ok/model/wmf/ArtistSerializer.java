package ru.ok.model.wmf;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class ArtistSerializer {
    public static void write(SimpleSerialOutputStream out, Artist artist) throws IOException {
        out.writeInt(1);
        out.writeLong(artist.id);
        out.writeString(artist.name);
        out.writeString(artist.imageUrl);
    }

    public static Artist read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version == 1) {
            return new Artist(in.readLong(), in.readString(), in.readString());
        }
        throw new SimpleSerialException("Unsupported serial version: " + version);
    }
}
