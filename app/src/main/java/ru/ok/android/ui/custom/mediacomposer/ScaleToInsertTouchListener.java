package ru.ok.android.ui.custom.mediacomposer;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import ru.ok.android.utils.animation.SimpleAnimatorListener;

public class ScaleToInsertTouchListener {
    private DoubleTouchEventProcessor doubleTouchEventProcessor;
    boolean hasRemovedItem;
    ScaleToInsertInfo info;
    int pointerId0;
    int pointerId1;
    private final ScaleToInsertProvider provider;
    int removedItemPosition;
    float startX0;
    float startX1;
    float startY0;
    float startY1;
    boolean switchedPointers;

    public static class ScaleToInsertInfo {
        public int mode;
        public ViewGroup parent;
        public int pos1;
        public int pos2;
        public boolean swapPosition;
        public float x1;
        public float x2;
        public float y1;
        public float y2;
    }

    public interface ScaleToInsertProvider {
        void animateChildren(ScaleToInsertInfo scaleToInsertInfo, float f, AnimatorListener animatorListener);

        ScaleToInsertInfo canScaleToInsert(float f, float f2, float f3, float f4);

        void disallowInterceptTouchEvent(boolean z);

        int getInsertHeight();

        View insertNewChild(ScaleToInsertInfo scaleToInsertInfo);

        void onAfterItemRemoved(int i);

        void removeChildPinchIn(int i);

        void setOnLayoutListener(OnLayoutListener onLayoutListener);

        void translateChildren(ScaleToInsertInfo scaleToInsertInfo, float f, float f2);
    }

    private abstract class DoubleTouchEventProcessor {
        int child0;
        int child1;

        /* renamed from: ru.ok.android.ui.custom.mediacomposer.ScaleToInsertTouchListener.DoubleTouchEventProcessor.1 */
        class C06811 extends SimpleAnimatorListener {
            final /* synthetic */ int val$notifyRemovedPosition;

            C06811(int i) {
                this.val$notifyRemovedPosition = i;
            }

            public void onAnimationEnd(Animator animation) {
                ScaleToInsertTouchListener.this.provider.onAfterItemRemoved(this.val$notifyRemovedPosition);
            }
        }

        public DoubleTouchEventProcessor(int child0, int child1) {
            this.child0 = child0;
            this.child1 = child1;
        }

        void onDoubleTouchMove(float y0, float y1, float translationY0, float translationY1) {
            ScaleToInsertTouchListener.this.provider.translateChildren(ScaleToInsertTouchListener.this.info, translationY0, translationY1);
        }

        final void cancelGesture() {
            ScaleToInsertTouchListener.this.provider.disallowInterceptTouchEvent(false);
            startCancelAnimation();
        }

        void startCancelAnimation() {
            AnimatorListener listener = null;
            if (ScaleToInsertTouchListener.this.hasRemovedItem) {
                listener = new C06811(ScaleToInsertTouchListener.this.removedItemPosition);
            }
            ScaleToInsertTouchListener.this.provider.animateChildren(ScaleToInsertTouchListener.this.info, 0.0f, listener);
        }
    }

    private class BlockExpandProcessor extends DoubleTouchEventProcessor {
        public BlockExpandProcessor(int child0, int child1) {
            super(child0, child1);
        }

        public void onDoubleTouchMove(float y0, float y1, float translationY0, float translationY1) {
            float delta = translationY1 - translationY0;
            if (delta > 0.0f) {
                translationY1 -= 0.4f * delta;
                translationY0 += 0.4f * delta;
            }
            super.onDoubleTouchMove(y0, y1, translationY0, translationY1);
        }
    }

    private class BlockShrinkExpandProcessor extends DoubleTouchEventProcessor {
        float delta;

        public BlockShrinkExpandProcessor(int child0, int child1, float initialDelta) {
            super(child0, child1);
            this.delta = initialDelta;
        }

        void addDelta(float delta) {
            this.delta += delta;
        }

        public void onDoubleTouchMove(float y0, float y1, float translationY0, float translationY1) {
            float delta = (translationY1 - translationY0) - this.delta;
            super.onDoubleTouchMove(y0, y1, translationY0 + (0.45f * delta), translationY1 - (0.45f * delta));
        }
    }

    private class BlockShrinkProcessor extends DoubleTouchEventProcessor {
        public BlockShrinkProcessor(int child0, int child1) {
            super(child0, child1);
        }

        public void onDoubleTouchMove(float y0, float y1, float translationY0, float translationY1) {
            float delta = translationY1 - translationY0;
            if (delta < 0.0f) {
                translationY1 -= 0.4f * delta;
                translationY0 += 0.4f * delta;
            }
            super.onDoubleTouchMove(y0, y1, translationY0, translationY1);
        }
    }

    private class OnLayoutAfterItemRemovedListener implements OnGlobalLayoutListener {
        final BlockShrinkExpandProcessor nextProcessor;
        final int onScreenYBeforeLayout0;
        final int onScreenYBeforeLayout1;
        final DoubleTouchEventProcessor processorToStartCancelAnimation;
        final View view0;
        final View view1;
        final ViewTreeObserver viewTreeObserver;

        OnLayoutAfterItemRemovedListener(DoubleTouchEventProcessor processorToStartCancelAnimation, BlockShrinkExpandProcessor nextProcessor, ViewTreeObserver viewTreeObserver, View previousView, View nextView) {
            this.processorToStartCancelAnimation = processorToStartCancelAnimation;
            this.nextProcessor = nextProcessor;
            this.viewTreeObserver = viewTreeObserver;
            this.view0 = previousView;
            this.view1 = nextView;
            this.onScreenYBeforeLayout0 = ViewUtils.getOnScreenY(this.view0);
            this.onScreenYBeforeLayout1 = ViewUtils.getOnScreenY(this.view1);
        }

        public void onGlobalLayout() {
            unregisterLayoutListener();
            int delta0 = this.onScreenYBeforeLayout0 - ViewUtils.getOnScreenY(this.view0);
            int delta1 = this.onScreenYBeforeLayout1 - ViewUtils.getOnScreenY(this.view1);
            int childCount = ScaleToInsertTouchListener.this.info.parent.getChildCount();
            boolean beforeView0 = true;
            boolean afterView1 = false;
            for (int i = 0; i < childCount; i++) {
                View view = ScaleToInsertTouchListener.this.info.parent.getChildAt(i);
                if (beforeView0) {
                    view.setTranslationY(view.getTranslationY() + ((float) delta0));
                    if (view == this.view0) {
                        beforeView0 = false;
                    }
                }
                if (afterView1 || view == this.view1) {
                    afterView1 = true;
                    view.setTranslationY(view.getTranslationY() + ((float) delta1));
                }
            }
            ScaleToInsertTouchListener scaleToInsertTouchListener = ScaleToInsertTouchListener.this;
            scaleToInsertTouchListener.startY0 -= (float) delta0;
            scaleToInsertTouchListener = ScaleToInsertTouchListener.this;
            scaleToInsertTouchListener.startY1 -= (float) delta1;
            if (this.nextProcessor != null) {
                this.nextProcessor.addDelta((float) (delta1 - delta0));
            }
            if (this.processorToStartCancelAnimation != null) {
                this.processorToStartCancelAnimation.startCancelAnimation();
            }
        }

        @SuppressLint({"NewApi"})
        private void unregisterLayoutListener() {
            if (VERSION.SDK_INT < 16) {
                this.viewTreeObserver.removeGlobalOnLayoutListener(this);
            } else {
                this.viewTreeObserver.removeOnGlobalLayoutListener(this);
            }
        }
    }

    private class PinchToRemoveProcessor extends DoubleTouchEventProcessor {
        private final BlockExpandProcessor blockExpandProcessor;
        private boolean gestureIsCanceled;
        private boolean inCollapseAnimation;
        private final float initialDistance;
        private final float removeDistance;
        private float y0;
        private float y1;

        /* renamed from: ru.ok.android.ui.custom.mediacomposer.ScaleToInsertTouchListener.PinchToRemoveProcessor.2 */
        class C06822 implements AnimatorListener {
            final /* synthetic */ float val$delta;
            final /* synthetic */ float val$finalTranslationY0;
            final /* synthetic */ float val$finalTranslationY1;

            C06822(float f, float f2, float f3) {
                this.val$finalTranslationY0 = f;
                this.val$finalTranslationY1 = f2;
                this.val$delta = f3;
            }

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                BlockShrinkExpandProcessor nextProcessor;
                animator.removeAllListeners();
                PinchToRemoveProcessor.this.inCollapseAnimation = false;
                if (PinchToRemoveProcessor.this.gestureIsCanceled) {
                    nextProcessor = null;
                } else {
                    ScaleToInsertTouchListener.this.startY0 = PinchToRemoveProcessor.this.y0 - this.val$finalTranslationY0;
                    ScaleToInsertTouchListener.this.startY1 = PinchToRemoveProcessor.this.y1 - this.val$finalTranslationY1;
                    nextProcessor = new BlockShrinkExpandProcessor(PinchToRemoveProcessor.this.child0, PinchToRemoveProcessor.this.child1 - 1, this.val$finalTranslationY1 - this.val$finalTranslationY0);
                }
                ScaleToInsertTouchListener.this.doubleTouchEventProcessor = nextProcessor;
                ViewTreeObserver viewTreeObserver = ScaleToInsertTouchListener.this.info.parent.getViewTreeObserver();
                if (viewTreeObserver != null) {
                    viewTreeObserver.addOnGlobalLayoutListener(new OnLayoutAfterItemRemovedListener(PinchToRemoveProcessor.this.gestureIsCanceled ? PinchToRemoveProcessor.this : null, nextProcessor, viewTreeObserver, ScaleToInsertTouchListener.this.info.parent.getChildAt(PinchToRemoveProcessor.this.child0), ScaleToInsertTouchListener.this.info.parent.getChildAt(PinchToRemoveProcessor.this.child1)));
                }
                ScaleToInsertTouchListener.this.provider.removeChildPinchIn(PinchToRemoveProcessor.this.child0 + 1);
                PinchToRemoveProcessor pinchToRemoveProcessor = PinchToRemoveProcessor.this;
                pinchToRemoveProcessor.child1--;
                ScaleToInsertTouchListener.this.setRemovedItemPosition(PinchToRemoveProcessor.this.child0 + 1);
            }

            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }
        }

        public PinchToRemoveProcessor(Context context, int child0, int child1) {
            super(child0, child1);
            this.inCollapseAnimation = false;
            this.gestureIsCanceled = false;
            this.blockExpandProcessor = new BlockExpandProcessor(child0, child1);
            View view1 = ScaleToInsertTouchListener.this.info.parent.getChildAt(child1);
            View view0 = ScaleToInsertTouchListener.this.info.parent.getChildAt(child0);
            this.initialDistance = (float) (((view1.getTop() - ((MarginLayoutParams) view1.getLayoutParams()).topMargin) - view0.getBottom()) - ((MarginLayoutParams) view0.getLayoutParams()).bottomMargin);
            this.removeDistance = context.getResources().getDisplayMetrics().density * 80.0f;
        }

        public void onDoubleTouchMove(float y0, float y1, float translationY0, float translationY1) {
            this.y0 = y0;
            this.y1 = y1;
            if (!this.inCollapseAnimation) {
                float delta = translationY0 - translationY1;
                if (delta > 0.0f) {
                    translationY0 -= 0.45f * delta;
                    translationY1 += 0.45f * delta;
                }
                if (delta >= this.removeDistance) {
                    startCollapse(translationY0, translationY1);
                }
                this.blockExpandProcessor.onDoubleTouchMove(y0, y1, translationY0, translationY1);
            }
        }

        private void startCollapse(float initialTranslationY0, float initialTranslationY1) {
            float delta = initialTranslationY0 - initialTranslationY1;
            float remainingDistance = this.initialDistance - delta;
            float finalTranslationY0 = initialTranslationY0 + (0.5f * remainingDistance);
            float finalTranslationY1 = initialTranslationY1 - (0.5f * remainingDistance);
            int childCount = ScaleToInsertTouchListener.this.info.parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                float finalTranslationY;
                if (i <= this.child0) {
                    finalTranslationY = finalTranslationY0;
                } else {
                    finalTranslationY = finalTranslationY1;
                }
                ObjectAnimator animator = ObjectAnimator.ofFloat(ScaleToInsertTouchListener.this.info.parent.getChildAt(i), "translationY", new float[]{finalTranslationY});
                animator.setDuration(200);
                if (i == childCount - 1) {
                    animator.addListener(new C06822(finalTranslationY0, finalTranslationY1, delta));
                }
                animator.start();
            }
            this.inCollapseAnimation = true;
        }

        void startCancelAnimation() {
            if (!this.inCollapseAnimation) {
                super.startCancelAnimation();
            }
            this.gestureIsCanceled = true;
        }
    }

    private class ScaleToInsertProcessor extends DoubleTouchEventProcessor implements OnLayoutListener {
        private final BlockShrinkProcessor blockShrinkProcessor;
        int insertHeight;
        private View insertedChild;
        float translationY0;
        float translationY1;
        float y0;
        float y1;

        public ScaleToInsertProcessor(int child0, int child1) {
            super(child0, child1);
            this.blockShrinkProcessor = new BlockShrinkProcessor(child0, child1);
            this.insertedChild = null;
            this.insertHeight = ScaleToInsertTouchListener.this.provider.getInsertHeight();
        }

        public void onDoubleTouchMove(float y0, float y1, float translationY0, float translationY1) {
            if (translationY1 - translationY0 > ((float) this.insertHeight) && this.insertedChild == null && insertNewChild()) {
                ScaleToInsertTouchListener.this.doubleTouchEventProcessor = new BlockShrinkProcessor(this.child0, this.child1);
            }
            this.translationY0 = translationY0;
            this.translationY1 = translationY1;
            this.y0 = y0;
            this.y1 = y1;
            this.blockShrinkProcessor.onDoubleTouchMove(y0, y1, translationY0, translationY1);
        }

        private boolean insertNewChild() {
            this.insertedChild = ScaleToInsertTouchListener.this.provider.insertNewChild(ScaleToInsertTouchListener.this.info);
            if (this.insertedChild == null) {
                return false;
            }
            this.insertedChild.setTranslationX((float) ScaleToInsertTouchListener.this.info.parent.getWidth());
            this.insertedChild.setAlpha(0.0f);
            AnimatorSet animatorSet = new AnimatorSet();
            r3 = new Animator[2];
            r3[0] = ObjectAnimator.ofFloat(this.insertedChild, "translationX", new float[]{0.0f});
            r3[1] = ObjectAnimator.ofFloat(this.insertedChild, "alpha", new float[]{1.0f});
            animatorSet.playTogether(r3);
            animatorSet.setDuration(600);
            animatorSet.start();
            ScaleToInsertTouchListener.this.provider.setOnLayoutListener(this);
            this.child0 = ScaleToInsertTouchListener.this.info.pos1;
            this.child1 = ScaleToInsertTouchListener.this.info.pos2;
            return true;
        }

        public void onLayout() {
            ScaleToInsertTouchListener.this.provider.setOnLayoutListener(null);
            ScaleToInsertTouchListener scaleToInsertTouchListener = ScaleToInsertTouchListener.this;
            scaleToInsertTouchListener.startY1 += (float) this.insertHeight;
            this.translationY1 -= (float) this.insertHeight;
            super.onDoubleTouchMove(this.y0, this.y1, this.translationY0, this.translationY1);
        }
    }

    public ScaleToInsertTouchListener(ScaleToInsertProvider provider) {
        this.provider = provider;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.doubleTouchEventProcessor != null) {
            return true;
        }
        resetGesture();
        if (ev.getPointerCount() == 2) {
            float x0 = ev.getX(0);
            float y0 = ev.getY(0);
            float x1 = ev.getX(1);
            float y1 = ev.getY(1);
            this.pointerId0 = ev.getPointerId(0);
            this.pointerId1 = ev.getPointerId(1);
            ScaleToInsertInfo info = this.provider.canScaleToInsert(x0, y0, x1, y1);
            if (info != null) {
                this.info = info;
                float rawX0 = ev.getRawX();
                float rawY0 = ev.getRawY();
                float rawY1 = y1 - (y0 - rawY0);
                float rawX1 = x1 - (x0 - rawX0);
                this.switchedPointers = info.swapPosition;
                if (this.switchedPointers) {
                    float tmp = rawY0;
                    rawY0 = rawY1;
                    rawY1 = tmp;
                    tmp = rawX0;
                    rawX0 = rawX1;
                    rawX1 = tmp;
                    this.pointerId0 ^= this.pointerId1;
                    this.pointerId1 ^= this.pointerId0;
                    this.pointerId0 ^= this.pointerId1;
                }
                int i = info.mode;
                if (r0 == 1) {
                    this.doubleTouchEventProcessor = new ScaleToInsertProcessor(info.pos1, info.pos2);
                } else {
                    i = info.mode;
                    if (r0 == 2) {
                        this.doubleTouchEventProcessor = new PinchToRemoveProcessor(info.parent.getContext(), info.pos1, info.pos2);
                    }
                }
                if (this.doubleTouchEventProcessor != null) {
                    this.startX0 = rawX0;
                    this.startY0 = rawY0;
                    this.startX1 = rawX1;
                    this.startY1 = rawY1;
                    this.provider.disallowInterceptTouchEvent(true);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean onTouch(MotionEvent ev) {
        if (this.doubleTouchEventProcessor == null) {
            return false;
        }
        int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        if (ev.getPointerCount() != 2 || action == 3 || action == 1) {
            this.doubleTouchEventProcessor.cancelGesture();
            this.doubleTouchEventProcessor = null;
            return true;
        }
        float y0 = ev.getRawY();
        float y1 = ev.getY(1) - (ev.getY(0) - y0);
        if (ev.getPointerId(0) != this.pointerId0) {
            float tmp = y0;
            y0 = y1;
            y1 = tmp;
        }
        this.doubleTouchEventProcessor.onDoubleTouchMove(y0, y1, y0 - this.startY0, y1 - this.startY1);
        return true;
    }

    private void resetGesture() {
        this.hasRemovedItem = false;
    }

    private void setRemovedItemPosition(int position) {
        this.hasRemovedItem = true;
        this.removedItemPosition = position;
    }
}
