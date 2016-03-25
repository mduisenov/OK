package ru.ok.android.services.processors.stream;

import android.support.annotation.NonNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import ru.ok.android.storage.ISerializer;
import ru.ok.android.storage.serializer.holiday.HolidaySerialInputStream;
import ru.ok.android.storage.serializer.holiday.HolidaySerialOutputStream;
import ru.ok.model.stream.Holidays;

public class StreamHolidaySerializer implements ISerializer<Holidays> {
    public void write(@NonNull Holidays obj, @NonNull OutputStream out) throws IOException {
        new HolidaySerialOutputStream(out).writeObject(obj);
    }

    @NonNull
    public Holidays read(@NonNull InputStream in) throws IOException {
        return (Holidays) new HolidaySerialInputStream(in).readObject();
    }
}
