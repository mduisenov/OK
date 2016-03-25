package ru.ok.android.ui.utils;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

public final class HideTabbarItemDecorator extends ItemDecoration {
    private final int toolbarHeight;

    public HideTabbarItemDecorator(Context context) {
        this.toolbarHeight = context.getResources().getDimensionPixelOffset(2131231193);
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildPosition(view) == parent.getAdapter().getItemCount() - 1) {
            outRect.bottom += this.toolbarHeight;
        }
    }
}
