package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView.ScaleType;
import com.facebook.common.references.CloseableReference;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.ui.custom.ProgressWheelView;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

public class StaticAttachPhotoView extends AbstractAttachPhotoView implements StaticPhoto {
    private DraweeHolder holder;
    protected PinchZoomImageView mZoomableImageView;
    private int placeholderHeight;
    private int placeholderWidth;
    private Uri previewUri;
    private boolean readyForAnimation;
    private Uri uri;

    /* renamed from: ru.ok.android.ui.custom.photo.StaticAttachPhotoView.1 */
    class C07291 implements OnPhotoTapListener {
        C07291() {
        }

        public void onPhotoTap(View view, float x, float y) {
            StaticAttachPhotoView.this.onViewTap();
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.StaticAttachPhotoView.2 */
    class C07302 implements OnMatrixChangedListener {
        C07302() {
        }

        public void onMatrixChanged(RectF rect) {
            if (StaticAttachPhotoView.this.mDecorViewsHandler != null) {
                StaticAttachPhotoView.this.mDecorViewsHandler.setDecorVisibility(((double) StaticAttachPhotoView.this.mZoomableImageView.getScale()) <= 1.2d, true);
            }
        }
    }

    public StaticAttachPhotoView(Context context) {
        super(context);
        this.placeholderWidth = -1;
        this.placeholderHeight = -1;
        this.mZoomableImageView = (PinchZoomImageView) findViewById(C0263R.id.image);
        this.mZoomableImageView.setScaleType(ScaleType.FIT_CENTER);
        this.mZoomableImageView.setOnPhotoTapListener(new C07291());
        this.mZoomableImageView.setOnMatrixChangeListener(new C07302());
        this.mProgressView = (ProgressWheelView) findViewById(2131624548);
    }

    public void initHierarchy(@Nullable CloseableReference<CloseableImage> placeholderRef) {
        super.initHierarchy(placeholderRef);
        Bitmap placeholderBitmap = null;
        if (placeholderRef != null && placeholderRef.isValid()) {
            placeholderBitmap = ((CloseableBitmap) placeholderRef.get()).getUnderlyingBitmap();
            this.placeholderWidth = placeholderBitmap.getWidth();
            this.placeholderHeight = placeholderBitmap.getHeight();
        }
        this.holder = DraweeHolder.create(FrescoOdkl.createProgressListenerHierarchy(getContext(), this, placeholderBitmap), getContext());
    }

    public void setProgressVisible(boolean visible) {
        this.mProgressView.setVisibility(visible ? 0 : 8);
    }

    public void setReadyForAnimation(boolean isReady) {
        this.readyForAnimation = isReady;
    }

    public boolean hasPlaceholder() {
        return (this.placeholderHeight == -1 || this.placeholderWidth == -1) ? false : true;
    }

    protected int getDraggableContentViewId() {
        return 2130903106;
    }

    public final int getImageDisplayedX() {
        RectF displayRect = this.mZoomableImageView.getDisplayRect();
        if (displayRect == null) {
            return 0;
        }
        return (int) displayRect.left;
    }

    public final int getImageDisplayedY() {
        RectF displayRect = this.mZoomableImageView.getDisplayRect();
        if (displayRect == null) {
            return 0;
        }
        return (int) displayRect.top;
    }

    public final int getImageDisplayedWidth() {
        RectF displayRect = this.mZoomableImageView.getDisplayRect();
        if (displayRect == null) {
            return 0;
        }
        return (int) displayRect.width();
    }

    public final int getImageDisplayedHeight() {
        RectF displayRect = this.mZoomableImageView.getDisplayRect();
        if (displayRect == null) {
            return 0;
        }
        return (int) displayRect.height();
    }

    public float getImageScale() {
        return this.mZoomableImageView.getScale();
    }

    public RectF getImageDisplayRect() {
        return this.mZoomableImageView.getDisplayRect();
    }

    public Drawable getDrawable() {
        return this.mZoomableImageView.getDrawable();
    }

    public boolean isReadyForAnimation() {
        return this.readyForAnimation && this.mZoomableImageView.isValid();
    }

    public Uri getUri() {
        return this.uri;
    }

    public Uri getPreviewUri() {
        return this.previewUri;
    }

    public int getPlaceholderWidth() {
        return this.placeholderWidth;
    }

    public int getPlaceholderHeight() {
        return this.placeholderHeight;
    }

    public void setImageDrawable(Drawable drawable) {
        this.mZoomableImageView.createPhotoAttacher();
        this.mZoomableImageView.setImageDrawable(drawable);
    }

    public void setImageUri(Uri uri, Uri previewUri) {
        if (this.uri == null || !this.uri.equals(uri)) {
            this.uri = uri;
            this.previewUri = previewUri;
            setImageConstantSize(this.holder, this, uri, previewUri);
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.holder != null) {
            this.holder.onDetach();
        }
        this.mZoomableImageView.cleanup();
    }

    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        if (this.holder != null) {
            this.holder.onDetach();
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.holder != null) {
            this.holder.onAttach();
        }
    }

    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        if (this.holder != null) {
            this.holder.onAttach();
        }
    }
}
