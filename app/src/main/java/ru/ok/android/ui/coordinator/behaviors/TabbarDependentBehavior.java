package ru.ok.android.ui.coordinator.behaviors;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.util.AttributeSet;
import android.view.View;
import ru.ok.android.utils.DeviceUtils;

public class TabbarDependentBehavior<V extends View> extends Behavior<V> {
    protected final boolean enabled;
    protected boolean hasTabbar;
    protected int tabbarHeight;

    public TabbarDependentBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = DeviceUtils.isShowTabbar();
    }

    public boolean layoutDependsOn(CoordinatorLayout parent, V v, View dependency) {
        boolean isTabbar = false;
        if (this.enabled) {
            if (dependency.getId() == 2131624436) {
                isTabbar = true;
            }
            if (isTabbar) {
                this.hasTabbar = isTabbar;
                this.tabbarHeight = dependency.getMeasuredHeight();
            }
        }
        return isTabbar;
    }
}
