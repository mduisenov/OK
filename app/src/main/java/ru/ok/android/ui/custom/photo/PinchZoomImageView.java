package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView.ScaleType;
import ru.ok.android.utils.Logger;
import uk.co.senab.photoview.DefaultOnDoubleTapListener;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

public class PinchZoomImageView extends CrossFadeImageView implements OnDoubleTapListener, OnTouchListener {
    private DefaultOnDoubleTapListener onDoubleTabInternal;
    private OnTouchListener onTouchListener;
    protected PhotoViewAttacher photoAttacher;

    public PinchZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreate();
    }

    public PinchZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public PinchZoomImageView(Context context) {
        super(context);
        onCreate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void createPhotoAttacher() {
        super.setOnTouchListener(this);
        if (this.photoAttacher != null) {
            cleanup();
        }
        this.photoAttacher = new PhotoViewAttacher(this);
        Logger.m173d("PhotoAttacher created. id: %s", Integer.valueOf(System.identityHashCode(this.photoAttacher)));
        this.photoAttacher.setOnDoubleTapListener(this);
        this.onDoubleTabInternal = new DefaultOnDoubleTapListener(this.photoAttacher);
        setAttacherScaleType(getScaleType());
    }

    private final void onCreate() {
        createPhotoAttacher();
    }

    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(scaleType);
        setAttacherScaleType(scaleType);
    }

    private final void setAttacherScaleType(ScaleType scaleType) {
        if (this.photoAttacher != null && scaleType != ScaleType.MATRIX) {
            this.photoAttacher.setScaleType(scaleType);
            this.photoAttacher.update();
        }
    }

    public final void setOnMatrixChangeListener(OnMatrixChangedListener onMatrixChangeListener) {
        this.photoAttacher.setOnMatrixChangeListener(onMatrixChangeListener);
    }

    public final PhotoViewAttacher getPhotoAttacher() {
        return this.photoAttacher;
    }

    public final float getScale() {
        return this.photoAttacher.getScale();
    }

    public final void setMinScale(float minScale) {
        this.photoAttacher.setMinScale(minScale);
    }

    public final void setMidScale(float midScale) {
        this.photoAttacher.setMidScale(midScale);
    }

    public final void setMaxScale(float maxScale) {
        this.photoAttacher.setMaxScale(maxScale);
    }

    public final void update() {
        this.photoAttacher.update();
    }

    public final void cleanup() {
        this.photoAttacher.cleanup();
        Logger.m173d("Cleanup called. Id: %s", Integer.valueOf(System.identityHashCode(this.photoAttacher)));
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        return this.onDoubleTabInternal.onSingleTapConfirmed(e);
    }

    public boolean onDoubleTap(MotionEvent event) {
        float scale = this.photoAttacher.getScale();
        float x = event.getX();
        float y = event.getY();
        if (scale < this.photoAttacher.getMediumScale()) {
            this.photoAttacher.setScale(this.photoAttacher.getMediumScale(), x, y, true);
        } else if (scale >= this.photoAttacher.getMediumScale()) {
            this.photoAttacher.setScale(this.photoAttacher.getMinimumScale(), x, y, true);
        }
        return true;
    }

    public boolean onDoubleTapEvent(MotionEvent e) {
        return this.onDoubleTabInternal.onDoubleTapEvent(e);
    }

    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        update();
    }

    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }

    public void setOnTouchListener(OnTouchListener listener) {
        this.onTouchListener = listener;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cleanup();
    }

    public boolean onTouch(View view, MotionEvent event) {
        if (!isPhotoHit(event)) {
            return false;
        }
        boolean handled = false;
        boolean handleResult = false;
        if (this.onTouchListener != null) {
            try {
                handleResult = this.onTouchListener.onTouch(this, event);
                handled = true;
            } catch (Exception e) {
            }
        }
        if (handled) {
            return handleResult;
        }
        return super.onTouchEvent(event);
    }

    protected final boolean isPhotoHit(MotionEvent event) {
        RectF displayRect = getDisplayRect();
        if (displayRect != null) {
            return displayRect.contains(event.getX(), event.getY());
        }
        return false;
    }

    public RectF getDisplayRect() {
        return this.photoAttacher.getDisplayRect();
    }

    public boolean isValid() {
        return this.photoAttacher.getImageView() != null;
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        this.photoAttacher.setOnPhotoTapListener(listener);
    }
}
