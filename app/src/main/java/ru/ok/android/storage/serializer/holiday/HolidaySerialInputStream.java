package ru.ok.android.storage.serializer.holiday;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.model.UserInfoLocationSerializer;
import ru.ok.model.UserInfoSerializer;
import ru.ok.model.UserStatusSerializer;
import ru.ok.model.stream.HolidaySerializer;
import ru.ok.model.stream.HolidaysSerializer;

public class HolidaySerialInputStream extends SimpleSerialInputStream {
    public HolidaySerialInputStream(@NonNull InputStream in) {
        super(in);
    }

    @Nullable
    public <T> T readObject() throws IOException {
        if (!readBoolean()) {
            return null;
        }
        int type = readInt();
        switch (type) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return HolidaySerializer.read(this);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return UserInfoSerializer.read(this);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return HolidaysSerializer.read(this);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return UserInfoLocationSerializer.read(this);
            case Message.UUID_FIELD_NUMBER /*5*/:
                return UserStatusSerializer.read(this);
            default:
                try {
                    throw new SimpleSerialException("Unexpected type: " + type);
                } catch (ClassCastException e) {
                    throw new SimpleSerialException("Type mismatch: " + e, e);
                }
        }
        throw new SimpleSerialException("Type mismatch: " + e, e);
    }
}
