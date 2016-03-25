package ru.ok.android.ui.image.crop.gallery;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import com.google.android.gms.plus.PlusShare;

public class ImageList extends BaseImageList implements IImageList {
    private static final String[] ACCEPTABLE_IMAGE_TYPES;
    static final String[] IMAGE_PROJECTION;

    static {
        ACCEPTABLE_IMAGE_TYPES = new String[]{"image/jpeg", "image/png", "image/gif"};
        IMAGE_PROJECTION = new String[]{"_id", "_data", "datetaken", "mini_thumb_magic", "orientation", PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, "mime_type", "date_modified"};
    }

    public ImageList(ContentResolver resolver, Uri imageUri, int sort, String bucketId) {
        super(resolver, imageUri, sort, bucketId);
    }

    protected String whereClause() {
        return this.mBucketId == null ? "(mime_type in (?, ?, ?))" : "(mime_type in (?, ?, ?)) AND bucket_id = ?";
    }

    protected String[] whereClauseArgs() {
        if (this.mBucketId == null) {
            return ACCEPTABLE_IMAGE_TYPES;
        }
        int count = ACCEPTABLE_IMAGE_TYPES.length;
        String[] result = new String[(count + 1)];
        System.arraycopy(ACCEPTABLE_IMAGE_TYPES, 0, result, 0, count);
        result[count] = this.mBucketId;
        return result;
    }

    protected Cursor createCursor() {
        return Media.query(this.mContentResolver, this.mBaseUri, IMAGE_PROJECTION, whereClause(), whereClauseArgs(), sortOrder());
    }

    protected long getImageId(Cursor cursor) {
        return cursor.getLong(0);
    }

    protected BaseImage loadImageFromCursor(Cursor cursor) {
        long id = cursor.getLong(0);
        String dataPath = cursor.getString(1);
        long dateTaken = cursor.getLong(2);
        if (dateTaken == 0) {
            dateTaken = cursor.getLong(7) * 1000;
        }
        long miniThumbMagic = cursor.getLong(3);
        String title = cursor.getString(5);
        String mimeType = cursor.getString(6);
        if (title == null || title.length() == 0) {
            title = dataPath;
        }
        return new Image(this, this.mContentResolver, id, cursor.getPosition(), contentUri(id), dataPath, miniThumbMagic, mimeType, dateTaken, title, title, 0);
    }
}
