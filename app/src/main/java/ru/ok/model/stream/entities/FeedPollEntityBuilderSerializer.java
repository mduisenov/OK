package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedPollEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedPollEntityBuilder entity) throws IOException {
        out.writeInt(1);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeInt(entity.count);
        out.writeString(entity.question);
        out.writeList(entity.answers);
        out.writeStringList(entity.options);
    }

    public static FeedPollEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedPollEntityBuilder entity = new FeedPollEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        entity.count = in.readInt();
        entity.question = in.readString();
        in.readArrayList(entity.answers);
        in.readStringArrayList(entity.options);
        return entity;
    }
}
