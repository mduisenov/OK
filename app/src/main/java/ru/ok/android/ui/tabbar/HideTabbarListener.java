package ru.ok.android.ui.tabbar;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.custom.scroll.BaseRecyclerScrollListener;
import ru.ok.android.ui.custom.scroll.BaseScrollListener;
import ru.ok.android.ui.custom.scroll.DeltaListScrollListener;
import ru.ok.android.ui.tabbar.manager.BaseTabbarManager;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;

public class HideTabbarListener implements DeltaListScrollListener {
    private static final Point DEFAULT_Y;
    private final BaseTabbarManager activity;
    private ObjectAnimator animator;
    private final Context context;
    private final int deltaThreshold;
    private boolean isStartTouch;
    private Point last;
    private Point preLast;

    public static final class DeltaRecyclerScrollListener extends BaseRecyclerScrollListener {
        public DeltaRecyclerScrollListener(HideTabbarListener listener, Adapter adapter) {
            super(listener);
            if (adapter != null) {
                adapter.registerAdapterDataObserver(getDataSetObserver());
            }
        }
    }

    public static final class DeltaScrollListener extends BaseScrollListener {
        public DeltaScrollListener(HideTabbarListener listener, ListAdapter adapter) {
            super(listener);
            if (adapter != null) {
                adapter.registerDataSetObserver(this);
            }
        }
    }

    static {
        DEFAULT_Y = new Point(0, 0);
    }

    public HideTabbarListener(Context context) {
        BaseTabbarManager baseTabbarManager = null;
        this.last = DEFAULT_Y;
        this.preLast = DEFAULT_Y;
        this.isStartTouch = false;
        this.animator = null;
        this.context = context;
        if (BaseCompatToolbarActivity.isUseTabbar(context)) {
            baseTabbarManager = (BaseTabbarManager) context;
        }
        this.activity = baseTabbarManager;
        this.deltaThreshold = (int) TypedValue.applyDimension(1, 8.0f, context.getResources().getDisplayMetrics());
    }

    public void onTouchEvent(MotionEvent event) {
        if (this.isStartTouch && ((float) this.last.y) != event.getY() && (this.last == DEFAULT_Y || Math.abs(((float) this.last.y) - event.getY()) > ((float) this.deltaThreshold))) {
            this.preLast = this.last;
            this.last = new Point((int) event.getX(), (int) event.getY());
        }
        switch (event.getAction()) {
            case RECEIVED_VALUE:
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                onTouchEvent(true);
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                onTouchEvent(false);
            default:
        }
    }

    private int getDeltaY() {
        if (this.last == DEFAULT_Y || this.preLast == DEFAULT_Y) {
            return 0;
        }
        return Math.abs(this.last.y - this.preLast.y);
    }

    private int getDeltaX() {
        if (this.last == DEFAULT_Y || this.preLast == DEFAULT_Y) {
            return 0;
        }
        return Math.abs(this.last.x - this.preLast.x);
    }

    public void onTouchEvent(boolean isStartTouch) {
        int to = 0;
        if (this.activity != null && this.isStartTouch != isStartTouch) {
            this.isStartTouch = isStartTouch;
            if (isStartTouch) {
                this.last = DEFAULT_Y;
                this.preLast = DEFAULT_Y;
                stopAnimator();
                return;
            }
            if (getDeltaX() > getDeltaY()) {
                if (getScrollTabbar() > getTabbarH() / 2) {
                    to = getTabbarH();
                }
            } else if (!(this.last == DEFAULT_Y || this.preLast == DEFAULT_Y || this.last.y >= this.preLast.y)) {
                to = getTabbarH();
            }
            animateTo(to);
        }
    }

    private int getTabbarH() {
        View view = this.activity == null ? null : this.activity.getTabbarView();
        return view == null ? 0 : view.getHeight();
    }

    public void onListScroll(int deltaY, int firstVisisbleItem, int invisibleItemCount, int visibleItemCount) {
        onDeltaScroll(deltaY);
    }

    public void onScroll(int deltaY, int top, int maxScroll, int height) {
        onDeltaScroll(deltaY);
    }

    public void onDeltaScroll(int deltaY) {
        if (this.activity != null && deltaY != 0 && DeviceUtils.getType(this.context) == DeviceLayoutType.SMALL) {
            int scroll = this.activity.getScrollTabbar() - deltaY;
            if (this.isStartTouch) {
                stopAnimator();
                this.activity.setScrollTabbar((float) scroll);
            }
        }
    }

    private int getScrollTabbar() {
        return this.activity != null ? this.activity.getScrollTabbar() : 0;
    }

    public void stopAnimator() {
        if (this.animator != null) {
            this.animator.cancel();
            this.animator = null;
        }
    }

    public static ObjectAnimator animateTo(BaseTabbarManager activity, int to) {
        if (activity == null) {
            return null;
        }
        OdklTabbar toolbar = activity.getTabbarView();
        if (toolbar == null) {
            return null;
        }
        int size = Math.abs(to - activity.getScrollTabbar());
        int h = toolbar.getHeight();
        float duration = h == 0 ? 0.0f : (200.0f * ((float) size)) / ((float) h);
        ObjectAnimator animator = ObjectAnimator.ofFloat(toolbar, "translationY", new float[]{(float) to});
        animator.setDuration((long) ((int) duration));
        animator.start();
        return animator;
    }

    private void animateTo(int to) {
        this.animator = animateTo(this.activity, to);
    }

    public static BaseScrollListener create(ListAdapter listAdapter, HideTabbarListener listener) {
        return new DeltaScrollListener(listener, listAdapter);
    }

    public static BaseRecyclerScrollListener create(Adapter recyclerAdapter, HideTabbarListener listener) {
        return new DeltaRecyclerScrollListener(listener, recyclerAdapter);
    }
}
