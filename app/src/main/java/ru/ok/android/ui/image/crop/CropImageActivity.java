package ru.ok.android.ui.image.crop;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.View.OnClickListener;
import java.util.concurrent.CountDownLatch;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.image.crop.gallery.IImage;
import ru.ok.android.ui.image.crop.gallery.IImageList;
import ru.ok.android.utils.BitmapRender;
import ru.ok.android.utils.localization.LocalizationManager;

public class CropImageActivity extends MonitoredActivity {
    protected IImageList mAllImages;
    protected int mApplyRotation;
    protected int mAspectX;
    protected int mAspectY;
    protected Bitmap mBitmap;
    protected boolean mCircleCrop;
    protected ContentResolver mContentResolver;
    HighlightView mCrop;
    protected boolean mDoFaceDetection;
    protected final Handler mHandler;
    protected IImage mImage;
    protected CropImageView mImageView;
    protected CompressFormat mOutputFormat;
    protected int mOutputX;
    protected int mOutputY;
    protected boolean mReturnData;
    Runnable mRunFaceDetection;
    protected boolean mSaveToTemp;
    protected Uri mSaveUri;
    boolean mSaving;
    protected boolean mScale;
    protected boolean mScaleUp;
    protected Uri mUri;
    boolean mWaitingToPick;

    /* renamed from: ru.ok.android.ui.image.crop.CropImageActivity.1 */
    class C09701 implements OnClickListener {
        C09701() {
        }

        public void onClick(View v) {
            CropImageActivity.this.setResult(0);
            CropImageActivity.this.finish();
        }
    }

    /* renamed from: ru.ok.android.ui.image.crop.CropImageActivity.2 */
    class C09722 implements OnClickListener {

        /* renamed from: ru.ok.android.ui.image.crop.CropImageActivity.2.1 */
        class C09711 implements Runnable {
            C09711() {
            }

            public void run() {
                CropImageActivity.this.onSaveClicked();
            }
        }

        C09722() {
        }

        public void onClick(View v) {
            Util.startBackgroundJob(CropImageActivity.this, null, LocalizationManager.getString(CropImageActivity.this, 2131166476), new C09711(), CropImageActivity.this.mHandler);
        }
    }

    /* renamed from: ru.ok.android.ui.image.crop.CropImageActivity.3 */
    class C09743 implements Runnable {

        /* renamed from: ru.ok.android.ui.image.crop.CropImageActivity.3.1 */
        class C09731 implements Runnable {
            final /* synthetic */ Bitmap val$b;
            final /* synthetic */ CountDownLatch val$latch;

            C09731(Bitmap bitmap, CountDownLatch countDownLatch) {
                this.val$b = bitmap;
                this.val$latch = countDownLatch;
            }

            public void run() {
                if (!(this.val$b == CropImageActivity.this.mBitmap || this.val$b == null)) {
                    CropImageActivity.this.mImageView.setImageBitmapResetBase(this.val$b, true);
                    CropImageActivity.this.mBitmap.recycle();
                    CropImageActivity.this.mBitmap = this.val$b;
                }
                if (CropImageActivity.this.mImageView.getScale() == 1.0f) {
                    CropImageActivity.this.mImageView.center(true, true);
                }
                this.val$latch.countDown();
            }
        }

        C09743() {
        }

        public void run() {
            Bitmap b;
            CountDownLatch latch = new CountDownLatch(1);
            if (CropImageActivity.this.mImage != null) {
                b = CropImageActivity.this.mImage.fullSizeBitmap(-1, 1048576);
            } else {
                b = CropImageActivity.this.mBitmap;
            }
            CropImageActivity.this.mHandler.post(new C09731(b, latch));
            try {
                latch.await();
                CropImageActivity.this.mRunFaceDetection.run();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.image.crop.CropImageActivity.4 */
    class C09754 implements Runnable {
        C09754() {
        }

        public void run() {
            CropImageActivity.this.mImageView.clear();
            CropImageActivity.this.mBitmap.recycle();
        }
    }

    /* renamed from: ru.ok.android.ui.image.crop.CropImageActivity.5 */
    class C09765 implements Runnable {
        C09765() {
        }

        public void run() {
            CropImageActivity.this.mImageView.clear();
            CropImageActivity.this.mBitmap.recycle();
        }
    }

    /* renamed from: ru.ok.android.ui.image.crop.CropImageActivity.6 */
    class C09776 implements Runnable {
        final /* synthetic */ Bitmap val$fCroppedImage;

        C09776(Bitmap bitmap) {
            this.val$fCroppedImage = bitmap;
        }

        public void run() {
            CropImageActivity.this.mImageView.setImageBitmapResetBase(this.val$fCroppedImage, true);
            CropImageActivity.this.mImageView.center(true, true);
            CropImageActivity.this.mImageView.mHighlightViews.clear();
        }
    }

    /* renamed from: ru.ok.android.ui.image.crop.CropImageActivity.7 */
    class C09787 implements Runnable {
        final /* synthetic */ Bitmap val$fCroppedImage;

        C09787(Bitmap bitmap) {
            this.val$fCroppedImage = bitmap;
        }

        public void run() {
            CropImageActivity.this.mImageView.clear();
            if (!CropImageActivity.this.mReturnData) {
                this.val$fCroppedImage.recycle();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.image.crop.CropImageActivity.8 */
    class C09798 implements Runnable {
        final /* synthetic */ Bitmap val$croppedImage;

        C09798(Bitmap bitmap) {
            this.val$croppedImage = bitmap;
        }

        public void run() {
            CropImageActivity.this.mImageView.clear();
            if (!CropImageActivity.this.mReturnData) {
                this.val$croppedImage.recycle();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.image.crop.CropImageActivity.9 */
    class C09819 implements Runnable {
        Face[] mFaces;
        Matrix mImageMatrix;
        int mNumFaces;
        float mScale;

        /* renamed from: ru.ok.android.ui.image.crop.CropImageActivity.9.1 */
        class C09801 implements Runnable {
            C09801() {
            }

            public void run() {
                boolean z;
                CropImageActivity cropImageActivity = CropImageActivity.this;
                if (C09819.this.mNumFaces > 1) {
                    z = true;
                } else {
                    z = false;
                }
                cropImageActivity.mWaitingToPick = z;
                if (C09819.this.mNumFaces > 0) {
                    for (int i = 0; i < C09819.this.mNumFaces; i++) {
                        C09819.this.handleFace(C09819.this.mFaces[i]);
                    }
                } else {
                    C09819.this.makeDefault();
                }
                CropImageActivity.this.mImageView.invalidate();
                if (CropImageActivity.this.mImageView.mHighlightViews.size() == 1) {
                    CropImageActivity.this.mCrop = (HighlightView) CropImageActivity.this.mImageView.mHighlightViews.get(0);
                    CropImageActivity.this.mCrop.setFocus(true);
                }
                if (C09819.this.mNumFaces <= 1) {
                }
            }
        }

        C09819() {
            this.mScale = 1.0f;
            this.mFaces = new Face[3];
        }

        private void handleFace(Face f) {
            PointF midPoint = new PointF();
            int r = ((int) (f.eyesDistance() * this.mScale)) * 2;
            f.getMidPoint(midPoint);
            midPoint.x *= this.mScale;
            midPoint.y *= this.mScale;
            int midX = (int) midPoint.x;
            int midY = (int) midPoint.y;
            HighlightView hv = new HighlightView(CropImageActivity.this.mImageView);
            Rect imageRect = new Rect(0, 0, CropImageActivity.this.mBitmap.getWidth(), CropImageActivity.this.mBitmap.getHeight());
            RectF faceRect = new RectF((float) midX, (float) midY, (float) midX, (float) midY);
            faceRect.inset((float) (-r), (float) (-r));
            if (faceRect.left < 0.0f) {
                faceRect.inset(-faceRect.left, -faceRect.left);
            }
            if (faceRect.top < 0.0f) {
                faceRect.inset(-faceRect.top, -faceRect.top);
            }
            if (faceRect.right > ((float) imageRect.right)) {
                faceRect.inset(faceRect.right - ((float) imageRect.right), faceRect.right - ((float) imageRect.right));
            }
            if (faceRect.bottom > ((float) imageRect.bottom)) {
                faceRect.inset(faceRect.bottom - ((float) imageRect.bottom), faceRect.bottom - ((float) imageRect.bottom));
            }
            hv.setup(this.mImageMatrix, imageRect, faceRect, CropImageActivity.this.mCircleCrop, false);
            CropImageActivity.this.mImageView.add(hv);
        }

        protected void makeDefault() {
            int cropWidth;
            int cropHeight;
            HighlightView hv = new HighlightView(CropImageActivity.this.mImageView);
            int width = CropImageActivity.this.mBitmap.getWidth();
            int height = CropImageActivity.this.mBitmap.getHeight();
            Rect imageRect = new Rect(0, 0, width, height);
            if (width < 125) {
                cropWidth = width;
            } else {
                cropWidth = (int) (((double) width) * 0.8d);
            }
            if (height < 125) {
                cropHeight = height;
            } else {
                cropHeight = (int) (((double) height) * 0.8d);
            }
            int currentMin = Math.min(cropHeight, cropWidth);
            if (currentMin >= 100) {
                cropWidth = currentMin;
                cropHeight = currentMin;
            }
            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;
            hv.setup(this.mImageMatrix, imageRect, new RectF((float) x, (float) y, (float) (x + cropWidth), (float) (y + cropHeight)), CropImageActivity.this.mCircleCrop, false);
            CropImageActivity.this.mImageView.add(hv);
        }

        private Bitmap prepareBitmap() {
            if (CropImageActivity.this.mBitmap == null) {
                return null;
            }
            if (CropImageActivity.this.mBitmap.getWidth() > NotificationCompat.FLAG_LOCAL_ONLY) {
                this.mScale = 256.0f / ((float) CropImageActivity.this.mBitmap.getWidth());
            }
            Matrix matrix = new Matrix();
            matrix.setScale(this.mScale, this.mScale);
            return Bitmap.createBitmap(CropImageActivity.this.mBitmap, 0, 0, CropImageActivity.this.mBitmap.getWidth(), CropImageActivity.this.mBitmap.getHeight(), matrix, true);
        }

        public void run() {
            this.mImageMatrix = CropImageActivity.this.mImageView.getImageMatrix();
            Bitmap faceBitmap = prepareBitmap();
            this.mScale = 1.0f / this.mScale;
            if (faceBitmap != null && CropImageActivity.this.mDoFaceDetection) {
                this.mNumFaces = new FaceDetector(faceBitmap.getWidth(), faceBitmap.getHeight(), this.mFaces.length).findFaces(faceBitmap, this.mFaces);
            }
            if (!(faceBitmap == null || faceBitmap == CropImageActivity.this.mBitmap)) {
                faceBitmap.recycle();
            }
            CropImageActivity.this.mHandler.post(new C09801());
        }
    }

    public CropImageActivity() {
        this.mOutputFormat = CompressFormat.JPEG;
        this.mSaveUri = null;
        this.mDoFaceDetection = true;
        this.mCircleCrop = false;
        this.mSaveToTemp = false;
        this.mReturnData = false;
        this.mHandler = new Handler();
        this.mScaleUp = true;
        this.mRunFaceDetection = new C09819();
    }

    public void onCreateLocalized(Bundle icicle) {
        super.onCreateLocalized(icicle);
        this.mContentResolver = getContentResolver();
        setContentView(2130903238);
        this.mImageView = (CropImageView) findViewById(C0263R.id.image);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.getString("circleCrop") != null) {
                this.mCircleCrop = true;
                this.mAspectX = 1;
                this.mAspectY = 1;
            }
            this.mSaveUri = (Uri) extras.getParcelable("output");
            if (this.mSaveUri != null) {
                String outputFormatString = extras.getString("outputFormat");
                if (outputFormatString != null) {
                    this.mOutputFormat = CompressFormat.valueOf(outputFormatString);
                }
            }
            this.mBitmap = (Bitmap) extras.getParcelable("data");
            this.mAspectX = extras.getInt("aspectX");
            this.mAspectY = extras.getInt("aspectY");
            this.mOutputX = extras.getInt("outputX");
            this.mOutputY = extras.getInt("outputY");
            this.mScale = extras.getBoolean("scale", true);
            this.mScaleUp = extras.getBoolean("scaleUpIfNeeded", true);
            this.mApplyRotation = extras.getInt("applyRotation");
            this.mSaveToTemp = extras.getBoolean("saveToTemp");
            this.mReturnData = extras.getBoolean("returnData");
            boolean z = extras.containsKey("noFaceDetection") ? !extras.getBoolean("noFaceDetection") : true;
            this.mDoFaceDetection = z;
        }
        if (this.mBitmap == null) {
            this.mUri = intent.getData();
            this.mAllImages = ImageManager.makeImageList(this.mContentResolver, this.mUri, 1);
            this.mImage = this.mAllImages.getImageForUri(this.mUri);
            if (this.mApplyRotation != 0) {
                this.mImage.rotateImageBy(this.mApplyRotation);
            }
            if (this.mImage != null) {
                this.mBitmap = this.mImage.thumbBitmap(true);
            }
        } else if (this.mApplyRotation != 0) {
            this.mBitmap = BitmapRender.rotate(this.mBitmap, (float) this.mApplyRotation);
        }
        if (this.mBitmap == null) {
            finish();
            return;
        }
        getWindow().addFlags(1024);
        findViewById(2131624936).setOnClickListener(new C09701());
        findViewById(2131624799).setOnClickListener(new C09722());
        startFaceDetection();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void startFaceDetection() {
        if (!isFinishing()) {
            this.mImageView.setImageBitmapResetBase(this.mBitmap, true);
            Util.startBackgroundJob(this, null, LocalizationManager.getString((Context) this, 2131166393), new C09743(), this.mHandler);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void onSaveClicked() {
        /*
        r47 = this;
        r0 = r47;
        r2 = r0.mCrop;
        if (r2 != 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r0 = r47;
        r2 = r0.mSaving;
        if (r2 != 0) goto L_0x0006;
    L_0x000d:
        r2 = 1;
        r0 = r47;
        r0.mSaving = r2;
        r0 = r47;
        r2 = r0.mCrop;
        r13 = r2.getCropRect();
        r0 = r47;
        r2 = r0.mBitmap;
        r2 = r2.getWidth();
        r2 = (float) r2;
        r4 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r46 = r2 / r4;
        r2 = r13.left;
        r2 = (float) r2;
        r28 = r2 / r46;
        r2 = r13.right;
        r2 = (float) r2;
        r41 = r2 / r46;
        r0 = r47;
        r2 = r0.mBitmap;
        r2 = r2.getHeight();
        r2 = (float) r2;
        r4 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r27 = r2 / r4;
        r2 = r13.top;
        r2 = (float) r2;
        r42 = r2 / r27;
        r2 = r13.bottom;
        r2 = (float) r2;
        r8 = r2 / r27;
        r0 = r47;
        r2 = r0.mOutputX;
        if (r2 == 0) goto L_0x01c0;
    L_0x004e:
        r0 = r47;
        r2 = r0.mOutputY;
        if (r2 == 0) goto L_0x01c0;
    L_0x0054:
        r0 = r47;
        r2 = r0.mScale;
        if (r2 != 0) goto L_0x01c0;
    L_0x005a:
        r0 = r47;
        r2 = r0.mOutputX;
        r0 = r47;
        r4 = r0.mOutputY;
        r5 = android.graphics.Bitmap.Config.RGB_565;
        r3 = android.graphics.Bitmap.createBitmap(r2, r4, r5);
        r12 = new android.graphics.Canvas;
        r12.<init>(r3);
        r15 = new android.graphics.Rect;
        r2 = 0;
        r4 = 0;
        r0 = r47;
        r5 = r0.mOutputX;
        r0 = r47;
        r6 = r0.mOutputY;
        r15.<init>(r2, r4, r5, r6);
        r2 = r13.width();
        r4 = r15.width();
        r2 = r2 - r4;
        r16 = r2 / 2;
        r2 = r13.height();
        r4 = r15.height();
        r2 = r2 - r4;
        r17 = r2 / 2;
        r2 = 0;
        r0 = r16;
        r2 = java.lang.Math.max(r2, r0);
        r4 = 0;
        r0 = r17;
        r4 = java.lang.Math.max(r4, r0);
        r13.inset(r2, r4);
        r2 = 0;
        r0 = r16;
        r4 = -r0;
        r2 = java.lang.Math.max(r2, r4);
        r4 = 0;
        r0 = r17;
        r5 = -r0;
        r4 = java.lang.Math.max(r4, r5);
        r15.inset(r2, r4);
        r0 = r47;
        r2 = r0.mBitmap;
        r4 = 0;
        r12.drawBitmap(r2, r13, r15, r4);
        r0 = r47;
        r2 = r0.mImageView;
        r4 = new ru.ok.android.ui.image.crop.CropImageActivity$4;
        r0 = r47;
        r4.<init>();
        r2.post(r4);
    L_0x00cc:
        r19 = r3;
        r0 = r47;
        r2 = r0.mImageView;
        r4 = new ru.ok.android.ui.image.crop.CropImageActivity$6;
        r0 = r47;
        r1 = r19;
        r4.<init>(r1);
        r2.post(r4);
        r0 = r47;
        r2 = r0.mApplyRotation;
        if (r2 <= 0) goto L_0x00fb;
    L_0x00e4:
        r31 = r28;
        r32 = r41;
        r33 = r42;
        r29 = r8;
        r0 = r47;
        r2 = r0.mApplyRotation;
        switch(r2) {
            case 90: goto L_0x0262;
            case 180: goto L_0x0270;
            case 270: goto L_0x0282;
            default: goto L_0x00f3;
        };
    L_0x00f3:
        r28 = r31;
        r41 = r32;
        r42 = r33;
        r8 = r29;
    L_0x00fb:
        r38 = 0;
        r37 = 0;
        r20 = 0;
        r43 = 0;
        r9 = new android.graphics.BitmapFactory$Options;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r9.<init>();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = 1;
        r9.inJustDecodeBounds = r2;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = r47.getContentResolver();	 Catch:{ Exception -> 0x0397, OutOfMemoryError -> 0x02b5 }
        r0 = r47;
        r4 = r0.mUri;	 Catch:{ Exception -> 0x0397, OutOfMemoryError -> 0x02b5 }
        r5 = "r";
        r37 = r2.openFileDescriptor(r4, r5);	 Catch:{ Exception -> 0x0397, OutOfMemoryError -> 0x02b5 }
    L_0x011a:
        if (r37 == 0) goto L_0x0136;
    L_0x011c:
        r20 = r37.getFileDescriptor();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = 0;
        r0 = r20;
        android.graphics.BitmapFactory.decodeFileDescriptor(r0, r2, r9);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = r9.mCancel;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        if (r2 != 0) goto L_0x0136;
    L_0x012a:
        r2 = r9.outWidth;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = -1;
        if (r2 == r4) goto L_0x0136;
    L_0x012f:
        r2 = r9.outHeight;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = -1;
        if (r2 == r4) goto L_0x0136;
    L_0x0134:
        r43 = 1;
    L_0x0136:
        if (r43 != 0) goto L_0x018f;
    L_0x0138:
        if (r37 == 0) goto L_0x013d;
    L_0x013a:
        ru.ok.android.utils.IOUtils.closeSilently(r37);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
    L_0x013d:
        r0 = r47;
        r2 = r0.mUri;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = r2.getScheme();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = "file";
        r2 = r2.equals(r4);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        if (r2 == 0) goto L_0x018f;
    L_0x014e:
        r0 = r47;
        r2 = r0.mUri;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = "file://";
        r5 = "";
        r22 = r2.replaceFirst(r4, r5);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r21 = new java.io.File;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r21.<init>(r22);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = r21.exists();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        if (r2 == 0) goto L_0x018f;
    L_0x016b:
        r2 = 268435456; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        r0 = r21;
        r37 = android.os.ParcelFileDescriptor.open(r0, r2);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        if (r37 == 0) goto L_0x018f;
    L_0x0175:
        r20 = r37.getFileDescriptor();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = 0;
        r0 = r20;
        android.graphics.BitmapFactory.decodeFileDescriptor(r0, r2, r9);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = r9.mCancel;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        if (r2 != 0) goto L_0x018f;
    L_0x0183:
        r2 = r9.outWidth;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = -1;
        if (r2 == r4) goto L_0x018f;
    L_0x0188:
        r2 = r9.outHeight;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = -1;
        if (r2 == r4) goto L_0x018f;
    L_0x018d:
        r43 = 1;
    L_0x018f:
        if (r43 != 0) goto L_0x02b8;
    L_0x0191:
        if (r37 != 0) goto L_0x0290;
    L_0x0193:
        r2 = new java.io.IOException;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4.<init>();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r5 = "Can't open file descriptor for uri: ";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r0 = r47;
        r5 = r0.mUri;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2.<init>(r4);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        throw r2;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
    L_0x01b5:
        r18 = move-exception;
    L_0x01b6:
        ru.ok.android.utils.Logger.m178e(r18);
        r0 = r47;
        r0.fallbackCrop(r3);
        goto L_0x0006;
    L_0x01c0:
        r45 = r13.width();
        r26 = r13.height();
        r0 = r47;
        r2 = r0.mCircleCrop;
        if (r2 == 0) goto L_0x025e;
    L_0x01ce:
        r2 = android.graphics.Bitmap.Config.ARGB_8888;
    L_0x01d0:
        r0 = r45;
        r1 = r26;
        r3 = android.graphics.Bitmap.createBitmap(r0, r1, r2);
        r12 = new android.graphics.Canvas;
        r12.<init>(r3);
        r15 = new android.graphics.Rect;
        r2 = 0;
        r4 = 0;
        r0 = r45;
        r1 = r26;
        r15.<init>(r2, r4, r0, r1);
        r0 = r47;
        r2 = r0.mBitmap;
        r4 = 0;
        r12.drawBitmap(r2, r13, r15, r4);
        r0 = r47;
        r2 = r0.mImageView;
        r4 = new ru.ok.android.ui.image.crop.CropImageActivity$5;
        r0 = r47;
        r4.<init>();
        r2.post(r4);
        r0 = r47;
        r2 = r0.mCircleCrop;
        if (r2 == 0) goto L_0x0234;
    L_0x0204:
        r10 = new android.graphics.Canvas;
        r10.<init>(r3);
        r36 = new android.graphics.Path;
        r36.<init>();
        r0 = r45;
        r2 = (float) r0;
        r4 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r2 = r2 / r4;
        r0 = r26;
        r4 = (float) r0;
        r5 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = r4 / r5;
        r0 = r45;
        r5 = (float) r0;
        r6 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r5 = r5 / r6;
        r6 = android.graphics.Path.Direction.CW;
        r0 = r36;
        r0.addCircle(r2, r4, r5, r6);
        r2 = android.graphics.Region.Op.DIFFERENCE;
        r0 = r36;
        r10.clipPath(r0, r2);
        r2 = 0;
        r4 = android.graphics.PorterDuff.Mode.CLEAR;
        r10.drawColor(r2, r4);
    L_0x0234:
        r0 = r47;
        r2 = r0.mOutputX;
        if (r2 == 0) goto L_0x00cc;
    L_0x023a:
        r0 = r47;
        r2 = r0.mOutputY;
        if (r2 == 0) goto L_0x00cc;
    L_0x0240:
        r0 = r47;
        r2 = r0.mScale;
        if (r2 == 0) goto L_0x00cc;
    L_0x0246:
        r2 = new android.graphics.Matrix;
        r2.<init>();
        r0 = r47;
        r4 = r0.mOutputX;
        r0 = r47;
        r5 = r0.mOutputY;
        r0 = r47;
        r6 = r0.mScaleUp;
        r7 = 1;
        r3 = ru.ok.android.ui.image.crop.Util.transform(r2, r3, r4, r5, r6, r7);
        goto L_0x00cc;
    L_0x025e:
        r2 = android.graphics.Bitmap.Config.RGB_565;
        goto L_0x01d0;
    L_0x0262:
        r31 = r42;
        r32 = r8;
        r2 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r33 = r2 - r41;
        r2 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r29 = r2 - r28;
        goto L_0x00f3;
    L_0x0270:
        r2 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r31 = r2 - r41;
        r2 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r32 = r2 - r28;
        r2 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r33 = r2 - r8;
        r2 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r29 = r2 - r42;
        goto L_0x00f3;
    L_0x0282:
        r2 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r31 = r2 - r8;
        r2 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r32 = r2 - r42;
        r33 = r28;
        r29 = r41;
        goto L_0x00f3;
    L_0x0290:
        ru.ok.android.utils.IOUtils.closeSilently(r37);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = new java.io.IOException;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4.<init>();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r5 = "Can't decode bounds for file at uri: ";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r0 = r47;
        r5 = r0.mUri;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2.<init>(r4);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        throw r2;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
    L_0x02b5:
        r18 = move-exception;
        goto L_0x01b6;
    L_0x02b8:
        r2 = r47.getContentResolver();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r0 = r47;
        r4 = r0.mUri;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = ru.ok.android.utils.BitmapRender.getBitmapInfo(r2, r4);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r9 = r2.options;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r0 = r9.outWidth;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r45 = r0;
        r0 = r9.outHeight;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r26 = r0;
        r0 = r45;
        r2 = (float) r0;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r44 = r2 / r4;
        r0 = r26;
        r2 = (float) r0;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r25 = r2 / r4;
        r23 = new android.graphics.Rect;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r23.<init>();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = r28 * r44;
        r2 = (int) r2;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r0 = r23;
        r0.left = r2;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = r41 * r44;
        r2 = (int) r2;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r0 = r23;
        r0.right = r2;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = r42 * r25;
        r2 = (int) r2;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r0 = r23;
        r0.top = r2;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = r8 * r25;
        r2 = (int) r2;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r0 = r23;
        r0.bottom = r2;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = 1;
        r0 = r20;
        r14 = android.graphics.BitmapRegionDecoder.newInstance(r0, r2);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r9 = new android.graphics.BitmapFactory$Options;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r9.<init>();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r9.inPreferredConfig = r2;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r0 = r23;
        r38 = r14.decodeRegion(r0, r9);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        ru.ok.android.utils.IOUtils.closeSilently(r37);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        if (r38 == 0) goto L_0x0390;
    L_0x0318:
        r2 = r47.getIntent();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = "out_dir";
        r35 = r2.getStringExtra(r4);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r11 = ru.ok.android.utils.Storage.External.Application.getCacheDir(r47);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        if (r35 != 0) goto L_0x0386;
    L_0x0329:
        r34 = r11;
    L_0x032b:
        r2 = ".jpg";
        r0 = r34;
        r30 = ru.ok.android.utils.FileUtils.generateEmptyFile(r0, r2);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        if (r30 == 0) goto L_0x0006;
    L_0x0336:
        r2 = r30.exists();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        if (r2 == 0) goto L_0x0006;
    L_0x033c:
        r24 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r0 = r24;
        r1 = r30;
        r0.<init>(r1);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = 90;
        r0 = r38;
        r1 = r24;
        r0.compress(r2, r4, r1);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r24.close();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r38.recycle();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r40 = android.net.Uri.fromFile(r30);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r39 = new android.content.Intent;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r39.<init>();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = "file_uri";
        r0 = r39;
        r1 = r40;
        r0.putExtra(r2, r1);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r0 = r47;
        r2 = r0.mHandler;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r4 = new ru.ok.android.ui.image.crop.CropImageActivity$7;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r0 = r47;
        r1 = r19;
        r4.<init>(r1);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2.post(r4);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r2 = -1;
        r0 = r47;
        r1 = r39;
        r0.setResult(r2, r1);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r47.finish();	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        goto L_0x0006;
    L_0x0386:
        r34 = new java.io.File;	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        r0 = r34;
        r1 = r35;
        r0.<init>(r11, r1);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        goto L_0x032b;
    L_0x0390:
        r0 = r47;
        r0.fallbackCrop(r3);	 Catch:{ Exception -> 0x01b5, OutOfMemoryError -> 0x02b5 }
        goto L_0x0006;
    L_0x0397:
        r2 = move-exception;
        goto L_0x011a;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.ui.image.crop.CropImageActivity.onSaveClicked():void");
    }

    protected final void fallbackCrop(Bitmap croppedImage) {
        Intent resultIntent = new Intent();
        if (this.mSaveToTemp) {
            saveOutput(croppedImage, resultIntent);
            return;
        }
        resultIntent.setAction("inline-data");
        setResult(-1, resultIntent);
        finish();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void saveOutput(android.graphics.Bitmap r11, android.content.Intent r12) {
        /*
        r10 = this;
        r7 = r10.mSaveUri;
        if (r7 == 0) goto L_0x005a;
    L_0x0004:
        r6 = 0;
        r7 = r10.mContentResolver;	 Catch:{ IOException -> 0x0034 }
        r8 = r10.mSaveUri;	 Catch:{ IOException -> 0x0034 }
        r6 = r7.openOutputStream(r8);	 Catch:{ IOException -> 0x0034 }
        if (r6 == 0) goto L_0x0016;
    L_0x000f:
        r7 = r10.mOutputFormat;	 Catch:{ IOException -> 0x0034 }
        r8 = 75;
        r11.compress(r7, r8, r6);	 Catch:{ IOException -> 0x0034 }
    L_0x0016:
        ru.ok.android.ui.image.crop.Util.closeSilently(r6);
    L_0x0019:
        r7 = r10.mSaveUri;
        r7 = r7.toString();
        r12.setAction(r7);
    L_0x0022:
        r7 = r10.mHandler;
        r8 = new ru.ok.android.ui.image.crop.CropImageActivity$8;
        r8.<init>(r11);
        r7.post(r8);
        r7 = -1;
        r10.setResult(r7, r12);
        r10.finish();
        return;
    L_0x0034:
        r1 = move-exception;
        r7 = "CropImage";
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0055 }
        r8.<init>();	 Catch:{ all -> 0x0055 }
        r9 = "Cannot open file: ";
        r8 = r8.append(r9);	 Catch:{ all -> 0x0055 }
        r9 = r10.mSaveUri;	 Catch:{ all -> 0x0055 }
        r8 = r8.append(r9);	 Catch:{ all -> 0x0055 }
        r8 = r8.toString();	 Catch:{ all -> 0x0055 }
        android.util.Log.e(r7, r8, r1);	 Catch:{ all -> 0x0055 }
        ru.ok.android.ui.image.crop.Util.closeSilently(r6);
        goto L_0x0019;
    L_0x0055:
        r7 = move-exception;
        ru.ok.android.ui.image.crop.Util.closeSilently(r6);
        throw r7;
    L_0x005a:
        r7 = "rect";
        r8 = r10.mCrop;
        r8 = r8.getCropRect();
        r8 = r8.toString();
        r12.putExtra(r7, r8);
        r7 = r10.getIntent();	 Catch:{ Exception -> 0x00a0 }
        r8 = "out_dir";
        r5 = r7.getStringExtra(r8);	 Catch:{ Exception -> 0x00a0 }
        r0 = ru.ok.android.utils.Storage.External.Application.getCacheDir(r10);	 Catch:{ Exception -> 0x00a0 }
        if (r5 != 0) goto L_0x00ac;
    L_0x007b:
        r4 = r0;
    L_0x007c:
        r7 = ".png";
        r2 = ru.ok.android.utils.FileUtils.generateEmptyFile(r4, r7);	 Catch:{ Exception -> 0x00a0 }
        r3 = ru.ok.android.utils.FileUtils.saveBitmapToFile(r11, r2);	 Catch:{ Exception -> 0x00a0 }
        r7 = r10.mUri;	 Catch:{ Exception -> 0x00a0 }
        if (r7 == 0) goto L_0x0099;
    L_0x008b:
        r7 = new java.io.File;	 Catch:{ Exception -> 0x00a0 }
        r8 = r10.mUri;	 Catch:{ Exception -> 0x00a0 }
        r8 = r8.getPath();	 Catch:{ Exception -> 0x00a0 }
        r7.<init>(r8);	 Catch:{ Exception -> 0x00a0 }
        ru.ok.android.utils.FileUtils.copyExif(r7, r2);	 Catch:{ Exception -> 0x00a0 }
    L_0x0099:
        r7 = "uri";
        r12.putExtra(r7, r3);	 Catch:{ Exception -> 0x00a0 }
        goto L_0x0022;
    L_0x00a0:
        r1 = move-exception;
        r7 = "CropImage";
        r8 = "store image fail, continue anyway";
        android.util.Log.e(r7, r8, r1);
        goto L_0x0022;
    L_0x00ac:
        r4 = new java.io.File;	 Catch:{ Exception -> 0x00a0 }
        r4.<init>(r0, r5);	 Catch:{ Exception -> 0x00a0 }
        goto L_0x007c;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.ui.image.crop.CropImageActivity.saveOutput(android.graphics.Bitmap, android.content.Intent):void");
    }

    protected void onDestroy() {
        if (this.mAllImages != null) {
            this.mAllImages.close();
        }
        super.onDestroy();
    }

    protected boolean isSupportToolbarVisible() {
        return false;
    }
}
