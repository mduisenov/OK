package ru.ok.android.ui.image.pick;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.support.v4.content.GeneralDataLoader;
import android.text.TextUtils;
import android.util.SparseArray;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import ru.ok.android.utils.BitmapRender;
import ru.ok.android.utils.BitmapRender.BitmapInfoStruct;
import ru.ok.android.utils.localization.LocalizationManager;

public final class GalleriesLoader extends GeneralDataLoader<ArrayList<DeviceGalleryInfo>> {
    private static final String[] PROJECTION;

    public GalleriesLoader(Context context) {
        super(context);
    }

    protected ArrayList<DeviceGalleryInfo> loadData() {
        Cursor cursor = getContext().getContentResolver().query(Media.EXTERNAL_CONTENT_URI, PROJECTION, null, null, "date_added DESC");
        if (cursor == null) {
            return null;
        }
        try {
            ArrayList<DeviceGalleryInfo> galleries = extractGalleries(getContext(), cursor);
            if (!(galleries == null || galleries.isEmpty())) {
                DeviceGalleryInfo allPhotos = new DeviceGalleryInfo(0, LocalizationManager.getString(getContext(), 2131166352));
                Iterator i$ = galleries.iterator();
                while (i$.hasNext()) {
                    allPhotos.photos.addAll(((DeviceGalleryInfo) i$.next()).photos);
                }
                Collections.sort(allPhotos.photos, GalleryImageInfo.COMPARATOR_DATE_ADDED);
                galleries.add(0, allPhotos);
            }
            cursor.close();
            return galleries;
        } catch (Throwable th) {
            cursor.close();
        }
    }

    protected List<Uri> observableUris(ArrayList<DeviceGalleryInfo> arrayList) {
        return Arrays.asList(new Uri[]{Media.EXTERNAL_CONTENT_URI});
    }

    private static ArrayList<DeviceGalleryInfo> extractGalleries(Context context, Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        ArrayList<DeviceGalleryInfo> result = new ArrayList();
        SparseArray<DeviceGalleryInfo> collector = new SparseArray(cursor.getCount());
        while (cursor.moveToNext()) {
            boolean broken = false;
            BitmapInfoStruct bitmapInfoStruct = null;
            String filePath = cursor.getString(3);
            if (!TextUtils.isEmpty(filePath)) {
                int bucketId = cursor.getInt(0);
                DeviceGalleryInfo gallery = (DeviceGalleryInfo) collector.get(bucketId);
                if (gallery == null) {
                    gallery = new DeviceGalleryInfo(bucketId, cursor.getString(1));
                    collector.put(bucketId, gallery);
                    result.add(gallery);
                }
                int width = cursor.getInt(5);
                int height = cursor.getInt(6);
                Uri fileUri = Uri.fromFile(new File(filePath));
                if (width == 0 || height == 0) {
                    bitmapInfoStruct = BitmapRender.getBitmapInfo(context.getContentResolver(), fileUri);
                    Options options = bitmapInfoStruct.options;
                    width = options.outWidth;
                    height = options.outHeight;
                }
                if (bitmapInfoStruct != null && bitmapInfoStruct.broken) {
                    broken = true;
                }
                gallery.photos.add(new GalleryImageInfo(fileUri, cursor.getString(8), cursor.getInt(7), cursor.getLong(4), width, height, broken));
            }
        }
        return result;
    }

    static {
        PROJECTION = new String[]{"bucket_id", "bucket_display_name", "_id", "_data", "date_added", "width", "height", "orientation", "mime_type"};
    }
}
