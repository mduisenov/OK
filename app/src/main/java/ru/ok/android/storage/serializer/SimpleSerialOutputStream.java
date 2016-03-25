package ru.ok.android.storage.serializer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class SimpleSerialOutputStream extends DataOutputStream {
    public SimpleSerialOutputStream(OutputStream out) {
        super(out);
    }

    public void writeString(String s) throws IOException {
        if (s == null) {
            write(0);
        } else if (s.length() < 65535) {
            write(1);
            writeUTF(s);
        } else {
            write(2);
            byte[] bytes = s.getBytes();
            writeInt(bytes.length);
            write(bytes);
        }
    }

    public void writeList(List list) throws IOException {
        writeBoolean(list != null);
        if (list != null) {
            int size = list.size();
            writeInt(size);
            for (int i = 0; i < size; i++) {
                writeObject(list.get(i));
            }
        }
    }

    public void writeStringList(List<String> list) throws IOException {
        writeBoolean(list != null);
        if (list != null) {
            int size = list.size();
            writeInt(size);
            for (int i = 0; i < size; i++) {
                writeString((String) list.get(i));
            }
        }
    }

    public void writeStringMap(Map<String, ?> map) throws IOException {
        writeBoolean(map != null);
        if (map != null) {
            writeInt(map.size());
            for (Entry<String, ?> entry : map.entrySet()) {
                writeString((String) entry.getKey());
                writeObject(entry.getValue());
            }
        }
    }

    public <T extends Enum> void writeEnum(T value) throws IOException {
        writeBoolean(value != null);
        if (value != null) {
            writeInt(value.ordinal());
        }
    }

    public <T extends Enum> void writeEnumList(List<T> list) throws IOException {
        writeBoolean(list != null);
        if (list != null) {
            int size = list.size();
            writeInt(size);
            for (int i = 0; i < size; i++) {
                writeEnum((Enum) list.get(i));
            }
        }
    }

    public void writeDate(Date date) throws IOException {
        writeBoolean(date != null);
        if (date != null) {
            writeLong(date.getTime());
        }
    }

    public void writeObject(Object o) throws IOException {
        throw new SimpleSerialException("Must implement writeObject() for class: " + o.getClass());
    }
}
