package ru.ok.android.ui.custom.profiles;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;

public class VerticalProfileItemsView extends AdapterView<ListAdapter> {
    private ListAdapter mAdapter;
    private DataSetObserver mDataObserver;
    private GestureDetector mGesture;
    private OnGestureListener mOnGesture;
    private OnItemClickListener mOnItemClicked;
    private OnItemLongClickListener mOnItemLongClicked;
    private int mTotalLength;

    /* renamed from: ru.ok.android.ui.custom.profiles.VerticalProfileItemsView.1 */
    class C07471 extends SimpleOnGestureListener {
        C07471() {
        }

        public boolean onDown(MotionEvent event) {
            for (int i = 0; i < VerticalProfileItemsView.this.getChildCount(); i++) {
                View child = VerticalProfileItemsView.this.getChildAt(i);
                if (VerticalProfileItemsView.this.isEventWithinView(event, child)) {
                    child.setPressed(true);
                    break;
                }
            }
            return true;
        }

        public boolean onSingleTapUp(MotionEvent event) {
            for (int i = 0; i < VerticalProfileItemsView.this.getChildCount(); i++) {
                VerticalProfileItemsView.this.getChildAt(i).setPressed(false);
            }
            return false;
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            for (int i = 0; i < VerticalProfileItemsView.this.getChildCount(); i++) {
                View child = VerticalProfileItemsView.this.getChildAt(i);
                if (VerticalProfileItemsView.this.isEventWithinView(e, child)) {
                    child.setPressed(false);
                    if (VerticalProfileItemsView.this.mOnItemClicked != null) {
                        VerticalProfileItemsView.this.mOnItemClicked.onItemClick(VerticalProfileItemsView.this, child, i, VerticalProfileItemsView.this.mAdapter.getItemId(i + 1));
                    }
                    return false;
                }
            }
            return false;
        }

        public void onLongPress(MotionEvent e) {
            int childCount = VerticalProfileItemsView.this.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = VerticalProfileItemsView.this.getChildAt(i);
                if (VerticalProfileItemsView.this.isEventWithinView(e, child)) {
                    child.setPressed(false);
                    if (VerticalProfileItemsView.this.mOnItemLongClicked != null) {
                        VerticalProfileItemsView.this.mOnItemLongClicked.onItemLongClick(VerticalProfileItemsView.this, child, i, VerticalProfileItemsView.this.mAdapter.getItemId(i + 1));
                        return;
                    }
                    return;
                }
            }
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            for (int i = 0; i < VerticalProfileItemsView.this.getChildCount(); i++) {
                VerticalProfileItemsView.this.getChildAt(i).setPressed(false);
            }
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.custom.profiles.VerticalProfileItemsView.2 */
    class C07482 extends DataSetObserver {
        C07482() {
        }

        public void onChanged() {
            VerticalProfileItemsView.this.invalidate();
            VerticalProfileItemsView.this.requestLayout();
        }

        public void onInvalidated() {
            VerticalProfileItemsView.this.reset();
            VerticalProfileItemsView.this.invalidate();
            VerticalProfileItemsView.this.requestLayout();
        }
    }

    public VerticalProfileItemsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mOnGesture = new C07471();
        this.mDataObserver = new C07482();
        initThings();
    }

    public VerticalProfileItemsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mOnGesture = new C07471();
        this.mDataObserver = new C07482();
        initThings();
    }

    private void initThings() {
        this.mGesture = new GestureDetector(getContext(), this.mOnGesture);
    }

    public ListAdapter getAdapter() {
        return this.mAdapter;
    }

    public void setAdapter(ListAdapter adapter) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mDataObserver);
        }
        this.mAdapter = adapter;
        this.mAdapter.registerDataSetObserver(this.mDataObserver);
        reset();
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        removeAllViewsInLayout();
        if (this.mAdapter != null) {
            int childTop = 0;
            for (int position = 0; position < this.mAdapter.getCount(); position++) {
                View child = this.mAdapter.getView(position, null, this);
                addAndMeasureChild(child);
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                int left = (getWidth() - width) / 2;
                child.layout(left, childTop, left + width, childTop + height);
                childTop += height;
            }
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.mTotalLength = 0;
        int count = this.mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (!(child == null || child.getVisibility() == 8)) {
                child.measure(1073741824, 0);
                this.mTotalLength += child.getMeasuredHeight();
            }
        }
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), this.mTotalLength + 10);
    }

    private void addAndMeasureChild(View child) {
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(-2, -2);
        }
        addViewInLayout(child, -1, params, true);
        child.measure(1073741824 | getWidth(), 0);
    }

    public View getSelectedView() {
        return null;
    }

    public void setSelection(int position) {
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClicked = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClicked = listener;
    }

    private synchronized void reset() {
        initThings();
        removeAllViewsInLayout();
        requestLayout();
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            requestDisallowInterceptTouchEvent(false);
        } else if (ev.getAction() == 1 || ev.getAction() == 3) {
            for (int i = 0; i < getChildCount(); i++) {
                getChildAt(i).setPressed(false);
            }
            requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev) | this.mGesture.onTouchEvent(ev);
    }

    private boolean isEventWithinView(MotionEvent e, View child) {
        Rect viewRect = new Rect();
        int[] childPosition = new int[2];
        child.getLocationOnScreen(childPosition);
        int left = childPosition[0];
        int right = left + child.getWidth();
        int top = childPosition[1];
        viewRect.set(left, top, right, top + child.getHeight());
        return viewRect.contains((int) e.getRawX(), (int) e.getRawY());
    }
}
