package ru.ok.android.ui.custom.imageview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import java.util.List;
import ru.ok.android.ui.custom.profiles.PresentView;
import ru.ok.android.ui.presents.helpers.PresentSettingsHelper;
import ru.ok.android.ui.users.fragments.data.UserMergedPresent;
import ru.ok.sprites.SpriteDrawable.AnimationListener;

public final class CarouselPresentsImageView extends FrameLayout implements AnimationListener {
    private int currentStep;
    private PresentView currentView;
    private final Handler handler;
    private boolean isAttachedToWindow;
    private PresentView nextView;
    private int padding;
    private List<UserMergedPresent> presents;
    private Runnable runnable;

    /* renamed from: ru.ok.android.ui.custom.imageview.CarouselPresentsImageView.1 */
    class C06551 implements Runnable {
        C06551() {
        }

        public void run() {
            if (CarouselPresentsImageView.this.isAttachedToWindow) {
                CarouselPresentsImageView.this.startChangePresentAnimation();
                CarouselPresentsImageView.this.currentStep = CarouselPresentsImageView.this.currentStep + 1;
                CarouselPresentsImageView.this.startHandler(false);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.imageview.CarouselPresentsImageView.2 */
    class C06562 extends AnimatorListenerAdapter {
        C06562() {
        }

        public void onAnimationEnd(Animator animation) {
            CarouselPresentsImageView.this.syncPresent(false);
        }
    }

    private void startHandler(boolean isFirst) {
        if (isFirst) {
            this.currentStep = 0;
            syncPresent(true);
        }
        this.handler.removeCallbacks(this.runnable);
        if (this.isAttachedToWindow && this.presents != null && this.presents.size() > 1) {
            if (!getPresentForIndex(this.currentStep).isAnimated || !PresentSettingsHelper.isAnimatedPresentsEnabled()) {
                this.handler.postDelayed(this.runnable, 2100);
            }
        }
    }

    public CarouselPresentsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isAttachedToWindow = false;
        this.currentStep = 0;
        this.handler = new Handler();
        this.runnable = new C06551();
        this.padding = ((int) context.getResources().getDimension(2131230730)) - ((int) context.getResources().getDimension(2131230731));
        LayoutInflater.from(context).inflate(2130903237, this, true);
        this.currentView = (PresentView) findViewById(2131624933);
        this.nextView = (PresentView) findViewById(2131624934);
    }

    private void startChangePresentAnimation() {
        AnimatorSet fadeAnimator = new AnimatorSet().setDuration(300);
        r1 = new Animator[2];
        r1[0] = ObjectAnimator.ofFloat(this.currentView, "alpha", new float[]{this.currentView.getAlpha(), 0.0f});
        r1[1] = ObjectAnimator.ofFloat(this.nextView, "alpha", new float[]{this.nextView.getAlpha(), 1.0f});
        fadeAnimator.playSequentially(r1);
        fadeAnimator.addListener(new C06562());
        fadeAnimator.start();
    }

    public void onAnimationComplete() {
        this.currentView.image.setAnimationEnabled(false);
        this.runnable.run();
    }

    private void syncPresent(boolean isFirst) {
        if (isFirst) {
            UserMergedPresent currentPresent = getPresentForIndex(this.currentStep);
            if (currentPresent != null) {
                setPresentPadding(this.currentView, currentPresent);
                this.currentView.setPresent(currentPresent, true);
                this.currentView.image.setAnimationEnabled(true);
                this.currentView.image.setAnimationListener(this);
            }
        } else {
            PresentView temp = this.currentView;
            this.currentView = this.nextView;
            this.nextView = temp;
            this.currentView.setAlpha(1.0f);
            this.nextView.setAlpha(0.0f);
            this.currentView.image.setAnimationEnabled(true);
            this.nextView.image.setAnimationEnabled(false);
            this.currentView.image.setAnimationListener(this);
            this.nextView.image.setAnimationListener(null);
        }
        UserMergedPresent nextPresent = getPresentForIndex(this.currentStep + 1);
        if (nextPresent != null) {
            setPresentPadding(this.nextView, nextPresent);
            this.nextView.setPresent(nextPresent, false);
        }
    }

    private void setPresentPadding(PresentView presentView, UserMergedPresent present) {
        presentView.setPadding(present.isBig ? 0 : this.padding, present.isBig ? 0 : this.padding, 0, 0);
    }

    public void setPresents(List<UserMergedPresent> urls) {
        if (urls == null || !urls.equals(this.presents)) {
            this.presents = urls;
            startHandler(true);
        }
    }

    private UserMergedPresent getPresentForIndex(int index) {
        return (this.presents == null || this.presents.size() == 0) ? null : (UserMergedPresent) this.presents.get(index % this.presents.size());
    }

    public UserMergedPresent getCurrentVisiblePresent() {
        return getPresentForIndex(this.currentStep);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.isAttachedToWindow = true;
        startHandler(true);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isAttachedToWindow = false;
        this.handler.removeCallbacks(this.runnable);
    }
}
