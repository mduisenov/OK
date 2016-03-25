package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v4.view.ViewPagerExposed;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import ru.ok.android.utils.Logger;

public class StableViewPager extends ViewPagerExposed {
    public StableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StableViewPager(Context context) {
        super(context);
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean z = false;
        try {
            z = super.onInterceptTouchEvent(event);
        } catch (Throwable exc) {
            Logger.m179e(exc, "ViewPager throwed an error on touch");
        } catch (Throwable exc2) {
            Logger.m179e(exc2, "ViewPager throwed an error on touch");
        }
        return z;
    }

    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);
        updateChildVisibility();
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        updateChildVisibility();
    }

    private void updateChildVisibility() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!((LayoutParams) child.getLayoutParams()).isDecor) {
                boolean visible;
                int left = child.getLeft();
                int right = child.getRight();
                int scrollX = getScrollX();
                right -= scrollX;
                if (left - scrollX >= getWidth() - getPaddingRight() || right <= getPaddingLeft()) {
                    visible = false;
                } else {
                    visible = true;
                }
                child.setVisibility(visible ? 0 : 4);
            }
        }
    }
}
