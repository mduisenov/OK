package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.stream.banner.BannerBuilder;

public final class FeedBannerEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedBannerEntityBuilder entity) throws IOException {
        out.writeInt(1);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeObject(entity.banner);
    }

    public static FeedBannerEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedBannerEntityBuilder entity = new FeedBannerEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        entity.banner = (BannerBuilder) in.readObject();
        return entity;
    }
}
