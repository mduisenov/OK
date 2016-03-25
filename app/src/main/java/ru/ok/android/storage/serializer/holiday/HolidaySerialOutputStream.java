package ru.ok.android.storage.serializer.holiday;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.Location;
import ru.ok.model.UserInfoLocationSerializer;
import ru.ok.model.UserInfoSerializer;
import ru.ok.model.UserStatus;
import ru.ok.model.UserStatusSerializer;
import ru.ok.model.stream.Holiday;
import ru.ok.model.stream.HolidaySerializer;
import ru.ok.model.stream.Holidays;
import ru.ok.model.stream.HolidaysSerializer;

public class HolidaySerialOutputStream extends SimpleSerialOutputStream {
    public HolidaySerialOutputStream(@NonNull OutputStream out) {
        super(out);
    }

    public void writeObject(@Nullable Object o) throws IOException {
        writeBoolean(o != null);
        if (o != null) {
            Class klass = o.getClass();
            if (klass == Holiday.class) {
                writeInt(1);
                HolidaySerializer.write(this, (Holiday) o);
            } else if (klass == UserInfo.class) {
                writeInt(2);
                UserInfoSerializer.write(this, (UserInfo) o);
            } else if (klass == Holidays.class) {
                writeInt(3);
                HolidaysSerializer.write(this, (Holidays) o);
            } else if (klass == Location.class) {
                writeInt(4);
                UserInfoLocationSerializer.write(this, (Location) o);
            } else if (klass == UserStatus.class) {
                writeInt(5);
                UserStatusSerializer.write(this, (UserStatus) o);
            } else {
                throw new SimpleSerialException("Not simple serializable class: " + klass.getName());
            }
        }
    }
}
