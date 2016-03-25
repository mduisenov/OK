package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedAppEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedAppEntityBuilder entity) throws IOException {
        out.writeInt(1);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeString(entity.iconUrl);
        out.writeInt(entity.width);
        out.writeInt(entity.height);
        out.writeString(entity.url);
        out.writeString(entity.name);
        out.writeString(entity.storeId);
        out.writeString(entity.tabStoreId);
    }

    public static FeedAppEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedAppEntityBuilder entity = new FeedAppEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        entity.iconUrl = in.readString();
        entity.width = in.readInt();
        entity.height = in.readInt();
        entity.url = in.readString();
        entity.name = in.readString();
        entity.storeId = in.readString();
        entity.tabStoreId = in.readString();
        return entity;
    }
}
