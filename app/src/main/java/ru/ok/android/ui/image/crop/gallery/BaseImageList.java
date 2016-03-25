package ru.ok.android.ui.image.crop.gallery;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.google.android.gms.ads.AdRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.ok.android.ui.image.crop.Util;
import ru.ok.android.utils.LruCache;

public abstract class BaseImageList implements IImageList {
    private static final Pattern sPathWithId;
    protected Uri mBaseUri;
    protected String mBucketId;
    private final LruCache<Integer, BaseImage> mCache;
    protected ContentResolver mContentResolver;
    protected Cursor mCursor;
    protected boolean mCursorDeactivated;
    protected int mSort;

    protected abstract Cursor createCursor();

    protected abstract long getImageId(Cursor cursor);

    protected abstract BaseImage loadImageFromCursor(Cursor cursor);

    public BaseImageList(ContentResolver resolver, Uri uri, int sort, String bucketId) {
        this.mCache = new LruCache(AdRequest.MAX_CONTENT_URL_LENGTH);
        this.mCursorDeactivated = false;
        this.mSort = sort;
        this.mBaseUri = uri;
        this.mBucketId = bucketId;
        this.mContentResolver = resolver;
        this.mCursor = createCursor();
        if (this.mCursor == null) {
            Log.w("BaseImageList", "createCursor returns null.");
        }
        this.mCache.evictAll();
    }

    public void close() {
        try {
            invalidateCursor();
        } catch (IllegalStateException e) {
            Log.e("BaseImageList", "Caught exception while deactivating cursor.", e);
        }
        this.mContentResolver = null;
        if (this.mCursor != null) {
            this.mCursor.close();
            this.mCursor = null;
        }
    }

    public Uri contentUri(long id) {
        try {
            if (ContentUris.parseId(this.mBaseUri) != id) {
                Log.e("BaseImageList", "id mismatch");
            }
            return this.mBaseUri;
        } catch (NumberFormatException e) {
            return ContentUris.withAppendedId(this.mBaseUri, id);
        }
    }

    public int getCount() {
        Cursor cursor = getCursor();
        if (cursor == null) {
            return 0;
        }
        int count;
        synchronized (this) {
            count = cursor.getCount();
        }
        return count;
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }

    private Cursor getCursor() {
        Cursor cursor;
        synchronized (this) {
            if (this.mCursor == null) {
                cursor = null;
            } else {
                if (this.mCursorDeactivated) {
                    this.mCursor.requery();
                    this.mCursorDeactivated = false;
                }
                cursor = this.mCursor;
            }
        }
        return cursor;
    }

    public IImage getImageAt(int i) {
        BaseImage result = (BaseImage) this.mCache.get(Integer.valueOf(i));
        if (result == null) {
            Cursor cursor = getCursor();
            if (cursor == null) {
                return null;
            }
            synchronized (this) {
                if (cursor.moveToPosition(i)) {
                    result = loadImageFromCursor(cursor);
                } else {
                    result = null;
                }
                this.mCache.put(Integer.valueOf(i), result);
            }
        }
        return result;
    }

    protected void invalidateCursor() {
        if (this.mCursor != null) {
            this.mCursor.deactivate();
            this.mCursorDeactivated = true;
        }
    }

    static {
        sPathWithId = Pattern.compile("(.*)/\\d+");
    }

    private static String getPathWithoutId(Uri uri) {
        String path = uri.getPath();
        Matcher matcher = sPathWithId.matcher(path);
        return matcher.matches() ? matcher.group(1) : path;
    }

    private boolean isChildImageUri(Uri uri) {
        Uri base = this.mBaseUri;
        return Util.equals(base.getScheme(), uri.getScheme()) && Util.equals(base.getHost(), uri.getHost()) && Util.equals(base.getAuthority(), uri.getAuthority()) && Util.equals(base.getPath(), getPathWithoutId(uri));
    }

    public IImage getImageForUri(Uri uri) {
        IImage iImage = null;
        if (isChildImageUri(uri)) {
            try {
                long matchId = ContentUris.parseId(uri);
                Cursor cursor = getCursor();
                if (cursor != null) {
                    synchronized (this) {
                        cursor.moveToPosition(-1);
                        int i = 0;
                        while (cursor.moveToNext()) {
                            if (getImageId(cursor) == matchId) {
                                iImage = (BaseImage) this.mCache.get(Integer.valueOf(i));
                                if (iImage == null) {
                                    iImage = loadImageFromCursor(cursor);
                                    this.mCache.put(Integer.valueOf(i), iImage);
                                }
                            } else {
                                i++;
                            }
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                Log.i("BaseImageList", "fail to get id in: " + uri, ex);
            }
        }
        return iImage;
    }

    protected String sortOrder() {
        String ascending = this.mSort == 1 ? " ASC" : " DESC";
        return "case ifnull(datetaken,0) when 0 then date_modified*1000 else datetaken end" + ascending + ", _id" + ascending;
    }
}
