package ru.ok.android.ui.search.fragment;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import java.util.Collection;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.cards.SuggestionsListView;
import ru.ok.android.utils.UIUtils;

public final class SuggestionsViewController implements AnimatorListener {
    private AnimatorSet animator;
    private boolean dirty;
    private int state;
    private SuggestionsListView suggestionsView;

    /* renamed from: ru.ok.android.ui.search.fragment.SuggestionsViewController.1 */
    class C11991 implements OnGlobalLayoutListener {
        final /* synthetic */ boolean val$animate;
        final /* synthetic */ boolean val$visible;

        C11991(boolean z, boolean z2) {
            this.val$visible = z;
            this.val$animate = z2;
        }

        public void onGlobalLayout() {
            UIUtils.removeOnGlobalLayoutListener(SuggestionsViewController.this.suggestionsView, this);
            SuggestionsViewController.this.updateState(this.val$visible, this.val$animate);
        }
    }

    public SuggestionsViewController(SuggestionsListView suggestionsView) {
        this.dirty = true;
        this.suggestionsView = suggestionsView;
        this.state = suggestionsView.isShown() ? 0 : 2;
    }

    public void setVisible(boolean visible, boolean animate) {
        if (!visible || (this.state != 0 && this.state != 3)) {
            if (!visible && (this.state == 2 || this.state == 1)) {
                return;
            }
            if (!visible && !animate) {
                setState(2);
                cancelAnimation();
                this.suggestionsView.setVisibility(8);
            } else if (this.dirty) {
                this.suggestionsView.getViewTreeObserver().addOnGlobalLayoutListener(new C11991(visible, animate));
            } else {
                updateState(visible, animate);
            }
        }
    }

    private void updateState(boolean visible, boolean animate) {
        switch (this.state) {
            case RECEIVED_VALUE:
                updateFromShown(visible, animate);
            case Message.TEXT_FIELD_NUMBER /*1*/:
                updateFromTransitHide(visible, animate);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                updateFromHidden(visible, animate);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                updateFromTransitShow(visible, animate);
            default:
        }
    }

    private void updateFromShown(boolean visible, boolean animate) {
        if (!visible && animate) {
            animate(0.0f, 1.0f, (float) (-(this.suggestionsView.getHeight() / 2)), 0.0f);
            setState(1);
        }
    }

    private void updateFromTransitHide(boolean visible, boolean animate) {
        if (!visible && animate) {
            return;
        }
        if (animate) {
            animate(this.suggestionsView.getTranslationY(), this.suggestionsView.getAlpha(), 0.0f, 1.0f);
            setState(3);
            return;
        }
        cancelAnimation();
        this.suggestionsView.setAlpha(1.0f);
        this.suggestionsView.setTranslationY(0.0f);
        setState(0);
    }

    private void updateFromHidden(boolean visible, boolean animate) {
        if (visible && animate) {
            animate((float) (-this.suggestionsView.getMeasuredHeight()), 0.0f, 0.0f, 1.0f);
            setState(3);
        }
    }

    private void updateFromTransitShow(boolean visible, boolean animate) {
        if (!visible || !animate) {
            if (visible && !animate) {
                cancelAnimation();
                this.suggestionsView.setAlpha(1.0f);
                this.suggestionsView.setTranslationY(0.0f);
                setState(0);
            } else if (!visible && animate) {
                animate(this.suggestionsView.getTranslationY(), this.suggestionsView.getAlpha(), (float) (-this.suggestionsView.getMeasuredHeight()), 0.0f);
                setState(1);
            }
        }
    }

    public void onAnimationEnd(Animator animation) {
        if (this.state == 1) {
            this.suggestionsView.setVisibility(8);
            setState(2);
        } else if (this.state == 3) {
            setState(0);
        }
    }

    private void animate(float fromTranslationY, float fromAlpha, float toTranslationY, float toAlpha) {
        cancelAnimation();
        this.suggestionsView.setVisibility(0);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(this.suggestionsView, "alpha", new float[]{fromAlpha, toAlpha});
        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(this.suggestionsView, "translationY", new float[]{fromTranslationY, toTranslationY});
        this.animator = new AnimatorSet();
        this.animator.playTogether(new Animator[]{alphaAnimator, translationYAnimator});
        this.animator.setDuration(250);
        this.animator.setInterpolator(new DecelerateInterpolator());
        this.animator.addListener(this);
        this.animator.start();
    }

    private void cancelAnimation() {
        if (this.animator != null && this.animator.isRunning()) {
            this.animator.cancel();
        }
    }

    private void setState(int state) {
        this.state = state;
    }

    public void setSuggestions(Collection<String> suggestions) {
        this.suggestionsView.setSuggestions((Collection) suggestions);
        this.dirty = true;
    }

    public void onAnimationStart(Animator animation) {
    }

    public void onAnimationCancel(Animator animation) {
        animation.removeListener(this);
    }

    public void onAnimationRepeat(Animator animation) {
    }
}
