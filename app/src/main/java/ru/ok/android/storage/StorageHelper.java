package ru.ok.android.storage;

import android.content.Context;
import java.io.File;

public final class StorageHelper {
    static File getRootDir(Context context) {
        return context.getFilesDir();
    }

    public static boolean removeFile(Context context, String fileName) {
        return getFileByName(context, fileName).delete();
    }

    public static File getFileByName(Context context, String fileName) {
        return new File(getRootDir(context), fileName);
    }
}
