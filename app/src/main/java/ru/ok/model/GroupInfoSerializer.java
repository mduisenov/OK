package ru.ok.model;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class GroupInfoSerializer {
    public static void write(SimpleSerialOutputStream out, GroupInfo info) throws IOException {
        out.writeInt(1);
        out.writeString(info.id);
        out.writeString(info.name);
        out.writeString(info.description);
        out.writeInt(info.membersCount);
        out.writeString(info.avatarUrl);
        out.writeInt(info.flags);
        out.writeString(info.photoId);
        out.writeString(info.bigPicUrl);
        out.writeEnum(info.type);
        out.writeString(info.adminUid);
        out.writeLong(info.createdMs);
        out.writeObject(info.address);
        out.writeObject(info.location);
        out.writeString(info.scope);
        out.writeLong(info.start_date);
        out.writeLong(info.end_date);
        out.writeString(info.webUrl);
        out.writeString(info.phone);
        out.writeObject(info.subCategory);
        out.writeBoolean(info.business);
        out.writeBoolean(info.isAllDataAvailable);
        out.writeLong(info.unreadEventsCount);
        out.writeString(info.status);
    }

    public static GroupInfo read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        GroupInfo info = new GroupInfo();
        info.id = in.readString();
        info.name = in.readString();
        info.description = in.readString();
        info.membersCount = in.readInt();
        info.avatarUrl = in.readString();
        info.flags = in.readInt();
        info.photoId = in.readString();
        info.bigPicUrl = in.readString();
        info.type = (GroupType) in.readEnum(GroupType.class);
        info.adminUid = in.readString();
        info.createdMs = in.readLong();
        info.address = (Address) in.readObject();
        info.location = (Location) in.readObject();
        info.scope = in.readString();
        info.start_date = in.readLong();
        info.end_date = in.readLong();
        info.webUrl = in.readString();
        info.phone = in.readString();
        info.subCategory = (GroupSubCategory) in.readObject();
        info.business = in.readBoolean();
        info.isAllDataAvailable = in.readBoolean();
        info.unreadEventsCount = in.readLong();
        info.status = in.readString();
        return info;
    }
}
