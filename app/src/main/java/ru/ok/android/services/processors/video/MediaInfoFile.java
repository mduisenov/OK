package ru.ok.android.services.processors.video;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.io.FileNotFoundException;
import java.io.InputStream;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.InputStreamHolder;
import ru.ok.android.utils.Logger;

class MediaInfoFile extends MediaInfo {
    public static final Creator<MediaInfoFile> CREATOR;
    private static final long serialVersionUID = 1;

    /* renamed from: ru.ok.android.services.processors.video.MediaInfoFile.1 */
    static class C05031 implements Creator<MediaInfoFile> {
        C05031() {
        }

        public MediaInfoFile createFromParcel(Parcel source) {
            return new MediaInfoFile(source);
        }

        public MediaInfoFile[] newArray(int size) {
            return new MediaInfoFile[size];
        }
    }

    protected MediaInfoFile(Uri uri, String displayName, long sizeBytes, String mimeType) {
        super(uri, displayName, sizeBytes, mimeType);
    }

    protected MediaInfoFile(Parcel src) {
        super(src);
    }

    static MediaInfoFile fromFileUri(Context context, Uri fileUri, String defaultDisplayName) {
        checkFileUri(fileUri);
        return createMediaInfoFromCursor(context.getContentResolver(), fileUri, defaultDisplayName);
    }

    @NonNull
    private static MediaInfoFile createMediaInfoFromCursor(ContentResolver cr, Uri fileUri, String defaultDisplayName) {
        try {
            AssetFileDescriptor fd = cr.openAssetFileDescriptor(fileUri, "r");
            long sizeBytes = fd.getLength();
            String name = fileUri.getLastPathSegment();
            if (TextUtils.isEmpty(name)) {
                name = defaultDisplayName;
            }
            MediaInfoFile mediaInfoFile = new MediaInfoFile(fileUri, name, sizeBytes, FileUtils.getMimeType(name));
            IOUtils.closeSilently(fd);
            return mediaInfoFile;
        } catch (Throwable e) {
            Logger.m177e("Failed to get media info from file: %s", fileUri);
            Logger.m178e(e);
            throw new IllegalArgumentException("Failed to get media info from file: " + fileUri, e);
        } catch (Throwable th) {
            IOUtils.closeSilently(null);
        }
    }

    private static void checkFileUri(Uri fileUri) {
        if (fileUri == null || !"file".equals(fileUri.getScheme())) {
            throw new IllegalArgumentException("Not a file uri: " + fileUri);
        }
    }

    public void cleanUp() {
    }

    public InputStream open(ContentResolver cr) throws FileNotFoundException {
        return cr.openInputStream(getUri());
    }

    public Bitmap getThumbnail(ContentResolver cr, int thumbWidth, int thumbHeight) {
        return null;
    }

    public InputStreamHolder getThumbnailStreamHolder(ContentResolver cr, int thumbWidth, int thumbHeight) {
        return null;
    }

    static {
        CREATOR = new C05031();
    }
}
