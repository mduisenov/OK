package ru.ok.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import org.jivesoftware.smack.util.StringUtils;
import ru.ok.android.utils.Storage.External;
import ru.ok.android.utils.Storage.External.Application;

public class FileUtils {
    public static File generateEmptyFile(File where, String extension) throws IOException {
        if (where.exists() && !where.isDirectory()) {
            throw new IOException("Not directory: " + where.getPath());
        } else if (where.exists() || where.mkdirs()) {
            File result;
            do {
                result = new File(where, System.currentTimeMillis() + extension);
            } while (result.exists());
            if (!result.createNewFile()) {
                Logger.m177e("Not possible to create a new file (%s)", result);
            }
            return result;
        } else {
            throw new IOException("Failed to create directory: " + where.getPath());
        }
    }

    public static void copyFile(File src, File dest, int bufferSize) throws IOException {
        Closeable srcChannel = null;
        Closeable destChannel = null;
        try {
            srcChannel = new FileInputStream(src).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            if (srcChannel == null || destChannel == null) {
                throw new IOException("Can't open channels for copy");
            }
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufferSize);
            while (srcChannel.read(byteBuffer) != -1) {
                byteBuffer.flip();
                destChannel.write(byteBuffer);
                byteBuffer.clear();
            }
        } finally {
            IOUtils.closeSilently(srcChannel);
            IOUtils.closeSilently(destChannel);
        }
    }

    public static void moveFile(File from, File to) throws IOException {
        if (!from.renameTo(to)) {
            copyFile(from, to, FragmentTransaction.TRANSIT_EXIT_MASK);
            if (!from.delete()) {
                if (to.delete()) {
                    throw new IOException("Unable to delete " + from);
                }
                throw new IOException("Unable to delete " + to);
            }
        }
    }

    public static Uri saveBitmapToFile(Bitmap bitmap, File file) throws FileNotFoundException {
        Throwable th;
        if (bitmap != null) {
            Closeable outputStream = null;
            try {
                Closeable outputStream2 = new FileOutputStream(file);
                try {
                    bitmap.compress(CompressFormat.PNG, 100, outputStream2);
                    IOUtils.closeSilently(outputStream2);
                } catch (Throwable th2) {
                    th = th2;
                    outputStream = outputStream2;
                    IOUtils.closeSilently(outputStream);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                IOUtils.closeSilently(outputStream);
                throw th;
            }
        }
        return Uri.fromFile(file);
    }

    public static void deleteFileAtUri(Uri uri) {
        if (new File(uri.getPath()).delete()) {
            Logger.m177e("Unable to delete file at uri (%s)", uri);
        }
    }

    public static boolean copyExif(File src, File dest) {
        try {
            ExifInterface oldexif = new ExifInterface(src.getAbsolutePath());
            ExifInterface newexif = new ExifInterface(dest.getAbsolutePath());
            int build = VERSION.SDK_INT;
            if (build >= 11) {
                if (oldexif.getAttribute("FNumber") != null) {
                    newexif.setAttribute("FNumber", oldexif.getAttribute("FNumber"));
                }
                if (oldexif.getAttribute("ExposureTime") != null) {
                    newexif.setAttribute("ExposureTime", oldexif.getAttribute("ExposureTime"));
                }
                if (oldexif.getAttribute("ISOSpeedRatings") != null) {
                    newexif.setAttribute("ISOSpeedRatings", oldexif.getAttribute("ISOSpeedRatings"));
                }
            }
            if (build >= 9) {
                if (oldexif.getAttribute("GPSAltitude") != null) {
                    newexif.setAttribute("GPSAltitude", oldexif.getAttribute("GPSAltitude"));
                }
                if (oldexif.getAttribute("GPSAltitudeRef") != null) {
                    newexif.setAttribute("GPSAltitudeRef", oldexif.getAttribute("GPSAltitudeRef"));
                }
            }
            if (build >= 8) {
                if (oldexif.getAttribute("FocalLength") != null) {
                    newexif.setAttribute("FocalLength", oldexif.getAttribute("FocalLength"));
                }
                if (oldexif.getAttribute("GPSDateStamp") != null) {
                    newexif.setAttribute("GPSDateStamp", oldexif.getAttribute("GPSDateStamp"));
                }
                if (oldexif.getAttribute("GPSProcessingMethod") != null) {
                    newexif.setAttribute("GPSProcessingMethod", oldexif.getAttribute("GPSProcessingMethod"));
                }
                if (oldexif.getAttribute("GPSTimeStamp") != null) {
                    newexif.setAttribute("GPSTimeStamp", oldexif.getAttribute("GPSTimeStamp"));
                }
            }
            if (oldexif.getAttribute("DateTime") != null) {
                newexif.setAttribute("DateTime", oldexif.getAttribute("DateTime"));
            }
            if (oldexif.getAttribute("Flash") != null) {
                newexif.setAttribute("Flash", oldexif.getAttribute("Flash"));
            }
            if (oldexif.getAttribute("GPSLatitude") != null) {
                newexif.setAttribute("GPSLatitude", oldexif.getAttribute("GPSLatitude"));
            }
            if (oldexif.getAttribute("GPSLatitudeRef") != null) {
                newexif.setAttribute("GPSLatitudeRef", oldexif.getAttribute("GPSLatitudeRef"));
            }
            if (oldexif.getAttribute("GPSLongitude") != null) {
                newexif.setAttribute("GPSLongitude", oldexif.getAttribute("GPSLongitude"));
            }
            if (oldexif.getAttribute("GPSLatitudeRef") != null) {
                newexif.setAttribute("GPSLongitudeRef", oldexif.getAttribute("GPSLongitudeRef"));
            }
            if (oldexif.getAttribute("Make") != null) {
                newexif.setAttribute("Make", oldexif.getAttribute("Make"));
            }
            if (oldexif.getAttribute("Model") != null) {
                newexif.setAttribute("Model", oldexif.getAttribute("Model"));
            }
            if (oldexif.getAttribute("Orientation") != null) {
                newexif.setAttribute("Orientation", oldexif.getAttribute("Orientation"));
            }
            if (oldexif.getAttribute("WhiteBalance") != null) {
                newexif.setAttribute("WhiteBalance", oldexif.getAttribute("WhiteBalance"));
            }
            newexif.saveAttributes();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static File getCacheDir(Context context, String cacheFolder) {
        if (External.externalMemoryAvailable()) {
            return new File(Application.getCacheDir(context), cacheFolder);
        }
        return context.getCacheDir();
    }

    public static boolean isExternalStoragePath(File file) {
        return isExternalStoragePath(file == null ? "" : file.getAbsolutePath());
    }

    public static boolean isExternalStoragePath(String pathname) {
        if (pathname == null) {
            return false;
        }
        return pathname.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    public static long folderSize(File directory) {
        if (directory == null) {
            return 0;
        }
        long result = directory.length();
        if (!directory.isDirectory()) {
            return result;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return result;
        }
        for (File file : files) {
            result += folderSize(file);
        }
        return result;
    }

    public static boolean deleteFolder(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return directory.delete();
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int index = indexOfExtension(fileName);
        if (index != -1) {
            return fileName.substring(index + 1);
        }
        return null;
    }

    private static int indexOfExtension(String fileName) {
        if (fileName == null) {
            return -1;
        }
        int extensionIndex = fileName.lastIndexOf(46);
        if (fileName.lastIndexOf(File.separatorChar) > extensionIndex) {
            extensionIndex = -1;
        }
        return extensionIndex;
    }

    public static String getFileExtension(String fileName, String mimeType) {
        String ext = getFileExtension(fileName);
        if (mimeType == null) {
            return ext;
        }
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeTypeFromExt = mimeTypeMap.getMimeTypeFromExtension(ext);
        if (mimeTypeFromExt == null || !mimeTypeFromExt.equalsIgnoreCase(mimeType)) {
            return mimeTypeMap.getExtensionFromMimeType(mimeType);
        }
        return ext;
    }

    public static String getMimeType(String fileName) {
        String ext = getFileExtension(fileName);
        if (TextUtils.isEmpty(ext)) {
            return null;
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }

    public static void mkdirsChecked(File dir) throws IOException {
        if (dir.exists()) {
            if (!dir.isDirectory()) {
                throw new IOException("File " + dir + " is not a directory");
            }
        } else if (!dir.mkdirs()) {
            throw new IOException("Can't create dir: " + dir);
        }
    }

    public static void copyToFile(InputStream fromStream, File toFile, IOProgressCallbalck callback, long progreesCalbackStep) throws IOException {
        Throwable th;
        Closeable out = null;
        try {
            byte[] buffer = new byte[32768];
            File destDir = toFile.getParentFile();
            if ((destDir.exists() || destDir.mkdirs()) && destDir.isDirectory()) {
                if (progreesCalbackStep <= 0) {
                    progreesCalbackStep = 1;
                }
                try {
                    Closeable out2 = new FileOutputStream(toFile);
                    long totalBytes = 0;
                    long nextProgress = progreesCalbackStep;
                    while (true) {
                        try {
                            int bytesRead = fromStream.read(buffer);
                            if (bytesRead == -1) {
                                break;
                            }
                            out2.write(buffer, 0, bytesRead);
                            totalBytes += (long) bytesRead;
                            if (callback != null && totalBytes >= nextProgress) {
                                callback.onIOProgress(totalBytes);
                                nextProgress = ((totalBytes / progreesCalbackStep) + 1) * progreesCalbackStep;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            out = out2;
                        }
                    }
                    if (callback != null) {
                        callback.onIOProgress(totalBytes);
                    }
                    IOUtils.closeSilently(out2);
                    return;
                } catch (Throwable th3) {
                    th = th3;
                    IOUtils.closeSilently(out);
                    throw th;
                }
            }
            throw new IOException("Failed to create destination directory: " + destDir);
        } catch (OutOfMemoryError e) {
            throw new IOException("Not enough memory for IO buffer");
        }
    }

    public static String id2filename(String id) {
        try {
            id = URLEncoder.encode(id, StringUtils.UTF8);
        } catch (UnsupportedEncodingException e) {
        }
        return id;
    }
}
