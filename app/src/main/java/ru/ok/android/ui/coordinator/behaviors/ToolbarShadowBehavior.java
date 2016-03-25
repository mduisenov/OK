package ru.ok.android.ui.coordinator.behaviors;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.util.AttributeSet;
import android.view.View;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;

public class ToolbarShadowBehavior extends Behavior<View> {
    public ToolbarShadowBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        View view = parent.findViewById(C0263R.id.indicator);
        if (view == null || view.getVisibility() != 0) {
            boolean activityShadowVisible;
            int i;
            if ((parent.getContext() instanceof BaseCompatToolbarActivity) && ((BaseCompatToolbarActivity) parent.getContext()).isShadowVisible()) {
                activityShadowVisible = true;
            } else {
                activityShadowVisible = false;
            }
            if (activityShadowVisible) {
                i = 0;
            } else {
                i = 8;
            }
            child.setVisibility(i);
        } else {
            child.setVisibility(8);
        }
        return false;
    }
}
