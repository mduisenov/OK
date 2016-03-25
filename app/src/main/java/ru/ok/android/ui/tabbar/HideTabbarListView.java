package ru.ok.android.ui.tabbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import ru.ok.android.ui.WorkaroundListView;
import ru.ok.android.ui.custom.scroll.ScrollListenerSet;

public class HideTabbarListView extends WorkaroundListView {
    protected HideTabbarListener hideTabbarListener;
    protected OnScrollListener onScrollListener;
    private OnTouchListener toolbarListener;

    public HideTabbarListView(Context context) {
        super(context);
        this.hideTabbarListener = new HideTabbarListener(context);
    }

    public HideTabbarListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.hideTabbarListener = new HideTabbarListener(context);
    }

    public HideTabbarListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.hideTabbarListener = new HideTabbarListener(context);
    }

    public void setToolbarListener(OnTouchListener listener) {
        this.toolbarListener = listener;
    }

    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        updateScrollListener();
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (this.toolbarListener != null) {
            this.toolbarListener.onTouch(this, ev);
        }
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
