package ru.ok.android.ui.custom.photo;

import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import ru.ok.android.utils.Logger;

public class PhotoTagDrawable extends Drawable {
    protected boolean animatingPopOut;
    private int arrowDirection;
    private final Drawable arrowDrawable;
    final Rect arrowRect;
    protected int circleInnerDiameter;
    protected float curScale;
    protected int curUserNamePatchWidth;
    private Drawable mCircleDrawable;
    private Paint mPaint;
    private Drawable mPlaceholderDrawable;
    private Paint mShaderPaint;
    private RectF mShaderRectF;
    private final int mSize;
    private Drawable mUserPhotoDrawable;
    private String name;
    private int namePatchDirection;
    private ValueAnimator scaleAnimator;
    private boolean showingUserNamePatch;
    private int substractAlpha;
    private int textHorPadding;
    private TextPaint textPaint;
    private int textSize;
    private int textVerPadding;
    private ValueAnimator userNamePatchAnimator;
    protected int userNamePatchMaxWidth;
    protected int userNamePatchPadding;

    /* renamed from: ru.ok.android.ui.custom.photo.PhotoTagDrawable.1 */
    class C07201 implements AnimatorUpdateListener {
        C07201() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            PhotoTagDrawable.this.curScale = ((Float) animation.getAnimatedValue()).floatValue();
            PhotoTagDrawable.this.invalidateSelf();
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.PhotoTagDrawable.2 */
    class C07212 implements AnimatorUpdateListener {
        C07212() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            PhotoTagDrawable.this.curUserNamePatchWidth = ((Integer) animation.getAnimatedValue()).intValue();
            PhotoTagDrawable.this.invalidateSelf();
        }
    }

    public PhotoTagDrawable(Resources res, Drawable defaultDrawable, int namePatchDirection, int arrowDirection, boolean clickable) {
        this.curScale = 1.0f;
        this.curUserNamePatchWidth = 0;
        this.arrowRect = new Rect();
        this.showingUserNamePatch = false;
        this.textPaint = new TextPaint();
        this.mCircleDrawable = res.getDrawable(2130838529).mutate();
        this.mSize = res.getDimensionPixelSize(2131231121);
        this.arrowDrawable = res.getDrawable(2130838528).mutate();
        this.mPlaceholderDrawable = defaultDrawable;
        this.userNamePatchMaxWidth = res.getDimensionPixelSize(2131231120);
        this.userNamePatchPadding = res.getDimensionPixelSize(2131231119);
        this.circleInnerDiameter = res.getDimensionPixelSize(2131231118);
        this.mShaderRectF = new RectF();
        this.mShaderPaint = new Paint();
        this.mShaderPaint.setAntiAlias(true);
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.namePatchDirection = namePatchDirection;
        this.arrowDirection = arrowDirection;
        this.textHorPadding = res.getDimensionPixelSize(2131231213);
        this.textVerPadding = res.getDimensionPixelSize(2131231214);
        this.textSize = res.getDimensionPixelSize(2131231197);
        if (clickable) {
            this.textPaint.setColor(res.getColor(2131493013));
        } else {
            this.textPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        }
        this.textPaint.setTextSize((float) this.textSize);
        this.textPaint.setAntiAlias(true);
    }

    public void draw(Canvas canvas) {
        if (this.animatingPopOut) {
            if (this.scaleAnimator.isStarted()) {
                if (!this.scaleAnimator.isRunning()) {
                    return;
                }
            }
        }
        canvas.save();
        canvas.scale(this.curScale, this.curScale, ((float) getBounds().right) - (((float) getBounds().width()) * 0.5f), ((float) getBounds().bottom) - (((float) getBounds().height()) * 0.5f));
        Paint paint = this.mPaint;
        int alpha = Math.max(r0.getAlpha() - this.substractAlpha, 0);
        if (alpha != 0) {
            int top;
            int i;
            int circleLeft;
            int circleRight;
            Drawable picDrawable;
            int width = this.mSize;
            int height = this.mSize;
            int left = getBounds().left - (width / 2);
            if (this.arrowDirection == 0) {
                top = getBounds().top - height;
            } else {
                top = getBounds().top;
            }
            int shrink = 0;
            if (this.curUserNamePatchWidth < 0) {
                shrink = this.curUserNamePatchWidth;
            }
            this.arrowRect.left = left;
            this.arrowRect.top = top;
            int i2 = left + width;
            this.arrowRect.right = i;
            i2 = top + height;
            this.arrowRect.bottom = i;
            Rect rect;
            if (this.namePatchDirection == 0) {
                rect = this.arrowRect;
                rect.right += shrink;
            } else {
                rect = this.arrowRect;
                rect.left -= shrink;
            }
            this.arrowDrawable.setBounds(this.arrowRect);
            if (this.namePatchDirection == 0) {
                circleLeft = left;
            } else {
                circleLeft = left - this.curUserNamePatchWidth;
            }
            if (this.namePatchDirection == 0) {
                circleRight = (left + width) + this.curUserNamePatchWidth;
            } else {
                circleRight = left + width;
            }
            this.mCircleDrawable.setBounds(circleLeft, top, circleRight, top + height);
            this.mCircleDrawable.setAlpha(alpha);
            this.arrowDrawable.setAlpha(alpha);
            this.mCircleDrawable.draw(canvas);
            int i3 = this.arrowDirection;
            if (i2 == 1) {
                canvas.save();
                canvas.rotate(180.0f, this.arrowRect.exactCenterX(), this.arrowRect.exactCenterY());
            }
            this.arrowDrawable.draw(canvas);
            i3 = this.arrowDirection;
            if (i2 == 1) {
                canvas.restore();
            }
            int picLeft = left;
            int picRight = left + width;
            if (this.namePatchDirection == 0) {
                picRight += shrink;
            } else {
                picLeft -= shrink;
            }
            if (this.mUserPhotoDrawable == null) {
                picDrawable = this.mPlaceholderDrawable;
                picDrawable.setBounds(picLeft + 0, top + 0, picRight + 0, (top + height) + 0);
            } else {
                int offset = (this.mSize - this.circleInnerDiameter) / 2;
                picDrawable = this.mUserPhotoDrawable;
                int picLeftBound = picLeft + offset;
                int picTopBound = top + offset;
                picDrawable.setBounds(picLeftBound, picTopBound, this.circleInnerDiameter + picLeftBound, this.circleInnerDiameter + picTopBound);
            }
            picDrawable.setAlpha(alpha);
            picDrawable.draw(canvas);
            if (this.name != null && this.curUserNamePatchWidth > 0) {
                float x;
                this.textPaint.setAlpha(alpha);
                String ellipsized = TextUtils.ellipsize(this.name, this.textPaint, (float) (this.curUserNamePatchWidth - this.textHorPadding), TruncateAt.END).toString();
                float measuredTxtSize = this.textPaint.measureText(ellipsized);
                if (this.namePatchDirection == 0) {
                    x = (float) (left + width);
                } else {
                    x = ((float) left) - measuredTxtSize;
                }
                i3 = this.mSize;
                i = this.textSize;
                canvas.drawText(ellipsized, x, (float) ((((i2 / 2) + top) + (i2 / 4)) + this.textVerPadding), this.textPaint);
            }
            canvas.restore();
        }
    }

    public int getIntrinsicWidth() {
        return (int) (((float) this.curUserNamePatchWidth) + (((float) this.mSize) * this.curScale));
    }

    public int getIntrinsicHeight() {
        return (int) (((float) this.mSize) * this.curScale);
    }

    public void calculateBounds(Rect rect, int x, int y) {
        int width = (int) (((float) this.mSize) * this.curScale);
        int left = (int) (((double) x) - (((double) width) * 0.5d));
        rect.left = this.namePatchDirection == 0 ? left : left - this.curUserNamePatchWidth;
        rect.top = y - (this.arrowDirection == 0 ? width : 0);
        rect.right = this.namePatchDirection == 0 ? (left + width) + this.curUserNamePatchWidth : left + width;
        rect.bottom = rect.top + width;
    }

    public final boolean isNamepatchClicked(Rect tagRect, int x, int y) {
        boolean namepatchClicked = false;
        if (this.showingUserNamePatch) {
            int width = (int) (((float) this.mSize) * this.curScale);
            if (this.namePatchDirection != 0) {
                namepatchClicked = x < tagRect.right - width;
            } else if (x > tagRect.left + width) {
                namepatchClicked = true;
            } else {
                namepatchClicked = false;
            }
        }
        Logger.m172d("NAME PATCH CLICKED AT X: " + x + ", Y: " + y);
        return namepatchClicked;
    }

    public void setBounds(Rect bounds) {
        super.setBounds(bounds);
    }

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    public void setSubstractAlpha(int alpha) {
        this.substractAlpha = alpha;
    }

    public void setColorFilter(ColorFilter cf) {
        this.mPaint.setColorFilter(cf);
    }

    public void setName(String name) {
        this.name = name;
        if (!TextUtils.isEmpty(name)) {
            this.userNamePatchMaxWidth = (int) Math.min(((double) this.textPaint.measureText(name)) + (((double) this.mSize) * 0.5d), (double) this.userNamePatchMaxWidth);
        }
    }

    public final void startPopOutAnimation(float fromScale, float toScale, int duration, long delay, AnimatorListener listener) {
        this.animatingPopOut = true;
        animateTagScaleInternal(fromScale, toScale, duration, delay, new OvershootInterpolator(2.0f), listener);
    }

    public final void startCaveInAnimation(float fromScale, float toScale, int duration, long delay, AnimatorListener listener) {
        this.animatingPopOut = false;
        animateTagScaleInternal(fromScale, toScale, duration, delay, new LinearInterpolator(), listener);
    }

    private final void animateTagScaleInternal(float fromScale, float toScale, int duration, long delay, Interpolator interpolator, AnimatorListener listener) {
        this.scaleAnimator = ValueAnimator.ofFloat(new float[]{fromScale, toScale});
        this.scaleAnimator.setDuration((long) duration);
        this.scaleAnimator.setInterpolator(interpolator);
        this.scaleAnimator.addUpdateListener(new C07201());
        this.scaleAnimator.setStartDelay(delay);
        this.scaleAnimator.start();
        if (listener != null) {
            this.scaleAnimator.addListener(listener);
        }
        invalidateSelf();
    }

    public final void showUserNamePatch() {
        if (!this.showingUserNamePatch) {
            if (this.userNamePatchAnimator == null || !this.userNamePatchAnimator.isRunning()) {
                animateUserNamePatch(0, this.userNamePatchMaxWidth, null);
            } else {
                this.userNamePatchAnimator.reverse();
            }
            this.showingUserNamePatch = true;
        }
    }

    public final void hideUserNamePatch() {
        if (this.showingUserNamePatch) {
            if (this.userNamePatchAnimator == null || !this.userNamePatchAnimator.isRunning()) {
                animateUserNamePatch(this.userNamePatchMaxWidth, 0, null);
            } else {
                this.userNamePatchAnimator.reverse();
            }
            this.showingUserNamePatch = false;
        }
    }

    public final void toggleUserNamePatch() {
        if (this.showingUserNamePatch) {
            hideUserNamePatch();
        } else {
            showUserNamePatch();
        }
    }

    public final boolean isNamePatchShowing() {
        return this.showingUserNamePatch;
    }

    private final void animateUserNamePatch(int from, int to, AnimatorListener listener) {
        this.userNamePatchAnimator = ValueAnimator.ofInt(new int[]{from, to});
        this.userNamePatchAnimator.addUpdateListener(new C07212());
        if (listener != null) {
            this.userNamePatchAnimator.addListener(listener);
        }
        this.userNamePatchAnimator.setDuration(200);
        this.userNamePatchAnimator.start();
    }

    public void setUserDrawable(Drawable userDrawable) {
        this.mUserPhotoDrawable = userDrawable;
    }
}
