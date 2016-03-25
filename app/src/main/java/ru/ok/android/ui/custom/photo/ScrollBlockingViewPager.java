package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.util.AttributeSet;

public class ScrollBlockingViewPager extends StableViewPager {
    private boolean blockScrollToLeft;
    private boolean blockScrollToRight;
    private BlockingViewPagerListener listener;

    public interface BlockingViewPagerListener {
        boolean shouldNavigateToPosition(int i);
    }

    public ScrollBlockingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollBlockingViewPager(Context context) {
        super(context);
    }

    public void scrollTo(int x, int y) {
        int scrollOffset = x - getScrollX();
        if (scrollOffset > 0 && this.blockScrollToLeft) {
            return;
        }
        if (scrollOffset >= 0 || !this.blockScrollToRight) {
            super.scrollTo(x, y);
        }
    }

    protected void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
        int toNavigateTo = item;
        if (!(this.listener == null || this.listener.shouldNavigateToPosition(toNavigateTo))) {
            toNavigateTo = getCurrentItem();
        }
        super.setCurrentItemInternal(toNavigateTo, smoothScroll, always);
    }

    protected void setCurrentItemInternal(int item, boolean smoothScroll, boolean always, int velocity) {
        int toNavigateTo = item;
        if (!(this.listener == null || this.listener.shouldNavigateToPosition(toNavigateTo))) {
            toNavigateTo = getCurrentItem();
        }
        super.setCurrentItemInternal(toNavigateTo, smoothScroll, always, velocity);
    }

    public void setBlockScrollToLeft(boolean blockScrollToLeft) {
        this.blockScrollToLeft = blockScrollToLeft;
    }

    public void setBlockScrollToRight(boolean blockScrollToRight) {
        this.blockScrollToRight = blockScrollToRight;
    }

    public final void setBlockingViewPagerListener(BlockingViewPagerListener listener) {
        this.listener = listener;
    }
}
