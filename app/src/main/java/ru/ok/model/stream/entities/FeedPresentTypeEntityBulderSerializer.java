package ru.ok.model.stream.entities;

import java.io.IOException;
import java.util.Iterator;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.presents.AnimationProperties;

public final class FeedPresentTypeEntityBulderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedPresentTypeEntityBuilder entity) throws IOException {
        out.writeInt(2);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeInt(entity.pics.size());
        Iterator i$ = entity.pics.iterator();
        while (i$.hasNext()) {
            out.writeObject((PhotoSize) i$.next());
        }
        out.writeInt(entity.sprites.size());
        i$ = entity.sprites.iterator();
        while (i$.hasNext()) {
            out.writeObject((PhotoSize) i$.next());
        }
        out.writeBoolean(entity.isAnimated);
        out.writeBoolean(entity.isLive);
        out.writeBoolean(entity.animationProperties != null);
        if (entity.animationProperties != null) {
            out.writeInt(entity.animationProperties.duration);
            out.writeInt(entity.animationProperties.framesCount);
            out.writeInt(entity.animationProperties.replayDelay);
        }
    }

    public static FeedPresentTypeEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version < 1 || version > 2) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        int i;
        FeedPresentTypeEntityBuilder entity = new FeedPresentTypeEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        int photoCount = in.readInt();
        for (i = 0; i < photoCount; i++) {
            entity.pics.add((PhotoSize) in.readObject());
        }
        if (version >= 2) {
            int spriteCount = in.readInt();
            for (i = 0; i < spriteCount; i++) {
                entity.sprites.add((PhotoSize) in.readObject());
            }
            entity.isAnimated = in.readBoolean();
            entity.isLive = in.readBoolean();
            if (in.readBoolean()) {
                entity.animationProperties = new AnimationProperties(in.readInt(), in.readInt(), in.readInt());
            }
        }
        return entity;
    }
}
