package ru.ok.model.stream.entities;

import java.io.IOException;
import java.util.Iterator;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.photo.PhotoSize;

public final class FeedAchievementTypeEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedAchievementTypeEntityBuilder entity) throws IOException {
        out.writeInt(1);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeInt(entity.pics.size());
        Iterator i$ = entity.pics.iterator();
        while (i$.hasNext()) {
            out.writeObject((PhotoSize) i$.next());
        }
        out.writeString(entity.title);
    }

    public static FeedAchievementTypeEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedAchievementTypeEntityBuilder entity = new FeedAchievementTypeEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        int picsCount = in.readInt();
        for (int i = 0; i < picsCount; i++) {
            entity.pics.add((PhotoSize) in.readObject());
        }
        entity.title = in.readString();
        return entity;
    }
}
