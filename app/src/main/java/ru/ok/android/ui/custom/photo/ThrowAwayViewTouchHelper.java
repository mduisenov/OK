package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.Scroller;
import com.google.android.gms.location.LocationStatusCodes;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.DimenUtils;
import ru.ok.android.utils.Logger;

public class ThrowAwayViewTouchHelper implements OnTouchListener {
    private GestureDetector gestureDetector;
    private Callback mCallback;
    private float mDownTouchX;
    private boolean mDragging;
    private float mLastMotionY;
    private int mMaxVelocity;
    private Scroller mScroller;
    private boolean mThrowingAway;
    private int mTouchSlop;
    private boolean mTouching;
    private boolean mTrackMovement;
    private VelocityTracker mVelocityTracker;
    private View mView;
    private List<OnThrowedAwayListener> onThrowAwayListeners;

    public interface OnDragListener {
        void onFinishDrag();

        void onStartDrag();
    }

    public interface OnThrowAwayListener {
        void onThrowAway(boolean z);
    }

    public interface Callback {
        boolean isBlocked(MotionEvent motionEvent);

        void onBouncedBack();

        void onScrollUpdate();

        void onStartedDrag();

        void onTap();

        void onThrowAway(boolean z);
    }

    /* renamed from: ru.ok.android.ui.custom.photo.ThrowAwayViewTouchHelper.1 */
    class C07381 extends SimpleOnGestureListener {
        final /* synthetic */ Callback val$callback;

        C07381(Callback callback) {
            this.val$callback = callback;
        }

        public boolean onSingleTapConfirmed(MotionEvent event) {
            if (this.val$callback == null) {
                return super.onSingleTapConfirmed(event);
            }
            this.val$callback.onTap();
            return true;
        }
    }

    public interface OnThrowedAwayListener {
        void onThrowedAway();
    }

    public ThrowAwayViewTouchHelper(View view, Callback callback) {
        this.mLastMotionY = -1.0f;
        this.mDownTouchX = -1.0f;
        this.onThrowAwayListeners = new ArrayList();
        this.gestureDetector = new GestureDetector(view.getContext(), new C07381(callback));
        this.mView = view;
        this.mView.setOnTouchListener(this);
        this.mCallback = callback;
        Context context = view.getContext();
        this.mTouchSlop = (int) (((double) ViewConfiguration.get(context).getScaledTouchSlop()) * 1.5d);
        this.mMaxVelocity = DimenUtils.getRealDisplayPixels((int) LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, context);
        this.mScroller = new Scroller(context);
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercept = false;
        int action = event.getAction();
        float y = event.getY();
        switch (action) {
            case RECEIVED_VALUE:
                if (!this.mCallback.isBlocked(event)) {
                    if (!this.mScroller.isFinished()) {
                        intercept = true;
                        break;
                    }
                    this.mTrackMovement = true;
                    this.mLastMotionY = y;
                    this.mDownTouchX = event.getX();
                    getVelocityTracker().addMovement(event);
                    break;
                }
                clear();
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                this.mDownTouchX = -1.0f;
                clear();
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (!this.mDragging) {
                    if (this.mTrackMovement) {
                        if (event.getPointerCount() <= 1) {
                            if (this.mDownTouchX == -1.0f) {
                                this.mDownTouchX = event.getX();
                            }
                            if (Math.abs(this.mDownTouchX - event.getX()) <= ((float) this.mTouchSlop)) {
                                if (this.mLastMotionY == -1.0f) {
                                    this.mLastMotionY = y;
                                }
                                float deltaY = y - this.mLastMotionY;
                                if (Math.abs(deltaY) > ((float) this.mTouchSlop)) {
                                    this.mTrackMovement = false;
                                    this.mDragging = true;
                                    intercept = true;
                                    this.mLastMotionY = y;
                                    onStartDrag(deltaY);
                                    break;
                                }
                            }
                            clear();
                            break;
                        }
                        this.mTrackMovement = false;
                        this.mLastMotionY = -1.0f;
                        this.mDownTouchX = -1.0f;
                        this.mTouching = false;
                        break;
                    }
                }
                intercept = true;
                this.mTrackMovement = false;
                break;
                break;
        }
        setTouching(intercept);
        if (intercept) {
            Logger.m172d("Touch throw event intercepted");
            this.gestureDetector.onTouchEvent(event);
        }
        return intercept;
    }

    public boolean onTouch(View view, MotionEvent event) {
        Logger.m172d("Throw away view touched");
        this.gestureDetector.onTouchEvent(event);
        int action = event.getAction();
        float y = event.getY();
        switch (action) {
            case RECEIVED_VALUE:
                if (!this.mScroller.isFinished()) {
                    this.mDragging = true;
                    this.mThrowingAway = false;
                    getVelocityTracker().addMovement(event);
                    this.mScroller.abortAnimation();
                }
                this.mLastMotionY = y;
                setTouching(true);
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (this.mDragging) {
                    this.mVelocityTracker.computeCurrentVelocity(LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, (float) this.mMaxVelocity);
                    navigateWithVelocity(-this.mVelocityTracker.getYVelocity());
                    clear();
                }
                this.mDownTouchX = -1.0f;
                setTouching(false);
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (this.mLastMotionY == -1.0f) {
                    this.mLastMotionY = y;
                }
                float deltaY = y - this.mLastMotionY;
                if (!this.mDragging && Math.abs(deltaY) > ((float) this.mTouchSlop)) {
                    this.mDragging = true;
                    this.mLastMotionY = y;
                    onStartDrag(deltaY);
                }
                if (this.mDragging) {
                    doScroll(deltaY);
                    this.mLastMotionY = y;
                    getVelocityTracker().addMovement(event);
                }
                setTouching(true);
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                if (this.mDragging) {
                    this.mVelocityTracker.computeCurrentVelocity(LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, (float) this.mMaxVelocity);
                    bounceBack(-this.mVelocityTracker.getYVelocity());
                    clear();
                }
                this.mDownTouchX = -1.0f;
                setTouching(false);
                break;
        }
        return true;
    }

    private void setTouching(boolean touching) {
        this.mTouching = touching;
    }

    private void clear() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
        this.mTrackMovement = false;
        this.mDragging = false;
        this.mLastMotionY = -1.0f;
    }

    private VelocityTracker getVelocityTracker() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        return this.mVelocityTracker;
    }

    private final void navigateWithVelocity(float velocity) {
        boolean z = true;
        float currScrollY = (float) this.mView.getScrollY();
        int throwAwayAbsPos = this.mView.getMeasuredHeight() / 3;
        boolean bounceBack = true;
        Callback callback;
        if (velocity != 0.0f) {
            float endPosition = currScrollY + ((velocity / 1000.0f) * 400.0f);
            int throwAwayPos;
            if (velocity > 0.0f) {
                throwAwayPos = throwAwayAbsPos;
            } else {
                throwAwayPos = -throwAwayAbsPos;
            }
            if (((endPosition > 0.0f && throwAwayPos > 0) || (endPosition < 0.0f && throwAwayPos < 0)) && Math.abs(endPosition) >= ((float) Math.abs(throwAwayPos))) {
                bounceBack = false;
                callback = this.mCallback;
                if (currScrollY <= 0.0f) {
                    z = false;
                }
                callback.onThrowAway(z);
            }
        } else if (Math.abs(currScrollY) >= ((float) throwAwayAbsPos)) {
            bounceBack = false;
            callback = this.mCallback;
            if (currScrollY <= 0.0f) {
                z = false;
            }
            callback.onThrowAway(z);
        }
        if (bounceBack) {
            bounceBack(velocity);
        }
    }

    private final void onStartDrag(float deltaY) {
        this.mCallback.onStartedDrag();
    }

    private final void doScroll(float deltaY) {
        this.mView.scrollBy(0, (int) (-deltaY));
        this.mCallback.onScrollUpdate();
    }

    private void bounceBack(float velocity) {
        if (this.mView.getScrollY() != 0) {
            flingToPosition(0, velocity);
        } else {
            onBouncedBack();
        }
    }

    private final void onBouncedBack() {
        this.mCallback.onBouncedBack();
    }

    private final void throwAwayInternal(int toPosition, float velocity) {
        this.mThrowingAway = true;
        flingToPosition(toPosition, velocity);
    }

    private final void flingToPosition(int position, float velocity) {
        if (!this.mScroller.isFinished()) {
            this.mScroller.abortAnimation();
        }
        int currY = this.mView.getScrollY();
        this.mScroller.fling(0, currY, 0, (int) velocity, 0, 0, -2147483647, Integer.MAX_VALUE);
        this.mScroller.setFinalY(position);
        int velocityDuration = (int) (((float) Math.abs(currY)) / (Math.abs(velocity) / 1000.0f));
        int duration = velocityDuration;
        if (velocityDuration > 400) {
            duration = 400;
        } else if (velocityDuration < 250) {
            duration = 250;
        }
        this.mScroller.extendDuration(duration);
        this.mView.invalidate();
    }

    public void computeScroll() {
        try {
            if (this.mScroller.computeScrollOffset()) {
                int oldY = this.mView.getScrollY();
                int scrollY = this.mScroller.getCurrY();
                if (scrollY != oldY) {
                    this.mView.setScrollY(scrollY);
                    this.mCallback.onScrollUpdate();
                }
                if (this.mScroller.isFinished()) {
                    if (this.mThrowingAway) {
                        this.mThrowingAway = false;
                        for (OnThrowedAwayListener listener : this.onThrowAwayListeners) {
                            listener.onThrowedAway();
                        }
                    } else {
                        onBouncedBack();
                    }
                }
                this.mView.postInvalidate();
            }
        } catch (Throwable exc) {
            Logger.m178e(exc);
        }
    }

    public final void throwAway(boolean up, OnThrowedAwayListener onThrowAwayListener) {
        throwAwayInternal(up ? this.mView.getMeasuredHeight() : -this.mView.getMeasuredHeight(), 0.0f);
        if (onThrowAwayListener != null) {
            this.onThrowAwayListeners.add(onThrowAwayListener);
        }
    }

    public boolean isTouching() {
        return this.mTouching;
    }
}
