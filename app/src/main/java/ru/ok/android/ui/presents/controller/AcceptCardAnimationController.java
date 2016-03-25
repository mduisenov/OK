package ru.ok.android.ui.presents.controller;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import ru.ok.android.ui.presents.interpolators.CardBounceInterpolator;
import ru.ok.android.utils.localization.base.LocalizedActivity;

public class AcceptCardAnimationController {
    private final LocalizedActivity activity;
    private final View buttonsLayout;
    private final View firstStateLayout;
    private AnimationListener hideAnimationListener;
    private final OnAcceptAnimationComplete listener;
    private final TextView senderNameTxt;
    private final AnimationListener showAnimationListener;

    /* renamed from: ru.ok.android.ui.presents.controller.AcceptCardAnimationController.1 */
    class C11691 implements AnimationListener {
        C11691() {
        }

        public void onAnimationStart(@NonNull Animation animation) {
        }

        public void onAnimationEnd(@NonNull Animation animation) {
            AcceptCardAnimationController.this.prepareShowAnimation();
            AcceptCardAnimationController.this.startShowAnimation();
        }

        public void onAnimationRepeat(@NonNull Animation animation) {
        }
    }

    /* renamed from: ru.ok.android.ui.presents.controller.AcceptCardAnimationController.2 */
    class C11702 implements AnimationListener {
        C11702() {
        }

        public void onAnimationStart(@NonNull Animation animation) {
        }

        public void onAnimationEnd(@NonNull Animation animation) {
            if (AcceptCardAnimationController.this.listener != null) {
                AcceptCardAnimationController.this.listener.onAnimationAcceptComplete();
            }
        }

        public void onAnimationRepeat(@NonNull Animation animation) {
        }
    }

    public AcceptCardAnimationController(@NonNull LocalizedActivity activity, @Nullable OnAcceptAnimationComplete listener) {
        this.hideAnimationListener = new C11691();
        this.showAnimationListener = new C11702();
        this.activity = activity;
        this.listener = listener;
        this.senderNameTxt = (TextView) activity.findViewById(2131624553);
        this.firstStateLayout = activity.findViewById(2131624554);
        this.buttonsLayout = activity.findViewById(2131624568);
    }

    public void startAnimation(boolean immediately) {
        if (immediately) {
            applyHideAnimation();
            prepareShowAnimation();
            if (this.listener != null) {
                this.listener.onAnimationAcceptComplete();
                return;
            }
            return;
        }
        startHideAnimation();
    }

    private void prepareShowAnimation() {
        int lines = this.senderNameTxt.getLineCount();
        this.senderNameTxt.setVisibility(0);
        this.senderNameTxt.setText(this.activity.getStringLocalized(2131166433));
        if (this.senderNameTxt.getLineCount() < lines) {
            this.senderNameTxt.setLines(lines);
        }
    }

    private void startShowAnimation() {
        Animation textAnimation = AnimationUtils.loadAnimation(this.activity, 2130968621);
        textAnimation.setStartOffset(0);
        textAnimation.setAnimationListener(this.showAnimationListener);
        this.senderNameTxt.startAnimation(textAnimation);
    }

    private void startHideAnimation() {
        Animation hideTextAnimation = AnimationUtils.loadAnimation(this.activity, 2130968620);
        hideTextAnimation.setAnimationListener(this.hideAnimationListener);
        hideTextAnimation.setStartOffset(0);
        this.senderNameTxt.startAnimation(hideTextAnimation);
        this.buttonsLayout.startAnimation(hideTextAnimation);
        Animation cardAnimation = AnimationUtils.loadAnimation(this.activity, 2130968613);
        cardAnimation.setInterpolator(new CardBounceInterpolator());
        this.firstStateLayout.startAnimation(cardAnimation);
    }

    private void applyHideAnimation() {
        this.buttonsLayout.setVisibility(4);
        this.firstStateLayout.setVisibility(0);
    }
}
