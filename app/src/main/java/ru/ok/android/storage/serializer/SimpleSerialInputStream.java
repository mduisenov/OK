package ru.ok.android.storage.serializer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SimpleSerialInputStream extends DataInputStream {
    public SimpleSerialInputStream(InputStream in) {
        super(in);
    }

    public String readString() throws IOException {
        int mark = read();
        if (mark == 0) {
            return null;
        }
        if (mark == 1) {
            return readUTF();
        }
        if (mark != 2) {
            return null;
        }
        int length = readInt();
        byte[] buffer = new byte[length];
        int readLength = read(buffer);
        if (length == readLength) {
            return new String(buffer);
        }
        throw new IOException("Expected length(" + length + ") != read length (" + readLength + " )");
    }

    public <T> ArrayList<T> readArrayList() throws IOException {
        ArrayList<T> list = null;
        if (readBoolean()) {
            int size = readInt();
            list = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                list.add(readObject());
            }
        }
        return list;
    }

    public <T> void readArrayList(ArrayList<T> outList) throws IOException {
        readArrayList(outList, true);
    }

    public <T> void readArrayList(ArrayList<T> outList, boolean skipNulls) throws IOException {
        if (readBoolean()) {
            int size = readInt();
            outList.ensureCapacity(size);
            for (int i = 0; i < size; i++) {
                T obj = readObject();
                if (obj != null || !skipNulls) {
                    outList.add(obj);
                }
            }
        }
    }

    public ArrayList<String> readStringArrayList() throws IOException {
        ArrayList<String> list = null;
        if (readBoolean()) {
            int size = readInt();
            list = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                list.add(readString());
            }
        }
        return list;
    }

    public void readStringArrayList(ArrayList<String> outList) throws IOException {
        if (readBoolean()) {
            int size = readInt();
            outList.ensureCapacity(size);
            for (int i = 0; i < size; i++) {
                outList.add(readString());
            }
        }
    }

    public <T> void readStringHashMap(HashMap<String, T> map) throws IOException {
        if (readBoolean()) {
            int size = readInt();
            for (int i = 0; i < size; i++) {
                map.put(readString(), readObject());
            }
        }
    }

    public <T extends Enum> T readEnum(Class<T> klass) throws IOException {
        if (!readBoolean()) {
            return null;
        }
        int ordinal = readInt();
        Enum[] values = (Enum[]) klass.getEnumConstants();
        if (ordinal >= 0 && ordinal < values.length) {
            return values[ordinal];
        }
        throw new SimpleSerialException("Ordinal out of range for enum class: " + klass.getName());
    }

    public <T extends Enum> ArrayList<T> readEnumArrayList(Class<T> klass) throws IOException {
        if (!readBoolean()) {
            return null;
        }
        int size = readInt();
        ArrayList<T> list = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            list.add(readEnum(klass));
        }
        return list;
    }

    public Date readDate() throws IOException {
        if (readBoolean()) {
            return new Date(readLong());
        }
        return null;
    }

    public <T> T readObject() throws IOException {
        throw new SimpleSerialException("Must implement readObject()");
    }
}
