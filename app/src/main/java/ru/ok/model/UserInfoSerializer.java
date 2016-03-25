package ru.ok.model;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.UserInfo.Location;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.UserInfo.UserOnlineType;

public final class UserInfoSerializer {
    public static void write(SimpleSerialOutputStream out, UserInfo info) throws IOException {
        out.writeInt(1);
        out.writeString(info.uid);
        out.writeString(info.firstName);
        out.writeString(info.lastName);
        out.writeString(info.name);
        out.writeString(info.picUrl);
        out.writeString(info.bigPicUrl);
        out.writeString(info.pid);
        out.writeEnum(info.online);
        out.writeLong(info.lastOnline);
        out.writeEnum(info.genderType);
        out.writeBoolean(info.availableCall);
        out.writeBoolean(info.availableVMail);
        out.writeString(info.tag);
        out.writeInt(info.age);
        out.writeObject(info.location);
        out.writeBoolean(info.showLock);
        out.writeBoolean(info.privateProfile);
        out.writeBoolean(info.premiumProfile);
        out.writeBoolean(info.hasServiceInvisible);
        out.writeBoolean(info.isAllDataAvailable);
        out.writeObject(info.status);
        out.writeDate(info.birthday);
    }

    public static UserInfo read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        String uid = in.readString();
        String firstName = in.readString();
        String lastName = in.readString();
        String name = in.readString();
        String picUrl = in.readString();
        String bigPicUrl = in.readString();
        String pid = in.readString();
        UserOnlineType online = (UserOnlineType) in.readEnum(UserOnlineType.class);
        long lastOnline = in.readLong();
        UserGenderType genderType = (UserGenderType) in.readEnum(UserGenderType.class);
        boolean availableCall = in.readBoolean();
        boolean availableVMail = in.readBoolean();
        String tag = in.readString();
        int age = in.readInt();
        Location location = (Location) in.readObject();
        boolean showLock = in.readBoolean();
        return new UserInfo(uid, firstName, lastName, name, picUrl, null, null, null, age, location, online, lastOnline, genderType, availableCall, availableVMail, tag, pid, bigPicUrl, in.readBoolean(), in.readBoolean(), in.readBoolean(), (UserStatus) in.readObject(), in.readDate(), in.readBoolean(), showLock);
    }
}
