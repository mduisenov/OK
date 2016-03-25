package ru.ok.android.ui.custom.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.RelativeLayout;
import ru.ok.android.C0206R;

public class OkLargeTabletLayout extends RelativeLayout {
    private View divider;
    private View left;
    private float leftLayoutWeight;
    private View right;
    private float rightLayoutWeight;
    private View shadow;
    private View topContainer;
    private int topContentHeight;

    public OkLargeTabletLayout(Context context) {
        this(context, null);
    }

    public OkLargeTabletLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OkLargeTabletLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.leftLayoutWeight = 2.0f;
        this.rightLayoutWeight = 3.0f;
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, C0206R.styleable.OkLargeTabletLayout, defStyleAttr, 0);
        this.leftLayoutWeight = typedArray.getFloat(0, this.leftLayoutWeight);
        this.rightLayoutWeight = typedArray.getFloat(1, this.rightLayoutWeight);
        typedArray.recycle();
        if (this.leftLayoutWeight < 0.0f) {
            this.leftLayoutWeight = 0.0f;
        }
        if (this.rightLayoutWeight < 0.0f) {
            this.rightLayoutWeight = 0.0f;
        }
        if (this.leftLayoutWeight == 0.0f && this.rightLayoutWeight == 0.0f) {
            this.leftLayoutWeight = 1.0f;
            this.rightLayoutWeight = 1.0f;
        }
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.left = findViewById(2131625149);
        this.right = findViewById(2131625150);
        this.shadow = findViewById(2131625151);
        this.topContainer = findViewById(2131625148);
        this.divider = findViewById(2131624602);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        this.topContentHeight = this.topContainer.getMeasuredHeight() + this.divider.getMeasuredHeight();
        int leftWidth = (int) Math.floor((double) ((((float) width) * this.leftLayoutWeight) / (this.leftLayoutWeight + this.rightLayoutWeight)));
        this.left.measure(MeasureSpec.makeMeasureSpec(leftWidth, 1073741824), MeasureSpec.makeMeasureSpec(getMeasuredHeight() - this.topContentHeight, 1073741824));
        this.right.measure(MeasureSpec.makeMeasureSpec(width - leftWidth, 1073741824), MeasureSpec.makeMeasureSpec(getMeasuredHeight() - this.topContentHeight, 1073741824));
        measureChild(this.shadow, widthMeasureSpec, MeasureSpec.makeMeasureSpec(getMeasuredHeight() - this.topContentHeight, 1073741824));
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int width = r - l;
        int height = b - t;
        this.left.layout(0, this.topContentHeight, this.left.getMeasuredWidth(), height);
        this.right.layout(this.left.getMeasuredWidth(), this.topContentHeight, width, height);
        if (((FragmentActivity) getContext()).getSupportFragmentManager().findFragmentById(2131625149) != null) {
            this.shadow.layout(this.left.getMeasuredWidth(), this.topContentHeight, this.left.getMeasuredWidth() + this.shadow.getMeasuredWidth(), height);
        }
    }
}
