package ru.ok.android.ui.swiperefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import ru.ok.android.proto.MessagesProto.Message;

public class SwipeUpRefreshLayout extends ViewGroup {
    private static final int[] LAYOUT_ATTRS;
    private static final String LOG_TAG;
    private int mActivePointerId;
    private Animation mAlphaMaxAnimation;
    private Animation mAlphaStartAnimation;
    private final Animation mAnimateToCorrectPosition;
    private final Animation mAnimateToStartPosition;
    private int mCircleHeight;
    private CircleImageView mCircleView;
    private int mCircleViewIndex;
    private int mCircleWidth;
    private int mCurrentTargetOffsetTop;
    private final DecelerateInterpolator mDecelerateInterpolator;
    protected int mFrom;
    private float mInitialMotionY;
    private boolean mIsBeingDragged;
    private OnRefreshListener mListener;
    private int mMediumAnimationDuration;
    private boolean mNotify;
    private boolean mOriginalOffsetCalculated;
    protected int mOriginalOffsetTop;
    private MaterialProgressDrawable mProgress;
    private AnimationListener mRefreshListener;
    private boolean mRefreshing;
    private boolean mReturningToStart;
    private boolean mScale;
    private Animation mScaleAnimation;
    private Animation mScaleDownAnimation;
    private Animation mScaleDownToStartAnimation;
    private float mSpinnerFinalOffset;
    private float mStartingScale;
    private View mTarget;
    private float mTotalDragDistance;
    private int mTouchSlop;
    private boolean mUsingCustomStart;

    /* renamed from: ru.ok.android.ui.swiperefresh.SwipeUpRefreshLayout.1 */
    class C12741 implements AnimationListener {
        C12741() {
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            if (SwipeUpRefreshLayout.this.mRefreshing) {
                SwipeUpRefreshLayout.this.mProgress.setAlpha(MotionEventCompat.ACTION_MASK);
                SwipeUpRefreshLayout.this.mProgress.start();
                if (SwipeUpRefreshLayout.this.mNotify && SwipeUpRefreshLayout.this.mListener != null) {
                    SwipeUpRefreshLayout.this.mListener.onRefresh();
                }
            } else {
                SwipeUpRefreshLayout.this.mProgress.stop();
                SwipeUpRefreshLayout.this.mCircleView.setVisibility(8);
                SwipeUpRefreshLayout.this.setColorViewAlpha(MotionEventCompat.ACTION_MASK);
                if (SwipeUpRefreshLayout.this.mScale) {
                    SwipeUpRefreshLayout.this.setAnimationProgress(0.0f);
                } else {
                    SwipeUpRefreshLayout.this.setTargetOffsetCircleTopFromBottom(SwipeUpRefreshLayout.this.mOriginalOffsetTop);
                }
            }
            SwipeUpRefreshLayout.this.mCurrentTargetOffsetTop = SwipeUpRefreshLayout.this.getCurrentTargetOffsetTopByCircleTop();
        }
    }

    /* renamed from: ru.ok.android.ui.swiperefresh.SwipeUpRefreshLayout.2 */
    class C12752 extends Animation {
        C12752() {
        }

        public void applyTransformation(float interpolatedTime, Transformation t) {
            SwipeUpRefreshLayout.this.setAnimationProgress(interpolatedTime);
        }
    }

    /* renamed from: ru.ok.android.ui.swiperefresh.SwipeUpRefreshLayout.3 */
    class C12763 extends Animation {
        C12763() {
        }

        public void applyTransformation(float interpolatedTime, Transformation t) {
            SwipeUpRefreshLayout.this.setAnimationProgress(1.0f - interpolatedTime);
        }
    }

    /* renamed from: ru.ok.android.ui.swiperefresh.SwipeUpRefreshLayout.4 */
    class C12774 extends Animation {
        final /* synthetic */ int val$endingAlpha;
        final /* synthetic */ int val$startingAlpha;

        C12774(int i, int i2) {
            this.val$startingAlpha = i;
            this.val$endingAlpha = i2;
        }

        public void applyTransformation(float interpolatedTime, Transformation t) {
            SwipeUpRefreshLayout.this.mProgress.setAlpha((int) (((float) this.val$startingAlpha) + (((float) (this.val$endingAlpha - this.val$startingAlpha)) * interpolatedTime)));
        }
    }

    /* renamed from: ru.ok.android.ui.swiperefresh.SwipeUpRefreshLayout.5 */
    class C12785 implements AnimationListener {
        C12785() {
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            if (!SwipeUpRefreshLayout.this.mScale) {
                SwipeUpRefreshLayout.this.startScaleDownAnimation(null);
            }
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    /* renamed from: ru.ok.android.ui.swiperefresh.SwipeUpRefreshLayout.6 */
    class C12796 extends Animation {
        C12796() {
        }

        public void applyTransformation(float interpolatedTime, Transformation t) {
            int endTarget;
            if (SwipeUpRefreshLayout.this.mUsingCustomStart) {
                endTarget = (int) SwipeUpRefreshLayout.this.mSpinnerFinalOffset;
            } else {
                endTarget = (int) (SwipeUpRefreshLayout.this.mSpinnerFinalOffset - ((float) Math.abs(SwipeUpRefreshLayout.this.mOriginalOffsetTop)));
            }
            SwipeUpRefreshLayout.this.setTargetOffsetCircleTopFromBottom(SwipeUpRefreshLayout.this.mFrom + ((int) (((float) (endTarget - SwipeUpRefreshLayout.this.mFrom)) * interpolatedTime)));
        }
    }

    /* renamed from: ru.ok.android.ui.swiperefresh.SwipeUpRefreshLayout.7 */
    class C12807 extends Animation {
        C12807() {
        }

        public void applyTransformation(float interpolatedTime, Transformation t) {
            SwipeUpRefreshLayout.this.moveToStart(interpolatedTime);
        }
    }

    /* renamed from: ru.ok.android.ui.swiperefresh.SwipeUpRefreshLayout.8 */
    class C12818 extends Animation {
        C12818() {
        }

        public void applyTransformation(float interpolatedTime, Transformation t) {
            SwipeUpRefreshLayout.this.setAnimationProgress(SwipeUpRefreshLayout.this.mStartingScale + ((-SwipeUpRefreshLayout.this.mStartingScale) * interpolatedTime));
            SwipeUpRefreshLayout.this.moveToStart(interpolatedTime);
        }
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    static {
        LOG_TAG = SwipeUpRefreshLayout.class.getSimpleName();
        LAYOUT_ATTRS = new int[]{16842766};
    }

    private int getCurrentTargetOffsetTopByCircleTop() {
        return getMeasuredHeight() - this.mCircleView.getTop();
    }

    private void setColorViewAlpha(int targetAlpha) {
        this.mCircleView.getBackground().setAlpha(targetAlpha);
        this.mProgress.setAlpha(targetAlpha);
    }

    public void setProgressViewOffset(boolean scale, int start, int end) {
        this.mScale = scale;
        this.mCircleView.setVisibility(8);
        this.mCurrentTargetOffsetTop = start;
        this.mOriginalOffsetTop = start;
        this.mSpinnerFinalOffset = (float) end;
        this.mUsingCustomStart = true;
        this.mCircleView.invalidate();
    }

    public void setProgressViewOffset(boolean scale, int offset) {
        this.mScale = scale;
        this.mCircleView.setVisibility(8);
        this.mCurrentTargetOffsetTop = offset;
        this.mOriginalOffsetTop = offset;
        this.mSpinnerFinalOffset += (float) offset;
        this.mUsingCustomStart = true;
        this.mCircleView.invalidate();
    }

    public void setProgressViewEndTarget(boolean scale, int end) {
        this.mSpinnerFinalOffset = (float) end;
        this.mScale = scale;
        this.mCircleView.invalidate();
    }

    public void setSize(int size) {
        if (size == 0 || size == 1) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int i;
            if (size == 0) {
                i = (int) (56.0f * metrics.density);
                this.mCircleWidth = i;
                this.mCircleHeight = i;
            } else {
                i = (int) (40.0f * metrics.density);
                this.mCircleWidth = i;
                this.mCircleHeight = i;
            }
            this.mCircleView.setImageDrawable(null);
            this.mProgress.updateSizes(size);
            this.mCircleView.setImageDrawable(this.mProgress);
        }
    }

    public SwipeUpRefreshLayout(Context context) {
        this(context, null);
    }

    public SwipeUpRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRefreshing = false;
        this.mTotalDragDistance = -1.0f;
        this.mOriginalOffsetCalculated = false;
        this.mActivePointerId = -1;
        this.mCircleViewIndex = -1;
        this.mRefreshListener = new C12741();
        this.mAnimateToCorrectPosition = new C12796();
        this.mAnimateToStartPosition = new C12807();
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mMediumAnimationDuration = getResources().getInteger(17694721);
        setWillNotDraw(false);
        this.mDecelerateInterpolator = new DecelerateInterpolator(2.0f);
        TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        this.mCircleWidth = (int) (metrics.density * 40.0f);
        this.mCircleHeight = (int) (metrics.density * 40.0f);
        createProgressView();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        this.mSpinnerFinalOffset = 64.0f * metrics.density;
        this.mTotalDragDistance = this.mSpinnerFinalOffset;
    }

    protected int getChildDrawingOrder(int childCount, int i) {
        if (this.mCircleViewIndex < 0) {
            return i;
        }
        if (i == childCount - 1) {
            return this.mCircleViewIndex;
        }
        if (i >= this.mCircleViewIndex) {
            return i + 1;
        }
        return i;
    }

    private void createProgressView() {
        this.mCircleView = new CircleImageView(getContext(), -328966, 20.0f);
        this.mProgress = new MaterialProgressDrawable(getContext(), this.mCircleView);
        this.mProgress.setBackgroundColor(-328966);
        this.mCircleView.setImageDrawable(this.mProgress);
        this.mCircleView.setVisibility(8);
        addView(this.mCircleView);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mListener = listener;
    }

    public void setRefreshing(boolean refreshing) {
        if (!refreshing || this.mRefreshing == refreshing) {
            setRefreshing(refreshing, false);
            return;
        }
        int endTarget;
        this.mRefreshing = refreshing;
        if (this.mUsingCustomStart) {
            endTarget = (int) this.mSpinnerFinalOffset;
        } else {
            endTarget = (int) (this.mSpinnerFinalOffset + ((float) this.mOriginalOffsetTop));
        }
        setTargetOffsetCircleTopFromBottom(endTarget);
        this.mNotify = false;
        startScaleUpAnimation(this.mRefreshListener);
    }

    private void startScaleUpAnimation(AnimationListener listener) {
        this.mCircleView.setVisibility(0);
        this.mProgress.setAlpha(MotionEventCompat.ACTION_MASK);
        this.mScaleAnimation = new C12752();
        this.mScaleAnimation.setDuration((long) this.mMediumAnimationDuration);
        if (listener != null) {
            this.mCircleView.setAnimationListener(listener);
        }
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleAnimation);
    }

    private void setAnimationProgress(float progress) {
        if (progress > 0.0f) {
            ViewCompat.setScaleX(this.mCircleView, progress);
            ViewCompat.setScaleY(this.mCircleView, progress);
        }
    }

    private void setRefreshing(boolean refreshing, boolean notify) {
        if (this.mRefreshing != refreshing) {
            this.mNotify = notify;
            ensureTarget();
            this.mRefreshing = refreshing;
            if (this.mRefreshing) {
                animateOffsetToCorrectPosition(this.mCurrentTargetOffsetTop, this.mRefreshListener);
            } else {
                startScaleDownAnimation(this.mRefreshListener);
            }
        }
    }

    private void startScaleDownAnimation(AnimationListener listener) {
        this.mCircleView.setTop((int) (((float) getMeasuredHeight()) - this.mSpinnerFinalOffset));
        this.mScaleDownAnimation = new C12763();
        this.mScaleDownAnimation.setDuration(150);
        this.mScaleDownAnimation.setFillAfter(true);
        this.mCircleView.setAnimationListener(listener);
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleDownAnimation);
    }

    @SuppressLint({"NewApi"})
    private int getProgressAlpha() {
        return this.mProgress.getAlpha();
    }

    private void startProgressAlphaStartAnimation() {
        this.mAlphaStartAnimation = startAlphaAnimation(getProgressAlpha(), 76);
    }

    private void startProgressAlphaMaxAnimation() {
        this.mAlphaMaxAnimation = startAlphaAnimation(getProgressAlpha(), MotionEventCompat.ACTION_MASK);
    }

    private Animation startAlphaAnimation(int startingAlpha, int endingAlpha) {
        Animation alpha = new C12774(startingAlpha, endingAlpha);
        alpha.setDuration(300);
        this.mCircleView.setAnimationListener(null);
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(alpha);
        return alpha;
    }

    public void setProgressBackgroundColor(int colorRes) {
        this.mCircleView.setBackgroundColor(colorRes);
        this.mProgress.setBackgroundColor(getResources().getColor(colorRes));
    }

    @Deprecated
    public void setColorScheme(int... colors) {
        setColorSchemeResources(colors);
    }

    public void setColorSchemeResources(int... colorResIds) {
        Resources res = getResources();
        int[] colorRes = new int[colorResIds.length];
        for (int i = 0; i < colorResIds.length; i++) {
            colorRes[i] = res.getColor(colorResIds[i]);
        }
        setColorSchemeColors(colorRes);
    }

    public void setColorSchemeColors(int... colors) {
        ensureTarget();
        this.mProgress.setColorSchemeColors(colors);
    }

    public boolean isRefreshing() {
        return this.mRefreshing;
    }

    private void ensureTarget() {
        if (this.mTarget == null) {
            int i = 0;
            while (i < getChildCount()) {
                View child = getChildAt(i);
                if (child.equals(this.mCircleView)) {
                    i++;
                } else {
                    this.mTarget = child;
                    return;
                }
            }
        }
    }

    public void setDistanceToTriggerSync(int distance) {
        this.mTotalDragDistance = (float) distance;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (getChildCount() != 0) {
            if (this.mTarget == null) {
                ensureTarget();
            }
            if (this.mTarget != null) {
                View child = this.mTarget;
                int childLeft = getPaddingLeft();
                int childTop = getPaddingTop();
                child.layout(childLeft, childTop, childLeft + ((width - getPaddingLeft()) - getPaddingRight()), childTop + ((height - getPaddingTop()) - getPaddingBottom()));
                int circleWidth = this.mCircleView.getMeasuredWidth();
                this.mCircleView.layout((width / 2) - (circleWidth / 2), height - this.mCurrentTargetOffsetTop, (width / 2) + (circleWidth / 2), (height - this.mCurrentTargetOffsetTop) + this.mCircleView.getMeasuredHeight());
            }
        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mTarget == null) {
            ensureTarget();
        }
        if (this.mTarget != null) {
            this.mTarget.measure(MeasureSpec.makeMeasureSpec((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), 1073741824), MeasureSpec.makeMeasureSpec((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom(), 1073741824));
            this.mCircleView.measure(MeasureSpec.makeMeasureSpec(this.mCircleWidth, 1073741824), MeasureSpec.makeMeasureSpec(this.mCircleHeight, 1073741824));
            if (!(this.mUsingCustomStart || this.mOriginalOffsetCalculated)) {
                this.mOriginalOffsetCalculated = true;
                this.mOriginalOffsetTop = 0;
                this.mCurrentTargetOffsetTop = 0;
            }
            this.mCircleViewIndex = -1;
            for (int index = 0; index < getChildCount(); index++) {
                if (getChildAt(index) == this.mCircleView) {
                    this.mCircleViewIndex = index;
                    return;
                }
            }
        }
    }

    public boolean canChildScrollDown() {
        return ViewCompat.canScrollVertically(this.mTarget, 1);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        int action = MotionEventCompat.getActionMasked(ev);
        if (this.mReturningToStart && action == 0) {
            this.mReturningToStart = false;
        }
        if (!isEnabled() || this.mReturningToStart || canChildScrollDown() || this.mRefreshing) {
            return false;
        }
        switch (action) {
            case RECEIVED_VALUE:
                setTargetOffsetCircleTopFromBottom(this.mOriginalOffsetTop);
                this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                this.mIsBeingDragged = false;
                float initialMotionY = getMotionEventY(ev, this.mActivePointerId);
                if (initialMotionY != -1.0f) {
                    this.mInitialMotionY = initialMotionY;
                    break;
                }
                return false;
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                this.mIsBeingDragged = false;
                this.mActivePointerId = -1;
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                break;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                onSecondaryPointerUp(ev);
                break;
        }
        if (this.mActivePointerId == -1) {
            Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
            return false;
        }
        float y = getMotionEventY(ev, this.mActivePointerId);
        if (y == -1.0f) {
            return false;
        }
        if (this.mInitialMotionY - y > ((float) this.mTouchSlop) && !this.mIsBeingDragged) {
            this.mIsBeingDragged = true;
            this.mProgress.setAlpha(76);
        }
        return this.mIsBeingDragged;
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1.0f;
        }
        return MotionEventCompat.getY(ev, index);
    }

    public void requestDisallowInterceptTouchEvent(boolean b) {
    }

    private boolean isAnimationRunning(Animation animation) {
        return (animation == null || !animation.hasStarted() || animation.hasEnded()) ? false : true;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (this.mReturningToStart && action == 0) {
            this.mReturningToStart = false;
        }
        if (!isEnabled() || this.mReturningToStart || canChildScrollDown()) {
            return false;
        }
        int pointerIndex;
        float y;
        switch (action) {
            case RECEIVED_VALUE:
                this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                this.mIsBeingDragged = false;
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                int i = this.mActivePointerId;
                if (r0 == -1) {
                    if (action == 1) {
                        Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    }
                    return false;
                }
                this.mIsBeingDragged = false;
                pointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                if (pointerIndex < 0) {
                    this.mActivePointerId = -1;
                    return false;
                }
                y = MotionEventCompat.getY(ev, pointerIndex);
                float f = this.mInitialMotionY;
                if ((r0 - y) * 0.5f > this.mTotalDragDistance) {
                    setRefreshing(true, true);
                } else {
                    this.mRefreshing = false;
                    this.mProgress.setStartEndTrim(0.0f, 0.0f);
                    AnimationListener listener = null;
                    if (!this.mScale) {
                        listener = new C12785();
                    }
                    animateOffsetToStartPosition(getCurrentCircleTopOffset(), listener);
                    this.mProgress.showArrow(false);
                }
                this.mActivePointerId = -1;
                return false;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                pointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                if (pointerIndex >= 0) {
                    y = MotionEventCompat.getY(ev, pointerIndex);
                    float overscrollTop = (this.mInitialMotionY - y) * 0.5f;
                    if (this.mIsBeingDragged) {
                        this.mProgress.showArrow(true);
                        float originalDragPercent = overscrollTop / this.mTotalDragDistance;
                        if (originalDragPercent >= 0.0f) {
                            float slingshotDist;
                            float dragPercent = Math.min(1.0f, Math.abs(originalDragPercent));
                            float adjustedPercent = (((float) Math.max(((double) dragPercent) - 0.4d, 0.0d)) * 5.0f) / 3.0f;
                            float extraOS = Math.abs(overscrollTop) - this.mTotalDragDistance;
                            if (this.mUsingCustomStart) {
                                slingshotDist = this.mSpinnerFinalOffset - ((float) this.mOriginalOffsetTop);
                            } else {
                                slingshotDist = this.mSpinnerFinalOffset;
                            }
                            float tensionSlingshotPercent = Math.max(0.0f, Math.min(extraOS, 2.0f * slingshotDist) / slingshotDist);
                            float tensionPercent = ((float) (((double) (tensionSlingshotPercent / 4.0f)) - Math.pow((double) (tensionSlingshotPercent / 4.0f), 2.0d))) * 2.0f;
                            float extraMove = (slingshotDist * tensionPercent) * 2.0f;
                            int targetY = this.mOriginalOffsetTop + ((int) ((slingshotDist * dragPercent) + extraMove));
                            if (this.mCircleView.getVisibility() != 0) {
                                this.mCircleView.setVisibility(0);
                            }
                            if (!this.mScale) {
                                ViewCompat.setScaleX(this.mCircleView, 1.0f);
                                ViewCompat.setScaleY(this.mCircleView, 1.0f);
                            }
                            if (overscrollTop < this.mTotalDragDistance) {
                                if (this.mScale) {
                                    setAnimationProgress(overscrollTop / this.mTotalDragDistance);
                                }
                                if (getProgressAlpha() > 76) {
                                    if (!isAnimationRunning(this.mAlphaStartAnimation)) {
                                        startProgressAlphaStartAnimation();
                                    }
                                }
                                float strokeStart = adjustedPercent * 0.8f;
                                this.mProgress.setStartEndTrim(0.0f, Math.min(0.8f, strokeStart));
                                this.mProgress.setArrowScale(Math.min(1.0f, adjustedPercent));
                            } else if (getProgressAlpha() < 255) {
                                if (!isAnimationRunning(this.mAlphaMaxAnimation)) {
                                    startProgressAlphaMaxAnimation();
                                }
                            }
                            float rotation = ((-0.25f + (0.4f * adjustedPercent)) + (2.0f * tensionPercent)) * 0.5f;
                            this.mProgress.setProgressRotation(rotation);
                            setTargetOffsetCircleTopFromBottom(targetY);
                            break;
                        }
                        return false;
                    }
                }
                Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                return false;
                break;
            case Message.UUID_FIELD_NUMBER /*5*/:
                this.mActivePointerId = MotionEventCompat.getPointerId(ev, MotionEventCompat.getActionIndex(ev));
                break;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                onSecondaryPointerUp(ev);
                break;
        }
        return true;
    }

    private void animateOffsetToCorrectPosition(int from, AnimationListener listener) {
        this.mFrom = from;
        this.mAnimateToCorrectPosition.reset();
        this.mAnimateToCorrectPosition.setDuration(200);
        this.mAnimateToCorrectPosition.setInterpolator(this.mDecelerateInterpolator);
        if (listener != null) {
            this.mCircleView.setAnimationListener(listener);
        }
        this.mCircleView.clearAnimation();
        this.mAnimateToCorrectPosition.setFillAfter(true);
        this.mCircleView.startAnimation(this.mAnimateToCorrectPosition);
    }

    private void animateOffsetToStartPosition(int from, AnimationListener listener) {
        if (this.mScale) {
            startScaleDownReturnToStartAnimation(from, listener);
            return;
        }
        this.mFrom = from;
        this.mAnimateToStartPosition.reset();
        this.mAnimateToStartPosition.setDuration(200);
        this.mAnimateToStartPosition.setInterpolator(this.mDecelerateInterpolator);
        if (listener != null) {
            this.mCircleView.setAnimationListener(listener);
        }
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mAnimateToStartPosition);
    }

    private void moveToStart(float interpolatedTime) {
        setTargetOffsetCircleTopFromBottom(this.mFrom + ((int) (((float) (this.mOriginalOffsetTop - this.mFrom)) * interpolatedTime)));
    }

    private void startScaleDownReturnToStartAnimation(int from, AnimationListener listener) {
        this.mFrom = from;
        this.mStartingScale = ViewCompat.getScaleX(this.mCircleView);
        this.mScaleDownToStartAnimation = new C12818();
        this.mScaleDownToStartAnimation.setDuration(150);
        if (listener != null) {
            this.mCircleView.setAnimationListener(listener);
        }
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleDownToStartAnimation);
    }

    private void setTargetOffsetCircleTopFromBottom(int targetOffset) {
        this.mCircleView.bringToFront();
        this.mCircleView.offsetTopAndBottom(-(targetOffset - getCurrentCircleTopOffset()));
        this.mCurrentTargetOffsetTop = getCurrentTargetOffsetTopByCircleTop();
    }

    private int getCurrentCircleTopOffset() {
        return getMeasuredHeight() - this.mCircleView.getTop();
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = MotionEventCompat.getActionIndex(ev);
        if (MotionEventCompat.getPointerId(ev, pointerIndex) == this.mActivePointerId) {
            this.mActivePointerId = MotionEventCompat.getPointerId(ev, pointerIndex == 0 ? 1 : 0);
        }
    }

    public void setTarget(View target) {
        this.mTarget = target;
    }
}
