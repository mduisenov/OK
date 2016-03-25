package ru.ok.android.ui.custom.imageview;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import ru.ok.android.fresco.FrescoGifMarkerView;
import ru.ok.android.ui.custom.imageview.HorizontalSlidingPanoramaHelper.Slidable;

public class HorizontalSlidingTopCropAsyncImageView extends FrescoGifMarkerView implements Slidable, ViewWithSlideMode {
    private final PointF focusPoint;
    private boolean slidingMode;
    private final HorizontalSlidingPanoramaHelper slidingPanoramaHelper;

    public HorizontalSlidingTopCropAsyncImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.slidingMode = false;
        this.slidingPanoramaHelper = new HorizontalSlidingPanoramaHelper(this);
        this.focusPoint = new PointF(0.5f, 0.5f);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.slidingPanoramaHelper.setViewDimensions(getMeasuredHeight(), getMeasuredWidth());
    }

    public void setSlidingMode(boolean slidingMode) {
        this.slidingMode = slidingMode;
    }

    public void setImageDimensions(int height, int width) {
        this.slidingPanoramaHelper.setImageDimensions(height, width);
    }

    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (this.slidingMode) {
            return this.slidingPanoramaHelper.onTouch(event) || super.onTouchEvent(event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    public void onTranslate(float relativeTranslate) {
        this.focusPoint.set(0.5f - (relativeTranslate / 2.0f), 0.5f);
        ((GenericDraweeHierarchy) getHierarchy()).setActualImageFocusPoint(this.focusPoint);
    }
}
