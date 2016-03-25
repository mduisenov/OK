package ru.ok.android.ui.custom.mediacomposer;

import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;

public final class ViewUtils {
    private static int[] xy;

    public static int getChildPositionByXY(ViewGroup parent, float x, float y) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (x >= ((float) child.getLeft()) && x <= ((float) child.getRight()) && y >= ((float) child.getTop()) && y <= ((float) child.getBottom())) {
                return i;
            }
        }
        return -1;
    }

    public static int getChildPositionByY(ViewGroup parent, float y) {
        int start = 0;
        int end = parent.getChildCount();
        while (end - start > 1) {
            int middle = (end + start) >> 1;
            int position = checkChildPosition(parent, y, middle);
            if (position == 0) {
                return middle;
            }
            if (position > 0) {
                end = middle;
            } else {
                start = middle;
            }
        }
        return start;
    }

    public static int checkChildPosition(ViewGroup parent, float y, int position) {
        int childCount = parent.getChildCount();
        if (position < 0) {
            return -1;
        }
        if (position >= childCount) {
            return 1;
        }
        View child = parent.getChildAt(position);
        if (child == null || ((float) child.getBottom()) < y) {
            return -1;
        }
        if (((float) child.getTop()) > y) {
            return 1;
        }
        return 0;
    }

    public static void setChildrenTranslationY(ViewGroup parent, int fromIndex, int toIndex, float translationY) {
        for (int i = fromIndex; i <= toIndex; i++) {
            parent.getChildAt(i).setTranslationY(translationY);
        }
    }

    public static void startAnimateChildrenTranslationY(ViewGroup parent, int fromIndex, int toIndex, float finalTranslationY, AnimatorListener listener) {
        int i = fromIndex;
        while (i <= toIndex) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(parent.getChildAt(i), "translationY", new float[]{finalTranslationY}).setDuration(200);
            if (listener != null && i == toIndex) {
                animator.addListener(listener);
            }
            animator.start();
            i++;
        }
    }

    static {
        xy = new int[2];
    }

    public static int getOnScreenY(View view) {
        view.getLocationOnScreen(xy);
        return xy[1];
    }
}
