package ru.ok.android.ui.custom.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public final class RelativeSetSelectedLayout extends RelativeLayout {
    public RelativeSetSelectedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void dispatchSetSelected(boolean selected) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childAt = getChildAt(i);
            if (childAt.isDuplicateParentStateEnabled()) {
                childAt.setSelected(selected);
            }
        }
    }
}
