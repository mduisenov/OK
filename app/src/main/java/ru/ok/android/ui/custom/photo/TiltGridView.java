package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import ru.ok.android.ui.custom.OverscrollHelper;
import ru.ok.android.ui.custom.OverscrollHelper.OverscrollListener;
import ru.ok.android.ui.custom.OverscrollHelper.ScrollPositionProvider;
import ru.ok.android.ui.tabbar.HideTabbarGridView;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.animation.AnimationHelper;

public class TiltGridView extends HideTabbarGridView implements OverscrollListener, ScrollPositionProvider {
    private OverscrollHelper overscrollHelper;

    public TiltGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreate();
    }

    public TiltGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public TiltGridView(Context context) {
        super(context);
        onCreate();
    }

    private void onCreate() {
        if (DeviceUtils.hasSdk(11)) {
            setOverScrollMode(2);
            this.overscrollHelper = new OverscrollHelper(this, this);
            this.overscrollHelper.setOverscrollListener(this);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.hideTabbarListener.onTouchEvent(event);
        boolean consumed = false;
        if (this.overscrollHelper != null) {
            consumed = this.overscrollHelper.onTouchEvent(event);
        }
        if (consumed) {
            return consumed;
        }
        return super.onTouchEvent(event);
    }

    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (!DeviceUtils.hasSdk(11) || isTouchEvent) {
            return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
        }
        this.overscrollHelper.overScrollBy(deltaY, isTouchEvent);
        return true;
    }

    public boolean isScrolledToTop(View view) {
        if (getCount() == 0) {
            return true;
        }
        if (getFirstVisiblePosition() != 0) {
            return false;
        }
        View firstChild = getChildAt(0);
        if (firstChild == null || firstChild.getTop() < 0) {
            return false;
        }
        return true;
    }

    public boolean isScrolledToBottom(View view) {
        if (getCount() == 0) {
            return true;
        }
        if (getLastVisiblePosition() != getCount() - 1) {
            return false;
        }
        View lastChild = getChildAt(getChildCount() - 1);
        if (lastChild == null || lastChild.getBottom() > getMeasuredHeight()) {
            return false;
        }
        return true;
    }

    public void onOverscrolled(float overscrolledBy) {
        if (getCount() != 0) {
            float rotation = overscrolledBy / (((float) getMeasuredHeight()) / 30.0f);
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                int pivotX;
                int pivotY;
                View child = getChildAt(i);
                child.setRotationX(rotation);
                if (overscrolledBy == 0.0f) {
                    if (AnimationHelper.SHOULD_USE_SOFTWARE_LAYER_ON_ROTATION) {
                        child.setLayerType(2, null);
                    }
                    pivotX = child.getRight() - ((int) (((double) child.getMeasuredWidth()) * 0.5d));
                    pivotY = child.getBottom() - ((int) (((double) child.getMeasuredHeight()) * 0.5d));
                } else {
                    if (AnimationHelper.SHOULD_USE_SOFTWARE_LAYER_ON_ROTATION) {
                        child.setLayerType(1, null);
                    }
                    pivotX = (getMeasuredWidth() / 2) - child.getLeft();
                    pivotY = child.getMeasuredHeight() / 2;
                }
                child.setPivotX((float) pivotX);
                child.setPivotY((float) pivotY);
            }
        }
    }
}
