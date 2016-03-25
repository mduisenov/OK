package ru.ok.android.ui.tabbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ListAdapter;
import ru.ok.android.ui.custom.scroll.ScrollListenerSet;

public class HideTabbarGridView extends GridView {
    protected HideTabbarListener hideTabbarListener;
    protected OnScrollListener onScrollListener;

    public HideTabbarGridView(Context context) {
        super(context);
        this.hideTabbarListener = null;
        this.onScrollListener = null;
        this.hideTabbarListener = new HideTabbarListener(context);
    }

    public HideTabbarGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.hideTabbarListener = null;
        this.onScrollListener = null;
        this.hideTabbarListener = new HideTabbarListener(context);
    }

    public HideTabbarGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.hideTabbarListener = null;
        this.onScrollListener = null;
        this.hideTabbarListener = new HideTabbarListener(context);
    }

    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        updateScrollListener();
    }

    public boolean onTouchEvent(MotionEvent ev) {
        this.hideTabbarListener.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    public void setOnScrollListener(OnScrollListener listener) {
        this.onScrollListener = listener;
        updateScrollListener();
    }

    private void updateScrollListener() {
        ScrollListenerSet scrollListenerSet = new ScrollListenerSet();
        if (this.onScrollListener != null) {
            scrollListenerSet.addListener(this.onScrollListener);
        }
        scrollListenerSet.addListener(HideTabbarListener.create(getAdapter(), this.hideTabbarListener));
        super.setOnScrollListener(scrollListenerSet);
    }
}
