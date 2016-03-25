package ru.ok.android.ui.swiperefresh;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;

public class OkSwipeRefreshLayoutWrappedList extends OkSwipeRefreshLayout {
    public OkSwipeRefreshLayoutWrappedList(Context context) {
        super(context);
    }

    public OkSwipeRefreshLayoutWrappedList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean canChildScrollUp() {
        return ViewCompat.canScrollVertically(findViewById(2131624731), -1);
    }
}
