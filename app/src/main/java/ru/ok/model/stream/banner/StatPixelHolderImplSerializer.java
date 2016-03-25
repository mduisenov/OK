package ru.ok.model.stream.banner;

import android.support.annotation.NonNull;
import java.io.IOException;
import java.util.ArrayList;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class StatPixelHolderImplSerializer {
    public static void write(SimpleSerialOutputStream out, @NonNull StatPixelHolderImpl holder) throws IOException {
        boolean z = true;
        out.writeInt(1);
        if (holder.pixels == null) {
            z = false;
        }
        out.writeBoolean(z);
        if (holder.pixels != null) {
            out.writeInt(length);
            for (ArrayList<String> urls : holder.pixels) {
                out.writeStringList(urls);
            }
        }
    }

    @NonNull
    public static StatPixelHolderImpl read(SimpleSerialInputStream in) throws IOException {
        StatPixelHolderImpl holder = new StatPixelHolderImpl();
        read(in, holder);
        return holder;
    }

    public static void read(SimpleSerialInputStream in, StatPixelHolderImpl holder) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new IOException("Unsupported simple serial version ID: " + version);
        }
        readPixelsArray(in, holder);
    }

    public static void readPixelsArray(SimpleSerialInputStream in, StatPixelHolderImpl holder) throws IOException {
        ArrayList<String>[] pixels = null;
        if (in.readBoolean()) {
            pixels = new ArrayList[29];
            int storedLength = in.readInt();
            for (int i = 0; i < storedLength; i++) {
                ArrayList<String> urls = in.readStringArrayList();
                if (i < pixels.length) {
                    pixels[i] = urls;
                }
            }
        }
        holder.pixels = pixels;
    }
}
