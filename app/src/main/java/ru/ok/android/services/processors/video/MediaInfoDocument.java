package ru.ok.android.services.processors.video;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Parcel;
import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import android.os.Parcelable.Creator;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.InputStreamHolder;
import ru.ok.android.utils.Logger;

@TargetApi(19)
class MediaInfoDocument extends MediaInfo {
    public static final Creator<MediaInfoDocument> CREATOR;
    private static final String[] PROJECTION_DOCUMENT;
    private static final long serialVersionUID = 1;

    /* renamed from: ru.ok.android.services.processors.video.MediaInfoDocument.1 */
    static class C05021 implements Creator<MediaInfoDocument> {
        C05021() {
        }

        public MediaInfoDocument createFromParcel(Parcel source) {
            return new MediaInfoDocument(source);
        }

        public MediaInfoDocument[] newArray(int size) {
            return new MediaInfoDocument[size];
        }
    }

    protected MediaInfoDocument(Uri uri, String displayName, long sizeBytes, String mimeType) {
        super(uri, displayName, sizeBytes, mimeType);
    }

    protected MediaInfoDocument(Parcel src) {
        super(src);
    }

    static MediaInfoDocument fromDocumentUri(Context context, Uri documentUri, String defaultDisplayName) {
        checkDocumentUri(context, documentUri);
        return createMediaInfoFromCursor(context.getContentResolver(), documentUri, defaultDisplayName);
    }

    private static void checkDocumentUri(Context context, Uri documentUri) {
        if (documentUri == null || !DocumentsContract.isDocumentUri(context, documentUri)) {
            throw new IllegalArgumentException("Not a document uri: " + documentUri);
        }
    }

    @NonNull
    private static MediaInfoDocument createMediaInfoFromCursor(ContentResolver cr, Uri documentUri, String defaultDisplayName) {
        Cursor cursor = null;
        try {
            cursor = cr.query(documentUri, PROJECTION_DOCUMENT, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                throw new IllegalArgumentException("Failed to get media info for " + documentUri);
            }
            String filename = cursor.getString(0);
            MediaInfoDocument mediaInfoDocument = new MediaInfoDocument(documentUri, filename == null ? defaultDisplayName : new File(filename).getName(), cursor.getLong(1), cursor.getString(2));
            IOUtils.closeSilently(cursor);
            return mediaInfoDocument;
        } catch (Throwable e) {
            Logger.m177e("Failed to get media info: %s", e);
            Logger.m178e(e);
            throw new IllegalArgumentException("Failed to get media info for " + documentUri, e);
        } catch (Throwable th) {
            IOUtils.closeSilently(cursor);
        }
    }

    public InputStream open(ContentResolver cr) throws FileNotFoundException {
        return new AutoCloseInputStream(cr.openFileDescriptor(getUri(), "r"));
    }

    public Bitmap getThumbnail(ContentResolver cr, int thumbWidth, int thumbHeight) {
        return DocumentsContract.getDocumentThumbnail(cr, getUri(), new Point(thumbWidth, thumbHeight), null);
    }

    public InputStreamHolder getThumbnailStreamHolder(ContentResolver cr, int thumbWidth, int thumbHeight) {
        return new DocumentThumbnailStreamHolder(getUri(), new Point(thumbWidth, thumbHeight));
    }

    public void cleanUp() {
    }

    static {
        CREATOR = new C05021();
        PROJECTION_DOCUMENT = new String[]{"_display_name", "_size", "mime_type"};
    }
}
