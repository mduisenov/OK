package ru.ok.model;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class GroupSubCategorySerializer {
    public static void write(SimpleSerialOutputStream out, GroupSubCategory data) throws IOException {
        out.writeInt(1);
        out.writeString(data.id);
        out.writeString(data.name);
    }

    public static GroupSubCategory read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        GroupSubCategory data = new GroupSubCategory();
        data.id = in.readString();
        data.name = in.readString();
        return data;
    }
}
