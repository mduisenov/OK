package ru.ok.android.ui.custom.mediacomposer;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ViewUtil;

public class SwipeDismissTouchListener implements OnTouchListener {
    private boolean isProcessingGesture;
    private boolean lastDismiss;
    private long mAnimationTime;
    private OnDismissCallback mCallback;
    private int mDismissDistance;
    private float mDownX;
    private int mMaxFlingVelocity;
    private int mMinFlingVelocity;
    private int mParentWidth;
    private int mSlop;
    private boolean mSwiping;
    private Object mToken;
    private float mTranslationX;
    private VelocityTracker mVelocityTracker;
    private View mView;
    private int mViewWidth;

    public interface OnDismissCallback {
        void onDismiss(View view, Object obj);

        void onGoingToDismiss(View view, Object obj);

        void onGoingToStay(View view, Object obj);
    }

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.SwipeDismissTouchListener.1 */
    class C06831 extends AnimatorListenerAdapter {
        C06831() {
        }

        public void onAnimationEnd(Animator animation) {
            SwipeDismissTouchListener.this.performDismiss();
        }
    }

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.SwipeDismissTouchListener.2 */
    class C06842 extends AnimatorListenerAdapter {
        final /* synthetic */ int val$originalHeight;

        C06842(int i) {
            this.val$originalHeight = i;
        }

        public void onAnimationEnd(Animator animation) {
            SwipeDismissTouchListener.this.mCallback.onDismiss(SwipeDismissTouchListener.this.mView, SwipeDismissTouchListener.this.mToken);
            SwipeDismissTouchListener.this.mView.setAlpha(1.0f);
            SwipeDismissTouchListener.this.mView.setTranslationX(0.0f);
            LayoutParams lp = SwipeDismissTouchListener.this.mView.getLayoutParams();
            lp.height = this.val$originalHeight;
            SwipeDismissTouchListener.this.mView.setLayoutParams(lp);
        }
    }

    public SwipeDismissTouchListener(View view, Object token, OnDismissCallback callback) {
        this.mViewWidth = 1;
        this.mParentWidth = 1;
        this.mDismissDistance = 1;
        this.lastDismiss = false;
        this.isProcessingGesture = false;
        ViewConfiguration vc = ViewConfiguration.get(view.getContext());
        this.mSlop = vc.getScaledTouchSlop();
        this.mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        this.mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        this.mAnimationTime = (long) view.getContext().getResources().getInteger(17694720);
        this.mView = view;
        this.mToken = token;
        this.mCallback = callback;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        motionEvent.offsetLocation(this.mTranslationX, 0.0f);
        if (this.mViewWidth < 2) {
            this.mViewWidth = this.mView.getWidth();
            ViewParent parent = this.mView.getParent();
            if (parent instanceof View) {
                this.mParentWidth = ((View) parent).getWidth();
            } else {
                this.mParentWidth = this.mViewWidth;
            }
            this.mDismissDistance = this.mParentWidth / 3;
        }
        boolean dismiss;
        switch (motionEvent.getAction() & MotionEventCompat.ACTION_MASK) {
            case RECEIVED_VALUE:
                Logger.m172d("ACTION_DOWN");
                this.mDownX = motionEvent.getRawX();
                this.lastDismiss = false;
                this.isProcessingGesture = true;
                return false;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                Logger.m172d("ACTION_UP");
                if (this.isProcessingGesture) {
                    float deltaX = motionEvent.getRawX() - this.mDownX;
                    dismiss = false;
                    boolean dismissRight = false;
                    if (Math.abs(deltaX * 0.5f) > ((float) this.mDismissDistance)) {
                        dismiss = true;
                        dismissRight = deltaX > 0.0f;
                    }
                    if (dismiss) {
                        float f;
                        if (dismissRight) {
                            f = (float) this.mParentWidth;
                        } else {
                            f = (float) (-this.mParentWidth);
                        }
                        animate(f, 0.0f, new C06831());
                    } else {
                        animate(0.0f, 1.0f, null);
                    }
                    this.mVelocityTracker = null;
                    this.mTranslationX = 0.0f;
                    this.mDownX = 0.0f;
                    this.mSwiping = false;
                    break;
                }
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                Logger.m172d("ACTION_MOVE");
                if (this.isProcessingGesture) {
                    float effectiveDelta = (motionEvent.getRawX() - this.mDownX) * 0.5f;
                    if (Math.abs(effectiveDelta) > ((float) this.mSlop)) {
                        this.mSwiping = true;
                        this.mView.getParent().requestDisallowInterceptTouchEvent(true);
                        MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                        cancelEvent.setAction((motionEvent.getActionIndex() << 8) | 3);
                        this.mView.onTouchEvent(cancelEvent);
                    }
                    dismiss = false;
                    if (Math.abs(effectiveDelta) > ((float) this.mDismissDistance)) {
                        dismiss = true;
                    }
                    if (!this.lastDismiss && dismiss) {
                        this.mCallback.onGoingToDismiss(view, this.mToken);
                    } else if (this.lastDismiss && !dismiss) {
                        this.mCallback.onGoingToStay(view, this.mToken);
                    }
                    this.lastDismiss = dismiss;
                    if (this.mSwiping) {
                        this.mTranslationX = effectiveDelta;
                        this.mView.setTranslationX(effectiveDelta);
                        this.mView.setAlpha(Math.min(1.0f, 2.0f - (Math.max(Math.abs(effectiveDelta), (float) this.mDismissDistance) / ((float) this.mDismissDistance))));
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    private void animate(float finalTranslationX, float finalAlpha, AnimatorListener animatorListener) {
        AnimatorSet animatorSet = new AnimatorSet();
        r1 = new Animator[2];
        r1[0] = ObjectAnimator.ofFloat(this.mView, "translationX", new float[]{finalTranslationX});
        r1[1] = ObjectAnimator.ofFloat(this.mView, "alpha", new float[]{finalAlpha});
        animatorSet.playTogether(r1);
        animatorSet.setDuration(this.mAnimationTime);
        if (animatorListener != null) {
            animatorSet.addListener(animatorListener);
        }
        animatorSet.start();
    }

    private void performDismiss() {
        int originalHeight = this.mView.getHeight();
        ValueAnimator animator = ViewUtil.createHeightAnimator(this.mView, originalHeight, 1, this.mAnimationTime);
        animator.addListener(new C06842(originalHeight));
        animator.start();
    }
}
