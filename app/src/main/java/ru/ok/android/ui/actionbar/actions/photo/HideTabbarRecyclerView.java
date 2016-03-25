package ru.ok.android.ui.actionbar.actions.photo;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import ru.ok.android.ui.RecyclerViewSizeListenable;
import ru.ok.android.ui.custom.scroll.ScrollListenerRecyclerSet;
import ru.ok.android.ui.tabbar.HideTabbarListener;

public class HideTabbarRecyclerView extends RecyclerViewSizeListenable {
    protected HideTabbarListener hideTabbarListener;
    protected OnScrollListener onScrollListener;
    private OnTouchListener toolbarListener;

    public HideTabbarRecyclerView(Context context) {
        this(context, null);
    }

    public HideTabbarRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HideTabbarRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.hideTabbarListener = new HideTabbarListener(context);
    }

    public void setToolbarListener(OnTouchListener listener) {
        this.toolbarListener = listener;
    }

    public void setAdapter(Adapter adapter) {
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
        ScrollListenerRecyclerSet scrollListenerSet = new ScrollListenerRecyclerSet();
        if (this.onScrollListener != null) {
            scrollListenerSet.addListener(this.onScrollListener);
        }
        scrollListenerSet.addListener(HideTabbarListener.create(getAdapter(), this.hideTabbarListener));
        super.setOnScrollListener(scrollListenerSet);
    }
}
