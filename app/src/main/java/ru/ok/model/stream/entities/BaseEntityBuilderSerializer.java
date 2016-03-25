package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.LikeInfoContext;

final class BaseEntityBuilderSerializer {
    static void write(SimpleSerialOutputStream out, BaseEntityBuilder entity) throws IOException {
        out.writeInt(1);
        out.writeString(entity.id);
        out.writeInt(entity.type);
        out.writeObject(entity.likeInfo);
        out.writeObject(entity.discussionSummary);
    }

    static void read(SimpleSerialInputStream in, BaseEntityBuilder entity) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        entity.id = in.readString();
        entity.type = in.readInt();
        entity.likeInfo = (LikeInfoContext) in.readObject();
        entity.discussionSummary = (DiscussionSummary) in.readObject();
    }
}
