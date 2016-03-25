package ru.ok.android.ui.coordinator.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.Behavior;
import android.support.design.widget.AppBarLayout.LayoutParams;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import java.util.List;
import ru.ok.android.fragments.web.WebBaseFragment;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Utils;

public class AppBarLayoutBehavior extends Behavior {
    private final boolean isLargeDevice;
    public boolean toolbarLocked;

    public AppBarLayoutBehavior(Context context, AttributeSet attrs) {
        boolean z = false;
        super(context, attrs);
        this.toolbarLocked = false;
        if (!DeviceUtils.isSmall(context)) {
            z = true;
        }
        this.isLargeDevice = z;
    }

    public void setToolbarLocked(boolean toolbarLocked) {
        this.toolbarLocked = toolbarLocked;
    }

    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, View target) {
        super.onStopNestedScroll(coordinatorLayout, appBarLayout, target);
        float hidedHeight = (float) Math.abs(appBarLayout.getTop());
        float fullHeight = (float) appBarLayout.getMeasuredHeight();
        if (hidedHeight != 0.0f && hidedHeight != fullHeight) {
            appBarLayout.setExpanded(hidedHeight < fullHeight / 2.0f, true);
        }
    }

    public boolean isScrollLocked(CoordinatorLayout parent, AppBarLayout appBarLayout) {
        if (!(parent.getContext() instanceof FragmentActivity)) {
            return false;
        }
        List<Fragment> fragments = ((FragmentActivity) parent.getContext()).getSupportFragmentManager().getFragments();
        if (fragments != null) {
            int size = fragments.size();
            for (int i = 0; i < size; i++) {
                Fragment fragment = (Fragment) fragments.get(i);
                if (fragment != null && fragment.isVisible()) {
                    if ("tag_messages".equals(fragment.getTag()) && this.isLargeDevice) {
                        return true;
                    }
                    if ("tag_discussion_comments".equals(fragment.getTag())) {
                        return true;
                    }
                    if (fragment instanceof WebBaseFragment) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void updateToolbarScrollFlags(AppBarLayout child, boolean scrollLocked) {
        boolean currentScrollLocked;
        int i = 0;
        LayoutParams toolbarBarLayoutParams = (LayoutParams) ((Toolbar) Utils.findDirectChildById(child, 2131624641)).getLayoutParams();
        if (toolbarBarLayoutParams.getScrollFlags() == 0) {
            currentScrollLocked = true;
        } else {
            currentScrollLocked = false;
        }
        if (scrollLocked != currentScrollLocked) {
            if (!scrollLocked) {
                i = 5;
            }
            toolbarBarLayoutParams.setScrollFlags(i);
        }
    }

    public boolean onMeasureChild(CoordinatorLayout parent, AppBarLayout child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        if (!this.toolbarLocked) {
            updateToolbarScrollFlags(child, isScrollLocked(parent, child));
        }
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }
}
