package ru.ok.model;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class DiscussionSerializer {
    public static void write(SimpleSerialOutputStream out, Discussion disc) throws IOException {
        out.writeInt(1);
        out.writeString(disc.id);
        out.writeString(disc.type);
    }

    public static Discussion read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version == 1) {
            return new Discussion(in.readString(), in.readString());
        }
        throw new SimpleSerialException("Unsupported serial version: " + version);
    }
}
