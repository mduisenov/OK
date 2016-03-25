package ru.ok.android.ui.coordinator.behaviors;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.ViewUtil;
import ru.ok.android.utils.animation.AnimationHelper;

public class FabBottomBehavior extends TabbarDependentBehavior<FloatingActionButton> {
    private final Rect anchorRect;
    private final Rect coordinatorRect;
    private final int fabBottomMargin;
    private final int fabRightMargin;
    private final boolean isSmallDevice;

    public FabBottomBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.anchorRect = new Rect();
        this.coordinatorRect = new Rect();
        this.fabBottomMargin = context.getResources().getDimensionPixelSize(2131230798);
        this.fabRightMargin = context.getResources().getDimensionPixelSize(2131230799);
        this.isSmallDevice = DeviceUtils.isSmall(context);
    }

    public void setHideAmount(FloatingActionButton floatingActionButton, int numerator, int denominator) {
        if (numerator >= denominator) {
            ViewUtil.invisible(floatingActionButton);
        } else {
            ViewUtil.visible(floatingActionButton);
        }
        floatingActionButton.setTranslationY(((float) numerator) + (((float) (floatingActionButton.getPaddingBottom() + floatingActionButton.getHeight())) * (denominator != 0 ? AnimationHelper.hideInterpolator.getInterpolation(((float) numerator) / ((float) denominator)) : 1.0f)));
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        if (dependency.getId() != 2131624436) {
            return false;
        }
        setHideAmount(fab, (int) dependency.getTranslationY(), dependency.getHeight());
        return true;
    }

    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton fab, View directTargetChild, View target, int nestedScrollAxes) {
        return !this.hasTabbar && (nestedScrollAxes & 2) == 2;
    }

    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton fab, View target, int dx, int dy, int[] consumed) {
        if (!this.hasTabbar) {
            changeButtonVisibilityInternal(fab, dy <= 0);
        }
    }

    private int getFloatingActionButtonYTranslationHidden(FloatingActionButton fab) {
        return ((MarginLayoutParams) fab.getLayoutParams()).bottomMargin + fab.getHeight();
    }

    private void changeButtonVisibilityInternal(FloatingActionButton fab, boolean show) {
        int floatingActionButtonYTranslationHidden = getFloatingActionButtonYTranslationHidden(fab);
        if (fab.getTranslationY() == ((float) (show ? floatingActionButtonYTranslationHidden : 0))) {
            fab.animate().translationY(show ? 0.0f : (float) floatingActionButtonYTranslationHidden).setDuration(150).start();
        }
    }

    public boolean onMeasureChild(CoordinatorLayout parent, FloatingActionButton fab, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        processIgnoredMargins(parent, fab);
        return super.onMeasureChild(parent, fab, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    private void processIgnoredMargins(CoordinatorLayout parent, FloatingActionButton fab) {
        LayoutParams lp = (LayoutParams) fab.getLayoutParams();
        lp.bottomMargin = this.fabBottomMargin;
        lp.rightMargin = this.fabRightMargin;
        if (this.hasTabbar) {
            lp.bottomMargin += this.tabbarHeight;
        }
        if (!this.isSmallDevice && lp.getAnchorId() != -1 && (lp.anchorGravity & 5) == 5) {
            View anchor = parent.findViewById(lp.getAnchorId());
            if (anchor != null) {
                anchor.getGlobalVisibleRect(this.anchorRect);
                parent.getGlobalVisibleRect(this.coordinatorRect);
                if (this.anchorRect.right != this.coordinatorRect.right) {
                    fab.setTranslationX((float) (-this.fabRightMargin));
                }
            }
        }
    }
}
