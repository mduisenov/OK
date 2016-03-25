package ru.ok.android.ui.presents.controller;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import ru.ok.android.ui.presents.interpolators.CardFlipInInterpolator;
import ru.ok.android.ui.presents.interpolators.CardFlipOutInterpolator;
import ru.ok.android.utils.localization.base.LocalizedActivity;
import ru.ok.java.api.response.presents.PresentNotificationResponse;

public class ReplyAnimationController {
    private final LocalizedActivity activity;
    private final View buttonsLayout;
    private final View firstStateLayout;
    private final AnimationListener flipInAnimationListener;
    private PresentNotificationResponse response;
    private final View rootLayout;
    private final View secondStateLayout;
    private final TextView senderNameTxt;
    private final View showAllBtn;
    private final View thirdStateLayout;

    /* renamed from: ru.ok.android.ui.presents.controller.ReplyAnimationController.1 */
    class C11711 implements AnimationListener {
        C11711() {
        }

        public void onAnimationStart(@NonNull Animation animation) {
        }

        public void onAnimationEnd(@NonNull Animation animation) {
            ReplyAnimationController.this.applyFlipInAnimationResult();
            Animation replyAnimation = AnimationUtils.loadAnimation(ReplyAnimationController.this.activity, 2130968615);
            replyAnimation.setInterpolator(new CardFlipOutInterpolator());
            ReplyAnimationController.this.rootLayout.startAnimation(replyAnimation);
        }

        public void onAnimationRepeat(@NonNull Animation animation) {
        }
    }

    public ReplyAnimationController(@NonNull LocalizedActivity activity) {
        this.flipInAnimationListener = new C11711();
        this.activity = activity;
        this.rootLayout = activity.findViewById(2131624552);
        this.senderNameTxt = (TextView) activity.findViewById(2131624553);
        this.firstStateLayout = activity.findViewById(2131624554);
        this.secondStateLayout = activity.findViewById(2131624558);
        this.thirdStateLayout = activity.findViewById(2131624563);
        this.buttonsLayout = activity.findViewById(2131624568);
        this.showAllBtn = activity.findViewById(2131624571);
    }

    public void startAnimation(@NonNull PresentNotificationResponse response, boolean immediately) {
        this.response = response;
        if (immediately) {
            applyFlipInAnimationResult();
        } else {
            startFlipInAnimation();
        }
    }

    private void startFlipInAnimation() {
        Animation replyAnimation = AnimationUtils.loadAnimation(this.activity, 2130968614);
        replyAnimation.setAnimationListener(this.flipInAnimationListener);
        replyAnimation.setInterpolator(new CardFlipInInterpolator());
        this.rootLayout.startAnimation(replyAnimation);
    }

    private void applyFlipInAnimationResult() {
        this.senderNameTxt.setText(this.response.sendText);
        this.buttonsLayout.setVisibility(8);
        this.firstStateLayout.setVisibility(8);
        this.secondStateLayout.setVisibility(8);
        this.thirdStateLayout.setVisibility(0);
        this.showAllBtn.setVisibility(0);
    }
}
