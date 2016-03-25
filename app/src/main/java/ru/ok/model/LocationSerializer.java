package ru.ok.model;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class LocationSerializer {
    public static void write(SimpleSerialOutputStream out, Location location) throws IOException {
        boolean z = true;
        out.writeInt(1);
        out.writeBoolean(location.lat != null);
        if (location.lat != null) {
            out.writeDouble(location.lat.doubleValue());
        }
        if (location.lng == null) {
            z = false;
        }
        out.writeBoolean(z);
        if (location.lng != null) {
            out.writeDouble(location.lng.doubleValue());
        }
    }

    public static Location read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        Location location = new Location();
        if (in.readBoolean()) {
            location.lat = Double.valueOf(in.readDouble());
        }
        if (in.readBoolean()) {
            location.lng = Double.valueOf(in.readDouble());
        }
        return location;
    }
}
