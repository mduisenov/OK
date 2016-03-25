package ru.ok.model.stream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedStringRefsSerializer {
    public static void write(SimpleSerialOutputStream out, FeedStringRefs refs) throws IOException {
        out.writeInt(2);
        out.writeInt(size);
        for (List writeStringList : refs.refs) {
            out.writeStringList(writeStringList);
        }
    }

    public static FeedStringRefs read(SimpleSerialInputStream in) throws IOException {
        FeedStringRefs refs = new FeedStringRefs();
        read(in, refs);
        return refs;
    }

    static void read(SimpleSerialInputStream in, FeedStringRefs refs) throws IOException {
        int version = in.readInt();
        if (version < 1 || version > 2) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        int size = in.readInt();
        int length = refs.refs.length;
        int role = 0;
        while (role < size) {
            ArrayList<String> list = in.readStringArrayList();
            if (role < length) {
                if (version == 1 && role == 4 && list != null) {
                    refs.set(9, extractBanners(list));
                    if (list.isEmpty()) {
                        list = null;
                    }
                }
                refs.refs[role] = list;
            }
            role++;
        }
    }

    private static ArrayList<String> extractBanners(ArrayList<String> list) {
        ArrayList<String> banners = null;
        for (int j = list.size() - 1; j >= 0; j--) {
            String ref = (String) list.get(j);
            if (ref != null && ref.startsWith("banner")) {
                if (banners == null) {
                    banners = new ArrayList();
                }
                banners.add(ref);
                list.remove(j);
            }
        }
        return banners;
    }
}
