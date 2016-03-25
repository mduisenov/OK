package ru.ok.model.stream;

import android.support.annotation.NonNull;
import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class HolidaySerializer {
    public static void write(@NonNull SimpleSerialOutputStream out, @NonNull Holiday holiday) throws IOException {
        out.writeInt(1);
        out.writeString(holiday.id);
        out.writeString(holiday.message);
        out.writeInt(holiday.day);
        out.writeInt(holiday.month);
        out.writeBoolean(holiday.isNameday);
        out.writeStringList(holiday.userIds);
    }

    @NonNull
    public static Holiday read(@NonNull SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        Holiday holiday = new Holiday();
        holiday.id = in.readString();
        holiday.message = in.readString();
        holiday.day = in.readInt();
        holiday.month = in.readInt();
        holiday.isNameday = in.readBoolean();
        in.readStringArrayList(holiday.userIds);
        return holiday;
    }
}
