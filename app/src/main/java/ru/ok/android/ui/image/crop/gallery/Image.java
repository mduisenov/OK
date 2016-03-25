package ru.ok.android.ui.image.crop.gallery;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.provider.MediaStore.Images.Thumbnails;
import ru.ok.android.ui.image.crop.Util;

public class Image extends BaseImage implements IImage {
    private int mRotation;

    public Image(BaseImageList container, ContentResolver cr, long id, int index, Uri uri, String dataPath, long miniThumbMagic, String mimeType, long dateTaken, String title, String displayName, int rotation) {
        super(container, cr, id, index, uri, dataPath, miniThumbMagic, mimeType, dateTaken, title, displayName);
        this.mRotation = rotation;
    }

    public int getDegreesRotated() {
        return this.mRotation;
    }

    protected void setDegreesRotated(int degrees) {
        if (this.mRotation != degrees) {
            this.mRotation = degrees;
            ContentValues values = new ContentValues();
            values.put("orientation", Integer.valueOf(this.mRotation));
            this.mContentResolver.update(this.mUri, values, null, null);
        }
    }

    public boolean rotateImageBy(int degrees) {
        setDegreesRotated((getDegreesRotated() + degrees) % 360);
        return true;
    }

    public Bitmap thumbBitmap(boolean rotateAsNeeded) {
        Options options = new Options();
        options.inDither = false;
        options.inPreferredConfig = Config.ARGB_8888;
        Bitmap bitmap = Thumbnails.getThumbnail(this.mContentResolver, this.mId, 1, options);
        if (bitmap == null || !rotateAsNeeded) {
            return bitmap;
        }
        return Util.rotate(bitmap, getDegreesRotated());
    }
}
