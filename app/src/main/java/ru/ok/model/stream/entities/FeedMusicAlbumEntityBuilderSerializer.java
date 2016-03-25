package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.wmf.Album;

public final class FeedMusicAlbumEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedMusicAlbumEntityBuilder entity) throws IOException {
        out.writeInt(1);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeObject(entity.album);
    }

    public static FeedMusicAlbumEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedMusicAlbumEntityBuilder entity = new FeedMusicAlbumEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        entity.album = (Album) in.readObject();
        return entity;
    }
}
