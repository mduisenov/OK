package ru.ok.android.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class TouchObserverHeader extends View {
    private final GestureDetector gestureDetector;
    private final OnGestureListener gestureDetectorListener;
    private boolean isScrollProcessing;
    private ScrollListener scrollListener;
    private View touchObserverView;

    /* renamed from: ru.ok.android.ui.custom.TouchObserverHeader.1 */
    class C06271 extends SimpleOnGestureListener {
        C06271() {
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (TouchObserverHeader.this.scrollListener == null) {
                return false;
            }
            TouchObserverHeader.this.scrollListener.onScroll(distanceX, distanceY);
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return TouchObserverHeader.this.scrollListener != null && TouchObserverHeader.this.scrollListener.onFling(velocityX, velocityY);
        }
    }

    public interface ScrollListener {
        boolean onFling(float f, float f2);

        void onScroll(float f, float f2);
    }

    public TouchObserverHeader(Context context) {
        this(context, null);
    }

    public TouchObserverHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.gestureDetectorListener = new C06271();
        this.isScrollProcessing = false;
        this.gestureDetector = new GestureDetector(getContext(), this.gestureDetectorListener);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.touchObserverView == null) {
            return super.onTouchEvent(event);
        }
        boolean wasScrollProcessed = this.isScrollProcessing;
        this.isScrollProcessing = this.gestureDetector.onTouchEvent(event);
        if (!this.isScrollProcessing) {
            return this.touchObserverView.dispatchTouchEvent(event);
        }
        if (!wasScrollProcessed) {
            event.setAction(3);
            this.touchObserverView.dispatchTouchEvent(event);
        }
        return true;
    }

    public void setMeasuredDimensionSuper(int w, int h) {
        setMeasuredDimension(w, h);
    }

    public void setTouchObserverView(View touchObserverView) {
        this.touchObserverView = touchObserverView;
    }

    public void setScrollListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }
}
