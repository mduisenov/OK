package ru.ok.android.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import ru.ok.android.C0206R;

public class BubbleTextIcon extends FrameLayout {
    private TextView bubbleText;
    private Drawable iconSrc;
    private Drawable iconSrcSelect;
    private ImageView iconView;
    private final ArrayList<View> mMatchParentChildren;

    public BubbleTextIcon(Context context) {
        this(context, null);
    }

    public BubbleTextIcon(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public BubbleTextIcon(Context context, AttributeSet attrs, int defAttr, int defStyle) {
        ArrayList arrayList;
        super(context, attrs, defAttr);
        if (VERSION.SDK_INT < 11) {
            arrayList = new ArrayList();
        } else {
            arrayList = null;
        }
        this.mMatchParentChildren = arrayList;
        LayoutInflater.from(context).inflate(2130903116, this);
        this.bubbleText = (TextView) findViewById(2131624656);
        this.iconView = (ImageView) findViewById(2131624655);
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.BubbleTextIcon, defAttr, defStyle);
        this.iconSrc = a.getDrawable(1);
        this.iconSrcSelect = a.getDrawable(2);
        this.iconView.setImageDrawable(this.iconSrc);
        LayoutParams iconLp = (LayoutParams) this.iconView.getLayoutParams();
        if (iconLp == null) {
            iconLp = new LayoutParams(-2, -2);
            this.iconView.setLayoutParams(iconLp);
        }
        int dimensionPixelOffset = a.getDimensionPixelOffset(4, 0);
        iconLp.bottomMargin = dimensionPixelOffset;
        iconLp.topMargin = dimensionPixelOffset;
        iconLp.rightMargin = dimensionPixelOffset;
        iconLp.leftMargin = dimensionPixelOffset;
        this.bubbleText.setBackgroundDrawable(a.getDrawable(3));
        LayoutParams bubbleLp = (LayoutParams) this.bubbleText.getLayoutParams();
        if (bubbleLp == null) {
            bubbleLp = new LayoutParams(-2, -2);
            this.bubbleText.setLayoutParams(bubbleLp);
        }
        bubbleLp.gravity = a.getInt(0, 0);
        int margin = a.getDimensionPixelOffset(5, 0);
        bubbleLp.leftMargin = a.getDimensionPixelOffset(6, margin);
        bubbleLp.rightMargin = a.getDimensionPixelOffset(7, margin);
        bubbleLp.topMargin = a.getDimensionPixelOffset(8, margin);
        bubbleLp.bottomMargin = a.getDimensionPixelOffset(9, margin);
        int bubbleTextAppearanceStyleResId = a.getResourceId(10, 0);
        if (bubbleTextAppearanceStyleResId != 0) {
            this.bubbleText.setTextAppearance(context, bubbleTextAppearanceStyleResId);
        }
        a.recycle();
    }

    public void showBubble() {
        this.iconView.setImageDrawable(this.iconSrcSelect);
        this.bubbleText.setVisibility(0);
    }

    public void hideBubble() {
        this.iconView.setImageDrawable(this.iconSrc);
        this.bubbleText.setVisibility(4);
    }

    public void setBubbleText(CharSequence text) {
        this.bubbleText.setText(text);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (VERSION.SDK_INT < 11) {
            onMeasureForAndroid2_x(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void onMeasureForAndroid2_x(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        int count = getChildCount();
        boolean measureMatchParentChildren = (MeasureSpec.getMode(widthMeasureSpec) == 1073741824 && MeasureSpec.getMode(heightMeasureSpec) == 1073741824) ? false : true;
        this.mMatchParentChildren.clear();
        int maxHeight = 0;
        int maxWidth = 0;
        for (i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                maxWidth = Math.max(maxWidth, (child.getMeasuredWidth() + lp.leftMargin) + lp.rightMargin);
                maxHeight = Math.max(maxHeight, (child.getMeasuredHeight() + lp.topMargin) + lp.bottomMargin);
                if (measureMatchParentChildren && (lp.width == -1 || lp.height == -1)) {
                    this.mMatchParentChildren.add(child);
                }
            }
        }
        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight = Math.max(maxHeight + (getPaddingTop() + getPaddingBottom()), getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
        Drawable drawable = getForeground();
        if (drawable != null) {
            maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
            maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
        }
        setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec), resolveSize(maxHeight, heightMeasureSpec));
        count = this.mMatchParentChildren.size();
        if (count > 1) {
            for (i = 0; i < count; i++) {
                int childWidthMeasureSpec;
                int childHeightMeasureSpec;
                child = (View) this.mMatchParentChildren.get(i);
                MarginLayoutParams lp2 = (MarginLayoutParams) child.getLayoutParams();
                if (lp2.width == -1) {
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec((getMeasuredWidth() - lp2.leftMargin) - lp2.rightMargin, 1073741824);
                } else {
                    childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, lp2.leftMargin + lp2.rightMargin, lp2.width);
                }
                if (lp2.height == -1) {
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec((getMeasuredHeight() - lp2.topMargin) - lp2.bottomMargin, 1073741824);
                } else {
                    childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, lp2.topMargin + lp2.bottomMargin, lp2.height);
                }
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }
}
