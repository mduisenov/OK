package ru.ok.android.ui.swiperefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import ru.ok.android.emoji.C0263R;

public final class SwipeEmptyViewRefreshLayout extends OkSwipeRefreshLayoutWrappedList {
    private View emptyView;

    public SwipeEmptyViewRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.emptyView = findViewById(C0263R.id.empty_view);
        if (this.emptyView != null) {
            this.emptyView.setClickable(true);
        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.emptyView != null && this.emptyView.getVisibility() == 0) {
            measureChild(this.emptyView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.emptyView != null && this.emptyView.getVisibility() == 0) {
            this.emptyView.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + this.emptyView.getMeasuredWidth(), getPaddingTop() + this.emptyView.getMeasuredHeight());
        }
    }
}
