package ru.ok.android.ui.swiperefresh;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.utils.Logger;

public class OkSwipeRefreshLayout extends SwipeRefreshLayout {
    public OkSwipeRefreshLayout(Context context) {
        super(context);
    }

    public OkSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setColorSchemeResources(2131493185, 2131493186, 2131493187, 2131493188);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        boolean z = false;
        int action = MotionEventCompat.getActionMasked(ev);
        if (action != 3 && action != 1) {
            return super.onTouchEvent(ev);
        }
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            Logger.m185w("Caught exception from SwipeRefreshLayout.onTouchEvent: %s", e);
            return z;
        }
    }

    public boolean canChildScrollUp() {
        if (getContext() instanceof BaseCompatToolbarActivity) {
            AppBarLayout appBarLayout = ((BaseCompatToolbarActivity) getContext()).getAppBarLayout();
            if (!(appBarLayout == null || appBarLayout.getTop() == 0)) {
                return true;
            }
        }
        return super.canChildScrollUp();
    }
}
