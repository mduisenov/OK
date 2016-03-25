package ru.ok.android.ui.image.crop;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import ru.ok.android.ui.image.crop.MonitoredActivity.LifeCycleAdapter;
import ru.ok.android.utils.ThreadUtil;

public class Util {

    private static class BackgroundJob extends LifeCycleAdapter implements Runnable {
        private final MonitoredActivity mActivity;
        private final Runnable mCleanupRunner;
        private final ProgressDialog mDialog;
        private final Handler mHandler;
        private final Runnable mJob;

        /* renamed from: ru.ok.android.ui.image.crop.Util.BackgroundJob.1 */
        class C09851 implements Runnable {
            C09851() {
            }

            public void run() {
                BackgroundJob.this.mActivity.removeLifeCycleListener(BackgroundJob.this);
                if (BackgroundJob.this.mDialog.getWindow() != null) {
                    BackgroundJob.this.mDialog.dismiss();
                }
            }
        }

        public BackgroundJob(MonitoredActivity activity, Runnable job, ProgressDialog dialog, Handler handler) {
            this.mCleanupRunner = new C09851();
            this.mActivity = activity;
            this.mDialog = dialog;
            this.mJob = job;
            this.mActivity.addLifeCycleListener(this);
            this.mHandler = handler;
        }

        public void run() {
            try {
                this.mJob.run();
            } finally {
                this.mHandler.post(this.mCleanupRunner);
            }
        }

        public void onActivityDestroyed(MonitoredActivity activity) {
            this.mCleanupRunner.run();
            this.mHandler.removeCallbacks(this.mCleanupRunner);
        }

        public void onActivityStopped(MonitoredActivity activity) {
            this.mDialog.hide();
        }

        public void onActivityStarted(MonitoredActivity activity) {
            this.mDialog.show();
        }
    }

    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees == 0 || b == null) {
            return b;
        }
        Matrix m = new Matrix();
        m.setRotate((float) degrees, ((float) b.getWidth()) / 2.0f, ((float) b.getHeight()) / 2.0f);
        try {
            Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
            if (b == b2) {
                return b;
            }
            b.recycle();
            return b2;
        } catch (OutOfMemoryError e) {
            return b;
        }
    }

    public static int computeSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        if (initialSize > 8) {
            return ((initialSize + 7) / 8) * 8;
        }
        int roundedSize = 1;
        while (roundedSize < initialSize) {
            roundedSize <<= 1;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        double w = (double) options.outWidth;
        double h = (double) options.outHeight;
        int lowerBound = maxNumOfPixels == -1 ? 1 : (int) Math.ceil(Math.sqrt((w * h) / ((double) maxNumOfPixels)));
        int upperBound = minSideLength == -1 ? NotificationCompat.FLAG_HIGH_PRIORITY : (int) Math.min(Math.floor(w / ((double) minSideLength)), Math.floor(h / ((double) minSideLength)));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if (maxNumOfPixels == -1 && minSideLength == -1) {
            return 1;
        }
        if (minSideLength != -1) {
            return upperBound;
        }
        return lowerBound;
    }

    public static Bitmap transform(Matrix scaler, Bitmap source, int targetWidth, int targetHeight, boolean scaleUp, boolean recycle) {
        Bitmap b2;
        int deltaX = source.getWidth() - targetWidth;
        int deltaY = source.getHeight() - targetHeight;
        if (scaleUp || (deltaX >= 0 && deltaY >= 0)) {
            Bitmap b1;
            float bitmapWidthF = (float) source.getWidth();
            float bitmapHeightF = (float) source.getHeight();
            float scale;
            if (bitmapWidthF / bitmapHeightF > ((float) targetWidth) / ((float) targetHeight)) {
                scale = ((float) targetHeight) / bitmapHeightF;
                if (scale < 0.9f || scale > 1.0f) {
                    scaler.setScale(scale, scale);
                } else {
                    scaler = null;
                }
            } else {
                scale = ((float) targetWidth) / bitmapWidthF;
                if (scale < 0.9f || scale > 1.0f) {
                    scaler.setScale(scale, scale);
                } else {
                    scaler = null;
                }
            }
            if (scaler != null) {
                b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), scaler, true);
            } else {
                b1 = source;
            }
            if (recycle && b1 != source) {
                source.recycle();
            }
            b2 = Bitmap.createBitmap(b1, Math.max(0, b1.getWidth() - targetWidth) / 2, Math.max(0, b1.getHeight() - targetHeight) / 2, targetWidth, targetHeight);
            if (b2 != b1 && (recycle || b1 != source)) {
                b1.recycle();
            }
        } else {
            b2 = Bitmap.createBitmap(targetWidth, targetHeight, Config.ARGB_8888);
            Canvas c = new Canvas(b2);
            int deltaXHalf = Math.max(0, deltaX / 2);
            int deltaYHalf = Math.max(0, deltaY / 2);
            int i = deltaXHalf;
            int i2 = deltaYHalf;
            Rect rect = new Rect(i, i2, Math.min(targetWidth, source.getWidth()) + deltaXHalf, Math.min(targetHeight, source.getHeight()) + deltaYHalf);
            int dstX = (targetWidth - rect.width()) / 2;
            int dstY = (targetHeight - rect.height()) / 2;
            c.drawBitmap(source, rect, new Rect(dstX, dstY, targetWidth - dstX, targetHeight - dstY), null);
            if (recycle) {
                source.recycle();
            }
        }
        return b2;
    }

    public static void closeSilently(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable th) {
            }
        }
    }

    public static void closeSilently(ParcelFileDescriptor c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable th) {
            }
        }
    }

    public static Bitmap makeBitmap(int minSideLength, int maxNumOfPixels, Uri uri, ContentResolver cr, boolean useNative) {
        Bitmap makeBitmap;
        ParcelFileDescriptor input = null;
        try {
            input = cr.openFileDescriptor(uri, "r");
            Options options = null;
            if (useNative) {
                options = createNativeAllocOptions();
            }
            makeBitmap = makeBitmap(minSideLength, maxNumOfPixels, uri, cr, input, options);
        } catch (IOException e) {
            makeBitmap = null;
        } finally {
            closeSilently(input);
        }
        return makeBitmap;
    }

    public static Bitmap makeBitmap(int minSideLength, int maxNumOfPixels, ParcelFileDescriptor pfd, boolean useNative) {
        Options options = null;
        if (useNative) {
            options = createNativeAllocOptions();
        }
        return makeBitmap(minSideLength, maxNumOfPixels, null, null, pfd, options);
    }

    public static Bitmap makeBitmap(int minSideLength, int maxNumOfPixels, Uri uri, ContentResolver cr, ParcelFileDescriptor pfd, Options options) {
        Bitmap bitmap = null;
        if (pfd == null) {
            try {
                pfd = makeInputStream(uri, cr);
            } catch (OutOfMemoryError ex) {
                Log.e("Util", "Got oom exception ", ex);
            } finally {
                closeSilently(pfd);
            }
        }
        if (pfd == null) {
            closeSilently(pfd);
        } else {
            if (options == null) {
                options = new Options();
            }
            FileDescriptor fd = pfd.getFileDescriptor();
            options.inJustDecodeBounds = true;
            BitmapManager.instance().decodeFileDescriptor(fd, options);
            if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
                closeSilently(pfd);
            } else {
                options.inSampleSize = computeSampleSize(options, minSideLength, maxNumOfPixels);
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Config.ARGB_8888;
                bitmap = BitmapManager.instance().decodeFileDescriptor(fd, options);
                closeSilently(pfd);
            }
        }
        return bitmap;
    }

    private static ParcelFileDescriptor makeInputStream(Uri uri, ContentResolver cr) {
        try {
            return cr.openFileDescriptor(uri, "r");
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean equals(String a, String b) {
        return a == b || a.equals(b);
    }

    public static void startBackgroundJob(MonitoredActivity activity, String title, String message, Runnable job, Handler handler) {
        ThreadUtil.execute(new BackgroundJob(activity, job, ProgressDialog.show(activity, title, message, true, false), handler));
    }

    public static Options createNativeAllocOptions() {
        return new Options();
    }
}
