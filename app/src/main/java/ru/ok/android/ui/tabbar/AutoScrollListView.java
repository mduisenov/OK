package ru.ok.android.ui.tabbar;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class AutoScrollListView extends HideTabbarListView {
    private Message delayedMessage;
    private final Handler handler;
    private int mRequestedScrollPosition;
    private boolean mSmoothScrollRequested;

    /* renamed from: ru.ok.android.ui.tabbar.AutoScrollListView.1 */
    class C12821 extends Handler {
        C12821() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AutoScrollListView.this.processDelayedMessage();
        }
    }

    private void processDelayedMessage() {
        requestPositionToScreen(this.delayedMessage.arg1, this.delayedMessage.arg2 > 0);
        this.delayedMessage = null;
    }

    public AutoScrollListView(Context context) {
        super(context);
        this.mRequestedScrollPosition = -1;
        this.handler = new C12821();
    }

    public AutoScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRequestedScrollPosition = -1;
        this.handler = new C12821();
    }

    public AutoScrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mRequestedScrollPosition = -1;
        this.handler = new C12821();
    }

    public void requestPositionToScreen(int position, boolean smoothScroll) {
        Log.d(AutoScrollListView.class.getSimpleName(), String.format("requestPositionToScreen: %d, %s", new Object[]{Integer.valueOf(position), Boolean.valueOf(smoothScroll)}));
        this.mRequestedScrollPosition = position;
        this.mSmoothScrollRequested = smoothScroll;
        requestLayout();
    }

    public boolean onTouchEvent(MotionEvent ev) {
        cancelScheduledRequest();
        return super.onTouchEvent(ev);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && this.handler.hasMessages(0)) {
            processDelayedMessage();
            cancelScheduledRequest();
        }
    }

    protected void layoutChildren() {
        super.layoutChildren();
        if (this.mRequestedScrollPosition != -1) {
            boolean isLast;
            int position = this.mRequestedScrollPosition;
            this.mRequestedScrollPosition = -1;
            int firstPosition = getFirstVisiblePosition() + 1;
            int lastPosition = getLastVisiblePosition();
            if (position >= getCount() - 1) {
                isLast = true;
            } else {
                isLast = false;
            }
            Log.d(AutoScrollListView.class.getSimpleName(), String.format("layoutChildren: requested position: %d, first: %d, last %d, isLast: %s", new Object[]{Integer.valueOf(position), Integer.valueOf(firstPosition), Integer.valueOf(lastPosition), Boolean.valueOf(isLast)}));
            if (position < firstPosition || position > lastPosition || isLast) {
                int offset;
                if (isLast) {
                    offset = 0;
                } else {
                    offset = (int) (((float) getHeight()) * 0.33f);
                }
                if (this.mSmoothScrollRequested) {
                    int twoScreens = (lastPosition - firstPosition) * 2;
                    int preliminaryPosition;
                    if (position < firstPosition) {
                        preliminaryPosition = position + twoScreens;
                        if (preliminaryPosition >= getCount()) {
                            preliminaryPosition = getCount() - 1;
                        }
                        if (preliminaryPosition < firstPosition) {
                            setSelection(preliminaryPosition);
                            super.layoutChildren();
                        }
                    } else {
                        preliminaryPosition = position - twoScreens;
                        if (preliminaryPosition < 0) {
                            preliminaryPosition = 0;
                        }
                        if (preliminaryPosition > lastPosition) {
                            setSelection(preliminaryPosition);
                            super.layoutChildren();
                        }
                    }
                    Log.d(AutoScrollListView.class.getSimpleName(), String.format("Call setSelectionFromTop: position: %d, offset: %d", new Object[]{Integer.valueOf(position), Integer.valueOf(offset)}));
                    setSelectionFromTop(position, offset);
                    super.layoutChildren();
                    return;
                }
                setSelectionFromTop(position, offset);
                super.layoutChildren();
            }
        }
    }

    private void cancelScheduledRequest() {
        this.delayedMessage = null;
        this.handler.removeMessages(0);
    }
}
