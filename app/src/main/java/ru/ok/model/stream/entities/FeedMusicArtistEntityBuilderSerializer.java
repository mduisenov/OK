package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.wmf.Artist;

public final class FeedMusicArtistEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedMusicArtistEntityBuilder entity) throws IOException {
        out.writeInt(1);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeObject(entity.artist);
    }

    public static FeedMusicArtistEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedMusicArtistEntityBuilder entity = new FeedMusicArtistEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        entity.artist = (Artist) in.readObject();
        return entity;
    }
}
