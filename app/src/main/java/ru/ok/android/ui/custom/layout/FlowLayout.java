package ru.ok.android.ui.custom.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.C0206R;
import ru.ok.android.proto.MessagesProto.Message;

@TargetApi(14)
public class FlowLayout extends ViewGroup {
    private int mGravity;
    private final List<Integer> mLineHeights;
    private final List<Integer> mLineMargins;
    private final List<List<View>> mLines;

    public static class LayoutParams extends MarginLayoutParams {
        public int gravity;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.gravity = -1;
            TypedArray a = c.obtainStyledAttributes(attrs, C0206R.styleable.FlowLayout_Layout);
            try {
                this.gravity = a.getInt(0, -1);
            } finally {
                a.recycle();
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.gravity = -1;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
            this.gravity = -1;
        }
    }

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mGravity = (isIcs() ? GravityCompat.START : 3) | 48;
        this.mLines = new ArrayList();
        this.mLineHeights = new ArrayList();
        this.mLineMargins = new ArrayList();
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.FlowLayout, defStyle, 0);
        try {
            int index = a.getInt(0, -1);
            if (index > 0) {
                setGravity(index);
            }
            a.recycle();
        } catch (Throwable th) {
            a.recycle();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = (MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()) - getPaddingRight();
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int width = 0;
        int height = getPaddingTop() + getPaddingBottom();
        int lineWidth = 0;
        int lineHeight = 0;
        int childCount = getChildCount();
        int i = 0;
        while (i < childCount) {
            View child = getChildAt(i);
            boolean lastChild = i == childCount + -1;
            if (child.getVisibility() != 8) {
                measureChildWithMargins(child, widthMeasureSpec, lineWidth, heightMeasureSpec, height);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int childWidthMode = LinearLayoutManager.INVALID_OFFSET;
                int childWidthSize = sizeWidth;
                int childHeightMode = LinearLayoutManager.INVALID_OFFSET;
                int childHeightSize = sizeHeight;
                if (lp.width == -1) {
                    childWidthMode = 1073741824;
                    childWidthSize -= lp.leftMargin + lp.rightMargin;
                } else if (lp.width >= 0) {
                    childWidthMode = 1073741824;
                    childWidthSize = lp.width;
                }
                if (lp.height >= 0) {
                    childHeightMode = 1073741824;
                    childHeightSize = lp.height;
                } else if (modeHeight == 0) {
                    childHeightMode = 0;
                    childHeightSize = 0;
                }
                child.measure(MeasureSpec.makeMeasureSpec(childWidthSize, childWidthMode), MeasureSpec.makeMeasureSpec(childHeightSize, childHeightMode));
                int childWidth = (child.getMeasuredWidth() + lp.leftMargin) + lp.rightMargin;
                if (lineWidth + childWidth > sizeWidth) {
                    width = Math.max(width, lineWidth);
                    lineWidth = childWidth;
                    height += lineHeight;
                    lineHeight = (child.getMeasuredHeight() + lp.topMargin) + lp.bottomMargin;
                } else {
                    lineWidth += childWidth;
                    lineHeight = Math.max(lineHeight, (child.getMeasuredHeight() + lp.topMargin) + lp.bottomMargin);
                }
                if (lastChild) {
                    width = Math.max(width, lineWidth);
                    height += lineHeight;
                }
            } else if (lastChild) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
            i++;
        }
        width += getPaddingLeft() + getPaddingRight();
        if (modeWidth != 1073741824) {
            sizeWidth = width;
        }
        if (modeHeight != 1073741824) {
            sizeHeight = height;
        }
        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        float horizontalGravityFactor;
        int i;
        this.mLines.clear();
        this.mLineHeights.clear();
        this.mLineMargins.clear();
        int width = getWidth();
        int height = getHeight();
        int linesSum = getPaddingTop();
        int lineWidth = 0;
        int lineHeight = 0;
        List<View> lineViews = new ArrayList();
        switch (this.mGravity & 7) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                horizontalGravityFactor = 0.5f;
                break;
            case Message.UUID_FIELD_NUMBER /*5*/:
                horizontalGravityFactor = 1.0f;
                break;
            default:
                horizontalGravityFactor = 0.0f;
                break;
        }
        for (i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                int childWidth;
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int i2 = lp.leftMargin;
                childWidth = (child.getMeasuredWidth() + r0) + lp.rightMargin;
                i2 = lp.bottomMargin;
                int childHeight = (child.getMeasuredHeight() + r0) + lp.topMargin;
                if (lineWidth + childWidth > width) {
                    this.mLineHeights.add(Integer.valueOf(lineHeight));
                    this.mLines.add(lineViews);
                    this.mLineMargins.add(Integer.valueOf(((int) (((float) (width - lineWidth)) * horizontalGravityFactor)) + getPaddingLeft()));
                    linesSum += lineHeight;
                    lineHeight = 0;
                    lineWidth = 0;
                    lineViews = new ArrayList();
                }
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
                lineViews.add(child);
            }
        }
        this.mLineHeights.add(Integer.valueOf(lineHeight));
        this.mLines.add(lineViews);
        this.mLineMargins.add(Integer.valueOf(((int) (((float) (width - lineWidth)) * horizontalGravityFactor)) + getPaddingLeft()));
        linesSum += lineHeight;
        int verticalGravityMargin = 0;
        switch (this.mGravity & 112) {
            case C0206R.styleable.Toolbar_titleMarginBottom /*16*/:
                verticalGravityMargin = (height - linesSum) / 2;
                break;
            case C0206R.styleable.Theme_listChoiceBackgroundIndicator /*80*/:
                verticalGravityMargin = height - linesSum;
                break;
        }
        int numLines = this.mLines.size();
        int top = getPaddingTop();
        for (i = 0; i < numLines; i++) {
            lineHeight = ((Integer) this.mLineHeights.get(i)).intValue();
            lineViews = (List) this.mLines.get(i);
            int left = ((Integer) this.mLineMargins.get(i)).intValue();
            int children = lineViews.size();
            for (int j = 0; j < children; j++) {
                child = (View) lineViews.get(j);
                if (child.getVisibility() != 8) {
                    lp = (LayoutParams) child.getLayoutParams();
                    int i3 = lp.height;
                    if (r0 == -1) {
                        int childWidthMode = LinearLayoutManager.INVALID_OFFSET;
                        int childWidthSize = lineWidth;
                        i3 = lp.width;
                        if (r0 == -1) {
                            childWidthMode = 1073741824;
                        } else if (lp.width >= 0) {
                            childWidthMode = 1073741824;
                            childWidthSize = lp.width;
                        }
                        i3 = MeasureSpec.makeMeasureSpec(childWidthSize, childWidthMode);
                        i2 = lp.topMargin;
                        child.measure(i3, MeasureSpec.makeMeasureSpec((lineHeight - r0) - lp.bottomMargin, 1073741824));
                    }
                    childWidth = child.getMeasuredWidth();
                    childHeight = child.getMeasuredHeight();
                    int gravityMargin = 0;
                    if (Gravity.isVertical(lp.gravity)) {
                        switch (lp.gravity) {
                            case C0206R.styleable.Toolbar_titleMarginBottom /*16*/:
                            case C0206R.styleable.Toolbar_maxButtonHeight /*17*/:
                                i2 = lp.topMargin;
                                gravityMargin = (((lineHeight - childHeight) - r0) - lp.bottomMargin) / 2;
                                break;
                            case C0206R.styleable.Theme_listChoiceBackgroundIndicator /*80*/:
                                i2 = lp.topMargin;
                                gravityMargin = ((lineHeight - childHeight) - r0) - lp.bottomMargin;
                                break;
                        }
                    }
                    child.layout(lp.leftMargin + left, ((lp.topMargin + top) + gravityMargin) + verticalGravityMargin, (left + childWidth) + lp.leftMargin, (((top + childHeight) + lp.topMargin) + gravityMargin) + verticalGravityMargin);
                    i3 = lp.leftMargin;
                    left += (r0 + childWidth) + lp.rightMargin;
                }
            }
            top += lineHeight;
        }
    }

    protected LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -1);
    }

    @TargetApi(14)
    public void setGravity(int gravity) {
        if (this.mGravity != gravity) {
            if ((GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK & gravity) == 0) {
                gravity |= isIcs() ? GravityCompat.START : 3;
            }
            if ((gravity & 112) == 0) {
                gravity |= 48;
            }
            this.mGravity = gravity;
            requestLayout();
        }
    }

    public int getGravity() {
        return this.mGravity;
    }

    private static boolean isIcs() {
        return VERSION.SDK_INT >= 14;
    }
}
