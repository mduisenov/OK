package ru.ok.android.ui.presents.controller;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class ToastAnimationController {
    private final Activity activity;
    private final AnimationListener animationListener;
    private final View toastLayout;
    private final TextView toastTxt;

    /* renamed from: ru.ok.android.ui.presents.controller.ToastAnimationController.1 */
    class C11731 implements AnimationListener {

        /* renamed from: ru.ok.android.ui.presents.controller.ToastAnimationController.1.1 */
        class C11721 implements Runnable {
            C11721() {
            }

            public void run() {
                Animation animation = AnimationUtils.loadAnimation(ToastAnimationController.this.activity, 2130968599);
                animation.setFillAfter(true);
                ToastAnimationController.this.toastLayout.startAnimation(animation);
            }
        }

        C11731() {
        }

        public void onAnimationStart(@NonNull Animation animation) {
        }

        public void onAnimationEnd(@NonNull Animation animation) {
            ToastAnimationController.this.toastLayout.postDelayed(new C11721(), 2000);
        }

        public void onAnimationRepeat(@NonNull Animation animation) {
        }
    }

    public ToastAnimationController(@NonNull Activity activity) {
        this.animationListener = new C11731();
        this.activity = activity;
        this.toastTxt = (TextView) activity.findViewById(2131624573);
        this.toastLayout = activity.findViewById(2131624572);
    }

    public void showToast(@NonNull String text) {
        this.toastTxt.setText(text);
        Animation animation = AnimationUtils.loadAnimation(this.activity, 2130968598);
        animation.setFillAfter(true);
        animation.setAnimationListener(this.animationListener);
        this.toastLayout.startAnimation(animation);
    }
}
