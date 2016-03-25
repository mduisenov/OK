package ru.ok.android.services.processors.video;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import android.os.Parcelable.Creator;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.InputStreamHolder;
import ru.ok.android.utils.Logger;

class MediaInfoGenericContent extends MediaInfo {
    public static final Creator<MediaInfoGenericContent> CREATOR;
    private static final long serialVersionUID = 1;

    /* renamed from: ru.ok.android.services.processors.video.MediaInfoGenericContent.1 */
    static class C05041 implements Creator<MediaInfoGenericContent> {
        C05041() {
        }

        public MediaInfoGenericContent createFromParcel(Parcel src) {
            return new MediaInfoGenericContent(src);
        }

        public MediaInfoGenericContent[] newArray(int size) {
            return new MediaInfoGenericContent[size];
        }
    }

    protected MediaInfoGenericContent(Uri uri, String displayName, long sizeBytes) {
        super(uri, displayName, sizeBytes, null);
    }

    protected MediaInfoGenericContent(Parcel src) {
        super(src);
    }

    public InputStream open(ContentResolver cr) throws FileNotFoundException {
        return new AutoCloseInputStream(cr.openFileDescriptor(getUri(), "r"));
    }

    public Bitmap getThumbnail(ContentResolver cr, int thumbWidth, int thumbHeight) {
        Closeable ish = getThumbnailStreamHolder(cr, thumbWidth, thumbHeight);
        if (ish != null) {
            Bitmap decodeStream;
            try {
                decodeStream = BitmapFactory.decodeStream(ish.open(cr));
                return decodeStream;
            } catch (IOException e) {
                decodeStream = "Failed to decode bitmap from: %s";
                Logger.m177e((String) decodeStream, ish);
            } catch (OutOfMemoryError e2) {
                decodeStream = "Not enough memory for bitmap";
                Logger.m184w(decodeStream);
            } finally {
                IOUtils.closeSilently(ish);
            }
        }
        return null;
    }

    public InputStreamHolder getThumbnailStreamHolder(ContentResolver cr, int thumbWidth, int thumbHeight) {
        Uri uri = getUri();
        String[] mimeTypes = cr.getStreamTypes(uri, "*/*");
        if (mimeTypes == null || mimeTypes.length <= 0) {
            return null;
        }
        return new TypedAssetStreamHolder(uri, mimeTypes[0], null);
    }

    public boolean isPersistent() {
        return false;
    }

    public void cleanUp() {
    }

    static {
        CREATOR = new C05041();
    }
}
