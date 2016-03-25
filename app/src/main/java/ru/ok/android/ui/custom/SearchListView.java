package ru.ok.android.ui.custom;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.animation.SimpleAnimatorListener;

public class SearchListView extends RecyclerView implements AnimatorListener, AnimatorUpdateListener {
    private ValueAnimator animator;
    public final LinearLayoutManager layoutManager;

    /* renamed from: ru.ok.android.ui.custom.SearchListView.1 */
    class C06261 extends SimpleAnimatorListener {
        final /* synthetic */ Runnable val$endRunnable;

        C06261(Runnable runnable) {
            this.val$endRunnable = runnable;
        }

        public void onAnimationEnd(Animator animation) {
            this.val$endRunnable.run();
        }
    }

    public SearchListView(Context context) {
        this(context, null);
    }

    public SearchListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.layoutManager = new LinearLayoutManager(getContext(), 1, false);
        setLayoutManager(this.layoutManager);
    }

    public void animate(boolean show, Runnable endRunnable) {
        if (DeviceUtils.hasSdk(14)) {
            cancelRunningAnimation();
            int count = getChildCount();
            if (count > 0) {
                PropertyValuesHolder[] vh = new PropertyValuesHolder[(count * 2)];
                for (int i = 0; i < count; i++) {
                    View view = getChildAt(i);
                    view.setDrawingCacheEnabled(true);
                    float fromY = show ? -view.getY() : 0.0f;
                    float toY = show ? 0.0f : -view.getY();
                    vh[i * 2] = PropertyValuesHolder.ofFloat("vt" + i, new float[]{fromY, toY});
                    float fromAlpha = show ? 0.0f : 1.0f;
                    float toAlpha = show ? 1.0f : 0.0f;
                    vh[(i * 2) + 1] = PropertyValuesHolder.ofFloat("va" + i, new float[]{fromAlpha, toAlpha});
                    Logger.m173d("Will animate child %s y: %s to %s, alpha: %s to %s", Integer.valueOf(i), Float.valueOf(fromY), Float.valueOf(toY), Float.valueOf(fromAlpha), Float.valueOf(toAlpha));
                }
                this.animator = ValueAnimator.ofPropertyValuesHolder(vh);
                this.animator.setDuration(250);
                this.animator.setInterpolator(new AccelerateDecelerateInterpolator());
                this.animator.addUpdateListener(this);
                this.animator.addListener(this);
                if (endRunnable != null) {
                    this.animator.addListener(new C06261(endRunnable));
                }
                this.animator.start();
            } else if (endRunnable != null) {
                endRunnable.run();
            }
        } else if (endRunnable != null) {
            endRunnable.run();
        }
    }

    public void cancelRunningAnimation() {
        if (this.animator != null) {
            this.animator.cancel();
            this.animator = null;
        }
        resetViewsStates();
    }

    protected void resetViewsStates() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            view.setTranslationY(0.0f);
            view.setAlpha(1.0f);
            view.setDrawingCacheEnabled(false);
        }
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        if (animation != null) {
            int count = getChildCount();
            if (count > 0) {
                int i = 0;
                while (i < count) {
                    View view = getChildAt(i);
                    Object value = animation.getAnimatedValue("vt" + i);
                    if (value != null) {
                        Logger.m173d("Updating animated values for child %s y: %s, alpha: %s", Integer.valueOf(i), Float.valueOf(((Float) value).floatValue()), Float.valueOf(((Float) animation.getAnimatedValue("va" + i)).floatValue()));
                        view.setTranslationY(y);
                        view.setAlpha(alpha);
                        view.invalidate();
                        i++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public void onAnimationStart(Animator animation) {
    }

    public void onAnimationEnd(Animator animation) {
        resetViewsStates();
    }

    public void onAnimationCancel(Animator animation) {
    }

    public void onAnimationRepeat(Animator animation) {
    }
}
