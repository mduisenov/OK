package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.DraweeHolder;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.fresco.DraweeHolderView;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.imageview.GifMarkerDrawableHelper;
import ru.ok.android.utils.Logger;
import ru.ok.model.photo.PhotoInfo;

public class PhotoTileView extends DraweeHolderView implements Callback, OnClickListener, PhotoScaleDataProvider {
    private Drawable brokenImageDrawable;
    private final ColorMatrixColorFilter colorFilter;
    private final ColorMatrix colorMatrix;
    private final int[] coords;
    private final Rect coverDstRect;
    private final Paint coverPaint;
    private final Rect coverSrcRect;
    private boolean darken;
    private boolean displayDoubleSize;
    private boolean drawImage;
    private final GifMarkerDrawableHelper gifMarkerDrawableHelper;
    private GenericDraweeHierarchy hierarchy;
    protected OnPhotoTileClickListener onPhotoTileClickListener;
    protected PhotoInfo photoInfo;
    private boolean selected;
    private Drawable selectorDrawable;
    private final Handler selectorHandler;
    private Runnable setSelectedRunnable;
    private Uri uri;

    public interface OnPhotoTileClickListener {
        void onPhotoTileClicked(PhotoTileView photoTileView, PhotoInfo photoInfo);
    }

    /* renamed from: ru.ok.android.ui.custom.photo.PhotoTileView.1 */
    class C07221 implements Runnable {
        C07221() {
        }

        public void run() {
            PhotoTileView.this.setSelected(true);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.PhotoTileView.2 */
    class C07252 implements Runnable {

        /* renamed from: ru.ok.android.ui.custom.photo.PhotoTileView.2.1 */
        class C07241 implements Runnable {

            /* renamed from: ru.ok.android.ui.custom.photo.PhotoTileView.2.1.1 */
            class C07231 implements Runnable {
                C07231() {
                }

                public void run() {
                    if (PhotoTileView.this.onPhotoTileClickListener != null) {
                        PhotoTileView.this.onPhotoTileClickListener.onPhotoTileClicked(PhotoTileView.this, PhotoTileView.this.photoInfo);
                    }
                }
            }

            C07241() {
            }

            public void run() {
                PhotoTileView.this.setSelected(false);
                PhotoTileView.this.selectorHandler.post(new C07231());
            }
        }

        C07252() {
        }

        public void run() {
            PhotoTileView.this.setSelected(true);
            PhotoTileView.this.selectorHandler.post(new C07241());
        }
    }

    public PhotoTileView(Context context) {
        super(context);
        this.drawImage = true;
        this.coverPaint = new Paint();
        this.coverSrcRect = new Rect();
        this.coverDstRect = new Rect();
        this.gifMarkerDrawableHelper = new GifMarkerDrawableHelper();
        this.setSelectedRunnable = new C07221();
        this.selectorHandler = new Handler();
        this.coords = new int[2];
        int padding = getResources().getDimensionPixelSize(2131231111);
        setPadding(padding, padding, padding, padding);
        setOnClickListener(this);
        this.brokenImageDrawable = getResources().getDrawable(2130838537);
        this.coverPaint.setAntiAlias(true);
        this.coverPaint.setFilterBitmap(true);
        this.coverPaint.setDither(true);
        this.selectorDrawable = getResources().getDrawable(2130838652).mutate();
        this.selectorDrawable.setCallback(this);
        this.colorMatrix = new ColorMatrix();
        this.colorMatrix.setSaturation(0.0f);
        this.colorFilter = new ColorMatrixColorFilter(this.colorMatrix);
        this.hierarchy = GenericDraweeHierarchyBuilder.newInstance(getResources()).setFailureImage(this.brokenImageDrawable, ScaleType.CENTER_INSIDE).setBackground(new ColorDrawable(ContextCompat.getColor(getContext(), 2131493102))).build();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Logger.m172d("PTV. Attached");
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Logger.m172d("PTV. Detached");
    }

    public void setPhotoInfo(PhotoInfo photoInfo) {
        this.photoInfo = photoInfo;
        GifMarkerDrawableHelper gifMarkerDrawableHelper = this.gifMarkerDrawableHelper;
        boolean z = photoInfo != null && GifAsMp4PlayerHelper.shouldShowGifAsMp4(photoInfo);
        gifMarkerDrawableHelper.setShouldDrawGifMarker(z);
    }

    protected void onDraw(Canvas canvas) {
        this.selectorDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        this.selectorDrawable.draw(canvas);
        if (this.drawImage) {
            recalculateDstRect();
            if (getHolder() != null) {
                Drawable photo = getHolder().getTopLevelDrawable();
                if (photo != null) {
                    photo.setCallback(this);
                    photo.setBounds(this.coverDstRect);
                    photo.draw(canvas);
                    this.gifMarkerDrawableHelper.drawGifMarkerIfNecessary(this, canvas);
                }
            }
        }
    }

    private void recalculateDstRect() {
        this.coverDstRect.left = getPaddingLeft();
        this.coverDstRect.top = getPaddingTop();
        this.coverDstRect.right = this.coverDstRect.left + getContentWidth();
        this.coverDstRect.bottom = this.coverDstRect.top + getContentHeight();
    }

    public void invalidateDrawable(Drawable drawable) {
        invalidate();
    }

    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        this.selectorHandler.postAtTime(what, who, when);
    }

    public void unscheduleDrawable(Drawable who, Runnable what) {
        this.selectorHandler.removeCallbacks(what, who);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            switch (event.getAction()) {
                case RECEIVED_VALUE:
                    this.selectorHandler.postDelayed(this.setSelectedRunnable, 200);
                    break;
                case Message.TEXT_FIELD_NUMBER /*1*/:
                case Message.TYPE_FIELD_NUMBER /*3*/:
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    this.selectorHandler.removeCallbacks(this.setSelectedRunnable);
                    setSelected(false);
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    public void onClick(View view) {
        this.selectorHandler.removeCallbacks(this.setSelectedRunnable);
        this.selectorHandler.post(new C07252());
    }

    public int getDisplayedWidth() {
        return getImageWidth();
    }

    public int getDisplayedHeight() {
        return getImageHeight();
    }

    public int getDisplayedX() {
        return getImageRawX();
    }

    public int getDisplayedY() {
        return getImageRawY();
    }

    public void setOnPhotoTileClickListener(OnPhotoTileClickListener onPhotoTileClickListener) {
        this.onPhotoTileClickListener = onPhotoTileClickListener;
    }

    public final int getImageRawX() {
        getLocationOnScreen(this.coords);
        return this.coords[0] + getPaddingLeft();
    }

    public final int getImageRawY() {
        getLocationOnScreen(this.coords);
        return this.coords[1] + getPaddingTop();
    }

    public final int getImageWidth() {
        return getContentWidth();
    }

    public final int getImageHeight() {
        return getContentHeight();
    }

    public int getRealWidth() {
        if (this.photoInfo != null) {
            return this.photoInfo.getStandartWidth();
        }
        return -1;
    }

    public int getRealHeight() {
        if (this.photoInfo != null) {
            return this.photoInfo.getStandartHeight();
        }
        return -1;
    }

    public Uri getUri() {
        return this.uri;
    }

    public boolean isDisplayDoubleSize() {
        return this.displayDoubleSize;
    }

    public final void setImageViewVisibility(boolean visible) {
        this.drawImage = visible;
        invalidate();
    }

    public void setDisplayDoubleSize(boolean displayDoubleSize) {
        this.displayDoubleSize = displayDoubleSize;
    }

    public void setDarken(boolean darken) {
        this.darken = darken;
        setEnabled(!darken);
        Drawable photo = getHolder().getTopLevelDrawable();
        if (darken) {
            photo.setAlpha(150);
            photo.setColorFilter(this.colorFilter);
            return;
        }
        photo.setAlpha(MotionEventCompat.ACTION_MASK);
        photo.setColorFilter(null);
    }

    public PhotoInfo getPhotoInfo() {
        return this.photoInfo;
    }

    public final void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            if (selected) {
                this.selectorDrawable.setState(new int[]{16842919});
            } else {
                this.selectorDrawable.setState(new int[]{16842921});
            }
            postInvalidate();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public void setImageUri(Uri uri) {
        this.uri = uri;
        DraweeHolder<GenericDraweeHierarchy> holder = getHolder();
        if (holder == null) {
            holder = DraweeHolder.create(this.hierarchy, getContext());
        }
        holder.setController(FrescoOdkl.createSimpleController(holder, uri));
        setHolder(holder);
    }

    private int getContentWidth() {
        return (getMeasuredWidth() - getPaddingRight()) - getPaddingLeft();
    }

    private int getContentHeight() {
        return (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
    }
}
