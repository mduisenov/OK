package ru.ok.android.ui.fragments.messages.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.RelativeLayout;

public final class RelativeMessageLayout extends RelativeLayout {
    private View editedTime;
    private View messageData;
    private View status;

    public RelativeMessageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.status = findViewById(2131624717);
        this.editedTime = findViewById(2131624714);
        this.messageData = findViewById(2131624716);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChild(this.status, widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        boolean editTimeVisible = this.editedTime.getVisibility() == 0;
        if (editTimeVisible) {
            int left = (this.messageData.getLeft() - this.editedTime.getMeasuredWidth()) - ((MarginLayoutParams) this.editedTime.getLayoutParams()).rightMargin;
            this.editedTime.layout(left, this.editedTime.getTop(), this.editedTime.getMeasuredWidth() + left, this.editedTime.getBottom());
        }
        if (this.status.getVisibility() == 0) {
            left = ((editTimeVisible ? this.editedTime.getLeft() : this.messageData.getLeft()) - this.status.getMeasuredWidth()) - ((MarginLayoutParams) this.status.getLayoutParams()).rightMargin;
            this.status.layout(left, this.status.getTop(), this.status.getMeasuredWidth() + left, this.status.getBottom());
        }
    }
}
