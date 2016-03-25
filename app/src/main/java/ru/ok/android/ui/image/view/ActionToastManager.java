package ru.ok.android.ui.image.view;

import android.content.Context;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import ru.ok.android.ui.custom.photo.ActionToastView;
import ru.ok.android.utils.animation.SimpleAnimationListener;

public class ActionToastManager {

    /* renamed from: ru.ok.android.ui.image.view.ActionToastManager.1 */
    static class C09981 implements Runnable {
        final /* synthetic */ ActionToastView val$toastView;
        final /* synthetic */ ViewGroup val$viewGroup;

        /* renamed from: ru.ok.android.ui.image.view.ActionToastManager.1.1 */
        class C09971 extends SimpleAnimationListener {

            /* renamed from: ru.ok.android.ui.image.view.ActionToastManager.1.1.1 */
            class C09961 implements Runnable {
                C09961() {
                }

                public void run() {
                    C09981.this.val$viewGroup.removeView(C09981.this.val$toastView);
                }
            }

            C09971() {
            }

            public void onAnimationEnd(Animation animation) {
                C09981.this.val$viewGroup.post(new C09961());
            }
        }

        C09981(ActionToastView actionToastView, ViewGroup viewGroup) {
            this.val$toastView = actionToastView;
            this.val$viewGroup = viewGroup;
        }

        public void run() {
            if (this.val$toastView.getParent() == this.val$viewGroup) {
                this.val$toastView.fadeOut(new C09971());
            }
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.ActionToastManager.2 */
    static class C10002 extends SimpleAnimationListener {
        final /* synthetic */ ActionToastView val$toastView;
        final /* synthetic */ ViewGroup val$viewGroup;

        /* renamed from: ru.ok.android.ui.image.view.ActionToastManager.2.1 */
        class C09991 implements Runnable {
            C09991() {
            }

            public void run() {
                C10002.this.val$viewGroup.removeView(C10002.this.val$toastView);
            }
        }

        C10002(ActionToastView actionToastView, ViewGroup viewGroup) {
            this.val$toastView = actionToastView;
            this.val$viewGroup = viewGroup;
        }

        public void onAnimationEnd(Animation animation) {
            if (this.val$toastView.getParent() == this.val$viewGroup) {
                this.val$viewGroup.post(new C09991());
            }
        }
    }

    public static final ActionToastView newToastView(Context context, CharSequence infoMessage, OnClickListener onClickListener) {
        ActionToastView view = new ActionToastView(context);
        view.setInfoMessage(infoMessage);
        view.setOnClickListener(onClickListener);
        return view;
    }

    public static final void showToastAt(ViewGroup viewGroup, ActionToastView toastView, long duration) {
        viewGroup.addView(toastView);
        toastView.fadeIn(null);
        if (duration > 0) {
            viewGroup.postDelayed(new C09981(toastView, viewGroup), duration);
        }
    }

    public static final void hideToastFrom(ViewGroup viewGroup, ActionToastView toastView) {
        toastView.fadeOut(new C10002(toastView, viewGroup));
    }
}
