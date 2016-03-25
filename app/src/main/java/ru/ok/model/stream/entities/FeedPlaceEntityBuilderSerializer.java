package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedPlaceEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedPlaceEntityBuilder entity) throws IOException {
        out.writeInt(1);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeString(entity.name);
        out.writeDouble(entity.latitude);
        out.writeDouble(entity.longitude);
    }

    public static FeedPlaceEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedPlaceEntityBuilder entity = new FeedPlaceEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        entity.name = in.readString();
        entity.latitude = in.readDouble();
        entity.longitude = in.readDouble();
        return entity;
    }
}
