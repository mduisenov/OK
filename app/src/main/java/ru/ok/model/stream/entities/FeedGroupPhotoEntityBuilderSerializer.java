package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedGroupPhotoEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedGroupPhotoEntityBuilder entity) throws IOException {
        out.writeInt(1);
        AbsFeedPhotoEntityBuilderSerializer.write(out, entity);
    }

    public static FeedGroupPhotoEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedGroupPhotoEntityBuilder entity = new FeedGroupPhotoEntityBuilder();
        AbsFeedPhotoEntityBuilderSerializer.read(in, entity);
        return entity;
    }
}
