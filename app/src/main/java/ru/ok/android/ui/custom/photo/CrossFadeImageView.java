package ru.ok.android.ui.custom.photo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import ru.ok.android.utils.animation.SimpleAnimatorListener;

public class CrossFadeImageView extends ImageView {
    private ValueAnimator animator;
    private LayerDrawable layerDrawable;

    /* renamed from: ru.ok.android.ui.custom.photo.CrossFadeImageView.1 */
    class C07021 implements AnimatorUpdateListener {
        final /* synthetic */ Drawable val$newDrawable;

        C07021(Drawable drawable) {
            this.val$newDrawable = drawable;
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            this.val$newDrawable.setAlpha(((Integer) valueAnimator.getAnimatedValue()).intValue());
            CrossFadeImageView.this.layerDrawable.invalidateSelf();
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.CrossFadeImageView.2 */
    class C07032 extends SimpleAnimatorListener {
        final /* synthetic */ Bitmap val$bm;

        C07032(Bitmap bitmap) {
            this.val$bm = bitmap;
        }

        public void onAnimationEnd(Animator animation) {
            CrossFadeImageView.this.setImageBitmap(this.val$bm);
            CrossFadeImageView.this.animator = null;
        }
    }

    public CrossFadeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CrossFadeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CrossFadeImageView(Context context) {
        super(context);
    }

    public void setCrossFadeBitmap(Bitmap bm, boolean crossFade) {
        if (this.animator != null) {
            this.animator.cancel();
        }
        if (getDrawable() == null || !crossFade) {
            setImageBitmap(bm);
            return;
        }
        this.layerDrawable = new LayerDrawable(new Drawable[]{old, new BitmapDrawable(bm)});
        setImageDrawable(this.layerDrawable);
        this.layerDrawable.setCallback(this);
        this.animator = ValueAnimator.ofInt(new int[]{0, MotionEventCompat.ACTION_MASK});
        this.animator.addUpdateListener(new C07021(newDrawable));
        this.animator.addListener(new C07032(bm));
        this.animator.start();
    }
}
