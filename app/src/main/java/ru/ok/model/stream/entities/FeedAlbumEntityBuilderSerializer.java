package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.photo.PhotoAlbumInfo;

public final class FeedAlbumEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedAlbumEntityBuilder entity) throws IOException {
        out.writeInt(1);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeObject(entity.albumInfo);
    }

    public static FeedAlbumEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedAlbumEntityBuilder entity = new FeedAlbumEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        entity.albumInfo = (PhotoAlbumInfo) in.readObject();
        return entity;
    }
}
