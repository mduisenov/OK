package ru.ok.android.ui.custom;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationStatusCodes;
import java.lang.reflect.Field;
import java.util.List;
import ru.ok.android.C0206R;
import ru.ok.android.proto.MessagesProto.Message;

public class OverScrollView extends FrameLayout implements OnTouchListener {
    protected View child;
    boolean hasFailedObtainingScrollFields;
    boolean isInFlingMode;
    private int mActivePointerId;
    private View mChildToScrollTo;
    protected Context mContext;
    private boolean mFillViewport;
    private boolean mIsBeingDragged;
    private boolean mIsLayoutDirty;
    private float mLastMotionY;
    private long mLastScroll;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    protected boolean mScrollViewMovedFocus;
    Field mScrollXField;
    Field mScrollYField;
    protected Scroller mScroller;
    private boolean mSmoothScrollingEnabled;
    private final Rect mTempRect;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    DisplayMetrics metrics;
    protected Runnable overScrollerSpringbackTask;
    int prevScrollY;

    /* renamed from: ru.ok.android.ui.custom.OverScrollView.1 */
    class C06221 implements Runnable {
        C06221() {
        }

        public void run() {
            OverScrollView.this.mScroller.computeScrollOffset();
            OverScrollView.this.scrollTo(0, OverScrollView.this.mScroller.getCurrY());
            if (!OverScrollView.this.mScroller.isFinished()) {
                OverScrollView.this.post(this);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.OverScrollView.2 */
    class C06232 implements Runnable {
        C06232() {
        }

        public void run() {
            OverScrollView.this.scrollTo(0, OverScrollView.this.child.getPaddingTop());
        }
    }

    public OverScrollView(Context context) {
        this(context, null);
        this.mContext = context;
        initBounce();
    }

    public OverScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
        initBounce();
    }

    public OverScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mTempRect = new Rect();
        this.isInFlingMode = false;
        this.mIsLayoutDirty = true;
        this.mChildToScrollTo = null;
        this.mIsBeingDragged = false;
        this.mFillViewport = true;
        this.mSmoothScrollingEnabled = true;
        this.mActivePointerId = -1;
        this.mContext = context;
        initScrollView();
        setFillViewport(true);
        initBounce();
    }

    private void initBounce() {
        this.metrics = this.mContext.getResources().getDisplayMetrics();
        this.mScroller = new Scroller(getContext(), new OvershootInterpolator(0.75f));
        this.overScrollerSpringbackTask = new C06221();
        this.prevScrollY = getContainerPaddingTop();
        try {
            this.mScrollXField = View.class.getDeclaredField("mScrollX");
            this.mScrollYField = View.class.getDeclaredField("mScrollY");
        } catch (Exception e) {
            this.hasFailedObtainingScrollFields = true;
        }
    }

    private void SetScrollY(int value) {
        if (this.mScrollYField != null) {
            try {
                this.mScrollYField.setInt(this, value);
            } catch (Exception e) {
            }
        }
    }

    private void SetScrollX(int value) {
        if (this.mScrollXField != null) {
            try {
                this.mScrollXField.setInt(this, value);
            } catch (Exception e) {
            }
        }
    }

    public void initChildPointer() {
        this.child = getChildAt(0);
        this.child.setPadding(0, ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED, 0, ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED);
    }

    protected float getTopFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }
        int length = getVerticalFadingEdgeLength();
        if (getScrollY() < length) {
            return ((float) getScrollY()) / ((float) length);
        }
        return 1.0f;
    }

    protected float getBottomFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }
        int length = getVerticalFadingEdgeLength();
        int span = (getChildAt(0).getBottom() - getScrollY()) - (getContainerHeight() - getContainerPaddingBottom());
        if (span < length) {
            return ((float) span) / ((float) length);
        }
        return 1.0f;
    }

    public int getMaxScrollAmount() {
        return (int) (0.5f * ((float) (getContainerBottom() - getContainerTop())));
    }

    private void initScrollView() {
        this.mScroller = new Scroller(getContext());
        setFocusable(true);
        setDescendantFocusability(262144);
        setWillNotDraw(false);
        ViewConfiguration configuration = ViewConfiguration.get(this.mContext);
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        setOnTouchListener(this);
        post(new C06232());
    }

    public void addView(View child) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }
        super.addView(child);
        initChildPointer();
    }

    public void addView(View child, int index) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }
        super.addView(child, index);
        initChildPointer();
    }

    public void addView(View child, LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }
        super.addView(child, params);
        initChildPointer();
    }

    public void addView(View child, int index, LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }
        super.addView(child, index, params);
    }

    private boolean canScroll() {
        View child = getChildAt(0);
        if (child == null) {
            return false;
        }
        if (getContainerHeight() < (getContainerPaddingTop() + child.getHeight()) + getContainerPaddingBottom()) {
            return true;
        }
        return false;
    }

    public void setFillViewport(boolean fillViewport) {
        if (fillViewport != this.mFillViewport) {
            this.mFillViewport = fillViewport;
            requestLayout();
        }
    }

    public void setSmoothScrollingEnabled(boolean smoothScrollingEnabled) {
        this.mSmoothScrollingEnabled = smoothScrollingEnabled;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mFillViewport && MeasureSpec.getMode(heightMeasureSpec) != 0 && getChildCount() > 0) {
            View child = getChildAt(0);
            int height = getMeasuredHeight();
            if (child.getMeasuredHeight() < height) {
                child.measure(getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), ((FrameLayout.LayoutParams) child.getLayoutParams()).width), MeasureSpec.makeMeasureSpec((height - getContainerPaddingTop()) - getContainerPaddingBottom(), 1073741824));
            }
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event) || executeKeyEvent(event);
    }

    public boolean executeKeyEvent(KeyEvent event) {
        this.mTempRect.setEmpty();
        if (canScroll()) {
            boolean handled = false;
            if (event.getAction() == 0) {
                switch (event.getKeyCode()) {
                    case C0206R.styleable.Toolbar_collapseContentDescription /*19*/:
                        if (!event.isAltPressed()) {
                            handled = arrowScroll(33);
                            break;
                        }
                        handled = fullScroll(33);
                        break;
                    case C0206R.styleable.Toolbar_navigationIcon /*20*/:
                        if (!event.isAltPressed()) {
                            handled = arrowScroll(130);
                            break;
                        }
                        handled = fullScroll(130);
                        break;
                    case C0206R.styleable.Theme_editTextColor /*62*/:
                        pageScroll(event.isShiftPressed() ? 33 : 130);
                        break;
                }
            }
            return handled;
        } else if (!isFocused() || event.getKeyCode() == 4) {
            return false;
        } else {
            View currentFocused = findFocus();
            if (currentFocused == this) {
                currentFocused = null;
            }
            View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, 130);
            if (nextFocused == null || nextFocused == this || !nextFocused.requestFocus(130)) {
                return false;
            }
            return true;
        }
    }

    public boolean inChild(int x, int y) {
        if (getChildCount() <= 0) {
            return false;
        }
        int scrollY = getScrollY();
        View child = getChildAt(0);
        if (y < child.getTop() - scrollY || y >= child.getBottom() - scrollY || x < child.getLeft() || x >= child.getRight()) {
            return false;
        }
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean z = true;
        int action = ev.getAction();
        if (action == 2 && this.mIsBeingDragged) {
            return true;
        }
        float y;
        switch (action & MotionEventCompat.ACTION_MASK) {
            case RECEIVED_VALUE:
                y = ev.getY();
                if (!inChild((int) ev.getX(), (int) y)) {
                    this.mIsBeingDragged = false;
                    break;
                }
                this.mLastMotionY = y;
                this.mActivePointerId = ev.getPointerId(0);
                if (this.mScroller.isFinished()) {
                    z = false;
                }
                this.mIsBeingDragged = z;
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                this.mIsBeingDragged = false;
                this.mActivePointerId = -1;
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                int activePointerId = this.mActivePointerId;
                if (activePointerId != -1) {
                    y = ev.getY(ev.findPointerIndex(activePointerId));
                    if (((int) Math.abs(y - this.mLastMotionY)) > this.mTouchSlop) {
                        this.mIsBeingDragged = true;
                        this.mLastMotionY = y;
                        break;
                    }
                }
                break;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                onSecondaryPointerUp(ev);
                break;
        }
        return this.mIsBeingDragged;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0 && ev.getEdgeFlags() != 0) {
            return false;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
        float y;
        switch (ev.getAction() & MotionEventCompat.ACTION_MASK) {
            case RECEIVED_VALUE:
                y = ev.getY();
                boolean inChild = inChild((int) ev.getX(), (int) y);
                this.mIsBeingDragged = inChild;
                if (inChild) {
                    if (!this.mScroller.isFinished()) {
                        this.mScroller.abortAnimation();
                    }
                    this.mLastMotionY = y;
                    this.mActivePointerId = ev.getPointerId(0);
                    break;
                }
                return false;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (this.mIsBeingDragged) {
                    VelocityTracker velocityTracker = this.mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, (float) this.mMaximumVelocity);
                    int initialVelocity = (int) velocityTracker.getYVelocity(this.mActivePointerId);
                    if (getChildCount() > 0 && Math.abs(initialVelocity) > this.mMinimumVelocity) {
                        fling(-initialVelocity);
                    }
                    this.mActivePointerId = -1;
                    this.mIsBeingDragged = false;
                    if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.recycle();
                        this.mVelocityTracker = null;
                        break;
                    }
                }
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (this.mIsBeingDragged) {
                    y = ev.getY(ev.findPointerIndex(this.mActivePointerId));
                    int deltaY = (int) (this.mLastMotionY - y);
                    this.mLastMotionY = y;
                    if (!isOverScrolled()) {
                        scrollBy(0, deltaY);
                        break;
                    }
                    scrollBy(0, deltaY / 2);
                    break;
                }
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                if (this.mIsBeingDragged && getChildCount() > 0) {
                    this.mActivePointerId = -1;
                    this.mIsBeingDragged = false;
                    if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.recycle();
                        this.mVelocityTracker = null;
                        break;
                    }
                }
                break;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                onSecondaryPointerUp(ev);
                break;
        }
        return true;
    }

    public boolean isOverScrolled() {
        return getScrollY() < this.child.getPaddingTop() || getScrollY() > (this.child.getBottom() - this.child.getPaddingBottom()) - getContainerHeight();
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = (ev.getAction() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
        if (ev.getPointerId(pointerIndex) == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            this.mLastMotionY = ev.getY(newPointerIndex);
            this.mActivePointerId = ev.getPointerId(newPointerIndex);
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.clear();
            }
        }
    }

    protected View findFocusableViewInMyBounds(boolean topFocus, int top, View preferredFocusable) {
        int fadingEdgeLength = getVerticalFadingEdgeLength() / 2;
        int topWithoutFadingEdge = top + fadingEdgeLength;
        int bottomWithoutFadingEdge = (getContainerHeight() + top) - fadingEdgeLength;
        return (preferredFocusable == null || preferredFocusable.getTop() >= bottomWithoutFadingEdge || preferredFocusable.getBottom() <= topWithoutFadingEdge) ? findFocusableViewInBounds(topFocus, topWithoutFadingEdge, bottomWithoutFadingEdge) : preferredFocusable;
    }

    private View findFocusableViewInBounds(boolean topFocus, int top, int bottom) {
        List<View> focusables = getFocusables(2);
        View focusCandidate = null;
        boolean foundFullyContainedFocusable = false;
        int count = focusables.size();
        for (int i = 0; i < count; i++) {
            View view = (View) focusables.get(i);
            int viewTop = view.getTop();
            int viewBottom = view.getBottom();
            if (top < viewBottom && viewTop < bottom) {
                boolean viewIsFullyContained = top < viewTop && viewBottom < bottom;
                if (focusCandidate == null) {
                    focusCandidate = view;
                    foundFullyContainedFocusable = viewIsFullyContained;
                } else {
                    boolean viewIsCloserToBoundary = (topFocus && viewTop < focusCandidate.getTop()) || (!topFocus && viewBottom > focusCandidate.getBottom());
                    if (foundFullyContainedFocusable) {
                        if (viewIsFullyContained && viewIsCloserToBoundary) {
                            focusCandidate = view;
                        }
                    } else if (viewIsFullyContained) {
                        focusCandidate = view;
                        foundFullyContainedFocusable = true;
                    } else if (viewIsCloserToBoundary) {
                        focusCandidate = view;
                    }
                }
            }
        }
        return focusCandidate;
    }

    public boolean pageScroll(int direction) {
        boolean down;
        if (direction == 130) {
            down = true;
        } else {
            down = false;
        }
        int height = getContainerHeight();
        if (down) {
            this.mTempRect.top = getScrollY() + height;
            int count = getChildCount();
            if (count > 0) {
                View view = getChildAt(count - 1);
                if (this.mTempRect.top + height > view.getBottom()) {
                    this.mTempRect.top = view.getBottom() - height;
                }
            }
        } else {
            this.mTempRect.top = getScrollY() - height;
            if (this.mTempRect.top < 0) {
                this.mTempRect.top = 0;
            }
        }
        this.mTempRect.bottom = this.mTempRect.top + height;
        return scrollAndFocus(direction, this.mTempRect.top, this.mTempRect.bottom);
    }

    public boolean fullScroll(int direction) {
        boolean down;
        if (direction == 130) {
            down = true;
        } else {
            down = false;
        }
        int height = getContainerHeight();
        this.mTempRect.top = 0;
        this.mTempRect.bottom = height;
        if (down) {
            int count = getChildCount();
            if (count > 0) {
                View view = getChildAt(count - 1);
                this.mTempRect.bottom = view.getBottom();
                this.mTempRect.top = this.mTempRect.bottom - height;
            }
        }
        return scrollAndFocus(direction, this.mTempRect.top, this.mTempRect.bottom);
    }

    private boolean scrollAndFocus(int direction, int top, int bottom) {
        boolean up;
        boolean handled = true;
        int height = getContainerHeight();
        int containerTop = getScrollY();
        int containerBottom = containerTop + height;
        if (direction == 33) {
            up = true;
        } else {
            up = false;
        }
        View newFocused = findFocusableViewInBounds(up, top, bottom);
        if (newFocused == null) {
            newFocused = this;
        }
        if (top < containerTop || bottom > containerBottom) {
            doScrollY(up ? top - containerTop : bottom - containerBottom);
        } else {
            handled = false;
        }
        if (newFocused != findFocus() && newFocused.requestFocus(direction)) {
            this.mScrollViewMovedFocus = true;
            this.mScrollViewMovedFocus = false;
        }
        return handled;
    }

    public boolean arrowScroll(int direction) {
        View currentFocused = findFocus();
        if (currentFocused == this) {
            currentFocused = null;
        }
        View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, direction);
        int maxJump = getMaxScrollAmount();
        if (nextFocused == null || !isWithinDeltaOfScreen(nextFocused, maxJump, getContainerHeight())) {
            int scrollDelta = maxJump;
            if (direction == 33 && getScrollY() < scrollDelta) {
                scrollDelta = getScrollY();
            } else if (direction == 130 && getChildCount() > 0) {
                int daBottom = getChildAt(0).getBottom();
                int screenBottom = getScrollY() + getContainerHeight();
                if (daBottom - screenBottom < maxJump) {
                    scrollDelta = daBottom - screenBottom;
                }
            }
            if (scrollDelta == 0) {
                return false;
            }
            int i;
            if (direction == 130) {
                i = scrollDelta;
            } else {
                i = -scrollDelta;
            }
            doScrollY(i);
        } else {
            nextFocused.getDrawingRect(this.mTempRect);
            offsetDescendantRectToMyCoords(nextFocused, this.mTempRect);
            doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
            nextFocused.requestFocus(direction);
        }
        if (currentFocused != null && currentFocused.isFocused() && isOffScreen(currentFocused)) {
            int descendantFocusability = getDescendantFocusability();
            setDescendantFocusability(131072);
            requestFocus();
            setDescendantFocusability(descendantFocusability);
        }
        return true;
    }

    private boolean isOffScreen(View descendant) {
        return !isWithinDeltaOfScreen(descendant, 0, getContainerHeight());
    }

    private boolean isWithinDeltaOfScreen(View descendant, int delta, int height) {
        descendant.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(descendant, this.mTempRect);
        return this.mTempRect.bottom + delta >= getScrollY() && this.mTempRect.top - delta <= getScrollY() + height;
    }

    private void doScrollY(int delta) {
        if (delta == 0) {
            return;
        }
        if (this.mSmoothScrollingEnabled) {
            smoothScrollBy(0, delta);
        } else {
            scrollBy(0, delta);
        }
    }

    public final void smoothScrollBy(int dx, int dy) {
        if (getChildCount() != 0) {
            if (AnimationUtils.currentAnimationTimeMillis() - this.mLastScroll > 250) {
                int maxY = Math.max(0, getChildAt(0).getHeight() - ((getContainerHeight() - getContainerPaddingBottom()) - getContainerPaddingTop()));
                int scrollY = getScrollY();
                this.mScroller.startScroll(getScrollX(), scrollY, 0, Math.max(0, Math.min(scrollY + dy, maxY)) - scrollY);
                invalidate();
            } else {
                if (!this.mScroller.isFinished()) {
                    this.mScroller.abortAnimation();
                }
                scrollBy(dx, dy);
            }
            this.mLastScroll = AnimationUtils.currentAnimationTimeMillis();
        }
    }

    protected int computeVerticalScrollRange() {
        return getChildCount() == 0 ? (getContainerHeight() - getContainerPaddingBottom()) - getContainerPaddingTop() : getChildAt(0).getBottom();
    }

    protected int computeVerticalScrollOffset() {
        return Math.max(0, super.computeVerticalScrollOffset());
    }

    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        child.measure(getChildMeasureSpec(parentWidthMeasureSpec, getPaddingLeft() + getPaddingRight(), child.getLayoutParams().width), MeasureSpec.makeMeasureSpec(0, 0));
    }

    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        child.measure(getChildMeasureSpec(parentWidthMeasureSpec, (((getPaddingLeft() + getPaddingRight()) + lp.leftMargin) + lp.rightMargin) + widthUsed, lp.width), MeasureSpec.makeMeasureSpec(lp.topMargin + lp.bottomMargin, 0));
    }

    public void computeScroll() {
        if (this.hasFailedObtainingScrollFields) {
            super.computeScroll();
        } else if (this.mScroller.computeScrollOffset()) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = this.mScroller.getCurrX();
            int y = this.mScroller.getCurrY();
            if (getChildCount() > 0) {
                View child = getChildAt(0);
                x = clamp(x, (getWidth() - getPaddingRight()) - getPaddingLeft(), child.getWidth());
                y = clamp(y, (getContainerHeight() - getContainerPaddingBottom()) - getContainerPaddingTop(), child.getHeight());
                if (!(x == oldX && y == oldY)) {
                    SetScrollX(x);
                    SetScrollY(y);
                    onScrollChanged(x, y, oldX, oldY);
                }
            }
            awakenScrollBars();
            postInvalidate();
        }
    }

    private void scrollToChild(View child) {
        child.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(child, this.mTempRect);
        int scrollDelta = computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
        if (scrollDelta != 0) {
            scrollBy(0, scrollDelta);
        }
    }

    private boolean scrollToChildRect(Rect rect, boolean immediate) {
        boolean scroll;
        int delta = computeScrollDeltaToGetChildRectOnScreen(rect);
        if (delta != 0) {
            scroll = true;
        } else {
            scroll = false;
        }
        if (scroll) {
            if (immediate) {
                scrollBy(0, delta);
            } else {
                smoothScrollBy(0, delta);
            }
        }
        return scroll;
    }

    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        if (getChildCount() == 0) {
            return 0;
        }
        int height = getContainerHeight();
        int screenTop = getScrollY();
        int screenBottom = screenTop + height;
        int fadingEdge = getVerticalFadingEdgeLength();
        if (rect.top > 0) {
            screenTop += fadingEdge;
        }
        if (rect.bottom < getChildAt(0).getHeight()) {
            screenBottom -= fadingEdge;
        }
        int scrollYDelta;
        if (rect.bottom > screenBottom && rect.top > screenTop) {
            if (rect.height() > height) {
                scrollYDelta = 0 + (rect.top - screenTop);
            } else {
                scrollYDelta = 0 + (rect.bottom - screenBottom);
            }
            return Math.min(scrollYDelta, getChildAt(0).getBottom() - screenBottom);
        } else if (rect.top >= screenTop || rect.bottom >= screenBottom) {
            return 0;
        } else {
            if (rect.height() > height) {
                scrollYDelta = 0 - (screenBottom - rect.bottom);
            } else {
                scrollYDelta = 0 - (screenTop - rect.top);
            }
            return Math.max(scrollYDelta, -getScrollY());
        }
    }

    public void requestChildFocus(View child, View focused) {
        if (!this.mScrollViewMovedFocus) {
            if (this.mIsLayoutDirty) {
                this.mChildToScrollTo = focused;
            } else {
                scrollToChild(focused);
            }
        }
        super.requestChildFocus(child, focused);
    }

    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        if (direction == 2) {
            direction = 130;
        } else if (direction == 1) {
            direction = 33;
        }
        View nextFocus = previouslyFocusedRect == null ? FocusFinder.getInstance().findNextFocus(this, null, direction) : FocusFinder.getInstance().findNextFocusFromRect(this, previouslyFocusedRect, direction);
        if (nextFocus == null || isOffScreen(nextFocus)) {
            return false;
        }
        return nextFocus.requestFocus(direction, previouslyFocusedRect);
    }

    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
        return scrollToChildRect(rectangle, immediate);
    }

    public void requestLayout() {
        this.mIsLayoutDirty = true;
        super.requestLayout();
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.mIsLayoutDirty = false;
        if (this.mChildToScrollTo != null && isViewDescendantOf(this.mChildToScrollTo, this)) {
            scrollToChild(this.mChildToScrollTo);
        }
        this.mChildToScrollTo = null;
        scrollTo(getScrollX(), getScrollY());
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        View currentFocused = findFocus();
        if (currentFocused != null && this != currentFocused && isWithinDeltaOfScreen(currentFocused, 0, oldh)) {
            currentFocused.getDrawingRect(this.mTempRect);
            offsetDescendantRectToMyCoords(currentFocused, this.mTempRect);
            doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
        }
    }

    protected void onScrollChanged(int leftOfVisibleView, int topOfVisibleView, int oldLeftOfVisibleView, int oldTopOfVisibleView) {
        int displayHeight = getContainerHeight();
        int paddingTop = this.child.getPaddingTop();
        int contentBottom = this.child.getHeight() - this.child.getPaddingBottom();
        if (!this.isInFlingMode || (topOfVisibleView >= paddingTop && topOfVisibleView <= contentBottom - displayHeight)) {
            super.onScrollChanged(leftOfVisibleView, topOfVisibleView, oldLeftOfVisibleView, oldTopOfVisibleView);
            return;
        }
        if (topOfVisibleView < paddingTop) {
            this.mScroller.startScroll(0, topOfVisibleView, 0, paddingTop - topOfVisibleView, LocationStatusCodes.GEOFENCE_NOT_AVAILABLE);
        } else if (topOfVisibleView > contentBottom - displayHeight) {
            this.mScroller.startScroll(0, topOfVisibleView, 0, (contentBottom - displayHeight) - topOfVisibleView, LocationStatusCodes.GEOFENCE_NOT_AVAILABLE);
        }
        post(this.overScrollerSpringbackTask);
        this.isInFlingMode = false;
    }

    private boolean isViewDescendantOf(View child, View parent) {
        if (child == parent) {
            return true;
        }
        ViewParent theParent = child.getParent();
        if ((theParent instanceof ViewGroup) && isViewDescendantOf((View) theParent, parent)) {
            return true;
        }
        return false;
    }

    public void fling(int velocityY) {
        if (getChildCount() > 0) {
            boolean movingDown;
            int height = (getContainerHeight() - getContainerPaddingBottom()) - getContainerPaddingTop();
            this.mScroller.fling(getScrollX(), getScrollY(), 0, velocityY, 0, 0, 0, Math.max(0, getChildAt(0).getHeight() - height));
            if (velocityY > 0) {
                movingDown = true;
            } else {
                movingDown = false;
            }
            View newFocused = findFocusableViewInMyBounds(movingDown, this.mScroller.getFinalY(), findFocus());
            if (newFocused == null) {
                newFocused = this;
            }
            if (newFocused != findFocus()) {
                if (newFocused.requestFocus(movingDown ? 130 : 33)) {
                    this.mScrollViewMovedFocus = true;
                    this.mScrollViewMovedFocus = false;
                }
            }
            invalidate();
        }
    }

    public void scrollTo(int x, int y) {
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            x = clamp(x, (getWidth() - getPaddingRight()) - getPaddingLeft(), child.getWidth());
            y = clamp(y, (getContainerHeight() - getContainerPaddingBottom()) - getContainerPaddingTop(), child.getHeight());
            if (x != getScrollX() || y != getScrollY()) {
                super.scrollTo(x, y);
            }
        }
    }

    private int clamp(int n, int my, int child) {
        if (my >= child || n < 0) {
            return 0;
        }
        if (my + n > child) {
            return child - my;
        }
        return n;
    }

    public boolean onTouch(View v, MotionEvent event) {
        this.mScroller.forceFinished(true);
        removeCallbacks(this.overScrollerSpringbackTask);
        if (event.getAction() == 1) {
            return overScrollView();
        }
        if (event.getAction() == 3) {
            return overScrollView();
        }
        return false;
    }

    protected boolean overScrollView() {
        int scrollBy;
        int displayHeight = getContainerHeight();
        int contentTop = this.child.getPaddingTop();
        int contentBottom = this.child.getHeight() - this.child.getPaddingBottom();
        int currScrollY = getScrollY();
        if (currScrollY < contentTop) {
            onOverScroll(currScrollY);
            scrollBy = contentTop - currScrollY;
        } else if (currScrollY + displayHeight > contentBottom) {
            if ((this.child.getHeight() - this.child.getPaddingTop()) - this.child.getPaddingBottom() < displayHeight) {
                scrollBy = contentTop - currScrollY;
            } else {
                scrollBy = (contentBottom - displayHeight) - currScrollY;
            }
            scrollBy += onOverScroll(currScrollY);
        } else {
            this.isInFlingMode = true;
            return false;
        }
        this.mScroller.startScroll(0, currScrollY, 0, scrollBy, 500);
        post(this.overScrollerSpringbackTask);
        this.prevScrollY = currScrollY;
        return true;
    }

    protected int onOverScroll(int scrollY) {
        return 0;
    }

    protected int getContainerHeight() {
        return getHeight();
    }

    protected int getContainerTop() {
        return getTop();
    }

    protected int getContainerBottom() {
        return getBottom();
    }

    protected int getContainerPaddingTop() {
        return getPaddingTop();
    }

    protected int getContainerPaddingBottom() {
        return getPaddingBottom();
    }
}
