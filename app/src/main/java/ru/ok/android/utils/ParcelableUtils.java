package ru.ok.android.utils;

import android.os.Parcel;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public final class ParcelableUtils {
    public static <K, V> void writeLinkedMap(LinkedHashMap<K, V> map, Parcel dest, int flags) {
        if (map == null) {
            dest.writeInt(-1);
            return;
        }
        dest.writeInt(map.size());
        for (Entry<K, V> entry : map.entrySet()) {
            dest.writeValue(entry.getKey());
            dest.writeValue(entry.getValue());
        }
    }

    public static <K, V> LinkedHashMap<K, V> readLinkedMap(Parcel src, ClassLoader cl) {
        int size = src.readInt();
        if (size < 0) {
            return null;
        }
        LinkedHashMap<K, V> map = new LinkedHashMap(size);
        for (int i = 0; i < size; i++) {
            map.put(src.readValue(cl), src.readValue(cl));
        }
        return map;
    }
}
