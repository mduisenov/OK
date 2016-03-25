package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.GroupInfo;

public final class FeedGroupEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedGroupEntityBuilder entity) throws IOException {
        out.writeInt(1);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeObject(entity.groupInfo);
    }

    public static FeedGroupEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedGroupEntityBuilder entity = new FeedGroupEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        entity.groupInfo = (GroupInfo) in.readObject();
        return entity;
    }
}
