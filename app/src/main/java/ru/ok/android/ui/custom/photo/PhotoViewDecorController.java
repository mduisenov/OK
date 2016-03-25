package ru.ok.android.ui.custom.photo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.View;
import ru.ok.android.ui.image.view.DecorHandler.DecorCallback;
import ru.ok.android.ui.image.view.DecorHandler.DecorComponentController;
import ru.ok.android.utils.animation.SimpleAnimatorListener;

public class PhotoViewDecorController implements DecorComponentController {
    ValueAnimator animator;
    private final View bottomBarView;
    private final View commentView;
    private boolean commentVisible;

    /* renamed from: ru.ok.android.ui.custom.photo.PhotoViewDecorController.1 */
    class C07271 implements AnimatorUpdateListener {
        C07271() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            float alpha = ((Float) animation.getAnimatedValue()).floatValue();
            PhotoViewDecorController.this.bottomBarView.setAlpha(alpha);
            PhotoViewDecorController.this.commentView.setAlpha(alpha);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.PhotoViewDecorController.2 */
    class C07282 extends SimpleAnimatorListener {
        final /* synthetic */ DecorCallback val$callback;
        final /* synthetic */ int val$finalVisibility;

        C07282(int i, DecorCallback decorCallback) {
            this.val$finalVisibility = i;
            this.val$callback = decorCallback;
        }

        public void onAnimationStart(Animator animation) {
            PhotoViewDecorController.this.bottomBarView.setVisibility(0);
            if (PhotoViewDecorController.this.commentVisible) {
                PhotoViewDecorController.this.commentView.setVisibility(0);
            }
        }

        public void onAnimationEnd(Animator animation) {
            PhotoViewDecorController.this.bottomBarView.setVisibility(this.val$finalVisibility);
            if (PhotoViewDecorController.this.commentVisible) {
                PhotoViewDecorController.this.commentView.setVisibility(this.val$finalVisibility);
            }
            this.val$callback.visibilityChanged();
        }
    }

    public PhotoViewDecorController(View bottomBarView, View commentView) {
        this.bottomBarView = bottomBarView;
        this.commentView = commentView;
    }

    public void setComponentVisibility(Object component, boolean visible, boolean animate, DecorCallback callback) {
        float finalAlpha = visible ? 1.0f : 0.0f;
        int finalVisibility = visible ? 0 : 4;
        if (this.animator != null) {
            this.animator.cancel();
        }
        if (animate) {
            float startAlpha = this.bottomBarView.getAlpha();
            this.animator = ValueAnimator.ofFloat(new float[]{startAlpha, finalAlpha});
            this.animator.setDuration(200);
            this.animator.addUpdateListener(new C07271());
            this.animator.addListener(new C07282(finalVisibility, callback));
            this.animator.start();
            return;
        }
        this.bottomBarView.setAlpha(finalAlpha);
        this.bottomBarView.setVisibility(finalVisibility);
        this.commentView.setAlpha(finalAlpha);
        if (this.commentVisible) {
            this.commentView.setVisibility(finalVisibility);
        }
        callback.visibilityChanged();
    }

    public final void setCommentVisibile(boolean visible) {
        this.commentVisible = visible;
    }
}
