package ru.ok.android.services.processors.video;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.provider.MediaStore.Images.Thumbnails;
import android.provider.MediaStore.Video;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.InputStreamHolder;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.MimeTypes;

class MediaInfoMediaStore extends MediaInfo {
    public static final Creator<MediaInfoMediaStore> CREATOR;
    static final String[] PROJECTION_MEDIA_INFO;
    private static final long serialVersionUID = 1;
    protected final long mediaId;
    private ThumbnailHelper thumbnailHelper;

    /* renamed from: ru.ok.android.services.processors.video.MediaInfoMediaStore.1 */
    static class C05051 implements Creator<MediaInfoMediaStore> {
        C05051() {
        }

        public MediaInfoMediaStore createFromParcel(Parcel source) {
            return new MediaInfoMediaStore(source);
        }

        public MediaInfoMediaStore[] newArray(int size) {
            return new MediaInfoMediaStore[size];
        }
    }

    private interface ThumbnailHelper {
        Bitmap getThumbnail(ContentResolver contentResolver, long j);

        Cursor queryForPath(ContentResolver contentResolver, long j);
    }

    private static class ImageThumbnailHelper implements ThumbnailHelper {
        private ImageThumbnailHelper() {
        }

        public Bitmap getThumbnail(ContentResolver cr, long mediaId) {
            Bitmap bitmap = null;
            try {
                bitmap = Thumbnails.getThumbnail(cr, mediaId, 1, null);
            } catch (SecurityException e) {
                Logger.m185w("No permission to read thumbnail for mediaId = %d", Long.valueOf(mediaId));
            }
            return bitmap;
        }

        public Cursor queryForPath(ContentResolver cr, long mediaId) {
            try {
                return cr.query(Thumbnails.EXTERNAL_CONTENT_URI, new String[]{"_data"}, "image_id = ?", new String[]{String.valueOf(mediaId)}, null);
            } catch (SecurityException e) {
                Logger.m185w("No permission to read thumbnail for mediaId = %d", Long.valueOf(mediaId));
                return null;
            }
        }
    }

    private static class NoOpThumbnailHelper implements ThumbnailHelper {
        private NoOpThumbnailHelper() {
        }

        public Bitmap getThumbnail(ContentResolver cr, long mediaId) {
            return null;
        }

        public Cursor queryForPath(ContentResolver cr, long mediaId) {
            return null;
        }
    }

    private static class VideoThumbnailHelper implements ThumbnailHelper {
        private VideoThumbnailHelper() {
        }

        public Bitmap getThumbnail(ContentResolver cr, long mediaId) {
            Bitmap bitmap = null;
            try {
                bitmap = Video.Thumbnails.getThumbnail(cr, mediaId, 1, null);
            } catch (SecurityException e) {
                Logger.m185w("No permission to read thumbnail for mediaId = %d", Long.valueOf(mediaId));
            }
            return bitmap;
        }

        public Cursor queryForPath(ContentResolver cr, long mediaId) {
            try {
                return cr.query(Video.Thumbnails.EXTERNAL_CONTENT_URI, new String[]{"_data"}, "video_id = ?", new String[]{String.valueOf(mediaId)}, null);
            } catch (SecurityException e) {
                Logger.m185w("No permission to read thumbnail for mediaId = %d", Long.valueOf(mediaId));
                return null;
            }
        }
    }

    protected MediaInfoMediaStore(Uri uri, String displayName, long sizeBytes, String mimeType, long mediaId) {
        super(uri, displayName, sizeBytes, mimeType);
        this.mediaId = mediaId;
    }

    protected MediaInfoMediaStore(Parcel src) {
        super(src);
        this.mediaId = src.readLong();
    }

    static MediaInfoMediaStore fromMediaUri(Context context, Uri mediaUri, String defaultDisplayName) {
        checkMediaUri(mediaUri);
        return createMediaInfoFromCursor(context.getContentResolver(), mediaUri, getMediaIdFrommUri(mediaUri), defaultDisplayName);
    }

    private static void checkMediaUri(Uri mediaUri) {
        if (mediaUri == null || !"content".equals(mediaUri.getScheme()) || !"media".equals(mediaUri.getAuthority())) {
            throw new IllegalArgumentException("Not a MediaStore uri: " + mediaUri);
        }
    }

    @NonNull
    private static MediaInfoMediaStore createMediaInfoFromCursor(ContentResolver cr, Uri mediaUri, long mediaId, String defaultDisplayName) {
        Cursor cursor = null;
        try {
            cursor = cr.query(mediaUri, PROJECTION_MEDIA_INFO, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                throw new IllegalArgumentException("Failed to get media info for " + mediaUri);
            }
            String filename = cursor.getString(0);
            MediaInfoMediaStore mediaInfoMediaStore = new MediaInfoMediaStore(mediaUri, filename == null ? defaultDisplayName : new File(filename).getName(), cursor.getLong(1), cursor.getString(2), mediaId);
            IOUtils.closeSilently(cursor);
            return mediaInfoMediaStore;
        } catch (Throwable e) {
            Logger.m177e("Failed to get media info: %s", e);
            Logger.m178e(e);
            throw new IllegalArgumentException("Failed to get media info for " + mediaUri, e);
        } catch (Throwable th) {
            IOUtils.closeSilently(cursor);
        }
    }

    private static long getMediaIdFrommUri(Uri mediaUri) {
        try {
            long mediaId = ContentUris.parseId(mediaUri);
            if (mediaId > 0) {
                return mediaId;
            }
            throw new IllegalArgumentException("Invalid media ID: " + mediaId);
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to parse media ID");
            throw new IllegalArgumentException("Failed to parse media ID: " + e, e);
        }
    }

    public Bitmap getThumbnail(ContentResolver cr, int thumbWidth, int thumbHeight) {
        ensureThumbnailHelperValid();
        return this.thumbnailHelper.getThumbnail(cr, this.mediaId);
    }

    public InputStreamHolder getThumbnailStreamHolder(ContentResolver cr, int thumbWidth, int thumbHeight) {
        if (getThumbnail(cr, thumbWidth, thumbHeight) == null) {
            Logger.m185w("No thumbnail bitmap for mediaId = %d", Long.valueOf(this.mediaId));
        }
        Cursor c = this.thumbnailHelper.queryForPath(cr, this.mediaId);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    String path = c.getString(0);
                    if (!TextUtils.isEmpty(path)) {
                        InputStreamHolder genericFileStreamHolder = new GenericFileStreamHolder(path);
                        return genericFileStreamHolder;
                    }
                }
                c.close();
            } finally {
                c.close();
            }
        }
        return null;
    }

    public InputStream open(ContentResolver cr) throws FileNotFoundException {
        return cr.openInputStream(getUri());
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.mediaId);
    }

    public void cleanUp() {
    }

    private void ensureThumbnailHelperValid() {
        if (this.thumbnailHelper == null) {
            String mimeType = getMimeType();
            if (MimeTypes.isImage(mimeType)) {
                this.thumbnailHelper = new ImageThumbnailHelper();
            } else if (MimeTypes.isVideo(mimeType)) {
                this.thumbnailHelper = new VideoThumbnailHelper();
            } else {
                this.thumbnailHelper = new NoOpThumbnailHelper();
            }
        }
    }

    static {
        CREATOR = new C05051();
        PROJECTION_MEDIA_INFO = new String[]{"_display_name", "_size", "mime_type"};
    }
}
