package ru.ok.android.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import com.facebook.imagepipeline.nativecode.JpegTranscoder;
import com.google.android.gms.ads.AdRequest;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import ru.ok.android.services.processors.image.upload.ResizeSettings;

public class BitmapRender {
    private static int[] MUL_TABLE;
    private static int[] SHG_TABLE;

    public static class BitmapInfoStruct {
        public boolean broken;
        public Options options;

        public BitmapInfoStruct(Options options, boolean broken) {
            this.options = options;
            this.broken = broken;
        }
    }

    public static int getImageSize(Context context, int value) {
        return (int) TypedValue.applyDimension(1, (float) value, context.getResources().getDisplayMetrics());
    }

    public static Bitmap resizeForBoundsAndRotate(Bitmap bitmap, int desiredWidth, int desiredHeight, int scaleType, int rotationDegrees) {
        return resizeForBoundsAndRotate(bitmap, desiredWidth, desiredHeight, scaleType, rotationDegrees, true);
    }

    public static Bitmap resizeForBoundsAndRotate(Bitmap bitmap, int desiredWidth, int desiredHeight, int scaleType, int rotationDegrees, boolean recycleSource) {
        if (bitmap == null || bitmap.isRecycled()) {
            Log.e("BitmapRender", "Trying to resize an invalid bitmap");
            return null;
        }
        int bWidth = bitmap.getWidth();
        int bHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        int newWidth = bWidth;
        int newHeight = bHeight;
        int offsetX = 0;
        int offsetY = 0;
        if (!(bWidth == desiredWidth && bHeight == desiredHeight)) {
            if (scaleType == 4) {
                matrix.postScale(((float) desiredWidth) / ((float) bWidth), ((float) desiredHeight) / ((float) bHeight));
            } else if (scaleType == 2) {
                scaleToUse = Math.max(((float) desiredWidth) / ((float) bWidth), ((float) desiredHeight) / ((float) bHeight));
                matrix.postScale(scaleToUse, scaleToUse);
                newWidth = (int) (((float) desiredWidth) / scaleToUse);
                newHeight = (int) (((float) desiredHeight) / scaleToUse);
                offsetX = (bWidth - newWidth) / 2;
                offsetY = (bHeight - newHeight) / 2;
            } else if (bWidth <= desiredWidth && bHeight <= desiredHeight) {
                return bitmap;
            } else {
                if (scaleType == 1) {
                    offsetX = (bWidth - desiredWidth) / 2;
                    offsetY = (bHeight - desiredHeight) / 2;
                    newWidth = desiredWidth;
                    newHeight = desiredHeight;
                } else if (scaleType == 3) {
                    scaleToUse = Math.min(((float) desiredWidth) / ((float) bWidth), ((float) desiredHeight) / ((float) bHeight));
                    matrix.postScale(scaleToUse, scaleToUse);
                }
            }
        }
        if (rotationDegrees != 0) {
            matrix.postRotate((float) rotationDegrees);
        }
        Bitmap result = Bitmap.createBitmap(bitmap, offsetX, offsetY, newWidth, newHeight, matrix, true);
        if (!recycleSource || result == bitmap) {
            return result;
        }
        bitmap.recycle();
        return result;
    }

    public static Bitmap resizeForBounds(ContentResolver contentResolver, Uri uri, int desiredWidth, int desiredHeight, int scaleType) throws IOException {
        return resizeForBoundsAndRotate(contentResolver, uri, desiredWidth, desiredHeight, scaleType, 0);
    }

    public static Bitmap resizeForBoundsAndRotate(ContentResolver contentResolver, Uri uri, int desiredWidth, int desiredHeight, int scaleType, int rotationDegrees) throws IOException {
        if (desiredWidth < 1 || desiredHeight < 1) {
            throw new IOException("Desired bounds must be > 0");
        }
        Bitmap source;
        if (scaleType == 1) {
            source = Media.getBitmap(contentResolver, uri);
        } else {
            source = getBySampleSize(contentResolver, uri, desiredWidth, desiredHeight);
        }
        if (source != null) {
            return resizeForBoundsAndRotate(source, desiredWidth, desiredHeight, scaleType, rotationDegrees);
        }
        throw new IOException("Unable load image from storage for uri: " + uri.toString());
    }

    public static Bitmap getBySampleSize(ContentResolver contentResolver, Uri uri, int desiredWidth, int desiredHeight) throws IOException {
        return (Bitmap) getSampledBitmapAndSampleSize(contentResolver, uri, desiredWidth, desiredHeight).first;
    }

    public static Pair<Bitmap, Integer> getSampledBitmapAndSampleSize(ContentResolver contentResolver, Uri uri, int desiredWidth, int desiredHeight) throws IOException {
        ParcelFileDescriptor pfd = null;
        FileDescriptor fd = null;
        boolean valid = false;
        Options bounds = new Options();
        bounds.inJustDecodeBounds = true;
        try {
            pfd = contentResolver.openFileDescriptor(uri, "r");
        } catch (Exception ignored) {
            Logger.m180e(ignored, "Exception when opening file descriptor with uri (%s)", uri);
        }
        if (pfd != null) {
            fd = pfd.getFileDescriptor();
            BitmapFactory.decodeFileDescriptor(fd, null, bounds);
            if (!(bounds.mCancel || bounds.outWidth == -1 || bounds.outHeight == -1)) {
                valid = true;
            }
        }
        if (!valid) {
            if (pfd != null) {
                IOUtils.closeSilently(pfd);
            }
            if (uri.getScheme().equals("file")) {
                File file = new File(uri.toString().replaceFirst("file://", ""));
                if (file.exists()) {
                    pfd = ParcelFileDescriptor.open(file, 268435456);
                    if (pfd != null) {
                        fd = pfd.getFileDescriptor();
                        BitmapFactory.decodeFileDescriptor(fd, null, bounds);
                        if (!(bounds.mCancel || bounds.outWidth == -1 || bounds.outHeight == -1)) {
                            valid = true;
                        }
                    }
                } else {
                    Logger.m177e("FILE with path (%s) not exists", filePath);
                }
            }
        }
        if (valid) {
            Bitmap result;
            if (desiredHeight > 0 && desiredWidth > 0) {
                bounds.inSampleSize = Math.max((int) Math.ceil((double) (((float) bounds.outWidth) / ((float) desiredWidth))), (int) Math.ceil((double) (((float) bounds.outHeight) / ((float) desiredHeight))));
            }
            if (bounds.inSampleSize > 1) {
                bounds.inJustDecodeBounds = false;
                bounds.inDither = false;
                bounds.inPurgeable = true;
                bounds.inInputShareable = true;
                bounds.inScaled = false;
                bounds.inPreferQualityOverSpeed = true;
                bounds.inPreferredConfig = Config.RGB_565;
                result = BitmapFactory.decodeFileDescriptor(fd, null, bounds);
            } else {
                result = Media.getBitmap(contentResolver, uri);
            }
            IOUtils.closeSilently(pfd);
            return new Pair(result, Integer.valueOf(bounds.inSampleSize));
        } else if (pfd == null) {
            throw new IOException("Can't open file descriptor for uri: " + uri.toString());
        } else {
            IOUtils.closeSilently(pfd);
            throw new IOException("Can't decode bounds for file at uri: " + uri.toString());
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.support.annotation.NonNull
    public static ru.ok.android.utils.BitmapRender.BitmapInfoStruct getBitmapInfo(android.content.ContentResolver r12, android.net.Uri r13) {
        /*
        r10 = -1;
        r7 = 1;
        r8 = 0;
        r4 = new android.graphics.BitmapFactory$Options;
        r4.<init>();
        r4.inJustDecodeBounds = r7;
        r5 = 0;
        r6 = 0;
        r9 = "r";
        r5 = r12.openFileDescriptor(r13, r9);	 Catch:{ Exception -> 0x006b }
        if (r5 == 0) goto L_0x002a;
    L_0x0015:
        r0 = r5.getFileDescriptor();	 Catch:{ Exception -> 0x006b }
        r9 = 0;
        android.graphics.BitmapFactory.decodeFileDescriptor(r0, r9, r4);	 Catch:{ Exception -> 0x006b }
        r9 = r4.mCancel;	 Catch:{ Exception -> 0x006b }
        if (r9 != 0) goto L_0x002a;
    L_0x0021:
        r9 = r4.outWidth;	 Catch:{ Exception -> 0x006b }
        if (r9 == r10) goto L_0x002a;
    L_0x0025:
        r9 = r4.outHeight;	 Catch:{ Exception -> 0x006b }
        if (r9 == r10) goto L_0x002a;
    L_0x0029:
        r6 = 1;
    L_0x002a:
        if (r6 != 0) goto L_0x005c;
    L_0x002c:
        r9 = r13.getScheme();	 Catch:{ Exception -> 0x006b }
        r10 = "file";
        r9 = r9.equals(r10);	 Catch:{ Exception -> 0x006b }
        if (r9 == 0) goto L_0x005c;
    L_0x0039:
        r9 = r13.toString();	 Catch:{ Exception -> 0x006b }
        r10 = "file://";
        r11 = "";
        r2 = r9.replaceFirst(r10, r11);	 Catch:{ Exception -> 0x006b }
        r1 = new java.io.File;	 Catch:{ Exception -> 0x006b }
        r1.<init>(r2);	 Catch:{ Exception -> 0x006b }
        r9 = 268435456; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        r5 = android.os.ParcelFileDescriptor.open(r1, r9);	 Catch:{ Exception -> 0x006b }
        if (r5 == 0) goto L_0x005c;
    L_0x0054:
        r0 = r5.getFileDescriptor();	 Catch:{ Exception -> 0x006b }
        r9 = 0;
        android.graphics.BitmapFactory.decodeFileDescriptor(r0, r9, r4);	 Catch:{ Exception -> 0x006b }
    L_0x005c:
        ru.ok.android.utils.IOUtils.closeSilently(r5);
    L_0x005f:
        r4.inJustDecodeBounds = r8;
        r9 = new ru.ok.android.utils.BitmapRender$BitmapInfoStruct;
        r10 = r4.outMimeType;
        if (r10 != 0) goto L_0x0081;
    L_0x0067:
        r9.<init>(r4, r7);
        return r9;
    L_0x006b:
        r3 = move-exception;
        r9 = "Exception when decoding bounds for uri (%s)";
        r10 = 1;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x007c }
        r11 = 0;
        r10[r11] = r13;	 Catch:{ all -> 0x007c }
        ru.ok.android.utils.Logger.m180e(r3, r9, r10);	 Catch:{ all -> 0x007c }
        ru.ok.android.utils.IOUtils.closeSilently(r5);
        goto L_0x005f;
    L_0x007c:
        r7 = move-exception;
        ru.ok.android.utils.IOUtils.closeSilently(r5);
        throw r7;
    L_0x0081:
        r7 = r8;
        goto L_0x0067;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.utils.BitmapRender.getBitmapInfo(android.content.ContentResolver, android.net.Uri):ru.ok.android.utils.BitmapRender$BitmapInfoStruct");
    }

    public static Bitmap rotate(Bitmap source, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
    }

    public static Bitmap fastBlur(Bitmap sentBitmap, int radius, boolean inplace) {
        int y;
        int x;
        int width = sentBitmap.getWidth();
        int height = sentBitmap.getHeight();
        int[] pixels = new int[(width * height)];
        sentBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        if (radius >= MUL_TABLE.length) {
            radius = MUL_TABLE.length - 1;
        }
        int widthMinus1 = width - 1;
        int heightMinus1 = height - 1;
        int radiusPlus1 = radius + 1;
        int sumFactor = ((radiusPlus1 + 1) * radiusPlus1) / 2;
        int[] stackArray = new int[(((radius + radius) + 1) * 3)];
        int stackEndIndex = radiusPlus1;
        int yi = 0;
        int yw = 0;
        int mul_sum = MUL_TABLE[radius];
        int shg_sum = SHG_TABLE[radius];
        for (y = 0; y < height; y++) {
            int i;
            int g_sum = 0;
            int r_sum = 0;
            int b_in_sum = 0;
            int g_in_sum = 0;
            int r_in_sum = 0;
            int pixel = pixels[yi];
            int pr = pixel & MotionEventCompat.ACTION_MASK;
            int r_out_sum = radiusPlus1 * pr;
            int pg = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & pixel) >> 8;
            int g_out_sum = radiusPlus1 * pg;
            int pb = (16711680 & pixel) >> 16;
            int b_out_sum = radiusPlus1 * pb;
            r_sum += sumFactor * pr;
            g_sum += sumFactor * pg;
            int b_sum = 0 + (sumFactor * pb);
            int stackIndex = 0;
            for (i = 0; i < radiusPlus1; i++) {
                fillArrayByRGB(stackArray, stackIndex, pr, pg, pb);
                stackIndex++;
            }
            for (i = 1; i < radiusPlus1; i++) {
                int i2;
                if (widthMinus1 < i) {
                    i2 = widthMinus1;
                } else {
                    i2 = i;
                }
                pixel = pixels[yi + i2];
                pr = pixel & MotionEventCompat.ACTION_MASK;
                pg = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & pixel) >> 8;
                pb = (16711680 & pixel) >> 16;
                fillArrayByRGB(stackArray, stackIndex, pr, pg, pb);
                int rbs = radiusPlus1 - i;
                r_sum += pr * rbs;
                g_sum += pg * rbs;
                b_sum += pb * rbs;
                r_in_sum += pr;
                g_in_sum += pg;
                b_in_sum += pb;
                stackIndex++;
            }
            int stackInIndex = 0;
            int stackOutIndex = stackEndIndex;
            for (x = 0; x < width; x++) {
                int i3 = (r_sum * mul_sum) >> shg_sum;
                int i4 = (g_sum * mul_sum) >> shg_sum;
                pixels[yi] = Color.rgb(r, g, (b_sum * mul_sum) >> shg_sum);
                r_sum -= r_out_sum;
                g_sum -= g_out_sum;
                b_sum -= b_out_sum;
                int startIndex = getStartIndex(stackArray, stackInIndex);
                r_out_sum -= stackArray[startIndex + 0];
                g_out_sum -= stackArray[startIndex + 1];
                b_out_sum -= stackArray[startIndex + 2];
                int p = (x + radius) + 1;
                if (p >= widthMinus1) {
                    p = widthMinus1;
                }
                pixel = pixels[p + yw];
                pr = pixel & MotionEventCompat.ACTION_MASK;
                pg = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & pixel) >> 8;
                pb = (16711680 & pixel) >> 16;
                fillArrayByRGB(stackArray, stackInIndex, pr, pg, pb);
                r_in_sum += pr;
                g_in_sum += pg;
                b_in_sum += pb;
                r_sum += r_in_sum;
                g_sum += g_in_sum;
                b_sum += b_in_sum;
                stackInIndex++;
                startIndex = getStartIndex(stackArray, stackOutIndex);
                pr = stackArray[startIndex + 0];
                r_out_sum += pr;
                pg = stackArray[startIndex + 1];
                g_out_sum += pg;
                pb = stackArray[startIndex + 2];
                b_out_sum += pb;
                r_in_sum -= pr;
                g_in_sum -= pg;
                b_in_sum -= pb;
                stackOutIndex++;
                yi++;
            }
            yw += width;
        }
        for (x = 0; x < width; x++) {
            b_sum = 0;
            g_sum = 0;
            r_in_sum = 0;
            b_in_sum = 0;
            g_in_sum = 0;
            pixel = pixels[x];
            pr = pixel & MotionEventCompat.ACTION_MASK;
            r_out_sum = radiusPlus1 * pr;
            pg = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & pixel) >> 8;
            g_out_sum = radiusPlus1 * pg;
            pb = (16711680 & pixel) >> 16;
            b_out_sum = radiusPlus1 * pb;
            r_sum = 0 + (sumFactor * pr);
            g_sum += sumFactor * pg;
            b_sum += sumFactor * pb;
            stackIndex = 0;
            for (i = 0; i < radiusPlus1; i++) {
                fillArrayByRGB(stackArray, stackIndex, pr, pg, pb);
                stackIndex++;
            }
            int yp = width;
            for (i = 1; i <= radius; i++) {
                pixel = pixels[yp + x];
                pr = pixel & MotionEventCompat.ACTION_MASK;
                pg = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & pixel) >> 8;
                pb = (16711680 & pixel) >> 16;
                fillArrayByRGB(stackArray, stackIndex, pr, pg, pb);
                rbs = radiusPlus1 - i;
                r_sum += pr * rbs;
                g_sum += pg * rbs;
                b_sum += pb * rbs;
                r_in_sum += pr;
                g_in_sum += pg;
                b_in_sum += pb;
                stackIndex++;
                if (i < heightMinus1) {
                    yp += width;
                }
            }
            yi = x;
            stackInIndex = 0;
            stackOutIndex = stackEndIndex;
            for (y = 0; y < height; y++) {
                i3 = (r_sum * mul_sum) >> shg_sum;
                i4 = (g_sum * mul_sum) >> shg_sum;
                pixels[yi] = Color.rgb(r, g, (b_sum * mul_sum) >> shg_sum);
                r_sum -= r_out_sum;
                g_sum -= g_out_sum;
                b_sum -= b_out_sum;
                startIndex = getStartIndex(stackArray, stackInIndex);
                r_out_sum -= stackArray[startIndex];
                g_out_sum -= stackArray[startIndex + 1];
                b_out_sum -= stackArray[startIndex + 2];
                p = y + radiusPlus1;
                if (p >= heightMinus1) {
                    p = heightMinus1;
                }
                pixel = pixels[x + (p * width)];
                pr = pixel & MotionEventCompat.ACTION_MASK;
                pg = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & pixel) >> 8;
                pb = (16711680 & pixel) >> 16;
                fillArrayByRGB(stackArray, stackInIndex, pr, pg, pb);
                r_in_sum += pr;
                r_sum += r_in_sum;
                g_in_sum += pg;
                g_sum += g_in_sum;
                b_in_sum += pb;
                b_sum += b_in_sum;
                stackInIndex++;
                startIndex = getStartIndex(stackArray, stackOutIndex);
                pr = stackArray[startIndex];
                r_out_sum += pr;
                pg = stackArray[startIndex + 1];
                g_out_sum += pg;
                pb = stackArray[startIndex + 2];
                b_out_sum += pb;
                r_in_sum -= pr;
                g_in_sum -= pg;
                b_in_sum -= pb;
                stackOutIndex++;
                yi += width;
            }
        }
        if (!inplace) {
            sentBitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }
        sentBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return sentBitmap;
    }

    private static void fillArrayByRGB(int[] stackArray, int index, int r, int g, int b) {
        int startIndex = getStartIndex(stackArray, index);
        stackArray[startIndex] = r;
        stackArray[startIndex + 1] = g;
        stackArray[startIndex + 2] = b;
    }

    private static int getStartIndex(int[] stackArray, int index) {
        return (index * 3) % stackArray.length;
    }

    static {
        MUL_TABLE = new int[]{AdRequest.MAX_CONTENT_URL_LENGTH, AdRequest.MAX_CONTENT_URL_LENGTH, 456, AdRequest.MAX_CONTENT_URL_LENGTH, 328, 456, 335, AdRequest.MAX_CONTENT_URL_LENGTH, 405, 328, 271, 456, 388, 335, 292, AdRequest.MAX_CONTENT_URL_LENGTH, 454, 405, 364, 328, 298, 271, 496, 456, 420, 388, 360, 335, 312, 292, 273, AdRequest.MAX_CONTENT_URL_LENGTH, 482, 454, 428, 405, 383, 364, 345, 328, 312, 298, 284, 271, 259, 496, 475, 456, 437, 420, 404, 388, 374, 360, 347, 335, 323, 312, 302, 292, 282, 273, 265, AdRequest.MAX_CONTENT_URL_LENGTH, 497, 482, 468, 454, 441, 428, 417, 405, 394, 383, 373, 364, 354, 345, 337, 328, 320, 312, 305, 298, 291, 284, 278, 271, 265, 259, 507, 496, 485, 475, 465, 456, 446, 437, 428, 420, 412, 404, 396, 388, 381, 374, 367, 360, 354, 347, 341, 335, 329, 323, 318, 312, 307, 302, 297, 292, 287, 282, 278, 273, 269, 265, 261, AdRequest.MAX_CONTENT_URL_LENGTH, 505, 497, 489, 482, 475, 468, 461, 454, 447, 441, 435, 428, 422, 417, 411, 405, 399, 394, 389, 383, 378, 373, 368, 364, 359, 354, 350, 345, 341, 337, 332, 328, 324, 320, 316, 312, 309, 305, 301, 298, 294, 291, 287, 284, 281, 278, 274, 271, 268, 265, 262, 259, 257, 507, 501, 496, 491, 485, 480, 475, 470, 465, 460, 456, 451, 446, 442, 437, 433, 428, 424, 420, 416, 412, 408, 404, 400, 396, 392, 388, 385, 381, 377, 374, 370, 367, 363, 360, 357, 354, 350, 347, 344, 341, 338, 335, 332, 329, 326, 323, 320, 318, 315, 312, 310, 307, 304, 302, 299, 297, 294, 292, 289, 287, 285, 282, 280, 278, 275, 273, 271, 269, 267, 265, 263, 261, 259};
        SHG_TABLE = new int[]{9, 11, 12, 13, 13, 14, 14, 15, 15, 15, 15, 16, 16, 16, 16, 17, 17, 17, 17, 17, 17, 17, 18, 18, 18, 18, 18, 18, 18, 18, 18, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24};
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void resizeOnFly(android.content.ContentResolver r7, android.net.Uri r8, java.io.OutputStream r9, ru.ok.android.services.processors.image.upload.ResizeSettings r10) throws java.io.IOException {
        /*
        r4 = 1;
        r2 = 0;
        r0 = 0;
        r3 = getBitmapInfo(r7, r8);
        r1 = r3.options;
        r0 = r7.openInputStream(r8);	 Catch:{ all -> 0x0022 }
        if (r0 != 0) goto L_0x002a;
    L_0x000f:
        r2 = new java.io.IOException;	 Catch:{ all -> 0x0022 }
        r3 = "Failed to open input stream for uri (%s)";
        r4 = 1;
        r4 = new java.lang.Object[r4];	 Catch:{ all -> 0x0022 }
        r5 = 0;
        r4[r5] = r8;	 Catch:{ all -> 0x0022 }
        r3 = java.lang.String.format(r3, r4);	 Catch:{ all -> 0x0022 }
        r2.<init>(r3);	 Catch:{ all -> 0x0022 }
        throw r2;	 Catch:{ all -> 0x0022 }
    L_0x0022:
        r2 = move-exception;
        ru.ok.android.utils.IOUtils.closeSilently(r0);
        ru.ok.android.utils.IOUtils.closeSilently(r9);
        throw r2;
    L_0x002a:
        r5 = r1.outMimeType;	 Catch:{ all -> 0x0022 }
        r3 = -1;
        r6 = r5.hashCode();	 Catch:{ all -> 0x0022 }
        switch(r6) {
            case -1487394660: goto L_0x004b;
            case -879258763: goto L_0x0055;
            default: goto L_0x0034;
        };	 Catch:{ all -> 0x0022 }
    L_0x0034:
        r2 = r3;
    L_0x0035:
        switch(r2) {
            case 0: goto L_0x0060;
            case 1: goto L_0x006a;
            default: goto L_0x0038;
        };	 Catch:{ all -> 0x0022 }
    L_0x0038:
        r2 = new java.io.IOException;	 Catch:{ all -> 0x0022 }
        r3 = "Unsupported image format for uri (%s)";
        r4 = 1;
        r4 = new java.lang.Object[r4];	 Catch:{ all -> 0x0022 }
        r5 = 0;
        r4[r5] = r8;	 Catch:{ all -> 0x0022 }
        r3 = java.lang.String.format(r3, r4);	 Catch:{ all -> 0x0022 }
        r2.<init>(r3);	 Catch:{ all -> 0x0022 }
        throw r2;	 Catch:{ all -> 0x0022 }
    L_0x004b:
        r4 = "image/jpeg";
        r4 = r5.equals(r4);	 Catch:{ all -> 0x0022 }
        if (r4 == 0) goto L_0x0034;
    L_0x0054:
        goto L_0x0035;
    L_0x0055:
        r2 = "image/png";
        r2 = r5.equals(r2);	 Catch:{ all -> 0x0022 }
        if (r2 == 0) goto L_0x0034;
    L_0x005e:
        r2 = r4;
        goto L_0x0035;
    L_0x0060:
        resizeJpegOnFly(r10, r1, r0, r9);	 Catch:{ all -> 0x0022 }
    L_0x0063:
        ru.ok.android.utils.IOUtils.closeSilently(r0);
        ru.ok.android.utils.IOUtils.closeSilently(r9);
        return;
    L_0x006a:
        resizePng(r10, r1, r0, r9);	 Catch:{ all -> 0x0022 }
        goto L_0x0063;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.utils.BitmapRender.resizeOnFly(android.content.ContentResolver, android.net.Uri, java.io.OutputStream, ru.ok.android.services.processors.image.upload.ResizeSettings):void");
    }

    private static void resizePng(ResizeSettings resizeSettings, Options options, InputStream is, OutputStream os) throws IOException {
        decodeStream(is, options, resizeSettings.getDesiredWidth(), resizeSettings.getDesiredHeight()).compress(CompressFormat.PNG, 100, os);
    }

    private static void resizeJpegOnFly(ResizeSettings resizeSettings, Options options, InputStream is, OutputStream os) throws IOException {
        int scaleNumerator = resizeSettings.getScaleNumerator(options.outWidth, options.outHeight);
        if (scaleNumerator == 8 && resizeSettings.getRotation() == 0) {
            IOUtils.copyStreams(os, is);
            return;
        }
        long t = SystemClock.elapsedRealtime();
        JpegTranscoder.transcodeJpeg(is, os, resizeSettings.getRotation(), scaleNumerator, resizeSettings.getServerCompressQuality());
        logResizeResults(resizeSettings.getDesiredWidth(), resizeSettings.getDesiredHeight(), options, (float) scaleNumerator, t);
    }

    private static void logResizeResults(int desiredWidth, int desiredHeight, Options options, float scaleFactor, long t) {
        Logger.m173d("IMAGE_DOWNSCALE %d in:(%d, %d) out:(%d, %d) desired:(%d, %d)", Long.valueOf(SystemClock.elapsedRealtime() - t), Integer.valueOf(options.outWidth), Integer.valueOf(options.outHeight), Integer.valueOf((int) ((((float) options.outWidth) * scaleFactor) / 8.0f)), Integer.valueOf((int) ((((float) options.outHeight) * scaleFactor) / 8.0f)), Integer.valueOf(desiredWidth), Integer.valueOf(desiredHeight));
    }

    @NonNull
    private static Bitmap decodeStream(@NonNull InputStream is, @NonNull Options options, int desiredWidth, int desiredHeight) throws IOException {
        boolean purgeable;
        if (VERSION.SDK_INT < 21) {
            purgeable = true;
        } else {
            purgeable = false;
        }
        fillBitmapOptionsForUpload(options, desiredWidth, desiredHeight, purgeable);
        if (purgeable) {
            byte[] bytes = IOUtils.toByteArray(is);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
        if (bitmap != null) {
            return bitmap;
        }
        throw new IOException("Failed to decode bitmap from stream");
    }

    private static void fillBitmapOptionsForUpload(@NonNull Options options, int desiredWidth, int desiredHeight, boolean purgeable) {
        options.inSampleSize = calculateInSampleSizeForUpload(options.outWidth, options.outHeight, desiredWidth, desiredHeight);
        options.inPurgeable = purgeable;
        options.inInputShareable = purgeable;
    }

    private static int calculateInSampleSizeForUpload(int width, int height, int desiredWidth, int desiredHeight) {
        if (width > desiredWidth || height > desiredHeight) {
            return Math.max((int) Math.floor((double) (((float) width) / ((float) desiredWidth))), (int) Math.floor((double) (((float) height) / ((float) desiredHeight))));
        }
        return 1;
    }
}
