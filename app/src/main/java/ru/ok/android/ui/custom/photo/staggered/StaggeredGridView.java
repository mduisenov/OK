package ru.ok.android.ui.custom.photo.staggered;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import com.google.android.gms.location.LocationStatusCodes;
import java.util.ArrayList;
import java.util.Arrays;
import ru.ok.android.proto.MessagesProto.Message;

public class StaggeredGridView extends ViewGroup {
    private int mActivePointerId;
    private ListAdapter mAdapter;
    private final EdgeEffectCompat mBottomEdge;
    private int mColCount;
    private int mColCountSetting;
    private boolean mDataChanged;
    private boolean mFastChildLayout;
    private int mFirstPosition;
    private int mFlingVelocity;
    private boolean mForcePopulateOnLayout;
    private boolean mHasStableIds;
    private boolean mInLayout;
    private int[] mItemBottoms;
    private int mItemCount;
    private int mItemMargin;
    private int[] mItemTops;
    private float mLastTouchY;
    private final SparseArrayCompat<LayoutRecord> mLayoutRecords;
    private int mMaximumVelocity;
    private int mMinColWidth;
    private final AdapterDataSetObserver mObserver;
    private int mOldItemCount;
    private boolean mPopulating;
    private final RecycleBin mRecycler;
    private int mRestoreOffset;
    private int mScrollMovedBy;
    private final ScrollerCompat mScroller;
    private final EdgeEffectCompat mTopEdge;
    private int mTouchMode;
    private float mTouchRemainderY;
    private int mTouchSlop;
    private final VelocityTracker mVelocityTracker;
    private OnScrollChangeListener onScrollChangeListener;
    private ScrollUpdateListener scrollUpdateListener;

    private class AdapterDataSetObserver extends DataSetObserver {
        private AdapterDataSetObserver() {
        }

        public void onChanged() {
            StaggeredGridView.this.mDataChanged = true;
            StaggeredGridView.this.mOldItemCount = StaggeredGridView.this.mItemCount;
            StaggeredGridView.this.mItemCount = StaggeredGridView.this.mAdapter.getCount();
            StaggeredGridView.this.mRecycler.clearTransientViews();
            if (!StaggeredGridView.this.mHasStableIds) {
                StaggeredGridView.this.recycleAllViews();
                int colCount = StaggeredGridView.this.mColCount;
                for (int i = 0; i < colCount; i++) {
                    StaggeredGridView.this.mItemBottoms[i] = StaggeredGridView.this.mItemTops[i];
                }
            }
            StaggeredGridView.this.requestLayout();
        }

        public void onInvalidated() {
        }
    }

    public static class LayoutParams extends android.view.ViewGroup.LayoutParams {
        private static final int[] LAYOUT_ATTRS;
        int column;
        long id;
        int position;
        public int span;
        int viewType;

        static {
            LAYOUT_ATTRS = new int[]{16843085};
        }

        public LayoutParams(int height) {
            super(-1, height);
            this.span = 1;
            this.id = -1;
            if (this.height == -1) {
                Log.w("StaggeredGridView", "Constructing LayoutParams with height FILL_PARENT - impossible! Falling back to WRAP_CONTENT");
                this.height = -2;
            }
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.span = 1;
            this.id = -1;
            if (this.width != -1) {
                Log.w("StaggeredGridView", "Inflation setting LayoutParams width to " + this.width + " - must be MATCH_PARENT");
                this.width = -1;
            }
            if (this.height == -1) {
                Log.w("StaggeredGridView", "Inflation setting LayoutParams height to MATCH_PARENT - impossible! Falling back to WRAP_CONTENT");
                this.height = -2;
            }
            TypedArray a = c.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
            this.span = a.getInteger(0, 1);
            a.recycle();
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams other) {
            super(other);
            this.span = 1;
            this.id = -1;
            if (this.width != -1) {
                Log.w("StaggeredGridView", "Constructing LayoutParams with width " + this.width + " - must be MATCH_PARENT");
                this.width = -1;
            }
            if (this.height == -1) {
                Log.w("StaggeredGridView", "Constructing LayoutParams with height MATCH_PARENT - impossible! Falling back to WRAP_CONTENT");
                this.height = -2;
            }
        }
    }

    private static final class LayoutRecord {
        public int column;
        public int height;
        public long id;
        private int[] mMargins;
        public int span;

        private LayoutRecord() {
            this.id = -1;
        }

        private final void ensureMargins() {
            if (this.mMargins == null) {
                this.mMargins = new int[(this.span * 2)];
            }
        }

        public final int getMarginAbove(int col) {
            if (this.mMargins == null) {
                return 0;
            }
            return this.mMargins[col * 2];
        }

        public final int getMarginBelow(int col) {
            if (this.mMargins == null) {
                return 0;
            }
            return this.mMargins[(col * 2) + 1];
        }

        public final void setMarginAbove(int col, int margin) {
            if (this.mMargins != null || margin != 0) {
                ensureMargins();
                this.mMargins[col * 2] = margin;
            }
        }

        public final void setMarginBelow(int col, int margin) {
            if (this.mMargins != null || margin != 0) {
                ensureMargins();
                this.mMargins[(col * 2) + 1] = margin;
            }
        }

        public String toString() {
            String result = "LayoutRecord{c=" + this.column + ", id=" + this.id + " h=" + this.height + " s=" + this.span;
            if (this.mMargins != null) {
                result = result + " margins[above, below](";
                for (int i = 0; i < this.mMargins.length; i += 2) {
                    result = result + "[" + this.mMargins[i] + ", " + this.mMargins[i + 1] + "]";
                }
                result = result + ")";
            }
            return result + "}";
        }
    }

    public interface OnScrollChangeListener {
        void onScrollChanged(int i);
    }

    private class RecycleBin {
        private int mMaxScrap;
        private ArrayList<View>[] mScrapViews;
        private SparseArray<View> mTransientStateViews;
        private int mViewTypeCount;

        private RecycleBin() {
        }

        public void setViewTypeCount(int viewTypeCount) {
            if (viewTypeCount < 1) {
                throw new IllegalArgumentException("Must have at least one view type (" + viewTypeCount + " types reported)");
            } else if (viewTypeCount != this.mViewTypeCount) {
                ArrayList<View>[] scrapViews = new ArrayList[viewTypeCount];
                for (int i = 0; i < viewTypeCount; i++) {
                    scrapViews[i] = new ArrayList();
                }
                this.mViewTypeCount = viewTypeCount;
                this.mScrapViews = scrapViews;
            }
        }

        public void clear() {
            int typeCount = this.mViewTypeCount;
            for (int i = 0; i < typeCount; i++) {
                this.mScrapViews[i].clear();
            }
            if (this.mTransientStateViews != null) {
                this.mTransientStateViews.clear();
            }
        }

        public void clearTransientViews() {
            if (this.mTransientStateViews != null) {
                this.mTransientStateViews.clear();
            }
        }

        public void addScrap(View v) {
            LayoutParams lp = (LayoutParams) v.getLayoutParams();
            if (ViewCompat.hasTransientState(v)) {
                if (this.mTransientStateViews == null) {
                    this.mTransientStateViews = new SparseArray();
                }
                this.mTransientStateViews.put(lp.position, v);
                return;
            }
            int childCount = StaggeredGridView.this.getChildCount();
            if (childCount > this.mMaxScrap) {
                this.mMaxScrap = childCount;
            }
            ArrayList<View> scrap = this.mScrapViews[lp.viewType];
            if (scrap.size() < this.mMaxScrap) {
                scrap.add(v);
            }
        }

        public View getTransientStateView(int position) {
            if (this.mTransientStateViews == null) {
                return null;
            }
            View result = (View) this.mTransientStateViews.get(position);
            if (result == null) {
                return result;
            }
            this.mTransientStateViews.remove(position);
            return result;
        }

        public View getScrapView(int type) {
            ArrayList<View> scrap = this.mScrapViews[type];
            if (scrap.isEmpty()) {
                return null;
            }
            int index = scrap.size() - 1;
            View result = (View) scrap.get(index);
            scrap.remove(index);
            return result;
        }
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR;
        long firstId;
        int position;
        int topOffset;

        /* renamed from: ru.ok.android.ui.custom.photo.staggered.StaggeredGridView.SavedState.1 */
        static class C07401 implements Creator<SavedState> {
            C07401() {
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        SavedState(Parcelable superState) {
            super(superState);
            this.firstId = -1;
        }

        private SavedState(Parcel in) {
            super(in);
            this.firstId = -1;
            this.firstId = in.readLong();
            this.position = in.readInt();
            this.topOffset = in.readInt();
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(this.firstId);
            out.writeInt(this.position);
            out.writeInt(this.topOffset);
        }

        public String toString() {
            return "StaggereGridView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " firstId=" + this.firstId + " position=" + this.position + "}";
        }

        static {
            CREATOR = new C07401();
        }
    }

    public interface ScrollUpdateListener {
        void onScrollUpdated(int i);
    }

    public StaggeredGridView(Context context) {
        this(context, null);
    }

    public StaggeredGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StaggeredGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mColCountSetting = 2;
        this.mColCount = 2;
        this.mMinColWidth = 0;
        this.mRecycler = new RecycleBin();
        this.mObserver = new AdapterDataSetObserver();
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mLayoutRecords = new SparseArrayCompat();
        ViewConfiguration vc = ViewConfiguration.get(context);
        this.mTouchSlop = vc.getScaledTouchSlop() / 2;
        this.mMaximumVelocity = vc.getScaledMaximumFlingVelocity();
        this.mFlingVelocity = vc.getScaledMinimumFlingVelocity();
        this.mScroller = ScrollerCompat.from(context);
        this.mTopEdge = new EdgeEffectCompat(context);
        this.mBottomEdge = new EdgeEffectCompat(context);
        setWillNotDraw(false);
        setClipToPadding(false);
    }

    public void setColumnCount(int colCount) {
        boolean needsPopulate = true;
        if (colCount >= 1 || colCount == -1) {
            if (colCount == this.mColCount) {
                needsPopulate = false;
            }
            this.mColCountSetting = colCount;
            this.mColCount = colCount;
            if (needsPopulate) {
                populate();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Column count must be at least 1 - received " + colCount);
    }

    public int getColumnCount() {
        return this.mColCount;
    }

    public void setMinColumnWidth(int minColWidth) {
        this.mMinColWidth = minColWidth;
        setColumnCount(-1);
    }

    public void setItemMargin(int marginPixels) {
        boolean needsPopulate = marginPixels != this.mItemMargin;
        this.mItemMargin = marginPixels;
        if (needsPopulate) {
            populate();
        }
    }

    public int getFirstPosition() {
        return this.mFirstPosition;
    }

    public void setFirstPosition(int position) {
        this.mFirstPosition = position;
        removeAllViews();
        populate();
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        this.mVelocityTracker.addMovement(ev);
        switch (ev.getAction() & MotionEventCompat.ACTION_MASK) {
            case RECEIVED_VALUE:
                this.mVelocityTracker.clear();
                this.mScroller.abortAnimation();
                this.mLastTouchY = ev.getY();
                this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                this.mTouchRemainderY = 0.0f;
                if (this.mTouchMode == 2) {
                    this.mTouchMode = 1;
                    return true;
                }
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                int index = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                if (index < 0) {
                    Log.e("StaggeredGridView", "onInterceptTouchEvent could not find pointer with id " + this.mActivePointerId + " - did StaggeredGridView receive an inconsistent " + "event stream?");
                    return false;
                }
                float dy = (MotionEventCompat.getY(ev, index) - this.mLastTouchY) + this.mTouchRemainderY;
                this.mTouchRemainderY = dy - ((float) ((int) dy));
                if (Math.abs(dy) > ((float) this.mTouchSlop)) {
                    this.mTouchMode = 1;
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        this.mVelocityTracker.addMovement(ev);
        switch (ev.getAction() & MotionEventCompat.ACTION_MASK) {
            case RECEIVED_VALUE:
                this.mVelocityTracker.clear();
                this.mScroller.abortAnimation();
                this.mLastTouchY = ev.getY();
                this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                this.mTouchRemainderY = 0.0f;
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.mVelocityTracker.computeCurrentVelocity(LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, (float) this.mMaximumVelocity);
                float velocity = VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId);
                if (Math.abs(velocity) <= ((float) this.mFlingVelocity)) {
                    this.mTouchMode = 0;
                    break;
                }
                this.mTouchMode = 2;
                this.mScroller.fling(0, 0, 0, (int) velocity, 0, 0, LinearLayoutManager.INVALID_OFFSET, Integer.MAX_VALUE);
                this.mLastTouchY = 0.0f;
                ViewCompat.postInvalidateOnAnimation(this);
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                int index = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                if (index >= 0) {
                    float y = MotionEventCompat.getY(ev, index);
                    float dy = (y - this.mLastTouchY) + this.mTouchRemainderY;
                    int deltaY = (int) dy;
                    this.mTouchRemainderY = dy - ((float) deltaY);
                    if (Math.abs(dy) > ((float) this.mTouchSlop)) {
                        this.mTouchMode = 1;
                    }
                    if (this.mTouchMode == 1) {
                        this.mLastTouchY = y;
                        if (!trackMotionScroll(deltaY, true, true)) {
                            this.mVelocityTracker.clear();
                            break;
                        }
                    }
                }
                Log.e("StaggeredGridView", "onInterceptTouchEvent could not find pointer with id " + this.mActivePointerId + " - did StaggeredGridView receive an inconsistent " + "event stream?");
                return false;
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                this.mTouchMode = 0;
                break;
        }
        return true;
    }

    protected boolean trackMotionScroll(int deltaY, boolean allowOverScroll, boolean isTouchEvent) {
        int overScrolledBy;
        int movedBy;
        boolean contentFits = contentFits();
        int allowOverhang = Math.abs(deltaY);
        if (contentFits) {
            overScrolledBy = allowOverhang;
            movedBy = 0;
        } else {
            int overhang;
            boolean up;
            this.mPopulating = true;
            if (deltaY > 0) {
                overhang = fillUp(this.mFirstPosition - 1, allowOverhang);
                up = true;
            } else {
                overhang = fillDown(this.mFirstPosition + getChildCount(), allowOverhang) + this.mItemMargin;
                up = false;
            }
            movedBy = Math.min(overhang, allowOverhang);
            offsetChildren(up ? movedBy : -movedBy);
            recycleOffscreenViews();
            this.mPopulating = false;
            overScrolledBy = allowOverhang - overhang;
        }
        if (allowOverScroll) {
            overscrollBy(deltaY, overScrolledBy, contentFits, isTouchEvent);
        }
        if (movedBy != 0) {
            this.mScrollMovedBy = (deltaY > 0 ? movedBy : -movedBy) + this.mScrollMovedBy;
            if (this.scrollUpdateListener != null) {
                this.scrollUpdateListener.onScrollUpdated(this.mScrollMovedBy);
            }
            if (this.onScrollChangeListener != null) {
                int i;
                OnScrollChangeListener onScrollChangeListener = this.onScrollChangeListener;
                if (deltaY > 0) {
                    i = -movedBy;
                } else {
                    i = movedBy;
                }
                onScrollChangeListener.onScrollChanged(i);
            }
        }
        if (deltaY == 0 || movedBy != 0) {
            return true;
        }
        return false;
    }

    protected void overscrollBy(int deltaY, int overScrolledBy, boolean contentFits, boolean isTouchEvent) {
        int overScrollMode = ViewCompat.getOverScrollMode(this);
        if ((overScrollMode == 0 || (overScrollMode == 1 && !contentFits)) && overScrolledBy > 0) {
            (deltaY > 0 ? this.mTopEdge : this.mBottomEdge).onPull(((float) Math.abs(deltaY)) / ((float) getHeight()));
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private final boolean contentFits() {
        if (this.mFirstPosition != 0 || getChildCount() != this.mItemCount) {
            return false;
        }
        int topmost = Integer.MAX_VALUE;
        int bottommost = LinearLayoutManager.INVALID_OFFSET;
        for (int i = 0; i < this.mColCount; i++) {
            if (this.mItemTops[i] < topmost) {
                topmost = this.mItemTops[i];
            }
            if (this.mItemBottoms[i] > bottommost) {
                bottommost = this.mItemBottoms[i];
            }
        }
        if (topmost < getPaddingTop() || bottommost > getHeight() - getPaddingBottom()) {
            return false;
        }
        return true;
    }

    private void recycleAllViews() {
        for (int i = 0; i < getChildCount(); i++) {
            this.mRecycler.addScrap(getChildAt(i));
        }
        if (this.mInLayout) {
            removeAllViewsInLayout();
        } else {
            removeAllViews();
        }
    }

    private void recycleOffscreenViews() {
        int i;
        int height = getHeight();
        int clearAbove = -this.mItemMargin;
        int clearBelow = height + this.mItemMargin;
        for (i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (child.getTop() <= clearBelow) {
                break;
            }
            if (this.mInLayout) {
                removeViewsInLayout(i, 1);
            } else {
                removeViewAt(i);
            }
            this.mRecycler.addScrap(child);
        }
        while (getChildCount() > 0) {
            child = getChildAt(0);
            if (child.getBottom() >= clearAbove) {
                break;
            }
            if (this.mInLayout) {
                removeViewsInLayout(0, 1);
            } else {
                removeViewAt(0);
            }
            this.mRecycler.addScrap(child);
            this.mFirstPosition++;
        }
        int childCount = getChildCount();
        if (childCount > 0) {
            int col;
            Arrays.fill(this.mItemTops, Integer.MAX_VALUE);
            Arrays.fill(this.mItemBottoms, LinearLayoutManager.INVALID_OFFSET);
            for (i = 0; i < childCount; i++) {
                child = getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int top = child.getTop() - this.mItemMargin;
                int bottom = child.getBottom();
                LayoutRecord rec = (LayoutRecord) this.mLayoutRecords.get(this.mFirstPosition + i);
                int colEnd = lp.column + Math.min(this.mColCount, lp.span);
                for (col = lp.column; col < colEnd; col++) {
                    int colTop = top - rec.getMarginAbove(col - lp.column);
                    int colBottom = bottom + rec.getMarginBelow(col - lp.column);
                    if (colTop < this.mItemTops[col]) {
                        this.mItemTops[col] = colTop;
                    }
                    if (colBottom > this.mItemBottoms[col]) {
                        this.mItemBottoms[col] = colBottom;
                    }
                }
            }
            col = 0;
            while (true) {
                int i2 = this.mColCount;
                if (col < r0) {
                    if (this.mItemTops[col] == Integer.MAX_VALUE) {
                        this.mItemTops[col] = 0;
                        this.mItemBottoms[col] = 0;
                    }
                    col++;
                } else {
                    return;
                }
            }
        }
    }

    public void computeScroll() {
        if (this.mScroller.computeScrollOffset()) {
            boolean stopped;
            int y = this.mScroller.getCurrY();
            int dy = (int) (((float) y) - this.mLastTouchY);
            this.mLastTouchY = (float) y;
            if (trackMotionScroll(dy, false, false)) {
                stopped = false;
            } else {
                stopped = true;
            }
            if (stopped || this.mScroller.isFinished()) {
                if (stopped) {
                    if (ViewCompat.getOverScrollMode(this) != 2) {
                        EdgeEffectCompat edge;
                        if (dy > 0) {
                            edge = this.mTopEdge;
                        } else {
                            edge = this.mBottomEdge;
                        }
                        edge.onAbsorb(Math.abs((int) this.mScroller.getCurrVelocity()));
                        ViewCompat.postInvalidateOnAnimation(this);
                    }
                    this.mScroller.abortAnimation();
                }
                this.mTouchMode = 0;
                return;
            }
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mTopEdge != null) {
            boolean needsInvalidate = false;
            if (!this.mTopEdge.isFinished()) {
                this.mTopEdge.draw(canvas);
                needsInvalidate = true;
            }
            if (!this.mBottomEdge.isFinished()) {
                int restoreCount = canvas.save();
                int width = getWidth();
                canvas.translate((float) (-width), (float) getHeight());
                canvas.rotate(180.0f, (float) width, 0.0f);
                this.mBottomEdge.draw(canvas);
                canvas.restoreToCount(restoreCount);
                needsInvalidate = true;
            }
            if (needsInvalidate) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }

    public void requestLayout() {
        if (!this.mPopulating && !this.mFastChildLayout) {
            super.requestLayout();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode != 1073741824) {
            Log.e("StaggeredGridView", "onMeasure: must have an exact width or match_parent! Using fallback spec of EXACTLY " + widthSize);
        }
        if (heightMode != 1073741824) {
            Log.e("StaggeredGridView", "onMeasure: must have an exact height or match_parent! Using fallback spec of EXACTLY " + heightSize);
        }
        setMeasuredDimension(widthSize, heightSize);
        if (this.mColCountSetting == -1) {
            int colCount = widthSize / this.mMinColWidth;
            if (colCount != this.mColCount) {
                this.mColCount = colCount;
                this.mForcePopulateOnLayout = true;
            }
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.mInLayout = true;
        populate();
        this.mInLayout = false;
        this.mForcePopulateOnLayout = false;
        int width = r - l;
        int height = b - t;
        this.mTopEdge.setSize(width, height);
        this.mBottomEdge.setSize(width, height);
    }

    private void populate() {
        if (getWidth() != 0 && getHeight() != 0) {
            int colCount;
            if (this.mColCount == -1) {
                colCount = getWidth() / this.mMinColWidth;
                if (colCount != this.mColCount) {
                    this.mColCount = colCount;
                }
            }
            colCount = this.mColCount;
            if (this.mItemTops == null || this.mItemTops.length != colCount) {
                this.mItemTops = new int[colCount];
                this.mItemBottoms = new int[colCount];
                int offset = getPaddingTop() + Math.min(this.mRestoreOffset, 0);
                Arrays.fill(this.mItemTops, offset);
                Arrays.fill(this.mItemBottoms, offset);
                this.mLayoutRecords.clear();
                if (this.mInLayout) {
                    removeAllViewsInLayout();
                } else {
                    removeAllViews();
                }
                this.mRestoreOffset = 0;
            }
            this.mPopulating = true;
            layoutChildren(this.mDataChanged);
            fillDown(this.mFirstPosition + getChildCount(), 0);
            fillUp(this.mFirstPosition - 1, 0);
            this.mPopulating = false;
            this.mDataChanged = false;
        }
    }

    final void offsetChildren(int offset) {
        int i;
        int childCount = getChildCount();
        for (i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(child.getLeft(), child.getTop() + offset, child.getRight(), child.getBottom() + offset);
        }
        int colCount = this.mColCount;
        for (i = 0; i < colCount; i++) {
            int[] iArr = this.mItemTops;
            iArr[i] = iArr[i] + offset;
            iArr = this.mItemBottoms;
            iArr[i] = iArr[i] + offset;
        }
    }

    final void layoutChildren(boolean queryAdapter) {
        int i;
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int itemMargin = this.mItemMargin;
        int colWidth = (((getWidth() - paddingLeft) - paddingRight) - ((this.mColCount - 1) * itemMargin)) / this.mColCount;
        int rebuildLayoutRecordsBefore = -1;
        int rebuildLayoutRecordsAfter = -1;
        Arrays.fill(this.mItemBottoms, LinearLayoutManager.INVALID_OFFSET);
        int childCount = getChildCount();
        for (i = 0; i < childCount; i++) {
            int childTop;
            int j;
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int col = lp.column;
            int position = this.mFirstPosition + i;
            boolean needsLayout = queryAdapter || child.isLayoutRequested();
            if (queryAdapter) {
                View newView = obtainView(position, child);
                if (newView != child) {
                    removeViewAt(i);
                    addView(newView, i);
                    child = newView;
                }
                lp = (LayoutParams) child.getLayoutParams();
            }
            int span = Math.min(this.mColCount, lp.span);
            int widthSize = (colWidth * span) + ((span - 1) * itemMargin);
            if (needsLayout) {
                int heightSpec;
                int widthSpec = MeasureSpec.makeMeasureSpec(widthSize, 1073741824);
                int i2 = lp.height;
                if (r0 == -2) {
                    heightSpec = MeasureSpec.makeMeasureSpec(0, 0);
                } else {
                    heightSpec = MeasureSpec.makeMeasureSpec(lp.height, 1073741824);
                }
                child.measure(widthSpec, heightSpec);
            }
            if (this.mItemBottoms[col] > Integer.MIN_VALUE) {
                childTop = this.mItemBottoms[col] + this.mItemMargin;
            } else {
                childTop = child.getTop();
            }
            if (span > 1) {
                int lowest = childTop;
                for (j = col + 1; j < col + span; j++) {
                    int bottom = this.mItemBottoms[j] + this.mItemMargin;
                    if (bottom > lowest) {
                        lowest = bottom;
                    }
                }
                childTop = lowest;
            }
            int childHeight = child.getMeasuredHeight();
            int childBottom = childTop + childHeight;
            int childLeft = paddingLeft + ((colWidth + itemMargin) * col);
            child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childBottom);
            for (j = col; j < col + span; j++) {
                this.mItemBottoms[j] = childBottom;
            }
            LayoutRecord rec = (LayoutRecord) this.mLayoutRecords.get(position);
            if (rec != null) {
                i2 = rec.height;
                if (r0 != childHeight) {
                    rec.height = childHeight;
                    rebuildLayoutRecordsBefore = position;
                }
            }
            if (rec != null) {
                i2 = rec.span;
                if (r0 != span) {
                    rec.span = span;
                    rebuildLayoutRecordsAfter = position;
                }
            }
        }
        i = 0;
        while (true) {
            i2 = this.mColCount;
            if (i >= r0) {
                break;
            }
            if (this.mItemBottoms[i] == Integer.MIN_VALUE) {
                this.mItemBottoms[i] = this.mItemTops[i];
            }
            i++;
        }
        if (rebuildLayoutRecordsBefore >= 0 || rebuildLayoutRecordsAfter >= 0) {
            if (rebuildLayoutRecordsBefore >= 0) {
                invalidateLayoutRecordsBeforePosition(rebuildLayoutRecordsBefore);
            }
            if (rebuildLayoutRecordsAfter >= 0) {
                invalidateLayoutRecordsAfterPosition(rebuildLayoutRecordsAfter);
            }
            for (i = 0; i < childCount; i++) {
                position = this.mFirstPosition + i;
                child = getChildAt(i);
                lp = (LayoutParams) child.getLayoutParams();
                rec = (LayoutRecord) this.mLayoutRecords.get(position);
                if (rec == null) {
                    LayoutRecord layoutRecord = new LayoutRecord();
                    this.mLayoutRecords.put(position, layoutRecord);
                }
                rec.column = lp.column;
                rec.height = child.getHeight();
                rec.id = lp.id;
                rec.span = Math.min(this.mColCount, lp.span);
            }
        }
    }

    final void invalidateLayoutRecordsBeforePosition(int position) {
        int endAt = 0;
        while (endAt < this.mLayoutRecords.size() && this.mLayoutRecords.keyAt(endAt) < position) {
            endAt++;
        }
        this.mLayoutRecords.removeAtRange(0, endAt);
    }

    final void invalidateLayoutRecordsAfterPosition(int position) {
        int beginAt = this.mLayoutRecords.size() - 1;
        while (beginAt >= 0 && this.mLayoutRecords.keyAt(beginAt) > position) {
            beginAt--;
        }
        beginAt++;
        this.mLayoutRecords.removeAtRange(beginAt + 1, this.mLayoutRecords.size() - beginAt);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final int fillUp(int r36, int r37) {
        /*
        r35 = this;
        r23 = r35.getPaddingLeft();
        r24 = r35.getPaddingRight();
        r0 = r35;
        r0 = r0.mItemMargin;
        r20 = r0;
        r33 = r35.getWidth();
        r33 = r33 - r23;
        r33 = r33 - r24;
        r0 = r35;
        r0 = r0.mColCount;
        r34 = r0;
        r34 = r34 + -1;
        r34 = r34 * r20;
        r33 = r33 - r34;
        r0 = r35;
        r0 = r0.mColCount;
        r34 = r0;
        r10 = r33 / r34;
        r12 = r35.getPaddingTop();
        r11 = r12 - r37;
        r22 = r35.getNextColumnUp();
        r25 = r36;
        r26 = r25;
    L_0x0038:
        if (r22 < 0) goto L_0x020c;
    L_0x003a:
        r0 = r35;
        r0 = r0.mItemTops;
        r33 = r0;
        r33 = r33[r22];
        r0 = r33;
        if (r0 <= r11) goto L_0x020c;
    L_0x0046:
        if (r26 < 0) goto L_0x020c;
    L_0x0048:
        r33 = 0;
        r0 = r35;
        r1 = r26;
        r2 = r33;
        r4 = r0.obtainView(r1, r2);
        r21 = r4.getLayoutParams();
        r21 = (ru.ok.android.ui.custom.photo.staggered.StaggeredGridView.LayoutParams) r21;
        r33 = r4.getParent();
        r0 = r33;
        r1 = r35;
        if (r0 == r1) goto L_0x0077;
    L_0x0064:
        r0 = r35;
        r0 = r0.mInLayout;
        r33 = r0;
        if (r33 == 0) goto L_0x016c;
    L_0x006c:
        r33 = 0;
        r0 = r35;
        r1 = r33;
        r2 = r21;
        r0.addViewInLayout(r4, r1, r2);
    L_0x0077:
        r0 = r35;
        r0 = r0.mColCount;
        r33 = r0;
        r0 = r21;
        r0 = r0.span;
        r34 = r0;
        r28 = java.lang.Math.min(r33, r34);
        r33 = r10 * r28;
        r34 = r28 + -1;
        r34 = r34 * r20;
        r31 = r33 + r34;
        r33 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r0 = r31;
        r1 = r33;
        r32 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r1);
        r33 = 1;
        r0 = r28;
        r1 = r33;
        if (r0 <= r1) goto L_0x0177;
    L_0x00a1:
        r0 = r35;
        r1 = r26;
        r2 = r28;
        r27 = r0.getNextRecordUp(r1, r2);
        r0 = r27;
        r0 = r0.column;
        r22 = r0;
    L_0x00b1:
        r17 = 0;
        if (r27 != 0) goto L_0x0189;
    L_0x00b5:
        r27 = new ru.ok.android.ui.custom.photo.staggered.StaggeredGridView$LayoutRecord;
        r33 = 0;
        r0 = r27;
        r1 = r33;
        r0.<init>();
        r0 = r35;
        r0 = r0.mLayoutRecords;
        r33 = r0;
        r0 = r33;
        r1 = r26;
        r2 = r27;
        r0.put(r1, r2);
        r0 = r22;
        r1 = r27;
        r1.column = r0;
        r0 = r28;
        r1 = r27;
        r1.span = r0;
    L_0x00db:
        r0 = r35;
        r0 = r0.mHasStableIds;
        r33 = r0;
        if (r33 == 0) goto L_0x00fd;
    L_0x00e3:
        r0 = r35;
        r0 = r0.mAdapter;
        r33 = r0;
        r0 = r33;
        r1 = r26;
        r18 = r0.getItemId(r1);
        r0 = r18;
        r2 = r27;
        r2.id = r0;
        r0 = r18;
        r2 = r21;
        r2.id = r0;
    L_0x00fd:
        r0 = r22;
        r1 = r21;
        r1.column = r0;
        r0 = r21;
        r0 = r0.height;
        r33 = r0;
        r34 = -2;
        r0 = r33;
        r1 = r34;
        if (r0 != r1) goto L_0x01ad;
    L_0x0111:
        r33 = 0;
        r34 = 0;
        r13 = android.view.View.MeasureSpec.makeMeasureSpec(r33, r34);
    L_0x0119:
        r0 = r32;
        r4.measure(r0, r13);
        r6 = r4.getMeasuredHeight();
        if (r17 != 0) goto L_0x0136;
    L_0x0124:
        r0 = r27;
        r0 = r0.height;
        r33 = r0;
        r0 = r33;
        if (r6 == r0) goto L_0x013d;
    L_0x012e:
        r0 = r27;
        r0 = r0.height;
        r33 = r0;
        if (r33 <= 0) goto L_0x013d;
    L_0x0136:
        r0 = r35;
        r1 = r26;
        r0.invalidateLayoutRecordsBeforePosition(r1);
    L_0x013d:
        r0 = r27;
        r0.height = r6;
        r33 = 1;
        r0 = r28;
        r1 = r33;
        if (r0 <= r1) goto L_0x01f3;
    L_0x0149:
        r0 = r35;
        r0 = r0.mItemTops;
        r33 = r0;
        r14 = r33[r22];
        r16 = r22 + 1;
    L_0x0153:
        r33 = r22 + r28;
        r0 = r16;
        r1 = r33;
        if (r0 >= r1) goto L_0x01bb;
    L_0x015b:
        r0 = r35;
        r0 = r0.mItemTops;
        r33 = r0;
        r30 = r33[r16];
        r0 = r30;
        if (r0 >= r14) goto L_0x0169;
    L_0x0167:
        r14 = r30;
    L_0x0169:
        r16 = r16 + 1;
        goto L_0x0153;
    L_0x016c:
        r33 = 0;
        r0 = r35;
        r1 = r33;
        r0.addView(r4, r1);
        goto L_0x0077;
    L_0x0177:
        r0 = r35;
        r0 = r0.mLayoutRecords;
        r33 = r0;
        r0 = r33;
        r1 = r26;
        r27 = r0.get(r1);
        r27 = (ru.ok.android.ui.custom.photo.staggered.StaggeredGridView.LayoutRecord) r27;
        goto L_0x00b1;
    L_0x0189:
        r0 = r27;
        r0 = r0.span;
        r33 = r0;
        r0 = r28;
        r1 = r33;
        if (r0 == r1) goto L_0x01a5;
    L_0x0195:
        r0 = r28;
        r1 = r27;
        r1.span = r0;
        r0 = r22;
        r1 = r27;
        r1.column = r0;
        r17 = 1;
        goto L_0x00db;
    L_0x01a5:
        r0 = r27;
        r0 = r0.column;
        r22 = r0;
        goto L_0x00db;
    L_0x01ad:
        r0 = r21;
        r0 = r0.height;
        r33 = r0;
        r34 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r13 = android.view.View.MeasureSpec.makeMeasureSpec(r33, r34);
        goto L_0x0119;
    L_0x01bb:
        r29 = r14;
    L_0x01bd:
        r5 = r29;
        r9 = r5 - r6;
        r33 = r10 + r20;
        r33 = r33 * r22;
        r7 = r23 + r33;
        r33 = r4.getMeasuredWidth();
        r8 = r7 + r33;
        r4.layout(r7, r9, r8, r5);
        r16 = r22;
    L_0x01d2:
        r33 = r22 + r28;
        r0 = r16;
        r1 = r33;
        if (r0 >= r1) goto L_0x01fc;
    L_0x01da:
        r0 = r35;
        r0 = r0.mItemTops;
        r33 = r0;
        r34 = r16 - r22;
        r0 = r27;
        r1 = r34;
        r34 = r0.getMarginAbove(r1);
        r34 = r9 - r34;
        r34 = r34 - r20;
        r33[r16] = r34;
        r16 = r16 + 1;
        goto L_0x01d2;
    L_0x01f3:
        r0 = r35;
        r0 = r0.mItemTops;
        r33 = r0;
        r29 = r33[r22];
        goto L_0x01bd;
    L_0x01fc:
        r22 = r35.getNextColumnUp();
        r25 = r26 + -1;
        r0 = r26;
        r1 = r35;
        r1.mFirstPosition = r0;
        r26 = r25;
        goto L_0x0038;
    L_0x020c:
        r15 = r35.getHeight();
        r16 = 0;
    L_0x0212:
        r0 = r35;
        r0 = r0.mColCount;
        r33 = r0;
        r0 = r16;
        r1 = r33;
        if (r0 >= r1) goto L_0x0235;
    L_0x021e:
        r0 = r35;
        r0 = r0.mItemTops;
        r33 = r0;
        r33 = r33[r16];
        r0 = r33;
        if (r0 >= r15) goto L_0x0232;
    L_0x022a:
        r0 = r35;
        r0 = r0.mItemTops;
        r33 = r0;
        r15 = r33[r16];
    L_0x0232:
        r16 = r16 + 1;
        goto L_0x0212;
    L_0x0235:
        r33 = r12 - r15;
        return r33;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.ui.custom.photo.staggered.StaggeredGridView.fillUp(int, int):int");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final int fillDown(int r35, int r36) {
        /*
        r34 = this;
        r24 = r34.getPaddingLeft();
        r25 = r34.getPaddingRight();
        r0 = r34;
        r0 = r0.mItemMargin;
        r19 = r0;
        r32 = r34.getWidth();
        r32 = r32 - r24;
        r32 = r32 - r25;
        r0 = r34;
        r0 = r0.mColCount;
        r33 = r0;
        r33 = r33 + -1;
        r33 = r33 * r19;
        r32 = r32 - r33;
        r0 = r34;
        r0 = r0.mColCount;
        r33 = r0;
        r11 = r32 / r33;
        r32 = r34.getHeight();
        r33 = r34.getPaddingBottom();
        r13 = r32 - r33;
        r12 = r13 + r36;
        r23 = r34.getNextColumnDown();
        r26 = r35;
    L_0x003c:
        if (r23 < 0) goto L_0x0208;
    L_0x003e:
        r0 = r34;
        r0 = r0.mItemBottoms;
        r32 = r0;
        r32 = r32[r23];
        r0 = r32;
        if (r0 >= r12) goto L_0x0208;
    L_0x004a:
        r0 = r34;
        r0 = r0.mItemCount;
        r32 = r0;
        r0 = r26;
        r1 = r32;
        if (r0 >= r1) goto L_0x0208;
    L_0x0056:
        r32 = 0;
        r0 = r34;
        r1 = r26;
        r2 = r32;
        r5 = r0.obtainView(r1, r2);
        r22 = r5.getLayoutParams();
        r22 = (ru.ok.android.ui.custom.photo.staggered.StaggeredGridView.LayoutParams) r22;
        r32 = r5.getParent();
        r0 = r32;
        r1 = r34;
        if (r0 == r1) goto L_0x0085;
    L_0x0072:
        r0 = r34;
        r0 = r0.mInLayout;
        r32 = r0;
        if (r32 == 0) goto L_0x0178;
    L_0x007a:
        r32 = -1;
        r0 = r34;
        r1 = r32;
        r2 = r22;
        r0.addViewInLayout(r5, r1, r2);
    L_0x0085:
        r0 = r34;
        r0 = r0.mColCount;
        r32 = r0;
        r0 = r22;
        r0 = r0.span;
        r33 = r0;
        r28 = java.lang.Math.min(r32, r33);
        r32 = r11 * r28;
        r33 = r28 + -1;
        r33 = r33 * r19;
        r30 = r32 + r33;
        r32 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r0 = r30;
        r1 = r32;
        r31 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r1);
        r32 = 1;
        r0 = r28;
        r1 = r32;
        if (r0 <= r1) goto L_0x017f;
    L_0x00af:
        r0 = r34;
        r1 = r26;
        r2 = r28;
        r27 = r0.getNextRecordDown(r1, r2);
        r0 = r27;
        r0 = r0.column;
        r23 = r0;
    L_0x00bf:
        r18 = 0;
        if (r27 != 0) goto L_0x0191;
    L_0x00c3:
        r27 = new ru.ok.android.ui.custom.photo.staggered.StaggeredGridView$LayoutRecord;
        r32 = 0;
        r0 = r27;
        r1 = r32;
        r0.<init>();
        r0 = r34;
        r0 = r0.mLayoutRecords;
        r32 = r0;
        r0 = r32;
        r1 = r26;
        r2 = r27;
        r0.put(r1, r2);
        r0 = r23;
        r1 = r27;
        r1.column = r0;
        r0 = r28;
        r1 = r27;
        r1.span = r0;
    L_0x00e9:
        r0 = r34;
        r0 = r0.mHasStableIds;
        r32 = r0;
        if (r32 == 0) goto L_0x010b;
    L_0x00f1:
        r0 = r34;
        r0 = r0.mAdapter;
        r32 = r0;
        r0 = r32;
        r1 = r26;
        r16 = r0.getItemId(r1);
        r0 = r16;
        r2 = r27;
        r2.id = r0;
        r0 = r16;
        r2 = r22;
        r2.id = r0;
    L_0x010b:
        r0 = r23;
        r1 = r22;
        r1.column = r0;
        r0 = r22;
        r0 = r0.height;
        r32 = r0;
        r33 = -2;
        r0 = r32;
        r1 = r33;
        if (r0 != r1) goto L_0x01b5;
    L_0x011f:
        r32 = 0;
        r33 = 0;
        r14 = android.view.View.MeasureSpec.makeMeasureSpec(r32, r33);
    L_0x0127:
        r0 = r31;
        r5.measure(r0, r14);
        r7 = r5.getMeasuredHeight();
        if (r18 != 0) goto L_0x0144;
    L_0x0132:
        r0 = r27;
        r0 = r0.height;
        r32 = r0;
        r0 = r32;
        if (r7 == r0) goto L_0x014b;
    L_0x013c:
        r0 = r27;
        r0 = r0.height;
        r32 = r0;
        if (r32 <= 0) goto L_0x014b;
    L_0x0144:
        r0 = r34;
        r1 = r26;
        r0.invalidateLayoutRecordsAfterPosition(r1);
    L_0x014b:
        r0 = r27;
        r0.height = r7;
        r32 = 1;
        r0 = r28;
        r1 = r32;
        if (r0 <= r1) goto L_0x01f7;
    L_0x0157:
        r0 = r34;
        r0 = r0.mItemBottoms;
        r32 = r0;
        r20 = r32[r23];
        r15 = r23 + 1;
    L_0x0161:
        r32 = r23 + r28;
        r0 = r32;
        if (r15 >= r0) goto L_0x01c3;
    L_0x0167:
        r0 = r34;
        r0 = r0.mItemBottoms;
        r32 = r0;
        r4 = r32[r15];
        r0 = r20;
        if (r4 <= r0) goto L_0x0175;
    L_0x0173:
        r20 = r4;
    L_0x0175:
        r15 = r15 + 1;
        goto L_0x0161;
    L_0x0178:
        r0 = r34;
        r0.addView(r5);
        goto L_0x0085;
    L_0x017f:
        r0 = r34;
        r0 = r0.mLayoutRecords;
        r32 = r0;
        r0 = r32;
        r1 = r26;
        r27 = r0.get(r1);
        r27 = (ru.ok.android.ui.custom.photo.staggered.StaggeredGridView.LayoutRecord) r27;
        goto L_0x00bf;
    L_0x0191:
        r0 = r27;
        r0 = r0.span;
        r32 = r0;
        r0 = r28;
        r1 = r32;
        if (r0 == r1) goto L_0x01ad;
    L_0x019d:
        r0 = r28;
        r1 = r27;
        r1.span = r0;
        r0 = r23;
        r1 = r27;
        r1.column = r0;
        r18 = 1;
        goto L_0x00e9;
    L_0x01ad:
        r0 = r27;
        r0 = r0.column;
        r23 = r0;
        goto L_0x00e9;
    L_0x01b5:
        r0 = r22;
        r0 = r0.height;
        r32 = r0;
        r33 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r14 = android.view.View.MeasureSpec.makeMeasureSpec(r32, r33);
        goto L_0x0127;
    L_0x01c3:
        r29 = r20;
    L_0x01c5:
        r10 = r29 + r19;
        r6 = r10 + r7;
        r32 = r11 + r19;
        r32 = r32 * r23;
        r8 = r24 + r32;
        r32 = r5.getMeasuredWidth();
        r9 = r8 + r32;
        r5.layout(r8, r10, r9, r6);
        r15 = r23;
    L_0x01da:
        r32 = r23 + r28;
        r0 = r32;
        if (r15 >= r0) goto L_0x0200;
    L_0x01e0:
        r0 = r34;
        r0 = r0.mItemBottoms;
        r32 = r0;
        r33 = r15 - r23;
        r0 = r27;
        r1 = r33;
        r33 = r0.getMarginBelow(r1);
        r33 = r33 + r6;
        r32[r15] = r33;
        r15 = r15 + 1;
        goto L_0x01da;
    L_0x01f7:
        r0 = r34;
        r0 = r0.mItemBottoms;
        r32 = r0;
        r29 = r32[r23];
        goto L_0x01c5;
    L_0x0200:
        r23 = r34.getNextColumnDown();
        r26 = r26 + 1;
        goto L_0x003c;
    L_0x0208:
        r21 = 0;
        r15 = 0;
    L_0x020b:
        r0 = r34;
        r0 = r0.mColCount;
        r32 = r0;
        r0 = r32;
        if (r15 >= r0) goto L_0x022e;
    L_0x0215:
        r0 = r34;
        r0 = r0.mItemBottoms;
        r32 = r0;
        r32 = r32[r15];
        r0 = r32;
        r1 = r21;
        if (r0 <= r1) goto L_0x022b;
    L_0x0223:
        r0 = r34;
        r0 = r0.mItemBottoms;
        r32 = r0;
        r21 = r32[r15];
    L_0x022b:
        r15 = r15 + 1;
        goto L_0x020b;
    L_0x022e:
        r32 = r21 - r13;
        return r32;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.ui.custom.photo.staggered.StaggeredGridView.fillDown(int, int):int");
    }

    final int getNextColumnUp() {
        int result = -1;
        int bottomMost = LinearLayoutManager.INVALID_OFFSET;
        for (int i = this.mColCount - 1; i >= 0; i--) {
            int top = this.mItemTops[i];
            if (top > bottomMost) {
                bottomMost = top;
                result = i;
            }
        }
        return result;
    }

    final LayoutRecord getNextRecordUp(int position, int span) {
        int i;
        LayoutRecord rec = (LayoutRecord) this.mLayoutRecords.get(position);
        if (rec == null) {
            rec = new LayoutRecord();
            rec.span = span;
            this.mLayoutRecords.put(position, rec);
        } else if (rec.span != span) {
            throw new IllegalStateException("Invalid LayoutRecord! Record had span=" + rec.span + " but caller requested span=" + span + " for position=" + position);
        }
        int targetCol = -1;
        int bottomMost = LinearLayoutManager.INVALID_OFFSET;
        for (i = this.mColCount - span; i >= 0; i--) {
            int top = Integer.MAX_VALUE;
            for (int j = i; j < i + span; j++) {
                int singleTop = this.mItemTops[j];
                if (singleTop < top) {
                    top = singleTop;
                }
            }
            if (top > bottomMost) {
                bottomMost = top;
                targetCol = i;
            }
        }
        rec.column = targetCol;
        for (i = 0; i < span; i++) {
            rec.setMarginBelow(i, this.mItemTops[i + targetCol] - bottomMost);
        }
        return rec;
    }

    final int getNextColumnDown() {
        int result = -1;
        int topMost = Integer.MAX_VALUE;
        int colCount = this.mColCount;
        for (int i = 0; i < colCount; i++) {
            int bottom = this.mItemBottoms[i];
            if (bottom < topMost) {
                topMost = bottom;
                result = i;
            }
        }
        return result;
    }

    final LayoutRecord getNextRecordDown(int position, int span) {
        int i;
        LayoutRecord rec = (LayoutRecord) this.mLayoutRecords.get(position);
        if (rec == null) {
            rec = new LayoutRecord();
            rec.span = span;
            this.mLayoutRecords.put(position, rec);
        } else if (rec.span != span) {
            throw new IllegalStateException("Invalid LayoutRecord! Record had span=" + rec.span + " but caller requested span=" + span + " for position=" + position);
        }
        int targetCol = -1;
        int topMost = Integer.MAX_VALUE;
        int colCount = this.mColCount;
        for (i = 0; i <= colCount - span; i++) {
            int bottom = LinearLayoutManager.INVALID_OFFSET;
            for (int j = i; j < i + span; j++) {
                int singleBottom = this.mItemBottoms[j];
                if (singleBottom > bottom) {
                    bottom = singleBottom;
                }
            }
            if (bottom < topMost) {
                topMost = bottom;
                targetCol = i;
            }
        }
        rec.column = targetCol;
        for (i = 0; i < span; i++) {
            rec.setMarginAbove(i, topMost - this.mItemBottoms[i + targetCol]);
        }
        return rec;
    }

    final View obtainView(int position, View optScrap) {
        View view = this.mRecycler.getTransientStateView(position);
        if (view != null) {
            return view;
        }
        View scrap;
        int optType = optScrap != null ? ((LayoutParams) optScrap.getLayoutParams()).viewType : -1;
        int positionViewType = this.mAdapter.getItemViewType(position);
        if (optType == positionViewType) {
            scrap = optScrap;
        } else {
            scrap = this.mRecycler.getScrapView(positionViewType);
        }
        view = this.mAdapter.getView(position, scrap, this);
        if (!(view == scrap || scrap == null)) {
            this.mRecycler.addScrap(scrap);
        }
        android.view.ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (view.getParent() != this) {
            if (lp == null) {
                lp = generateDefaultLayoutParams();
            } else if (!checkLayoutParams(lp)) {
                lp = generateLayoutParams(lp);
            }
        }
        LayoutParams sglp = (LayoutParams) lp;
        sglp.position = position;
        sglp.viewType = positionViewType;
        return view;
    }

    public ListAdapter getAdapter() {
        return this.mAdapter;
    }

    public void setAdapter(ListAdapter adapter) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mObserver);
        }
        clearAllState();
        this.mAdapter = adapter;
        this.mDataChanged = true;
        int count = adapter != null ? adapter.getCount() : 0;
        this.mItemCount = count;
        this.mOldItemCount = count;
        if (adapter != null) {
            adapter.registerDataSetObserver(this.mObserver);
            this.mRecycler.setViewTypeCount(adapter.getViewTypeCount());
            this.mHasStableIds = adapter.hasStableIds();
        } else {
            this.mHasStableIds = false;
        }
        populate();
    }

    private void clearAllState() {
        this.mLayoutRecords.clear();
        removeAllViews();
        resetStateForGridTop();
        this.mRecycler.clear();
    }

    public void resetStateForGridTop() {
        int colCount = this.mColCount;
        if (this.mItemTops == null || this.mItemTops.length != colCount) {
            this.mItemTops = new int[colCount];
            this.mItemBottoms = new int[colCount];
        }
        int top = getPaddingTop();
        Arrays.fill(this.mItemTops, top);
        Arrays.fill(this.mItemBottoms, top);
        this.mFirstPosition = 0;
        this.mRestoreOffset = 0;
    }

    public void setSelectionToTop() {
        removeAllViews();
        resetStateForGridTop();
        populate();
    }

    public final int getMovedBy() {
        return this.mScrollMovedBy;
    }

    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2);
    }

    protected LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams lp) {
        return lp instanceof LayoutParams;
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        int position = this.mFirstPosition;
        ss.position = position;
        if (position >= 0 && this.mAdapter != null && position < this.mAdapter.getCount()) {
            ss.firstId = this.mAdapter.getItemId(position);
        }
        if (getChildCount() > 0) {
            ss.topOffset = (getChildAt(0).getTop() - this.mItemMargin) - getPaddingTop();
        }
        return ss;
    }

    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mDataChanged = true;
        this.mFirstPosition = ss.position;
        this.mRestoreOffset = ss.topOffset;
        requestLayout();
    }

    public void setScrollUpdateListener(ScrollUpdateListener scrollUpdateListener) {
        this.scrollUpdateListener = scrollUpdateListener;
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }
}
