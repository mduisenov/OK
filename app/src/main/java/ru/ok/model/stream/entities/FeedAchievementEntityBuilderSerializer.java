package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedAchievementEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedAchievementEntityBuilder entity) throws IOException {
        out.writeInt(1);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeString(entity.receiverRef);
        out.writeString(entity.achievementTypeRef);
    }

    public static FeedAchievementEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedAchievementEntityBuilder entity = new FeedAchievementEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        entity.receiverRef = in.readString();
        entity.achievementTypeRef = in.readString();
        return entity;
    }
}
