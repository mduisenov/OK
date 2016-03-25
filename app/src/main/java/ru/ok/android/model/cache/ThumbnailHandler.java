package ru.ok.android.model.cache;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.provider.MediaStore.Images.Thumbnails;
import java.io.Closeable;
import java.io.IOException;
import ru.ok.android.utils.BitmapRender;
import ru.ok.android.utils.IOUtils;

public final class ThumbnailHandler {
    public static Bitmap loadThumbnail(ContentResolver cr, Uri imageUri, int width, int height, int orientation) throws IOException {
        Bitmap miniBitmap;
        Options opts = new Options();
        if ("content".equals(imageUri.getScheme()) && "media".equals(imageUri.getAuthority())) {
            long imageId = ContentUris.parseId(imageUri);
            opts.inPreferredConfig = Config.RGB_565;
            miniBitmap = Thumbnails.getThumbnail(cr, imageId, 1, opts);
        } else {
            Closeable closeable = null;
            try {
                closeable = cr.openInputStream(imageUri);
                miniBitmap = BitmapFactory.decodeStream(closeable, null, opts);
            } finally {
                IOUtils.closeSilently(closeable);
            }
        }
        if (miniBitmap == null) {
            return null;
        }
        int originalWidth = miniBitmap.getWidth();
        int originalHeight = miniBitmap.getHeight();
        if (width == 0) {
            width = (originalWidth * height) / originalHeight;
        } else if (height == 0) {
            height = (originalHeight * width) / originalWidth;
        }
        return BitmapRender.resizeForBoundsAndRotate(miniBitmap, width, height, 2, orientation);
    }
}
