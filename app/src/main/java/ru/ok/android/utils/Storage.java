package ru.ok.android.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import java.io.File;

public final class Storage {

    public static final class External {

        public static final class Application {
            public static final File getDirectory(Context context, String dirName) {
                File result;
                File result2 = null;
                File externalPath = Environment.getExternalStorageDirectory();
                if (externalPath != null) {
                    result2 = new File(externalPath.getAbsolutePath() + "/Android/data/" + context.getPackageName() + File.separator);
                    if (!(result2.exists() || result2.mkdirs())) {
                        result = null;
                        if (dirName != null || result == null || !result.exists()) {
                            return result;
                        }
                        result2 = new File(result.getAbsolutePath() + File.separator + dirName + File.separator);
                        if (result2.exists() || result2.mkdirs()) {
                            return result2;
                        }
                        return null;
                    }
                }
                result = result2;
                if (dirName != null) {
                }
                return result;
            }

            public static final File getFilesDir(Context context) {
                return getDirectory(context, "files");
            }

            public static final File getCacheDir(Context context) {
                return context.getExternalCacheDir();
            }
        }

        public static boolean externalMemoryAvailable() {
            return Environment.getExternalStorageState().equals("mounted");
        }
    }

    public static long getAvailableSize(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        StatFs stat = new StatFs(dir.getPath());
        return ((long) stat.getAvailableBlocks()) * ((long) stat.getBlockSize());
    }
}
