package ru.ok.android.ui.presents.adapter;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

public class TopMarginItemDecoration extends ItemDecoration {
    private final int verticalSpace;

    public TopMarginItemDecoration(int verticalSpace) {
        this.verticalSpace = verticalSpace;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = this.verticalSpace;
        }
    }

    public void onDraw(Canvas canvas, RecyclerView parent, State state) {
        super.onDraw(canvas, parent, state);
        canvas.drawColor(parent.getResources().getColor(2131493183));
    }
}
