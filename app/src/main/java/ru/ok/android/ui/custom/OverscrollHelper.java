package ru.ok.android.ui.custom;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.animation.SimpleAnimatorListener;

public class OverscrollHelper implements AnimatorUpdateListener {
    protected float latestY;
    protected OverscrollListener overscrollListener;
    protected float overscrolledBy;
    protected ScrollPositionProvider scrollPositionProvider;
    protected int state;
    protected ValueAnimator swipeOverscrollAnimator;
    protected final int touchSlop;
    protected View view;

    /* renamed from: ru.ok.android.ui.custom.OverscrollHelper.1 */
    class C06241 extends SimpleAnimatorListener {
        C06241() {
        }

        public void onAnimationEnd(Animator animation) {
            OverscrollHelper.this.swipeOverscrollAnimator = null;
            OverscrollHelper.this.startBounceBack();
        }
    }

    /* renamed from: ru.ok.android.ui.custom.OverscrollHelper.2 */
    class C06252 extends SimpleAnimatorListener {
        C06252() {
        }

        public void onAnimationEnd(Animator animation) {
            OverscrollHelper.this.swipeOverscrollAnimator = null;
            OverscrollHelper.this.state = 0;
        }
    }

    public interface OverscrollListener {
        void onOverscrolled(float f);
    }

    public interface ScrollPositionProvider {
        boolean isScrolledToBottom(View view);

        boolean isScrolledToTop(View view);
    }

    public OverscrollHelper(View view, ScrollPositionProvider scrollPositionProvider) {
        this.state = 0;
        this.latestY = -1.0f;
        this.view = view;
        this.scrollPositionProvider = scrollPositionProvider;
        this.touchSlop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.swipeOverscrollAnimator != null && this.swipeOverscrollAnimator.isRunning()) {
            return false;
        }
        boolean consumed = false;
        switch (event.getAction()) {
            case RECEIVED_VALUE:
                if (this.state == 2) {
                    cancelSwipeOverscroll();
                    this.state = 1;
                    consumed = true;
                }
                this.latestY = event.getY();
                return consumed;
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                if (this.state == 1) {
                    startBounceBack();
                    consumed = true;
                }
                this.latestY = -1.0f;
                return consumed;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (this.latestY == -1.0f) {
                    this.latestY = event.getY();
                    return false;
                }
                float deltaY = this.latestY - event.getY();
                if (Math.abs(deltaY) < ((float) this.touchSlop)) {
                    return false;
                }
                if (this.scrollPositionProvider.isScrolledToTop(this.view)) {
                    this.overscrolledBy = Math.min(0.0f, this.overscrolledBy + deltaY);
                    if (this.overscrolledBy < 0.0f) {
                        this.state = 1;
                    } else {
                        this.state = 0;
                    }
                } else if (this.scrollPositionProvider.isScrolledToBottom(this.view)) {
                    this.overscrolledBy = Math.max(0.0f, this.overscrolledBy + deltaY);
                    if (this.overscrolledBy > 0.0f) {
                        this.state = 1;
                    } else {
                        this.state = 0;
                    }
                }
                if (this.state == 1) {
                    notifyOverscrollListener();
                    consumed = true;
                }
                this.latestY = event.getY();
                return consumed;
            default:
                return false;
        }
    }

    public void overScrollBy(int deltaY, boolean isTouchEvent) {
        if (!isTouchEvent) {
            if (this.swipeOverscrollAnimator == null || !this.swipeOverscrollAnimator.isRunning()) {
                this.swipeOverscrollAnimator = ValueAnimator.ofFloat(new float[]{this.overscrolledBy, (float) deltaY});
                this.swipeOverscrollAnimator.addUpdateListener(this);
                this.swipeOverscrollAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                this.swipeOverscrollAnimator.setDuration(125);
                this.swipeOverscrollAnimator.addListener(new C06241());
                this.swipeOverscrollAnimator.start();
                this.state = 1;
            }
        }
    }

    protected final void cancelSwipeOverscroll() {
        if (this.swipeOverscrollAnimator != null) {
            this.swipeOverscrollAnimator.cancel();
            this.swipeOverscrollAnimator = null;
        }
    }

    protected final void startBounceBack() {
        this.swipeOverscrollAnimator = ValueAnimator.ofFloat(new float[]{this.overscrolledBy, 0.0f});
        this.swipeOverscrollAnimator.addUpdateListener(this);
        this.swipeOverscrollAnimator.setInterpolator(new DecelerateInterpolator());
        this.swipeOverscrollAnimator.addListener(new C06252());
        this.swipeOverscrollAnimator.start();
        this.state = 2;
    }

    public void onAnimationUpdate(ValueAnimator animator) {
        this.overscrolledBy = ((Float) animator.getAnimatedValue()).floatValue();
        notifyOverscrollListener();
    }

    protected final void notifyOverscrollListener() {
        if (this.overscrollListener != null) {
            this.overscrollListener.onOverscrolled(this.overscrolledBy);
        }
    }

    public void setOverscrollListener(OverscrollListener overscrollListener) {
        this.overscrollListener = overscrollListener;
    }
}
