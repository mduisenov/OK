package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedMusicTrackEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedMusicTrackEntityBuilder entity) throws IOException {
        out.writeInt(1);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeString(entity.title);
        out.writeString(entity.albumName);
        out.writeString(entity.artistName);
        out.writeString(entity.imageUrl);
        out.writeString(entity.fullName);
        out.writeInt(entity.duration);
        out.writeStringList(entity.albumRefs);
        out.writeStringList(entity.artistRefs);
    }

    public static FeedMusicTrackEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedMusicTrackEntityBuilder entity = new FeedMusicTrackEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        entity.title = in.readString();
        entity.albumName = in.readString();
        entity.artistName = in.readString();
        entity.imageUrl = in.readString();
        entity.fullName = in.readString();
        entity.duration = in.readInt();
        entity.albumRefs = in.readStringArrayList();
        entity.artistRefs = in.readStringArrayList();
        return entity;
    }
}
