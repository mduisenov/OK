package ru.ok.model;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class AddressSerializer {
    public static void write(SimpleSerialOutputStream out, Address address) throws IOException {
        out.writeInt(1);
        out.writeString(address.country);
        out.writeString(address.countryISO);
        out.writeString(address.city);
        out.writeString(address.cityId);
        out.writeString(address.street);
        out.writeString(address.house);
    }

    public static Address read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        Address address = new Address();
        address.country = in.readString();
        address.countryISO = in.readString();
        address.city = in.readString();
        address.cityId = in.readString();
        address.street = in.readString();
        address.house = in.readString();
        return address;
    }
}
