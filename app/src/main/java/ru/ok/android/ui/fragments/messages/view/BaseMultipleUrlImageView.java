package ru.ok.android.ui.fragments.messages.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.utils.Logger;

public abstract class BaseMultipleUrlImageView extends ViewGroup {
    protected HandleBlocker blocker;
    private final int innerPadding;
    protected Boolean multiple;
    protected List<UrlImageView> urlImageViews;

    protected abstract void processConfig();

    public BaseMultipleUrlImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.innerPadding = (int) TypedValue.applyDimension(1, 1.0f, getResources().getDisplayMetrics());
    }

    protected void configureForMode(boolean isMultipleLayout) {
        if (isMultipleLayout) {
            for (int i = 0; i < 4; i++) {
                addView(popUrlImageView());
            }
            return;
        }
        addView(popUrlImageView());
    }

    public void configure(boolean isMultipleLayout) {
        if (this.multiple == null || isMultipleLayout != this.multiple.booleanValue()) {
            clearCurrentViews();
            Boolean valueOf = Boolean.valueOf(isMultipleLayout);
            this.multiple = valueOf;
            configureForMode(valueOf.booleanValue());
        }
        processConfig();
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int visibleChildren = getVisibleChildrenCount();
        int width = r - l;
        int height = b - t;
        int leftEdge = getPaddingLeft();
        int topEdge = getPaddingTop();
        int rightEdge = width - getPaddingRight();
        int bottomEdge = height - getPaddingBottom();
        int columnWidth = (((width - getPaddingLeft()) - getPaddingRight()) - this.innerPadding) / 2;
        int rowHeight = (((height - getPaddingTop()) - getPaddingBottom()) - this.innerPadding) / 2;
        int secondColumnStart = (leftEdge + columnWidth) + this.innerPadding;
        int secondRowStart = (topEdge + rowHeight) + this.innerPadding;
        View view1;
        View view2;
        View view3;
        switch (visibleChildren) {
            case RECEIVED_VALUE:
            case Message.TEXT_FIELD_NUMBER /*1*/:
                int right;
                int bottom;
                View view = getChildAt(0);
                if (this.multiple.booleanValue()) {
                    right = leftEdge + columnWidth;
                } else {
                    right = rightEdge;
                }
                if (this.multiple.booleanValue()) {
                    bottom = topEdge + rowHeight;
                } else {
                    bottom = bottomEdge;
                }
                view.layout(leftEdge, topEdge, right, bottom);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                view1 = getChildAt(0);
                view2 = getChildAt(1);
                view1.layout(leftEdge, topEdge, leftEdge + columnWidth, bottomEdge);
                view2.layout(secondColumnStart, topEdge, rightEdge, bottomEdge);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                view1 = getChildAt(0);
                view2 = getChildAt(1);
                view3 = getChildAt(2);
                view1.layout(leftEdge, topEdge, leftEdge + columnWidth, bottomEdge);
                view2.layout(secondColumnStart, topEdge, rightEdge, topEdge + rowHeight);
                view3.layout(secondColumnStart, secondRowStart, rightEdge, bottomEdge);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                view1 = getChildAt(0);
                view2 = getChildAt(1);
                view3 = getChildAt(2);
                View view4 = getChildAt(3);
                columnWidth = (((width - getPaddingLeft()) - getPaddingRight()) - this.innerPadding) / 2;
                rowHeight = (((height - getPaddingTop()) - getPaddingBottom()) - this.innerPadding) / 2;
                view1.layout(leftEdge, topEdge, leftEdge + columnWidth, topEdge + rowHeight);
                view2.layout(secondColumnStart, topEdge, rightEdge, topEdge + rowHeight);
                view3.layout(leftEdge, secondRowStart, leftEdge + columnWidth, bottomEdge);
                view4.layout(secondColumnStart, secondRowStart, rightEdge, bottomEdge);
            default:
                Logger.m177e("Unexpected visible items count: %d", Integer.valueOf(visibleChildren));
        }
    }

    protected int getVisibleChildrenCount() {
        int visibleChildren = 0;
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).getVisibility() == 0) {
                visibleChildren++;
            }
        }
        return visibleChildren;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        int i2 = 2;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        if (this.multiple.booleanValue()) {
            i = 2;
        } else {
            i = 1;
        }
        int widthSpec = MeasureSpec.makeMeasureSpec(width / i, 1073741824);
        if (!this.multiple.booleanValue()) {
            i2 = 1;
        }
        int heightSpec = MeasureSpec.makeMeasureSpec(height / i2, 1073741824);
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            getChildAt(i3).measure(widthSpec, heightSpec);
        }
    }

    protected List<UrlImageView> getUrlImageViews() {
        if (this.urlImageViews == null) {
            this.urlImageViews = new ArrayList();
        }
        return this.urlImageViews;
    }

    protected void clearCurrentViews() {
        while (getChildCount() > 0) {
            View child = getChildAt(0);
            clearView(child);
            removeView(child);
        }
    }

    protected void clearView(View child) {
        UrlImageView image = (UrlImageView) child;
        image.setUrl(null);
        getUrlImageViews().add(image);
    }

    protected UrlImageView popUrlImageView() {
        if (this.urlImageViews != null && !this.urlImageViews.isEmpty()) {
            return (UrlImageView) this.urlImageViews.remove(this.urlImageViews.size() - 1);
        }
        UrlImageView result = new UrlImageView(getContext());
        result.setScaleType(ScaleType.CENTER_CROP);
        if (getDefaultImgRes() > 0) {
            result.setImageResource(getDefaultImgRes());
        }
        return result;
    }

    public int getDefaultImgRes() {
        return -1;
    }

    public void setImageBlocker(HandleBlocker blocker) {
        this.blocker = blocker;
    }
}
