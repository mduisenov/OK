package ru.ok.android.ui.custom.animationlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.tabbar.HideTabbarListView;
import ru.ok.model.stream.Feed;

public class StreamListView extends HideTabbarListView {
    boolean isInLayout;
    boolean isInsideChildDrawableStateChanged;
    private int layoutHeight;
    private int layoutWidth;
    private OnSizeChangedListener onSizeChangedListener;

    public interface OnSizeChangedListener {
        void onSizeChanged(int i, int i2, int i3, int i4);
    }

    public StreamListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isInsideChildDrawableStateChanged = false;
    }

    protected void layoutChildren() {
        this.isInLayout = true;
        super.layoutChildren();
        this.isInLayout = false;
    }

    public void childDrawableStateChanged(View child) {
        if (!this.isInsideChildDrawableStateChanged && !this.isInLayout) {
            super.childDrawableStateChanged(child);
            Boolean sharePressedState = (Boolean) child.getTag(2131624340);
            if (sharePressedState != null && sharePressedState.booleanValue()) {
                Object holder = child.getTag();
                if (holder instanceof ViewHolder) {
                    Feed feed = ((ViewHolder) holder).feed;
                    if (feed != null) {
                        boolean isPressed = child.isPressed();
                        int childCount = getChildCount();
                        this.isInsideChildDrawableStateChanged = true;
                        for (int i = 0; i < childCount; i++) {
                            View view = getChildAt(i);
                            if (view != child) {
                                holder = view.getTag();
                                if ((holder instanceof ViewHolder) && ((ViewHolder) holder).feed == feed) {
                                    view.setPressed(isPressed);
                                }
                            }
                        }
                        this.isInsideChildDrawableStateChanged = false;
                    }
                }
            }
        }
    }

    public void setOnSizeChangedListener(OnSizeChangedListener listener) {
        this.onSizeChangedListener = listener;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        if (!(width == this.layoutWidth && height == this.layoutHeight)) {
            if (this.onSizeChangedListener != null) {
                this.onSizeChangedListener.onSizeChanged(width, height, this.layoutWidth, this.layoutHeight);
            }
            this.layoutWidth = width;
            this.layoutHeight = height;
        }
        super.onLayout(changed, l, t, r, b);
    }
}
