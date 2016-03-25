package ru.ok.model.stream;

import android.support.annotation.NonNull;
import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public class HolidaysSerializer {
    public static void write(@NonNull SimpleSerialOutputStream out, @NonNull Holidays holidays) throws IOException {
        out.writeInt(1);
        out.writeList(holidays.holidays);
        out.writeStringMap(holidays.userEntities);
    }

    @NonNull
    public static Holidays read(@NonNull SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        Holidays holidays = new Holidays();
        in.readArrayList(holidays.holidays);
        in.readStringHashMap(holidays.userEntities);
        return holidays;
    }
}
