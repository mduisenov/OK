package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedPlayListEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedPlayListEntityBuilder entity) throws IOException {
        out.writeInt(1);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeString(entity.title);
        out.writeString(entity.imageUrl);
        out.writeStringList(entity.trackRefs);
    }

    public static FeedPlayListEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedPlayListEntityBuilder entity = new FeedPlayListEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        entity.title = in.readString();
        entity.imageUrl = in.readString();
        entity.trackRefs = in.readStringArrayList();
        return entity;
    }
}
