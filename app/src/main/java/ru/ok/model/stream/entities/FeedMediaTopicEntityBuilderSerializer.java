package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedMediaTopicEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedMediaTopicEntityBuilder entity) throws IOException {
        out.writeInt(2);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeLong(entity.createdDate);
        out.writeBoolean(entity.hasMore);
        out.writeBoolean(entity.isSticky);
        out.writeBoolean(entity.isUnmodifiable);
        out.writeList(entity.mediaItemsBuilders);
        out.writeString(entity.authorRef);
        out.writeStringList(entity.friendRefs);
        out.writeStringList(entity.placesRefs);
        out.writeString(entity.ownerRef);
        out.writeString(entity.markAsSpamId);
        out.writeString(entity.deleteId);
        out.writeBoolean(entity.isPromo);
    }

    public static FeedMediaTopicEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version > 2) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedMediaTopicEntityBuilder entity = new FeedMediaTopicEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        entity.createdDate = in.readLong();
        entity.hasMore = in.readBoolean();
        entity.isSticky = in.readBoolean();
        entity.isUnmodifiable = in.readBoolean();
        in.readArrayList(entity.mediaItemsBuilders);
        entity.authorRef = in.readString();
        entity.friendRefs = in.readStringArrayList();
        entity.placesRefs = in.readStringArrayList();
        entity.ownerRef = in.readString();
        entity.markAsSpamId = in.readString();
        entity.deleteId = in.readString();
        if (version > 1) {
            entity.isPromo = in.readBoolean();
        }
        return entity;
    }
}
