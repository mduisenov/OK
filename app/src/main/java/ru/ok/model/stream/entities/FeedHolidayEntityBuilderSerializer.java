package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedHolidayEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedHolidayEntityBuilder entity) throws IOException {
        out.writeInt(1);
        BaseEntityBuilderSerializer.write(out, entity);
    }

    public static FeedHolidayEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedHolidayEntityBuilder entity = new FeedHolidayEntityBuilder();
        BaseEntityBuilderSerializer.read(in, entity);
        return entity;
    }
}
