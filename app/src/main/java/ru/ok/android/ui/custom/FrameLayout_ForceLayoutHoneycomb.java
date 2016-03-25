package ru.ok.android.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.FrameLayout;

public class FrameLayout_ForceLayoutHoneycomb extends FrameLayout {
    public FrameLayout_ForceLayoutHoneycomb(Context context) {
        super(context);
    }

    public FrameLayout_ForceLayoutHoneycomb(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameLayout_ForceLayoutHoneycomb(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        forceLayoutAllParents(this);
    }

    private void forceLayoutAllParents(View view) {
        view.forceLayout();
        view.requestLayout();
        ViewParent parent = view.getParent();
        if (parent instanceof View) {
            forceLayoutAllParents((View) parent);
        }
    }
}
