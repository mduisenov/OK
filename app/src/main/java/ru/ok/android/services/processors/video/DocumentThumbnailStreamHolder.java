package ru.ok.android.services.processors.video;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.provider.DocumentsContract;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.InputStreamHolder;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ThreadUtil;

@TargetApi(19)
class DocumentThumbnailStreamHolder implements InputStreamHolder {
    public static final Creator<DocumentThumbnailStreamHolder> CREATOR;
    private transient Bitmap bitmap;
    private final Uri documentUri;
    private final Point thumbSize;

    /* renamed from: ru.ok.android.services.processors.video.DocumentThumbnailStreamHolder.1 */
    class C04981 implements Runnable {
        final /* synthetic */ PipedOutputStream val$output;

        C04981(PipedOutputStream pipedOutputStream) {
            this.val$output = pipedOutputStream;
        }

        public void run() {
            try {
                DocumentThumbnailStreamHolder.this.bitmap.compress(CompressFormat.PNG, 90, this.val$output);
            } catch (Exception e) {
                Logger.m180e(e, "Failed to save bitmap: %s", e);
            } finally {
                IOUtils.closeSilently(this.val$output);
            }
        }
    }

    /* renamed from: ru.ok.android.services.processors.video.DocumentThumbnailStreamHolder.2 */
    static class C04992 implements Creator<DocumentThumbnailStreamHolder> {
        C04992() {
        }

        public DocumentThumbnailStreamHolder createFromParcel(Parcel source) {
            ClassLoader cl = DocumentThumbnailStreamHolder.class.getClassLoader();
            return new DocumentThumbnailStreamHolder((Uri) source.readParcelable(cl), (Point) source.readParcelable(cl));
        }

        public DocumentThumbnailStreamHolder[] newArray(int size) {
            return new DocumentThumbnailStreamHolder[size];
        }
    }

    DocumentThumbnailStreamHolder(Uri documentUri, Point thumbSize) {
        this.documentUri = documentUri;
        this.thumbSize = thumbSize;
    }

    public InputStream open(ContentResolver cr) throws FileNotFoundException {
        recycleBitmapIfNecessary();
        try {
            this.bitmap = DocumentsContract.getDocumentThumbnail(cr, this.documentUri, this.thumbSize, null);
            PipedOutputStream output = new PipedOutputStream();
            try {
                PipedInputStream inputStream = new PipedInputStream(output);
                ThreadUtil.execute(new C04981(output));
                return inputStream;
            } catch (IOException e) {
                throw new FileNotFoundException("Failed to open thumbnail bitmap: " + this.documentUri);
            }
        } catch (OutOfMemoryError e2) {
            throw new FileNotFoundException("Not enough memory to open thumbnail bitmap: " + this.documentUri);
        }
    }

    public void close() throws IOException {
        recycleBitmapIfNecessary();
    }

    private void recycleBitmapIfNecessary() {
        if (this.bitmap != null) {
            this.bitmap.recycle();
            this.bitmap = null;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.documentUri, flags);
        dest.writeParcelable(this.thumbSize, flags);
    }

    static {
        CREATOR = new C04992();
    }
}
