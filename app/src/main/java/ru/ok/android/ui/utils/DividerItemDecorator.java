package ru.ok.android.ui.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

public final class DividerItemDecorator extends ItemDecoration {
    private final int dividerHeight;
    private final int paddingLeft;
    private final Paint paint;

    public DividerItemDecorator(Context context) {
        this.paint = new Paint();
        Resources resources = context.getResources();
        this.paint.setColor(resources.getColor(2131492988));
        this.dividerHeight = resources.getDimensionPixelSize(2131230899);
        this.paint.setStrokeWidth((float) this.dividerHeight);
        this.paddingLeft = resources.getDimensionPixelOffset(2131230900);
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildPosition(view) > 0) {
            outRect.top += this.dividerHeight;
        }
    }

    public void onDrawOver(Canvas c, RecyclerView parent, State state) {
        super.onDrawOver(c, parent, state);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View childAt = parent.getChildAt(i);
            if (parent.getChildPosition(childAt) > 0) {
                float y = (((float) childAt.getTop()) + childAt.getTranslationY()) - (((float) this.dividerHeight) / 2.0f);
                c.drawLine((float) (childAt.getLeft() + this.paddingLeft), y, (float) childAt.getRight(), y, this.paint);
            }
        }
    }
}
