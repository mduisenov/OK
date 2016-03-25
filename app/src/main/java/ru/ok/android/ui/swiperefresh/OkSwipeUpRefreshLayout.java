package ru.ok.android.ui.swiperefresh;

import android.content.Context;
import android.util.AttributeSet;

public class OkSwipeUpRefreshLayout extends SwipeUpRefreshLayout {
    public OkSwipeUpRefreshLayout(Context context) {
        super(context);
    }

    public OkSwipeUpRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    private void init() {
        setColorSchemeResources(2131493185, 2131493186, 2131493187, 2131493188);
    }
}
