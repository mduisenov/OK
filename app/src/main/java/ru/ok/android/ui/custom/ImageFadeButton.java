package ru.ok.android.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;

public class ImageFadeButton extends FadeButton {
    private static final int[] ATTRS_ARRAY;
    private AnimationImageView mImageView;

    static {
        ATTRS_ARRAY = new int[]{16843033};
    }

    public ImageFadeButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ImageFadeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageFadeButton(Context context) {
        super(context);
    }

    protected final void build(Context context, AttributeSet attrs, int defStyle) {
        super.build(context, attrs, defStyle);
        this.mImageView = new AnimationImageView(context);
        this.mImageView.setEnabled(false);
        this.mImageView.setScaleType(ScaleType.CENTER);
        LayoutParams lp = new LayoutParams(-2, -2);
        lp.gravity = 17;
        addView(this.mImageView, lp);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, ATTRS_ARRAY);
            setImageDrawable(typedArray.getDrawable(0));
            typedArray.recycle();
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.mImageView.setAlpha(enabled ? MotionEventCompat.ACTION_MASK : 100);
    }

    public void setAlpha(int value) {
        this.mImageView.setAlpha(value);
    }

    public Drawable getDrawable() {
        return this.mImageView.getDrawable();
    }

    public void setImageDrawable(Drawable drawable) {
        this.mImageView.setImageDrawable(drawable);
    }

    public void setImageResource(int resId) {
        this.mImageView.setImageResource(resId);
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.mImageView.setImageBitmap(bitmap);
    }

    public AnimationImageView getImageView() {
        return this.mImageView;
    }
}
