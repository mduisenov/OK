package ru.ok.android.ui.fragments.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Editor;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;
import java.io.Closeable;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.PaddingBorderedBitmapView;
import ru.ok.android.ui.custom.mediacomposer.adapter.SimpleTextWatcher;
import ru.ok.android.ui.image.crop.CropImageActivity;
import ru.ok.android.utils.BitmapRender;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.MimeTypes;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.localization.LocalizationManager;

public class ImageEditFragment extends Fragment {
    protected boolean animating;
    protected Bitmap bitmap;
    private ExecutorService cacheExecutorService;
    protected EditText commentEdit;
    private boolean commentEnabled;
    protected Context context;
    private Future currentFuture;
    protected int currentHeight;
    protected int currentRotation;
    protected int currentWidth;
    private ExecutorService executorService;
    boolean forceReload;
    protected DiskLruCache imageCache;
    protected ImageView imageDefaultView;
    protected View imageErrorView;
    private String imageId;
    protected ImageLoader imageLoader;
    private String imageMimeType;
    protected View imagePlaceHolder;
    protected PaddingBorderedBitmapView imageView;
    protected View mainView;
    private int originalHeight;
    private int originalWidth;
    protected String outDirPath;
    protected View removeBtn;
    protected boolean removeButtonVisible;
    private boolean tempUri;
    private Uri uri;

    /* renamed from: ru.ok.android.ui.fragments.image.ImageEditFragment.1 */
    class C08211 implements OnTouchListener {
        C08211() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            ((OnTouchListener) ImageEditFragment.this.getActivity()).onTouch(v, event);
            return false;
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.image.ImageEditFragment.2 */
    class C08222 extends SimpleTextWatcher {
        C08222() {
        }

        public void afterTextChanged(Editable s) {
            ImageEditFragment.this.fireChangeComment(s.toString());
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.image.ImageEditFragment.3 */
    class C08233 implements OnClickListener {
        C08233() {
        }

        public void onClick(View view) {
            ((OnRemoveClickedListener) ImageEditFragment.this.getActivity()).onRemoveClicked(ImageEditFragment.this);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.image.ImageEditFragment.4 */
    class C08244 implements OnGlobalLayoutListener {
        C08244() {
        }

        public void onGlobalLayout() {
            int width = (ImageEditFragment.this.imageView.getMeasuredWidth() - ImageEditFragment.this.imageView.getPaddingLeft()) - ImageEditFragment.this.imageView.getPaddingRight();
            int height = (ImageEditFragment.this.imageView.getMeasuredHeight() - ImageEditFragment.this.imageView.getPaddingTop()) - ImageEditFragment.this.imageView.getPaddingBottom();
            if (ImageEditFragment.this.forceReload) {
                ImageEditFragment.this.forceReload = false;
                ImageEditFragment.this.currentWidth = width;
                ImageEditFragment.this.currentHeight = height;
                ImageEditFragment.this.loadImage(true);
            } else if (height > 0 && width > 0 && (width > ImageEditFragment.this.currentWidth || height > ImageEditFragment.this.currentHeight)) {
                ImageEditFragment.this.currentWidth = width;
                ImageEditFragment.this.currentHeight = height;
                if (ImageEditFragment.this.bitmap == null) {
                    ImageEditFragment.this.loadImage(false);
                } else if (ImageEditFragment.this.bitmap.getWidth() < width && ImageEditFragment.this.bitmap.getHeight() < height) {
                    ImageEditFragment.this.loadImage(false);
                }
            }
            ImageEditFragment.this.repositionRemoveButton();
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.image.ImageEditFragment.5 */
    class C08255 implements OnLoadCompleteListener {
        C08255() {
        }

        public void onLoadComplete(Bitmap bitmap) {
            ImageEditFragment.this.onLoadImageComplete(bitmap);
        }
    }

    private static final class ImageLoader extends Thread {
        protected DiskLruCache cache;
        private final Context context;
        protected ExecutorService executorService;
        private final int height;
        protected final String id;
        private boolean mCanceled;
        private OnLoadCompleteListener onLoadCompleteListener;
        private final boolean recache;
        private final int rotation;
        private final Uri uri;
        private final int width;

        public interface OnLoadCompleteListener {
            void onLoadComplete(@Nullable Bitmap bitmap);
        }

        /* renamed from: ru.ok.android.ui.fragments.image.ImageEditFragment.ImageLoader.1 */
        class C08261 implements Runnable {
            final /* synthetic */ Bitmap val$toCache;

            C08261(Bitmap bitmap) {
                this.val$toCache = bitmap;
            }

            public void run() {
                try {
                    Editor editor = ImageLoader.this.cache.edit(ImageLoader.this.id);
                    Closeable out = editor.newOutputStream(0);
                    this.val$toCache.compress(CompressFormat.JPEG, 100, out);
                    editor.commit();
                    IOUtils.closeSilently(out);
                } catch (Exception e) {
                }
            }
        }

        /* renamed from: ru.ok.android.ui.fragments.image.ImageEditFragment.ImageLoader.2 */
        class C08272 implements Runnable {
            final /* synthetic */ Bitmap val$result;

            C08272(Bitmap bitmap) {
                this.val$result = bitmap;
            }

            public void run() {
                ImageLoader.this.onLoadCompleteListener.onLoadComplete(this.val$result);
            }
        }

        public ImageLoader(Context context, DiskLruCache cache, ExecutorService executor, OnLoadCompleteListener onLoadCompleteListener, Uri uri, String id, int width, int height, int rotation, boolean recache) {
            super("ImageLoader");
            this.context = context;
            this.onLoadCompleteListener = onLoadCompleteListener;
            this.uri = uri;
            this.id = id;
            this.width = width;
            this.height = height;
            this.rotation = rotation;
            this.cache = cache;
            this.recache = recache;
            this.executorService = executor;
        }

        public void run() {
            if (!this.mCanceled) {
                Bitmap result = null;
                int reqWidth = this.width;
                int reqHeight = this.height;
                if (this.rotation == 90 || this.rotation == 270) {
                    reqWidth = this.height;
                    reqHeight = this.width;
                }
                if (!(this.cache == null || this.recache)) {
                    try {
                        Snapshot snapshot = this.cache.get(this.id);
                        if (snapshot != null) {
                            InputStream stream = snapshot.getInputStream(0);
                            result = BitmapFactory.decodeStream(stream);
                            stream.close();
                        }
                        if (result != null && result.getWidth() < reqWidth && result.getHeight() < reqHeight) {
                            result.recycle();
                            result = null;
                        }
                    } catch (Exception exc) {
                        Logger.m177e("Failed to get an image from cache", exc);
                    }
                }
                if (!this.mCanceled) {
                    int maxSide = Math.max(reqWidth, reqHeight);
                    if (result == null) {
                        try {
                            result = BitmapRender.resizeForBounds(this.context.getContentResolver(), this.uri, maxSide, maxSide, 3);
                            if (result != null) {
                                this.executorService.execute(new C08261(result));
                            }
                        } catch (Exception exc2) {
                            Logger.m177e("Failed to load an image", exc2);
                        }
                    }
                    if (!this.mCanceled) {
                        if (!(result == null || this.rotation == 0)) {
                            try {
                                result = BitmapRender.rotate(result, (float) this.rotation);
                            } catch (Exception exc22) {
                                Logger.m177e("Failed to rotate an image", exc22);
                            }
                        }
                        callOnLoadComplete(result);
                    }
                }
            }
        }

        private void callOnLoadComplete(Bitmap result) {
            if (this.onLoadCompleteListener != null) {
                ThreadUtil.executeOnMain(new C08272(result));
            }
        }

        public synchronized void cancel() {
            this.mCanceled = true;
        }
    }

    public interface OnRemoveClickedListener {
        void onRemoveClicked(ImageEditFragment imageEditFragment);
    }

    private class RotateAnimationListener implements AnimationListener {
        protected final int mRotation;

        /* renamed from: ru.ok.android.ui.fragments.image.ImageEditFragment.RotateAnimationListener.1 */
        class C08281 extends AsyncTask<Void, Void, Bitmap> {
            C08281() {
            }

            protected void onPreExecute() {
                ImageEditFragment.this.imageView.setDrawingCacheEnabled(false);
            }

            protected Bitmap doInBackground(Void... params) {
                return BitmapRender.rotate(ImageEditFragment.this.bitmap, (float) RotateAnimationListener.this.mRotation);
            }

            protected void onPostExecute(Bitmap rotatedBitmap) {
                ImageEditFragment.this.imageView.clearAnimation();
                ImageEditFragment.this.imageView.setImageBitmap(rotatedBitmap);
                ImageEditFragment.this.bitmap = rotatedBitmap;
                ImageEditFragment.this.repositionRemoveButton();
                ImageEditFragment.this.removeBtn.setVisibility(ImageEditFragment.this.removeButtonVisible ? 0 : 4);
                ImageEditFragment.this.imageView.setDrawBorder(true);
                ImageEditFragment.this.animating = false;
                ImageEditFragment.this.fireOnRotationChanged(ImageEditFragment.this.currentRotation);
            }
        }

        public RotateAnimationListener(int rotation) {
            this.mRotation = rotation;
        }

        public void onAnimationStart(Animation animation) {
            ImageEditFragment.this.removeBtn.setVisibility(4);
            ImageEditFragment.this.imageView.setDrawBorder(false);
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            new C08281().execute(new Void[0]);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.uri = (Uri) savedInstanceState.getParcelable("file_uri");
            this.tempUri = savedInstanceState.getBoolean("temp");
            this.currentRotation = savedInstanceState.getInt("rttn");
            this.imageId = savedInstanceState.getString("img_id");
            this.commentEnabled = savedInstanceState.getBoolean("comments_enabled");
            this.imageMimeType = savedInstanceState.getString("mime_type");
        }
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.context = container.getContext();
        this.mainView = LocalizationManager.inflate(getActivity(), 2130903239, container, false);
        this.imageView = (PaddingBorderedBitmapView) this.mainView.findViewById(C0263R.id.image);
        this.imagePlaceHolder = this.mainView.findViewById(2131624937);
        this.imageDefaultView = (ImageView) this.mainView.findViewById(2131624938);
        this.imageErrorView = this.mainView.findViewById(2131624805);
        this.commentEdit = (EditText) this.mainView.findViewById(2131624887);
        if (this.commentEnabled) {
            this.commentEdit.setOnTouchListener(new C08211());
            this.commentEdit.addTextChangedListener(new C08222());
        } else {
            this.commentEdit.setVisibility(8);
        }
        this.removeBtn = this.mainView.findViewById(2131624939);
        this.removeBtn.setOnClickListener(new C08233());
        this.mainView.getViewTreeObserver().addOnGlobalLayoutListener(new C08244());
        if (!(this.bitmap == null || this.bitmap.isRecycled())) {
            this.imageView.setImageBitmap(this.bitmap);
            this.imageView.setVisibility(0);
            this.imageView.setShouldDrawGifMarker(MimeTypes.isGif(this.imageMimeType));
            this.imagePlaceHolder.setVisibility(8);
        }
        repositionRemoveButton();
        this.removeBtn.setVisibility(this.removeButtonVisible ? 0 : 4);
        return this.mainView;
    }

    public void onDestroy() {
        cancelImageLoader();
        if (this.bitmap != null) {
            this.bitmap.recycle();
            this.bitmap = null;
            this.imageView.setImageBitmap(null);
        }
        super.onDestroy();
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("file_uri", this.uri);
        outState.putBoolean("temp", this.tempUri);
        outState.putInt("rttn", this.currentRotation);
        outState.putString("img_id", this.imageId);
        outState.putBoolean("comments_enabled", this.commentEnabled);
        outState.putString("mime_type", this.imageMimeType);
        super.onSaveInstanceState(outState);
    }

    public void setUri(Uri uri, Context context) {
        if ((uri == null && this.uri != null) || !uri.equals(this.uri)) {
            deleteTempUriAndContent();
        }
        if (uri != null) {
            try {
                Options bounds = BitmapRender.getBitmapInfo(context.getContentResolver(), uri).options;
                this.originalWidth = bounds.outWidth;
                this.originalHeight = bounds.outHeight;
            } catch (Exception exc) {
                Logger.m177e("Failed to seat a new Uri", exc);
            }
        }
        this.uri = uri;
    }

    public void setOutDirPath(String outDirPath) {
        this.outDirPath = outDirPath;
    }

    public final void deleteTempUriAndContent() {
        Uri uri = this.uri;
        if (this.tempUri) {
            FileUtils.deleteFileAtUri(uri);
        }
    }

    public void setCommentEnabled(boolean isEnabled) {
        this.commentEnabled = isEnabled;
    }

    public void setImageMimeType(String mimeType) {
        this.imageMimeType = mimeType;
    }

    protected final void loadImage(boolean recache) {
        cancelImageLoader();
        this.imageLoader = new ImageLoader(this.context, this.imageCache, this.cacheExecutorService, new C08255(), this.uri, this.imageId, this.currentWidth, this.currentHeight, this.currentRotation, recache);
        if (this.executorService == null) {
            this.imageLoader.start();
        } else if (!this.executorService.isShutdown()) {
            this.currentFuture = this.executorService.submit(this.imageLoader);
        }
    }

    private void onLoadImageComplete(@Nullable Bitmap bitmap) {
        int i = 0;
        if (bitmap != null) {
            this.bitmap = bitmap;
            this.imageView.setImageBitmap(this.bitmap);
            this.imageView.setVisibility(0);
            this.imageView.setShouldDrawGifMarker(MimeTypes.isGif(this.imageMimeType));
            this.imagePlaceHolder.setVisibility(8);
            this.imageLoader = null;
            repositionRemoveButton();
            View view = this.removeBtn;
            if (!this.removeButtonVisible) {
                i = 4;
            }
            view.setVisibility(i);
            return;
        }
        this.imageErrorView.setVisibility(0);
        this.imageDefaultView.setVisibility(8);
        Logger.m176e("Failed to seat a load an image for uri: " + this.uri.toString());
    }

    private void cancelImageLoader() {
        if (this.imageLoader != null) {
            this.imageLoader.cancel();
            if (this.executorService == null) {
                this.imageLoader.interrupt();
            } else if (this.currentFuture != null) {
                this.currentFuture.cancel(true);
            }
        }
    }

    public final void repositionRemoveButton() {
        int leftMargin;
        int topMargin;
        Rect rect = this.imageView.updateBitmapMetrics();
        if (rect.right == 0 || rect.bottom == 0) {
            leftMargin = this.imagePlaceHolder.getRight() - (this.removeBtn.getMeasuredWidth() / 2);
            topMargin = this.imagePlaceHolder.getTop() - (this.removeBtn.getMeasuredHeight() / 2);
        } else {
            leftMargin = (this.imageView.getLeft() + rect.right) - (this.removeBtn.getMeasuredWidth() / 2);
            topMargin = (this.imageView.getTop() + rect.top) - (this.removeBtn.getMeasuredHeight() / 2);
        }
        LayoutParams layoutParams = (LayoutParams) this.removeBtn.getLayoutParams();
        if (leftMargin != layoutParams.leftMargin || topMargin != layoutParams.topMargin) {
            layoutParams.leftMargin = leftMargin;
            layoutParams.topMargin = topMargin;
            this.removeBtn.setLayoutParams(layoutParams);
        }
    }

    public void setRemoveButtonVisibility(boolean visible) {
        this.removeButtonVisible = visible;
        if (!this.animating && this.removeBtn != null) {
            this.removeBtn.setVisibility(visible ? 0 : 4);
            repositionRemoveButton();
        }
    }

    public void setRemoveButtonEnabled(boolean enabled) {
        this.removeBtn.setEnabled(enabled);
    }

    public final void rotate(boolean cw) {
        if (!this.animating && this.bitmap != null) {
            this.animating = true;
            int rotation = cw ? 90 : -90;
            float scaleFactor = 1.0f;
            Rect rect = this.imageView.updateBitmapMetrics();
            float currentBitmapWidth = (float) rect.width();
            float currentBitmapHeight = (float) rect.height();
            int futureRotation = this.currentRotation + rotation;
            if (futureRotation == -90) {
                futureRotation = 270;
            } else if (futureRotation == 360) {
                futureRotation = 0;
            }
            float bitmapWidth = (float) this.originalWidth;
            float bitmapHeight = (float) this.originalHeight;
            if (futureRotation == 90 || futureRotation == 270) {
                bitmapWidth = (float) this.originalHeight;
                bitmapHeight = (float) this.originalWidth;
            }
            float newWidth = (float) ((this.imageView.getMeasuredWidth() - this.imageView.getPaddingLeft()) - this.imageView.getPaddingRight());
            float newHeight = (float) ((this.imageView.getMeasuredHeight() - this.imageView.getPaddingTop()) - this.imageView.getPaddingBottom());
            if (bitmapWidth < newWidth) {
                newWidth = bitmapWidth;
            }
            if (bitmapHeight < newHeight) {
                newHeight = bitmapHeight;
            }
            float xScale = newHeight / currentBitmapWidth;
            float yScale = newWidth / currentBitmapHeight;
            if (!(xScale == 1.0f || yScale == 1.0f)) {
                scaleFactor = (xScale > 1.0f || yScale > 1.0f) ? Math.min(xScale, yScale) : Math.max(xScale, yScale);
            }
            fireOnBeforeRotation(futureRotation);
            animate(250, 0.0f, (float) rotation, 1.0f, scaleFactor);
            this.currentRotation = futureRotation;
        }
    }

    private final void animate(long duration, float fromAngle, float toAngle, float fromScale, float toScale) {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setAnimationListener(new RotateAnimationListener((int) toAngle));
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.addAnimation(new RotateAnimation(fromAngle, toAngle, 1, 0.5f, 1, 0.5f));
        animationSet.addAnimation(new ScaleAnimation(fromScale, toScale, fromScale, toScale, 1, 0.5f, 1, 0.5f));
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        this.imageView.setDrawingCacheEnabled(true);
        this.imageView.startAnimation(animationSet);
    }

    public final void crop() {
        if (this.bitmap == null) {
            return;
        }
        if (this.bitmap.getWidth() >= 100 || this.bitmap.getHeight() >= 100) {
            Intent intent = new Intent(getActivity(), CropImageActivity.class);
            intent.setType("image/*");
            intent.setData(this.uri);
            intent.putExtra("returnData", true);
            intent.putExtra("saveToTemp", true);
            intent.putExtra("applyRotation", this.currentRotation);
            intent.putExtra("out_dir", this.outDirPath);
            startActivityForResult(intent, 1);
            return;
        }
        Toast.makeText(getActivity(), 2131165989, 0).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (1 == requestCode) {
            onCropResult(resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onCropResult(int resultCode, Intent data) {
        if (resultCode == -1) {
            Uri newUri = (Uri) data.getParcelableExtra("file_uri");
            if (newUri != null) {
                setUri(newUri, this.context);
                this.bitmap = (Bitmap) data.getParcelableExtra("data");
                this.imageView.setImageBitmap(this.bitmap);
                this.forceReload = true;
                this.tempUri = true;
                fireChangeUri();
                return;
            }
            Context activity = getActivity();
            if (activity != null) {
                Toast.makeText(activity, LocalizationManager.getString(activity, 2131165658), 0).show();
            }
        }
    }

    public String getImageId() {
        return this.imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setRotation(int rotation) {
        this.currentRotation = rotation;
    }

    public final void setExecutionService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public final void setCacheExecutionService(ExecutorService executorService) {
        this.cacheExecutorService = executorService;
    }

    public final void setImageCache(DiskLruCache diskLruCache) {
        this.imageCache = diskLruCache;
    }

    public void setTemporary(boolean temporary) {
        this.tempUri = temporary;
    }

    private void fireOnBeforeRotation(int rotation) {
        Intent intent = new Intent("INTENT_FILTER_IMAGE_EDIT");
        intent.putExtra("img_id", this.imageId);
        intent.putExtra("action", "before_rotation");
        intent.putExtra("rotation", rotation);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void fireOnRotationChanged(int rotation) {
        Intent intent = new Intent("INTENT_FILTER_IMAGE_EDIT");
        intent.putExtra("img_id", this.imageId);
        intent.putExtra("action", "change_rotation");
        intent.putExtra("rotation", rotation);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void fireChangeUri() {
        Intent intent = new Intent("INTENT_FILTER_IMAGE_EDIT");
        intent.putExtra("action", "change_uri");
        intent.putExtra("img_id", this.imageId);
        intent.putExtra("new_uri", this.uri);
        intent.putExtra("temporary", this.tempUri);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void fireChangeComment(String comment) {
        Intent intent = new Intent("INTENT_FILTER_IMAGE_EDIT");
        intent.putExtra("action", "change_comment");
        intent.putExtra("img_id", this.imageId);
        intent.putExtra("comment", comment);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
}
