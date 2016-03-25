package ru.ok.android.ui.custom.imageview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchy;

public class ScaleImageView extends UrlImageView {
    private boolean scaleToWidth;

    public ScaleImageView(Context context) {
        super(context);
        this.scaleToWidth = false;
        init();
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.scaleToWidth = false;
        init();
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.scaleToWidth = false;
        init();
    }

    private void init() {
        ((GenericDraweeHierarchy) getHierarchy()).setActualImageScaleType(ScaleType.FIT_CENTER);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == 1073741824 || widthMode == LinearLayoutManager.INVALID_OFFSET) {
            this.scaleToWidth = true;
        } else if (heightMode == 1073741824 || heightMode == LinearLayoutManager.INVALID_OFFSET) {
            this.scaleToWidth = false;
        } else {
            throw new IllegalStateException("width or height needs to be set to match_parent or a specific dimension");
        }
        if (getDrawable() == null || getDrawable().getIntrinsicWidth() == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else if (this.scaleToWidth) {
            setMeasuredDimension(width, width);
        } else {
            setMeasuredDimension(height, height);
        }
    }
}
