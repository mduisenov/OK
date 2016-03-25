package ru.ok.android.music.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.DraweeHolder;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import ru.ok.android.fresco.DraweeHolderView;
import ru.ok.android.fresco.InvalidateControllerListener;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.PlayerImageView;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ViewUtil;
import ru.ok.android.utils.animation.AnimationHelper;
import ru.ok.android.utils.animation.PlayerAnimationHelper;

public class FloatingPlayerButton extends DraweeHolderView implements AnimatorListener, AnimatorUpdateListener {
    private String albumUrl;
    private ObjectAnimator animator;
    private AppearanceState appearanceState;
    private final Paint fadePaint;
    private GenericDraweeHierarchy hierarchy;
    private PlayState playState;
    private Drawable playStateDrawable;
    private int radiusTranslation;
    private RectF roundRect;

    /* renamed from: ru.ok.android.music.view.FloatingPlayerButton.1 */
    static /* synthetic */ class C03811 {
        static final /* synthetic */ int[] f69xc4d48486;

        static {
            f69xc4d48486 = new int[AppearanceState.values().length];
            try {
                f69xc4d48486[AppearanceState.COLLAPSED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f69xc4d48486[AppearanceState.REVEALED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f69xc4d48486[AppearanceState.UNDEFINED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private enum AppearanceState {
        COLLAPSED,
        REVEALED,
        UNDEFINED
    }

    public enum PlayState {
        PLAY(2130838133),
        PAUSE(2130838132);
        
        private final int resId;

        private PlayState(int resId) {
            this.resId = resId;
        }
    }

    public FloatingPlayerButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    public FloatingPlayerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public FloatingPlayerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.appearanceState = AppearanceState.UNDEFINED;
        this.fadePaint = new Paint();
        this.roundRect = new RectF();
        this.hierarchy = GenericDraweeHierarchyBuilder.newInstance(getResources()).setFadeDuration(0).setPlaceholderImage(ResourcesCompat.getDrawable(getResources(), 2130838395, context.getTheme())).build();
        this.fadePaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.fadePaint.setAlpha(76);
        this.fadePaint.setStyle(Style.FILL);
        setPivotX(0.0f);
        setPivotY(0.0f);
        setPlayState(PlayState.PLAY);
    }

    public void setPlay() {
        setPlayState(PlayState.PLAY);
        postInvalidate();
    }

    public void setPause() {
        setPlayState(PlayState.PAUSE);
        postInvalidate();
    }

    protected void onDraw(@NonNull Canvas canvas) {
        if (getHolder() != null) {
            drawImage(canvas);
            drawPlayState(canvas);
        }
    }

    private void drawImage(Canvas canvas) {
        double t = 1.0d - (((double) this.radiusTranslation) / 100.0d);
        int padding = (int) (((double) getPaddingTop()) * t);
        int w = getWidth() - (padding * 2);
        int h = getHeight() - (padding * 2);
        int radius = (int) (((double) (Math.min(w, h) / 2)) * t);
        updateHolder(padding, w, h, radius);
        this.roundRect.set((float) padding, (float) padding, (float) (padding + w), (float) (padding + h));
        getHolder().getTopLevelDrawable().draw(canvas);
        canvas.drawRoundRect(this.roundRect, (float) radius, (float) radius, this.fadePaint);
    }

    private void updateHolder(int padding, int w, int h, int radius) {
        getHolder().getTopLevelDrawable().setBounds(padding, padding, padding + w, padding + h);
        ((GenericDraweeHierarchy) getHolder().getHierarchy()).setRoundingParams(RoundingParams.fromCornersRadius((float) radius));
    }

    private void drawPlayState(Canvas canvas) {
        if (this.animator == null || (!this.animator.isRunning() && this.appearanceState == AppearanceState.COLLAPSED)) {
            int h = this.playStateDrawable.getIntrinsicHeight();
            int w = this.playStateDrawable.getIntrinsicWidth();
            int deltaH = (getHeight() - h) / 2;
            int deltaW = (getWidth() - w) / 2;
            this.playStateDrawable.setBounds(deltaW, deltaH, deltaW + w, deltaH + h);
            this.playStateDrawable.draw(canvas);
        }
    }

    public void setAlbumUrl(String albumUrl) {
        if (albumUrl == null) {
            setVisibility(4);
        } else if (!albumUrl.equals(this.albumUrl)) {
            Logger.m173d("FPB imageUrl: %s", albumUrl);
            this.albumUrl = albumUrl;
            if (getHolder() == null) {
                setHolder(DraweeHolder.create(this.hierarchy, getContext()));
            }
            getHolder().setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setControllerListener(new InvalidateControllerListener(this))).setOldController(getHolder().getController())).setUri(PlayerImageView.isStubImageUrl(albumUrl) ? null : Uri.parse(albumUrl)).build());
            setVisibility(0);
            postInvalidate();
        }
    }

    public void setRadiusTranslation(int radiusTranslation) {
        this.radiusTranslation = radiusTranslation;
    }

    public int getRadiusTranslation() {
        return this.radiusTranslation;
    }

    public void setFadeAlpha(int alpha) {
        this.fadePaint.setAlpha(alpha);
    }

    public int getFadeAlpha() {
        return this.fadePaint.getAlpha();
    }

    public void startRevealAnimation(Context context, AnimatorListener listener) {
        if (this.animator != null || initAnimator(context)) {
            this.animator.addListener(listener);
            this.animator.start();
            this.appearanceState = AppearanceState.REVEALED;
        }
    }

    private boolean initAnimator(Context context) {
        float playerAlbumHeight = context.getResources().getDimension(2131230728);
        float playerAlbumWidth = context.getResources().getDimension(2131230729);
        View parent = (View) getParent();
        if (this.animator != null || parent == null) {
            return false;
        }
        int parentW = parent.getWidth();
        int parentH = parent.getHeight();
        if (parentW == 0 || parentH == 0 || getHeight() == 0 || getWidth() == 0) {
            return false;
        }
        if (playerAlbumWidth == 0.0f) {
            playerAlbumWidth = (float) parentW;
        }
        if (playerAlbumHeight == 0.0f) {
            playerAlbumHeight = (float) parentW;
        }
        float w = (float) getWidth();
        float h = (float) getHeight();
        r7 = new PropertyValuesHolder[6];
        r7[0] = PropertyValuesHolder.ofFloat("scaleX", new float[]{playerAlbumWidth / w});
        r7[1] = PropertyValuesHolder.ofFloat("scaleY", new float[]{playerAlbumHeight / h});
        r7[2] = PropertyValuesHolder.ofFloat(MUCUser.ELEMENT, new float[]{0.0f});
        r7[3] = PropertyValuesHolder.ofFloat("y", new float[]{0.0f});
        r7[4] = PropertyValuesHolder.ofInt("radiusTranslation", new int[]{0, 100});
        r7[5] = PropertyValuesHolder.ofInt("fadeAlpha", new int[]{76, 0});
        this.animator = ObjectAnimator.ofPropertyValuesHolder(this, r7);
        this.animator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.animator.setDuration(400);
        this.animator.addListener(this);
        this.animator.addUpdateListener(this);
        return true;
    }

    public void startCollapseAnimation(Context context) {
        if (this.appearanceState != AppearanceState.COLLAPSED) {
            if (this.animator != null || initAnimator(context)) {
                this.animator.reverse();
                this.appearanceState = AppearanceState.COLLAPSED;
            }
        }
    }

    public void setPlayState(PlayState playState) {
        if (this.playState != playState) {
            this.playState = playState;
            this.playStateDrawable = ContextCompat.getDrawable(getContext(), playState.resId);
        }
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        postInvalidate();
    }

    public void onAnimationStart(Animator animation) {
    }

    public void onAnimationEnd(Animator animation) {
        switch (C03811.f69xc4d48486[this.appearanceState.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                PlayerAnimationHelper.sendButtonCollapsed();
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                PlayerAnimationHelper.sendButtonRevealed();
                break;
        }
        postInvalidate();
    }

    public void onAnimationCancel(Animator animation) {
        resetToDefault();
        this.appearanceState = this.appearanceState == AppearanceState.REVEALED ? AppearanceState.COLLAPSED : AppearanceState.REVEALED;
    }

    public void onAnimationRepeat(Animator animation) {
    }

    public void resetAnimation() {
        if (this.animator != null && this.animator.isRunning()) {
            this.animator.cancel();
        }
        resetToDefault();
    }

    private void resetToDefault() {
        this.animator = null;
        setScaleX(1.0f);
        setScaleY(1.0f);
        setTranslationX(0.0f);
        setTranslationY(0.0f);
        setRadiusTranslation(0);
        setFadeAlpha(76);
    }

    public void removeAnimationListener(AnimatorListener listener) {
        if (this.animator != null) {
            this.animator.removeListener(listener);
        }
    }

    public void setHideAmount(int numerator, int denominator) {
        boolean z = true;
        if (numerator >= denominator) {
            ViewUtil.invisible(this);
        } else if (this.albumUrl != null) {
            ViewUtil.visible(this);
        }
        if (numerator != 0) {
            z = false;
        }
        setClickable(z);
        setTranslationY(((float) numerator) + (((float) (getPaddingBottom() + getHeight())) * (denominator != 0 ? AnimationHelper.hideInterpolator.getInterpolation(((float) numerator) / ((float) denominator)) : 1.0f)));
    }
}
