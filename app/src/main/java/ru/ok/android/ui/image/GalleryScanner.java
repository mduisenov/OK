package ru.ok.android.ui.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.ui.image.pick.GalleryImageInfo;
import ru.ok.android.utils.BitmapRender;
import ru.ok.android.utils.BitmapRender.BitmapInfoStruct;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.LruCache;

public class GalleryScanner {
    @SuppressLint({"InlinedApi"})
    private static final String[] PROJECTION;
    private final Context appContext;
    private final Uri galleryUri;
    private final LruCache<String, GalleryImageInfo> photoCache;

    static {
        PROJECTION = new String[]{"_id", "_data", "date_added", "width", "height", "orientation", "mime_type"};
    }

    public GalleryScanner(@NonNull Context appContext, @NonNull Uri galleryUri) {
        this.photoCache = new LruCache(20);
        this.appContext = appContext;
        this.galleryUri = galleryUri;
    }

    @NonNull
    public Uri getGalleryUri() {
        return this.galleryUri;
    }

    public List<GalleryImageInfo> scan(long dateToStartScanFromMs, int photoLimit) {
        Cursor cursor = null;
        try {
            cursor = query(dateToStartScanFromMs);
            List<GalleryImageInfo> arrayList;
            if (cursor == null || cursor.getCount() == 0) {
                arrayList = new ArrayList();
                return arrayList;
            }
            arrayList = toListOfGalleryImageInfos(cursor, photoLimit);
            if (cursor != null) {
                cursor.close();
            }
            return arrayList;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Nullable
    private Cursor query(long dateToStartScanFromMs) {
        long dateToStartScanFromSec = dateToStartScanFromMs / 1000;
        return this.appContext.getContentResolver().query(this.galleryUri, PROJECTION, "date_added >= ?", new String[]{Long.toString(dateToStartScanFromSec)}, "date_added DESC");
    }

    @NonNull
    private List<GalleryImageInfo> toListOfGalleryImageInfos(@NonNull Cursor c, int photoLimit) {
        List<GalleryImageInfo> photos = new ArrayList(c.getCount());
        int photosCount = 0;
        while (c.moveToNext() && photosCount < photoLimit) {
            GalleryImageInfo photo = toGalleryImageInfo(c);
            if (photo != null) {
                photos.add(photo);
                photosCount++;
            }
        }
        return photos;
    }

    @Nullable
    private GalleryImageInfo toGalleryImageInfo(@NonNull Cursor cursor) {
        String filePath = cursor.getString(1);
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        GalleryImageInfo photo = (GalleryImageInfo) this.photoCache.get(filePath);
        if (photo != null || !filter(filePath)) {
            return photo;
        }
        photo = toPhoto(cursor, filePath);
        this.photoCache.put(filePath, photo);
        return photo;
    }

    private boolean filter(@NonNull String filePath) {
        return "image/jpeg".equals(FileUtils.getMimeType(filePath)) && !filePath.toLowerCase().contains("odnoklassniki");
    }

    @NonNull
    private GalleryImageInfo toPhoto(@NonNull Cursor cursor, @NonNull String filePath) {
        Uri fileUri = Uri.fromFile(new File(filePath));
        int width = cursor.getInt(3);
        int height = cursor.getInt(4);
        boolean isBroken = false;
        if (cursor.getInt(3) == 0 || cursor.getInt(4) == 0) {
            BitmapInfoStruct bitmapInfoStruct = BitmapRender.getBitmapInfo(this.appContext.getContentResolver(), fileUri);
            width = bitmapInfoStruct.options.outWidth;
            height = bitmapInfoStruct.options.outHeight;
            isBroken = bitmapInfoStruct.broken;
        }
        return new GalleryImageInfo(fileUri, cursor.getString(6), cursor.getInt(5), cursor.getLong(2), width, height, isBroken);
    }
}
