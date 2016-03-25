package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.UserInfo;

public final class FeedUserEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedUserEntityBuilder entity) throws IOException {
        out.writeInt(1);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeObject(entity.userInfo);
    }

    public static FeedUserEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedUserEntityBuilder entity = new FeedUserEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        entity.userInfo = (UserInfo) in.readObject();
        return entity;
    }
}
