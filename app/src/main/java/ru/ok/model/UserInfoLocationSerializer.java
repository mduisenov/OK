package ru.ok.model;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.UserInfo.Location;

public final class UserInfoLocationSerializer {
    public static void write(SimpleSerialOutputStream out, Location location) throws IOException {
        out.writeInt(1);
        out.writeString(location.countryCode);
        out.writeString(location.country);
        out.writeString(location.city);
    }

    public static Location read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version == 1) {
            return new Location(in.readString(), in.readString(), in.readString());
        }
        throw new SimpleSerialException("Unsupported serial version: " + version);
    }
}
