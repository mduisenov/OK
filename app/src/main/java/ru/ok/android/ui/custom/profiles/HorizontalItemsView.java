package ru.ok.android.ui.custom.profiles;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import ru.mail.libverify.C0176R;
import ru.ok.android.ui.base.profile.ProfileSectionItem;
import ru.ok.android.ui.users.fragments.data.ProfileSectionsAdapter;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;

public class HorizontalItemsView extends AdapterView<ListAdapter> implements OnItemClickListener {
    protected ListAdapter mAdapter;
    private final DataSetObserver mDataObserver;
    private GestureDetector mGesture;
    private OnGestureListener mOnGesture;
    private OnItemClickListener mOnItemClicked;
    private OnItemLongClickListener mOnItemLongClicked;
    private OnItemSelectedListener mOnItemSelected;
    private int mRightViewIndex;
    private ListPopupWindow menu;
    View moreView;
    private int remainder;
    View[] viewsArray;

    /* renamed from: ru.ok.android.ui.custom.profiles.HorizontalItemsView.1 */
    class C07421 extends DataSetObserver {
        C07421() {
        }

        public void onChanged() {
            HorizontalItemsView.this.requestLayout();
        }

        public void onInvalidated() {
            HorizontalItemsView.this.requestLayout();
        }
    }

    /* renamed from: ru.ok.android.ui.custom.profiles.HorizontalItemsView.2 */
    class C07432 extends SimpleOnGestureListener {
        C07432() {
        }

        public boolean onSingleTapUp(MotionEvent e) {
            for (int i = 0; i < HorizontalItemsView.this.getChildCount(); i++) {
                View child = HorizontalItemsView.this.getChildAt(i);
                if (isEventWithinView(e, child)) {
                    HorizontalItemsView.this.onItemClick(child, i);
                    if (HorizontalItemsView.this.mOnItemSelected != null) {
                        HorizontalItemsView.this.mOnItemSelected.onItemSelected(HorizontalItemsView.this, child, i, HorizontalItemsView.this.mAdapter.getItemId(i));
                    }
                    return true;
                }
            }
            return true;
        }

        public void onLongPress(MotionEvent e) {
            int childCount = HorizontalItemsView.this.getChildCount();
            int i = 0;
            while (i < childCount) {
                View child = HorizontalItemsView.this.getChildAt(i);
                if (!isEventWithinView(e, child)) {
                    i++;
                } else if (HorizontalItemsView.this.mOnItemLongClicked != null) {
                    HorizontalItemsView.this.mOnItemLongClicked.onItemLongClick(HorizontalItemsView.this, child, i, HorizontalItemsView.this.mAdapter.getItemId(i));
                    return;
                } else {
                    return;
                }
            }
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

    class MenuAdapter extends BaseAdapter {
        private final ProfileSectionsAdapter baseAdapter;
        private final int startPosition;

        /* renamed from: ru.ok.android.ui.custom.profiles.HorizontalItemsView.MenuAdapter.1 */
        class C07441 extends DataSetObserver {
            final /* synthetic */ HorizontalItemsView val$this$0;

            C07441(HorizontalItemsView horizontalItemsView) {
                this.val$this$0 = horizontalItemsView;
            }

            public void onChanged() {
                super.onChanged();
                MenuAdapter.this.notifyDataSetChanged();
            }
        }

        class ViewHolder {
            public TextView textCount;
            public TextView textName;

            ViewHolder() {
            }
        }

        public MenuAdapter(ProfileSectionsAdapter baseAdapter, int startPosition) {
            this.baseAdapter = baseAdapter;
            this.startPosition = startPosition;
            if (baseAdapter != null) {
                baseAdapter.registerDataSetObserver(new C07441(HorizontalItemsView.this));
            }
        }

        public int getCount() {
            return this.baseAdapter.getCount() - this.startPosition;
        }

        public Object getItem(int position) {
            return this.baseAdapter.getItem(this.startPosition + position);
        }

        public long getItemId(int position) {
            return (long) (this.startPosition + position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = newView(parent);
            }
            bindView(convertView, (ProfileSectionItem) getItem(position));
            return convertView;
        }

        private View newView(ViewGroup parent) {
            View mainView = LocalizationManager.inflate(HorizontalItemsView.this.getContext(), 2130903411, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.textName = (TextView) mainView.findViewById(C0176R.id.title);
            holder.textCount = (TextView) mainView.findViewById(2131624446);
            mainView.setTag(holder);
            return mainView;
        }

        private void bindView(View view, ProfileSectionItem item) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.textName.setText(LocalizationManager.getString(HorizontalItemsView.this.getContext(), item.getNameResourceId()));
            int count = this.baseAdapter != null ? this.baseAdapter.getItemCount(item) : 0;
            Utils.setTextViewTextWithVisibilityState(holder.textCount, count > 0 ? String.valueOf(count) : null, 4);
        }
    }

    public HorizontalItemsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRightViewIndex = 0;
        this.remainder = 0;
        this.viewsArray = new View[100];
        this.mDataObserver = new C07421();
        this.mOnGesture = new C07432();
        initView();
    }

    private void initView() {
        this.menu = null;
        this.mGesture = new GestureDetector(getContext(), this.mOnGesture);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.mOnItemSelected = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClicked = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClicked = listener;
    }

    public ListAdapter getAdapter() {
        return this.mAdapter;
    }

    public View getSelectedView() {
        return null;
    }

    public void setAdapter(ListAdapter adapter) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mDataObserver);
        }
        this.mAdapter = adapter;
        this.mAdapter.registerDataSetObserver(this.mDataObserver);
        reset();
    }

    private synchronized void reset() {
        if (this.menu != null && this.menu.isShowing()) {
            this.menu.dismiss();
        }
        initView();
        requestLayout();
    }

    public void setSelection(int position) {
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mAdapter != null) {
            int leftMeasure = 0;
            removeAllViewsInLayout();
            for (int i = 0; i < this.mRightViewIndex; i++) {
                View child = this.viewsArray[i];
                addChild(child, leftMeasure, i);
                leftMeasure += child.getMeasuredWidth();
            }
            if (this.moreView.getParent() == null && this.mRightViewIndex < this.mAdapter.getCount()) {
                addChild(this.moreView, getWidth() - getHeight(), this.mRightViewIndex);
            }
        }
    }

    private void addChild(View child, int left, int position) {
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(-2, -1);
        }
        child.layout(left, 0, left + child.getMeasuredWidth(), getHeight());
        addViewInLayout(child, position, params, false);
    }

    private int measureChildValue(View child, int left, int matchValue) {
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(-2, -1);
        }
        child.measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(getHeight(), params.height == -1 ? 1073741824 : LinearLayoutManager.INVALID_OFFSET));
        int childWidth = child.getMeasuredWidth();
        if (left + childWidth < matchValue) {
            return childWidth;
        }
        return -1;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sumMeasureWidth = 0;
        int measureWidthView = 0;
        this.mRightViewIndex = 0;
        for (i = 0; i < this.mAdapter.getCount() && measureWidthView >= 0; i++) {
            View child = this.mAdapter.getView(i, this.viewsArray[this.mRightViewIndex], this);
            measureWidthView = measureChildValue(child, sumMeasureWidth, getMeasuredWidth() - getMeasuredHeight());
            if (measureWidthView >= 0) {
                sumMeasureWidth += measureWidthView;
                this.viewsArray[i] = child;
                this.mRightViewIndex++;
            }
        }
        if (this.mRightViewIndex > 0) {
            if (this.mRightViewIndex < this.mAdapter.getCount()) {
                this.remainder = ((getMeasuredWidth() - getMeasuredHeight()) - sumMeasureWidth) / this.mRightViewIndex;
            } else {
                this.remainder = (getMeasuredWidth() - sumMeasureWidth) / this.mRightViewIndex;
            }
            for (i = 0; i < this.mRightViewIndex; i++) {
                child = this.viewsArray[i];
                child.measure(MeasureSpec.makeMeasureSpec(child.getMeasuredWidth() + this.remainder, 1073741824), MeasureSpec.makeMeasureSpec(getHeight(), 1073741824));
            }
        }
        if (this.moreView == null) {
            this.moreView = createMoreView();
        }
        this.moreView.measure(MeasureSpec.makeMeasureSpec(getHeight(), 1073741824), MeasureSpec.makeMeasureSpec(getHeight(), 1073741824));
    }

    private View createMoreView() {
        return LocalizationManager.inflate(getContext(), 2130903332, (ViewGroup) this, false);
    }

    private ListPopupWindow createMenu() {
        ListPopupWindow menu = new ListPopupWindow(getContext(), null, 0);
        menu.setModal(true);
        menu.setWidth(getResources().getDimensionPixelSize(2131231217));
        menu.setAdapter(new MenuAdapter((ProfileSectionsAdapter) this.mAdapter, this.mRightViewIndex));
        menu.setOnItemClickListener(this);
        menu.setBackgroundDrawable(getResources().getDrawable(2130838641));
        return menu;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (this.menu != null && this.menu.isShowing()) {
            this.menu.dismiss();
        }
        if (this.mOnItemClicked != null) {
            this.mOnItemClicked.onItemClick(this, view, position + this.mRightViewIndex, this.mAdapter.getItemId(position));
        }
    }

    public void onItemClick(View view, int position) {
        if (position < this.mRightViewIndex) {
            if (this.mOnItemClicked != null) {
                this.mOnItemClicked.onItemClick(this, view, position, this.mAdapter.getItemId(position));
            }
        } else if (position != this.mRightViewIndex) {
        } else {
            if (this.menu == null) {
                this.menu = createMenu();
                this.menu.setAnchorView(view);
                this.menu.show();
            } else if (this.menu.isShowing()) {
                this.menu.dismiss();
            } else {
                this.menu.setAnchorView(view);
                this.menu.show();
            }
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            requestDisallowInterceptTouchEvent(true);
        } else if (ev.getAction() == 1 || ev.getAction() == 3) {
            requestDisallowInterceptTouchEvent(false);
        }
        return super.dispatchTouchEvent(ev) | this.mGesture.onTouchEvent(ev);
    }
}
