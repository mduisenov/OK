package ru.ok.android.ui.mediatopic.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.CoordinatorLayout.DefaultBehavior;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import ru.ok.android.C0206R;
import ru.ok.android.ui.coordinator.behaviors.MediaComposerBehavior;
import ru.ok.android.utils.ViewUtil;
import ru.ok.android.utils.animation.AnimationHelper;
import ru.ok.android.utils.animation.SimpleAnimationListener;
import ru.ok.android.utils.localization.LocalizationManager;

@DefaultBehavior(MediaComposerBehavior.class)
public final class MediaComposerPanel extends LinearLayout implements OnClickListener {
    private static final long ANIMATION_DURATION_LABELS_APPEAR;
    private static long ANIMATION_STEP_DURATION;
    private View circleNote;
    private View circlePhoto;
    private ImageView circlePlus;
    private View circleVideo;
    private View labels;
    private MediaComposerPanelListener listener;
    private int mainResId;

    /* renamed from: ru.ok.android.ui.mediatopic.view.MediaComposerPanel.4 */
    class C10344 extends SimpleAnimationListener {
        final /* synthetic */ View val$label;

        C10344(View view) {
            this.val$label = view;
        }

        public void onAnimationStart(Animation animation) {
            ViewUtil.visible(this.val$label);
        }
    }

    /* renamed from: ru.ok.android.ui.mediatopic.view.MediaComposerPanel.5 */
    class C10355 extends SimpleAnimationListener {
        final /* synthetic */ View val$view;

        C10355(View view) {
            this.val$view = view;
        }

        public void onAnimationEnd(Animation animation) {
            ViewUtil.gone(this.val$view);
        }
    }

    /* renamed from: ru.ok.android.ui.mediatopic.view.MediaComposerPanel.6 */
    class C10366 extends SimpleAnimationListener {
        final /* synthetic */ AnimationEndListener val$animationEndListener;
        final /* synthetic */ View val$circle;

        C10366(View view, AnimationEndListener animationEndListener) {
            this.val$circle = view;
            this.val$animationEndListener = animationEndListener;
        }

        public void onAnimationEnd(Animation animation) {
            ViewUtil.gone(this.val$circle);
            if (this.val$animationEndListener != null) {
                this.val$animationEndListener.onAnimationEnd();
            }
        }
    }

    private interface AnimationEndListener {
        void onAnimationEnd();
    }

    public interface MediaComposerPanelListener {
        void onUploadPhotoClicked();

        void onUploadVideoClicked();

        void onWriteNoteClicked();
    }

    static {
        ANIMATION_STEP_DURATION = 90;
        ANIMATION_DURATION_LABELS_APPEAR = 3 * ANIMATION_STEP_DURATION;
    }

    public void setHideAmount(int numerator, int denominator) {
        if (getTranslationY() == 0.0f && numerator > 0 && isExpanded()) {
            collapse(null, true);
        }
        if (numerator >= denominator) {
            ViewUtil.invisible(this.circlePlus);
        } else {
            ViewUtil.visible(this.circlePlus);
        }
        setTranslationY(((float) numerator) + (((float) (getPaddingBottom() + this.circlePlus.getHeight())) * (denominator != 0 ? AnimationHelper.hideInterpolator.getInterpolation(((float) numerator) / ((float) denominator)) : 1.0f)));
    }

    public void updateLayoutOnOrientationChange() {
        inflateInternals();
    }

    public MediaComposerPanel(Context context) {
        this(context, null);
    }

    public MediaComposerPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(0);
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.MediaComposerPanel);
        this.mainResId = a.getResourceId(0, 2130838035);
        a.recycle();
        inflateInternals();
    }

    public void setOnMainButtonClickListener(OnClickListener l) {
        if (this.circlePlus != null) {
            this.circlePlus.setOnClickListener(l);
        }
    }

    public void setMainImage(int resId) {
        if (this.circlePlus != null) {
            this.mainResId = resId;
            this.circlePlus.setImageResource(resId);
        }
    }

    private void inflateInternals() {
        removeAllViews();
        LocalizationManager.inflate(getContext(), 2130903293, (ViewGroup) this, true);
        this.circlePlus = (ImageView) findViewById(2131625050);
        this.circlePlus.setOnClickListener(this);
        this.circlePlus.setImageResource(this.mainResId);
        this.circleVideo = findViewById(2131625047);
        this.circleVideo.setOnClickListener(this);
        this.circlePhoto = findViewById(2131625048);
        this.circlePhoto.setOnClickListener(this);
        this.circleNote = findViewById(2131625049);
        this.circleNote.setOnClickListener(this);
        this.labels = findViewById(2131625051);
        if (this.labels != null) {
            this.labels.findViewById(2131625052).setOnClickListener(this);
            this.labels.findViewById(2131625053).setOnClickListener(this);
            this.labels.findViewById(2131625054).setOnClickListener(this);
        }
    }

    public void setListener(MediaComposerPanelListener listener) {
        this.listener = listener;
    }

    public void onClick(View v) {
        if (this.listener != null) {
            switch (v.getId()) {
                case 2131625047:
                case 2131625052:
                    this.listener.onUploadVideoClicked();
                    collapse(null, false);
                case 2131625048:
                case 2131625053:
                    this.listener.onUploadPhotoClicked();
                    collapse(null, false);
                case 2131625049:
                case 2131625054:
                    this.listener.onWriteNoteClicked();
                    collapse(null, false);
                case 2131625050:
                    if (isExpanded()) {
                        collapse(null, true);
                    } else {
                        expand();
                    }
                default:
            }
        }
    }

    public boolean isExpanded() {
        return this.circleNote.getVisibility() == 0;
    }

    private void processFadeInAnimationForLabel(View label) {
        Animation anim = AnimationUtils.loadAnimation(getContext(), 2130968606);
        anim.setDuration(ANIMATION_DURATION_LABELS_APPEAR);
        anim.setAnimationListener(new C10344(label));
        label.startAnimation(anim);
    }

    private void processFadeOutAnimationForLabel(View view, boolean animate) {
        Animation anim = AnimationUtils.loadAnimation(getContext(), 2130968607);
        if (animate) {
            anim.setDuration(0);
        }
        anim.setAnimationListener(new C10355(view));
        view.startAnimation(anim);
    }

    private boolean isPortraitLayout() {
        return this.labels != null;
    }

    private Animation createCircleAnimation(boolean isShow, boolean orientationDependant, float translation, long startOffset, long duration) {
        Animation animation;
        float f;
        if (!orientationDependant || isPortraitLayout()) {
            if (isShow) {
                f = translation;
            } else {
                f = 0.0f;
            }
            if (isShow) {
                translation = 0.0f;
            }
            animation = new TranslateAnimation(0.0f, 0.0f, f, translation);
        } else {
            f = isShow ? translation : 0.0f;
            if (isShow) {
                translation = 0.0f;
            }
            animation = new TranslateAnimation(f, translation, 0.0f, 0.0f);
        }
        animation.setInterpolator(isShow ? AnimationHelper.showInterpolator : AnimationHelper.hideInterpolator);
        animation.setFillBefore(true);
        animation.setStartOffset(startOffset);
        animation.setDuration(duration);
        return animation;
    }

    private void processCircleAppearAnimation(View circle, boolean orientationDependant, float translateFrom, long startOffset, long duration, AnimationListener animationListener) {
        Animation anim = createCircleAnimation(true, orientationDependant, translateFrom, startOffset, duration);
        if (animationListener != null) {
            anim.setAnimationListener(animationListener);
        }
        circle.startAnimation(anim);
    }

    private void processCircleDisappearAnimation(View circle, boolean orientationDependant, float translateTo, long startOffset, long duration, AnimationEndListener animationEndListener) {
        Animation anim = createCircleAnimation(false, orientationDependant, translateTo, startOffset, duration);
        anim.setAnimationListener(new C10366(circle, animationEndListener));
        circle.startAnimation(anim);
    }

    private void expand() {
        plusRotateAnimate(AnimationUtils.loadAnimation(getContext(), 2130968609), true);
        ViewUtil.visible(this.circleVideo, this.circlePhoto, this.circleNote);
        processCircleAppearAnimation(this.circleVideo, true, (float) getResources().getDimensionPixelSize(2131231067), 0, 3 * ANIMATION_STEP_DURATION, null);
        processCircleAppearAnimation(this.circlePhoto, true, (float) getResources().getDimensionPixelSize(2131231065), ANIMATION_STEP_DURATION, ANIMATION_STEP_DURATION * 2, null);
        processCircleAppearAnimation(this.circleNote, true, (float) getResources().getDimensionPixelSize(2131231064), ANIMATION_STEP_DURATION * 2, ANIMATION_STEP_DURATION, null);
        if (this.labels != null) {
            processFadeInAnimationForLabel(this.labels);
        }
    }

    public void collapse(AnimationEndListener animationEndListener, boolean animate) {
        if (this.labels != null) {
            processFadeOutAnimationForLabel(this.labels, animate);
        }
        processCircleDisappearAnimation(this.circleNote, true, (float) getResources().getDimensionPixelSize(2131231064), 0, animate ? 3 * ANIMATION_STEP_DURATION : 0, null);
        processCircleDisappearAnimation(this.circlePhoto, true, (float) getResources().getDimensionPixelSize(2131231065), animate ? ANIMATION_STEP_DURATION : 0, animate ? 2 * ANIMATION_STEP_DURATION : 0, null);
        processCircleDisappearAnimation(this.circleVideo, true, (float) getResources().getDimensionPixelSize(2131231067), animate ? 2 * ANIMATION_STEP_DURATION : 0, animate ? ANIMATION_STEP_DURATION : 0, animationEndListener);
        plusRotateAnimate(AnimationUtils.loadAnimation(getContext(), 2130968608), animate);
    }

    private void plusRotateAnimate(Animation animation, boolean animate) {
        if (!animate) {
            animation.setDuration(0);
        }
        this.circlePlus.startAnimation(animation);
    }

    public void applyStateCollapsed() {
        ViewUtil.gone(this.labels, this.circleNote, this.circlePhoto, this.circleVideo);
        if (this.labels != null) {
            this.labels.clearAnimation();
        }
        this.circleNote.clearAnimation();
        this.circlePhoto.clearAnimation();
        this.circleVideo.clearAnimation();
        this.circlePlus.clearAnimation();
        this.circlePlus.setRotation(0.0f);
    }
}
