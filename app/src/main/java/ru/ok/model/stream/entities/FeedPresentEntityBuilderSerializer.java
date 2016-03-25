package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.stream.message.FeedMessage;

public final class FeedPresentEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedPresentEntityBuilder entity) throws IOException {
        out.writeInt(2);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeString(entity.senderRef);
        out.writeString(entity.receiverRef);
        out.writeString(entity.presentTypeRef);
        out.writeStringList(entity.musicTrackRefs);
        out.writeObject(entity.senderLabel);
        out.writeObject(entity.receiverLabel);
    }

    public static FeedPresentEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version < 1 || version > 2) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedPresentEntityBuilder entity = new FeedPresentEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        entity.senderRef = in.readString();
        entity.receiverRef = in.readString();
        entity.presentTypeRef = in.readString();
        entity.musicTrackRefs = in.readStringArrayList();
        if (version >= 2) {
            entity.senderLabel = (FeedMessage) in.readObject();
            entity.receiverLabel = (FeedMessage) in.readObject();
        }
        return entity;
    }
}
