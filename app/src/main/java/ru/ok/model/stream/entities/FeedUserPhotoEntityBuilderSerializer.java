package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedUserPhotoEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedUserPhotoEntityBuilder entity) throws IOException {
        out.writeInt(1);
        AbsFeedPhotoEntityBuilderSerializer.write(out, entity);
    }

    public static FeedUserPhotoEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedUserPhotoEntityBuilder entity = new FeedUserPhotoEntityBuilder();
        AbsFeedPhotoEntityBuilderSerializer.read(in, entity);
        return entity;
    }
}
