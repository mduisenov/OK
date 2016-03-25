package ru.ok.android.ui.custom.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView;

public class RelativePhotoCanCallLayout extends ViewGroup {
    private AsyncDraweeView avatar;
    private View canCall;

    public RelativePhotoCanCallLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.avatar = (AsyncDraweeView) findViewById(2131624657);
        this.canCall = findViewById(2131624634);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        setMeasuredDimension(measuredWidth, measuredWidth);
        int measureSpec = MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824);
        for (int i = 0; i < getChildCount(); i++) {
            measureChild(getChildAt(i), measureSpec, measureSpec);
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.avatar.layout(0, 0, r - l, b - t);
        int left = (int) (((((float) (this.avatar.getLeft() + 0)) + (((float) ((this.avatar.getMeasuredWidth() / 2) - 0)) * 0.29289323f)) - ((float) this.canCall.getPaddingLeft())) - ((float) (((this.canCall.getMeasuredWidth() - this.canCall.getPaddingLeft()) - this.canCall.getPaddingRight()) / 2)));
        int top = (int) (((((float) (this.avatar.getTop() + 0)) + (((float) ((this.avatar.getMeasuredHeight() / 2) - 0)) * 1.7071068f)) - ((float) this.canCall.getPaddingTop())) - ((float) (((this.canCall.getMeasuredHeight() - this.canCall.getPaddingTop()) - this.canCall.getPaddingBottom()) / 2)));
        this.canCall.layout(left, top, this.canCall.getMeasuredWidth() + left, this.canCall.getMeasuredHeight() + top);
    }
}
