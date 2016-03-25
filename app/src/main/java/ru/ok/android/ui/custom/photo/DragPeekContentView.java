package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;
import com.google.android.gms.location.LocationStatusCodes;
import ru.ok.android.proto.MessagesProto.Message;

public class DragPeekContentView extends FrameLayout {
    protected Scroller mBounceBackScroller;
    protected View mChildView;
    protected boolean mDragging;
    protected Rect mHitRect;
    protected float mLastPointerY;
    protected int mMaximumVelocity;
    private boolean mMeasured;
    protected int mMinVisibilityHeight;
    protected int mMinimumVelocity;
    protected Scroller mScroller;
    protected int mTouchSlop;
    protected VelocityTracker mVelocityTracker;
    private OnScrollChangeListener onScrollChangeListener;

    public interface OnScrollChangeListener {
        void onScrollChanged(int i, int i2, int i3, int i4);
    }

    public DragPeekContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mHitRect = new Rect();
        onCreate();
    }

    public DragPeekContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mHitRect = new Rect();
        onCreate();
    }

    public DragPeekContentView(Context context) {
        super(context);
        this.mHitRect = new Rect();
        onCreate();
    }

    private void onCreate() {
        this.mBounceBackScroller = new Scroller(getContext());
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        this.mMinVisibilityHeight = getResources().getDimensionPixelSize(2131230953);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mChildView = getChildAt(0);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childHeight = this.mChildView.getMeasuredHeight();
        if (!this.mMeasured && childHeight > 0 && childHeight > this.mMinVisibilityHeight) {
            scrollBy(0, -(childHeight - this.mMinVisibilityHeight));
            this.mMeasured = true;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.mScroller != null && !this.mScroller.isFinished()) {
            return true;
        }
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case RECEIVED_VALUE:
                if (!isChildTouched(x, y)) {
                    return false;
                }
                this.mLastPointerY = y;
                if (this.mVelocityTracker == null) {
                    this.mVelocityTracker = VelocityTracker.obtain();
                }
                this.mVelocityTracker.addMovement(event);
                return false;
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                reset();
                return false;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (Math.abs(this.mLastPointerY - y) < ((float) this.mTouchSlop)) {
                    return false;
                }
                this.mLastPointerY = y;
                this.mDragging = true;
                return true;
            default:
                return false;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isShown() || !isEnabled()) {
            return false;
        }
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case RECEIVED_VALUE:
                if (!isChildTouched(x, y)) {
                    return false;
                }
                this.mDragging = true;
                this.mLastPointerY = y;
                if (this.mVelocityTracker == null) {
                    this.mVelocityTracker = VelocityTracker.obtain();
                }
                this.mVelocityTracker.addMovement(event);
                if (!(this.mScroller == null || this.mScroller.isFinished())) {
                    this.mScroller.abortAnimation();
                }
                return true;
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                if (!this.mDragging) {
                    return false;
                }
                this.mVelocityTracker.computeCurrentVelocity(LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, (float) this.mMaximumVelocity);
                if (Math.abs((int) this.mVelocityTracker.getYVelocity()) > this.mMinimumVelocity) {
                    bounceBack();
                    reset();
                } else {
                    bounceBack();
                    reset();
                }
                return true;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (!this.mDragging) {
                    return false;
                }
                this.mVelocityTracker.addMovement(event);
                scrollBy(0, (int) calculateDeltaY(x, y));
                this.mLastPointerY = y;
                return true;
            default:
                return false;
        }
    }

    private void reset() {
        this.mDragging = false;
        this.mLastPointerY = -1.0f;
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    public void computeScroll() {
        if (this.mBounceBackScroller.computeScrollOffset()) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            int scrollY = this.mBounceBackScroller.getCurrY();
            if (scrollY != oldY) {
                setScrollY(scrollY);
                onScrollChanged(oldX, scrollY, oldX, oldY);
            }
            postInvalidate();
        }
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.onScrollChangeListener != null) {
            this.onScrollChangeListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    private void bounceBack() {
        int currY = getScrollY();
        if (currY > 0) {
            this.mBounceBackScroller.startScroll(0, currY, 0, -currY, 750);
        } else {
            int minY = -(this.mChildView.getHeight() - this.mMinVisibilityHeight);
            if (currY < minY) {
                this.mBounceBackScroller.startScroll(0, currY, 0, Math.abs(minY - currY), 750);
            }
        }
        invalidate();
    }

    private boolean isChildTouched(float x, float y) {
        this.mHitRect.top = getChildTop();
        this.mHitRect.left = this.mChildView.getLeft();
        this.mHitRect.bottom = getChildBottom();
        this.mHitRect.right = this.mChildView.getRight();
        return this.mHitRect.contains((int) x, (int) y);
    }

    private int getOverscroll() {
        int childBottom = getChildBottom();
        if (childBottom < getHeight()) {
            return getHeight() - childBottom;
        }
        int childTop = getChildTop();
        int bottomEdge = getHeight() - this.mMinVisibilityHeight;
        if (childTop > bottomEdge) {
            return bottomEdge - childTop;
        }
        return 0;
    }

    private int getChildTop() {
        return this.mChildView.getTop() - getScrollY();
    }

    private int getChildBottom() {
        return this.mChildView.getBottom() - getScrollY();
    }

    private float calculateDeltaY(float x, float y) {
        float deltaY = this.mLastPointerY - y;
        int overScroll = getOverscroll();
        if (overScroll > 0) {
            return deltaY / 2.0f;
        }
        if (overScroll < 0) {
            return deltaY / 2.0f;
        }
        return deltaY;
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }
}
