package ru.ok.android.ui.coordinator.behaviors;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import ru.ok.android.ui.mediatopic.view.MediaComposerPanel;

public class MediaComposerBehavior extends TabbarDependentBehavior<MediaComposerPanel> {
    private int bottomHasTabbarPadding;

    public MediaComposerBehavior(Context context) {
        super(context, null);
        this.bottomHasTabbarPadding = context.getResources().getDimensionPixelSize(2131231193);
    }

    public MediaComposerBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, MediaComposerPanel child, View dependency) {
        if (this.hasTabbar) {
            child.setTranslationY((float) ((int) dependency.getTranslationY()));
        }
        return true;
    }

    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, MediaComposerPanel child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & 2) == 2;
    }

    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, MediaComposerPanel child, View target, int dx, int dy, int[] consumed) {
        if (child.getTranslationY() == 0.0f && dy > 0 && child.isExpanded()) {
            child.collapse(null, true);
        }
    }

    public boolean onMeasureChild(CoordinatorLayout parent, MediaComposerPanel child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        int bottomPadding;
        if (this.hasTabbar) {
            bottomPadding = this.bottomHasTabbarPadding;
        } else {
            bottomPadding = 0;
        }
        if (child.getPaddingBottom() != bottomPadding) {
            child.setPadding(0, 0, 0, bottomPadding);
        }
        return false;
    }
}
