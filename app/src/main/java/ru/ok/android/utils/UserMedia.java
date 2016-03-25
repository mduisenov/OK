package ru.ok.android.utils;

import android.content.Context;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.format.Time;
import java.io.File;
import java.io.IOException;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.Storage.External.User;

public class UserMedia {
    private static final String[] ORIENTATION_PROJECTION;

    public interface OnImageAddedListener {
        void onImageAdded(String str, Uri uri);
    }

    public static void addImageToMedia(File image, Context context, OnImageAddedListener onImageAddedListener) {
        new SingleMediaScanner(context, image, onImageAddedListener).scan();
    }

    public static File copyImageToUserGallery(File src, Context context, OnImageAddedListener onImageAddedListener) {
        File galleryFolder = User.getUserPicturesDirectory(context);
        if (galleryFolder == null) {
            return null;
        }
        File dest = new File(galleryFolder.getPath() + File.separator + generateFileName("IMG", null, "jpg"));
        try {
            FileUtils.copyFile(src, dest, 1024);
            addImageToMedia(dest, context, onImageAddedListener);
            return dest;
        } catch (IOException exc) {
            Logger.m180e(exc, "Not possible to add image (%s) to media", src);
            return null;
        }
    }

    public static String generateFileName(String prefix, String postfix, String extension) {
        Time time = new Time();
        time.setToNow();
        StringBuilder sb = new StringBuilder();
        if (prefix != null) {
            sb.append(prefix).append("_");
        }
        sb.append(time.year);
        append2Digits(sb, time.month + 1);
        append2Digits(sb, time.monthDay).append("_");
        append2Digits(sb, time.hour);
        append2Digits(sb, time.minute);
        append2Digits(sb, time.second);
        if (postfix != null) {
            sb.append("_").append(postfix);
        }
        if (extension != null) {
            sb.append('.').append(extension);
        }
        return sb.toString();
    }

    private static StringBuilder append2Digits(StringBuilder sb, int value) {
        if (value < 10) {
            sb.append('0');
        }
        sb.append(value);
        return sb;
    }

    static {
        ORIENTATION_PROJECTION = new String[]{"orientation"};
    }

    public static int getImageRotation(Context context, Uri uri) {
        int result = getRotationFromContentProvider(context, uri);
        return result == 0 ? getRotationFromExif(uri) : result;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int getRotationFromContentProvider(android.content.Context r5, android.net.Uri r6) {
        /*
        r2 = 0;
        r0 = 0;
        r3 = r5.getContentResolver();	 Catch:{ Exception -> 0x001d }
        r4 = ORIENTATION_PROJECTION;	 Catch:{ Exception -> 0x001d }
        r0 = android.provider.MediaStore.Images.Media.query(r3, r6, r4);	 Catch:{ Exception -> 0x001d }
        if (r0 == 0) goto L_0x0019;
    L_0x000e:
        r3 = r0.moveToFirst();	 Catch:{ Exception -> 0x001d }
        if (r3 == 0) goto L_0x0019;
    L_0x0014:
        r3 = 0;
        r2 = r0.getInt(r3);	 Catch:{ Exception -> 0x001d }
    L_0x0019:
        ru.ok.android.utils.IOUtils.closeSilently(r0);
    L_0x001c:
        return r2;
    L_0x001d:
        r1 = move-exception;
        ru.ok.android.utils.Logger.m178e(r1);	 Catch:{ all -> 0x0025 }
        ru.ok.android.utils.IOUtils.closeSilently(r0);
        goto L_0x001c;
    L_0x0025:
        r3 = move-exception;
        ru.ok.android.utils.IOUtils.closeSilently(r0);
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.utils.UserMedia.getRotationFromContentProvider(android.content.Context, android.net.Uri):int");
    }

    private static int getRotationFromExif(Uri uri) {
        try {
            if (!uri.getScheme().equals("file") || VERSION.SDK_INT <= 4) {
                return 0;
            }
            String filePath = uri.getPath();
            File file = new File(filePath);
            if (!file.exists() || file.isDirectory()) {
                filePath = uri.toString().replaceFirst("file://", "");
            }
            if (!new File(filePath).exists()) {
                return 0;
            }
            switch (new ExifInterface(filePath).getAttributeInt("Orientation", 1)) {
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    return 180;
                case Message.REPLYTO_FIELD_NUMBER /*6*/:
                    return 90;
                case Message.TASKID_FIELD_NUMBER /*8*/:
                    return 270;
                default:
                    return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }
}
