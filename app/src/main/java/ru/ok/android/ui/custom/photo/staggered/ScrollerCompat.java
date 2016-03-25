package ru.ok.android.ui.custom.photo.staggered;

import android.content.Context;
import android.os.Build.VERSION;
import android.widget.Scroller;

class ScrollerCompat {
    Scroller mScroller;

    static class ScrollerCompatImplIcs extends ScrollerCompat {
        public ScrollerCompatImplIcs(Context context) {
            super(context);
        }

        public float getCurrVelocity() {
            return ScrollerCompatIcs.getCurrVelocity(this.mScroller);
        }
    }

    public static ScrollerCompat from(Context context) {
        if (VERSION.SDK_INT >= 14) {
            return new ScrollerCompatImplIcs(context);
        }
        return new ScrollerCompat(context);
    }

    ScrollerCompat(Context context) {
        this.mScroller = new Scroller(context);
    }

    public boolean isFinished() {
        return this.mScroller.isFinished();
    }

    public int getCurrY() {
        return this.mScroller.getCurrY();
    }

    public float getCurrVelocity() {
        return 0.0f;
    }

    public boolean computeScrollOffset() {
        return this.mScroller.computeScrollOffset();
    }

    public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
        this.mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
    }

    public void abortAnimation() {
        this.mScroller.abortAnimation();
    }
}
