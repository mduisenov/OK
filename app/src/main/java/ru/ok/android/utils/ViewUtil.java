package ru.ok.android.utils;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import ru.ok.android.ui.custom.TouchFloatingDelegate;

public final class ViewUtil {

    /* renamed from: ru.ok.android.utils.ViewUtil.1 */
    static class C14291 implements Runnable {
        final /* synthetic */ int val$touchOffsetPixelsH;
        final /* synthetic */ int val$touchOffsetPixelsV;
        final /* synthetic */ View val$view;

        C14291(View view, int i, int i2) {
            this.val$view = view;
            this.val$touchOffsetPixelsH = i;
            this.val$touchOffsetPixelsV = i2;
        }

        public void run() {
            Rect hitRect = new Rect();
            this.val$view.getHitRect(hitRect);
            hitRect.inset(-this.val$touchOffsetPixelsH, -this.val$touchOffsetPixelsV);
            ((View) this.val$view.getParent()).setTouchDelegate(new TouchDelegate(hitRect, this.val$view));
        }
    }

    /* renamed from: ru.ok.android.utils.ViewUtil.2 */
    static class C14302 implements Runnable {
        final /* synthetic */ View val$child;
        final /* synthetic */ View val$parent;

        C14302(View view, View view2) {
            this.val$parent = view;
            this.val$child = view2;
        }

        public void run() {
            Rect rect = new Rect();
            rect.left = 0;
            rect.top = 0;
            rect.right = this.val$parent.getWidth();
            rect.bottom = this.val$parent.getHeight();
            this.val$parent.setTouchDelegate(new TouchDelegate(rect, this.val$child));
        }
    }

    /* renamed from: ru.ok.android.utils.ViewUtil.3 */
    static class C14313 implements AnimatorUpdateListener {
        final /* synthetic */ View val$view;

        C14313(View view) {
            this.val$view = view;
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            LayoutParams lp = this.val$view.getLayoutParams();
            lp.height = ((Integer) animation.getAnimatedValue()).intValue();
            this.val$view.setLayoutParams(lp);
        }
    }

    public static void visible(View... views) {
        for (View view : views) {
            setVisibility(view, true);
        }
    }

    public static void invisible(View... views) {
        for (View view : views) {
            setVisibility(view, 4);
        }
    }

    public static void gone(View... views) {
        for (View view : views) {
            setVisibility(view, false);
        }
    }

    public static void setVisibility(View view, boolean visibility) {
        if (view != null) {
            boolean currentVisibility;
            if (view.getVisibility() == 0) {
                currentVisibility = true;
            } else {
                currentVisibility = false;
            }
            if (currentVisibility == visibility) {
                return;
            }
            if (visibility) {
                view.setVisibility(0);
            } else {
                view.setVisibility(8);
            }
        }
    }

    public static void setVisibility(View view, int visibility) {
        if (view != null && view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }

    public static void resetLayoutParams(View v, int width, int height) {
        LayoutParams params = v.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(width, height);
        }
        params.width = width;
        params.height = height;
        v.setLayoutParams(params);
    }

    public static void resetLayoutParams(View v, int width, int height, int leftMargin, int rightMargin) {
        LayoutParams params = v.getLayoutParams();
        if (params == null) {
            params = new MarginLayoutParams(width, height);
        }
        if (params instanceof MarginLayoutParams) {
            MarginLayoutParams marginParams = (MarginLayoutParams) params;
            marginParams.leftMargin = leftMargin;
            marginParams.rightMargin = rightMargin;
        }
        params.width = width;
        params.height = height;
        v.setLayoutParams(params);
    }

    public static void resetLayoutMargins(View v, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LayoutParams params = v.getLayoutParams();
        if (params == null) {
            params = new MarginLayoutParams(-1, -1);
        }
        if (params instanceof MarginLayoutParams) {
            MarginLayoutParams marginParams = (MarginLayoutParams) params;
            marginParams.leftMargin = leftMargin;
            marginParams.topMargin = topMargin;
            marginParams.rightMargin = rightMargin;
            marginParams.bottomMargin = bottomMargin;
        }
        v.setLayoutParams(params);
    }

    public static void setTouchDelegate(View view, int touchOffsetPixels) {
        setTouchDelegate(view, touchOffsetPixels, touchOffsetPixels);
    }

    public static void setTouchDelegate(View view, int touchOffsetPixelsH, int touchOffsetPixelsV) {
        view.post(new C14291(view, touchOffsetPixelsH, touchOffsetPixelsV));
    }

    public static void setTouchDelegate(View view, int dLeft, int dTop, int dRight, int dBottom) {
        ((View) view.getParent()).setTouchDelegate(new TouchFloatingDelegate(view, dLeft, dTop, dRight, dBottom));
    }

    public static void expandChildClickAreaToParentClickArea(@NonNull View child) {
        View parent = (View) child.getParent();
        parent.post(new C14302(parent, child));
    }

    @NonNull
    public static ValueAnimator createHeightAnimator(View view, int startHeight, int endHeight, long duration) {
        ValueAnimator animator = ValueAnimator.ofInt(new int[]{startHeight, endHeight});
        animator.setDuration(duration).addUpdateListener(new C14313(view));
        return animator;
    }

    public static void setBackgroundCompat(@NonNull View view, @Nullable Drawable drawable) {
        if (VERSION.SDK_INT < 16) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }
}
