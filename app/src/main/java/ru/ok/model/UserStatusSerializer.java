package ru.ok.model;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class UserStatusSerializer {
    public static void write(SimpleSerialOutputStream out, UserStatus status) throws IOException {
        out.writeInt(1);
        out.writeString(status.id);
        out.writeString(status.text);
        out.writeLong(status.date);
        out.writeLong(status.trackId);
    }

    public static UserStatus read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version == 1) {
            return new UserStatus(in.readString(), in.readString(), in.readLong(), in.readLong());
        }
        throw new SimpleSerialException("Unsupported serial version: " + version);
    }
}
