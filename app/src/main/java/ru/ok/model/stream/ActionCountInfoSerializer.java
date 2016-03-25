package ru.ok.model.stream;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class ActionCountInfoSerializer {
    public static void write(SimpleSerialOutputStream out, ActionCountInfo info) throws IOException {
        out.writeInt(1);
        out.writeInt(info.count);
        out.writeBoolean(info.self);
        out.writeLong(info.lastDate);
    }

    public static ActionCountInfo read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version == 1) {
            return new ActionCountInfo(in.readInt(), in.readBoolean(), in.readLong());
        }
        throw new SimpleSerialException("Unsupported serial version: " + version);
    }
}
