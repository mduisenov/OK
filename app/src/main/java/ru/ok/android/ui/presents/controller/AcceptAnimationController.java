package ru.ok.android.ui.presents.controller;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import ru.ok.android.ui.custom.imageview.RoundAvatarImageView;
import ru.ok.android.ui.presents.interpolators.ShowPresentInterpolator;
import ru.ok.android.utils.localization.base.LocalizedActivity;

public class AcceptAnimationController {
    private final LocalizedActivity activity;
    private final View buttonsLayout;
    private final View firstStateLayout;
    private AnimationListener hideAnimationListener;
    private boolean isMusic;
    private final OnAcceptAnimationComplete listener;
    private final View musicImg;
    private final View secondStateLayout;
    private final TextView senderNameTxt;
    private final AnimationListener showAnimationListener;
    private final RoundAvatarImageView userAvatarImg;
    private final View userPresentImgLayout;

    /* renamed from: ru.ok.android.ui.presents.controller.AcceptAnimationController.1 */
    class C11671 implements AnimationListener {
        C11671() {
        }

        public void onAnimationStart(@NonNull Animation animation) {
        }

        public void onAnimationEnd(@NonNull Animation animation) {
            AcceptAnimationController.this.applyHideAnimation();
            AcceptAnimationController.this.prepareForShowUserAnimation();
            AcceptAnimationController.this.startShowUserAnimation();
        }

        public void onAnimationRepeat(@NonNull Animation animation) {
        }
    }

    /* renamed from: ru.ok.android.ui.presents.controller.AcceptAnimationController.2 */
    class C11682 implements AnimationListener {
        C11682() {
        }

        public void onAnimationStart(@NonNull Animation animation) {
        }

        public void onAnimationEnd(@NonNull Animation animation) {
            if (AcceptAnimationController.this.listener != null) {
                AcceptAnimationController.this.listener.onAnimationAcceptComplete();
            }
        }

        public void onAnimationRepeat(@NonNull Animation animation) {
        }
    }

    public AcceptAnimationController(@NonNull LocalizedActivity activity, @Nullable OnAcceptAnimationComplete listener) {
        this.hideAnimationListener = new C11671();
        this.showAnimationListener = new C11682();
        this.activity = activity;
        this.listener = listener;
        this.userPresentImgLayout = activity.findViewById(2131624560);
        this.userAvatarImg = (RoundAvatarImageView) activity.findViewById(2131624559);
        this.senderNameTxt = (TextView) activity.findViewById(2131624553);
        this.firstStateLayout = activity.findViewById(2131624554);
        this.secondStateLayout = activity.findViewById(2131624558);
        this.buttonsLayout = activity.findViewById(2131624568);
        this.musicImg = activity.findViewById(2131624562);
    }

    public void startAnimation(boolean immediately, boolean isMusic) {
        this.isMusic = isMusic;
        if (immediately) {
            applyHideAnimation();
            prepareForShowUserAnimation();
            if (this.listener != null) {
                this.listener.onAnimationAcceptComplete();
                return;
            }
            return;
        }
        startHideAnimation();
    }

    private void startHideAnimation() {
        startHideReceivedPresentAnimation();
        startHideSenderNameAnimation();
    }

    private void applyHideAnimation() {
        applyHideReceivedPresentAnimation();
        applyHideSenderNameAnimation();
    }

    private void startHideReceivedPresentAnimation() {
        Animation hideContentAnimation = AnimationUtils.loadAnimation(this.activity, 2130968616);
        hideContentAnimation.setAnimationListener(this.hideAnimationListener);
        this.firstStateLayout.startAnimation(hideContentAnimation);
    }

    private void applyHideReceivedPresentAnimation() {
        this.firstStateLayout.setVisibility(8);
        this.buttonsLayout.setVisibility(8);
    }

    private void startHideSenderNameAnimation() {
        Animation hideTextAnimation = AnimationUtils.loadAnimation(this.activity, 2130968620);
        this.senderNameTxt.startAnimation(hideTextAnimation);
        this.buttonsLayout.startAnimation(hideTextAnimation);
    }

    private void applyHideSenderNameAnimation() {
        this.senderNameTxt.setVisibility(4);
    }

    private void startShowUserAnimation() {
        Animation userAnimation = AnimationUtils.loadAnimation(this.activity, 2130968622);
        userAnimation.setAnimationListener(this.showAnimationListener);
        userAnimation.setInterpolator(new ShowPresentInterpolator());
        this.userAvatarImg.startAnimation(userAnimation);
        Animation presentAnimation = AnimationUtils.loadAnimation(this.activity, 2130968619);
        presentAnimation.setInterpolator(new ShowPresentInterpolator());
        this.userPresentImgLayout.startAnimation(presentAnimation);
        this.senderNameTxt.startAnimation(AnimationUtils.loadAnimation(this.activity, 2130968621));
    }

    private void prepareForShowUserAnimation() {
        this.secondStateLayout.setVisibility(0);
        this.senderNameTxt.setVisibility(0);
        this.senderNameTxt.setText(this.activity.getStringLocalized(2131166432));
        if (this.isMusic) {
            this.musicImg.setVisibility(0);
        } else {
            this.musicImg.setVisibility(4);
        }
    }
}
