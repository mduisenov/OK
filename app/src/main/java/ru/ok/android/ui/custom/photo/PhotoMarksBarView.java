package ru.ok.android.ui.custom.photo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Space;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.animation.SimpleAnimationListener;
import ru.ok.android.utils.animation.SimpleAnimatorListener;

public class PhotoMarksBarView extends RelativeLayout {
    protected boolean animating;
    protected final int animationDuration;
    protected int drawerState;
    protected final ImageView mControlView;
    protected final View mMark1View;
    protected final View mMark2View;
    protected final View mMark3View;
    protected final View mMark4View;
    protected final View mMark5View;
    protected final View mMark6View;
    protected final Space mMarksControlSpace;
    private final OnClickListener mOnMarkClickListenerInternal;
    protected final View[] marksViews;
    protected final View maxMarkView;
    protected OnDrawerStateChangeListener onDrawerStateChangeListener;
    protected OnMarkSelectedListener onMarkSelectedListener;
    protected final View raiseMarkLabelView;
    protected int userMark;
    protected final ImageView userMarkView;
    protected final View yourMarkLabelView;

    public interface OnDrawerStateChangeListener {
        void onDrawerStateChange(int i);
    }

    public interface OnMarkSelectedListener {
        void onMarkSelected(int i);
    }

    /* renamed from: ru.ok.android.ui.custom.photo.PhotoMarksBarView.1 */
    class C07121 implements OnClickListener {

        /* renamed from: ru.ok.android.ui.custom.photo.PhotoMarksBarView.1.1 */
        class C07101 implements Runnable {
            C07101() {
            }

            public void run() {
                if (PhotoMarksBarView.this.onMarkSelectedListener != null) {
                    PhotoMarksBarView.this.onMarkSelectedListener.onMarkSelected(PhotoMarksBarView.this.userMark);
                }
            }
        }

        /* renamed from: ru.ok.android.ui.custom.photo.PhotoMarksBarView.1.2 */
        class C07112 implements Runnable {
            C07112() {
            }

            public void run() {
                if (PhotoMarksBarView.this.onMarkSelectedListener != null) {
                    PhotoMarksBarView.this.onMarkSelectedListener.onMarkSelected(PhotoMarksBarView.this.userMark);
                }
            }
        }

        C07121() {
        }

        public void onClick(View view) {
            if (!PhotoMarksBarView.this.animating) {
                PhotoMarksBarView.this.updateDrawerState(0);
                PhotoMarksBarView.this.userMark = PhotoMarksBarView.getMarkFromView(view);
                if (view.getId() == 2131625206) {
                    PhotoMarksBarView.this.animateMarkChange(new C07101());
                    return;
                }
                PhotoMarksBarView.this.userMarkView.setImageResource(PhotoMarksBarView.getMarkImageRes(PhotoMarksBarView.this.userMark));
                PhotoMarksBarView.this.animateMarkSelection(view, new C07112());
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.PhotoMarksBarView.2 */
    class C07132 implements OnClickListener {
        C07132() {
        }

        public void onClick(View view) {
            if (!PhotoMarksBarView.this.animating) {
                if (PhotoMarksBarView.this.drawerState == 0) {
                    if (PhotoMarksBarView.this.userMark == 0) {
                        PhotoMarksBarView.this.animateMarksDrawer(true);
                    } else {
                        PhotoMarksBarView.this.animateUserMarkDrawer(true);
                    }
                } else if (PhotoMarksBarView.this.drawerState == 1) {
                    PhotoMarksBarView.this.animateMarksDrawer(false);
                } else if (PhotoMarksBarView.this.drawerState == 2) {
                    PhotoMarksBarView.this.animateUserMarkDrawer(false);
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.PhotoMarksBarView.3 */
    class C07143 extends SimpleAnimationListener {
        final /* synthetic */ Runnable val$endRunnable;

        C07143(Runnable runnable) {
            this.val$endRunnable = runnable;
        }

        public void onAnimationEnd(Animation animation) {
            PhotoMarksBarView.this.animating = false;
            PhotoMarksBarView.this.updateControlButton();
            PhotoMarksBarView.this.setMarkViewsVisibility(false);
            if (this.val$endRunnable != null) {
                this.val$endRunnable.run();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.PhotoMarksBarView.4 */
    class C07154 extends SimpleAnimationListener {
        final /* synthetic */ Runnable val$endRunnable;

        C07154(Runnable runnable) {
            this.val$endRunnable = runnable;
        }

        public void onAnimationEnd(Animation animation) {
            PhotoMarksBarView.this.animating = false;
            PhotoMarksBarView.this.updateControlButton();
            PhotoMarksBarView.this.setMarkViewsVisibility(false);
            PhotoMarksBarView.this.setUserMarkViewsVisibility(true);
            PhotoMarksBarView.this.maxMarkView.setVisibility(8);
            if (this.val$endRunnable != null) {
                this.val$endRunnable.run();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.PhotoMarksBarView.5 */
    class C07165 implements AnimatorUpdateListener {
        C07165() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            float alpha = ((Float) animation.getAnimatedValue()).floatValue();
            for (View view : PhotoMarksBarView.this.marksViews) {
                view.setAlpha(alpha);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.PhotoMarksBarView.6 */
    class C07176 extends SimpleAnimatorListener {
        final /* synthetic */ boolean val$open;

        C07176(boolean z) {
            this.val$open = z;
        }

        public void onAnimationStart(Animator animation) {
            PhotoMarksBarView.this.setMarkViewsVisibility(true);
        }

        public void onAnimationEnd(Animator animation) {
            PhotoMarksBarView.this.animating = false;
            PhotoMarksBarView.this.updateControlButton();
            if (!this.val$open) {
                PhotoMarksBarView.this.setMarkViewsVisibility(false);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.PhotoMarksBarView.7 */
    class C07187 implements AnimatorUpdateListener {
        C07187() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            float alpha = ((Float) animation.getAnimatedValue()).floatValue();
            PhotoMarksBarView.this.maxMarkView.setAlpha(alpha);
            PhotoMarksBarView.this.yourMarkLabelView.setAlpha(alpha);
            PhotoMarksBarView.this.raiseMarkLabelView.setAlpha(alpha);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.PhotoMarksBarView.8 */
    class C07198 extends SimpleAnimatorListener {
        final /* synthetic */ boolean val$open;

        C07198(boolean z) {
            this.val$open = z;
        }

        public void onAnimationStart(Animator animation) {
            PhotoMarksBarView.this.setUserMarkViewsVisibility(true);
        }

        public void onAnimationEnd(Animator animation) {
            if (!this.val$open) {
                PhotoMarksBarView.this.setUserMarkViewsVisibility(false);
            }
            PhotoMarksBarView.this.updateControlButton();
            PhotoMarksBarView.this.animating = false;
        }
    }

    public PhotoMarksBarView(Context context) {
        this(context, null);
    }

    public PhotoMarksBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoMarksBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.marksViews = new View[6];
        this.drawerState = 0;
        this.mOnMarkClickListenerInternal = new C07121();
        LayoutInflater.from(getContext()).inflate(2130903380, this, true);
        this.mMark1View = findViewById(2131625209);
        this.marksViews[0] = this.mMark1View;
        this.mMark1View.setOnClickListener(this.mOnMarkClickListenerInternal);
        this.mMark2View = findViewById(2131625210);
        this.marksViews[1] = this.mMark2View;
        this.mMark2View.setOnClickListener(this.mOnMarkClickListenerInternal);
        this.mMark3View = findViewById(2131625211);
        this.marksViews[2] = this.mMark3View;
        this.mMark3View.setOnClickListener(this.mOnMarkClickListenerInternal);
        this.mMark4View = findViewById(2131625212);
        this.marksViews[3] = this.mMark4View;
        this.mMark4View.setOnClickListener(this.mOnMarkClickListenerInternal);
        this.mMark5View = findViewById(2131625213);
        this.marksViews[4] = this.mMark5View;
        this.mMark5View.setOnClickListener(this.mOnMarkClickListenerInternal);
        this.mMark6View = findViewById(2131625214);
        this.marksViews[5] = this.mMark6View;
        this.mMark6View.setOnClickListener(this.mOnMarkClickListenerInternal);
        this.mControlView = (ImageView) findViewById(2131625208);
        this.mMarksControlSpace = (Space) findViewById(2131625202);
        this.mControlView.setOnClickListener(new C07132());
        updateControlButton();
        this.userMarkView = (ImageView) findViewById(2131625204);
        this.maxMarkView = findViewById(2131625206);
        this.maxMarkView.setOnClickListener(this.mOnMarkClickListenerInternal);
        this.yourMarkLabelView = findViewById(2131625203);
        this.raiseMarkLabelView = findViewById(2131625205);
        this.animationDuration = getResources().getInteger(2131427341);
    }

    public int getUserMark() {
        return this.userMark;
    }

    protected final void animateMarkSelection(View view, Runnable endRunnable) {
        this.animating = true;
        for (View markView : this.marksViews) {
            if (markView != view) {
                animateFadeView(markView, false, null);
            }
        }
        animateTranslateView(view, false, false, null);
        animateFadeView(this.mControlView, false, new C07143(endRunnable));
    }

    protected final void animateMarkChange(Runnable endRunnable) {
        this.animating = true;
        animateFadeView(this.userMarkView, true, null);
        animateFadeView(this.yourMarkLabelView, true, null);
        animateFadeView(this.raiseMarkLabelView, true, null);
        animateTranslateView(this.maxMarkView, false, false, null);
        animateFadeView(this.mControlView, false, new C07154(endRunnable));
    }

    protected final void animateMarksDrawer(boolean open) {
        int i;
        float alphaFrom;
        float alphaTo = 1.0f;
        this.animating = true;
        if (open) {
            i = 1;
        } else {
            i = 0;
        }
        updateDrawerState(i);
        for (View view : this.marksViews) {
            animateTranslateView(view, open, false, null);
        }
        if (open) {
            alphaFrom = 0.0f;
        } else {
            alphaFrom = 1.0f;
        }
        if (!open) {
            alphaTo = 0.0f;
        }
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(new float[]{alphaFrom, alphaTo});
        alphaAnimator.setDuration((long) this.animationDuration);
        alphaAnimator.addUpdateListener(new C07165());
        alphaAnimator.addListener(new C07176(open));
        alphaAnimator.start();
    }

    protected final void setMarkViewsVisibility(boolean visible) {
        int visibility = visible ? 0 : 4;
        for (View view : this.marksViews) {
            view.setVisibility(visibility);
        }
    }

    protected static int getMarkFromView(View view) {
        switch (view.getId()) {
            case 2131625206:
            case 2131625214:
                return 6;
            case 2131625209:
                return 1;
            case 2131625210:
                return 2;
            case 2131625211:
                return 3;
            case 2131625212:
                return 4;
            case 2131625213:
                return 5;
            default:
                return 0;
        }
    }

    protected final void animateUserMarkDrawer(boolean open) {
        int i;
        float alphaFrom;
        float alphaTo = 1.0f;
        this.animating = true;
        if (open) {
            i = 2;
        } else {
            i = 0;
        }
        updateDrawerState(i);
        animateTranslateView(this.userMarkView, open, false, null);
        animateTranslateView(this.yourMarkLabelView, open, false, null);
        if (this.userMark != 6) {
            animateTranslateView(this.maxMarkView, open, false, null);
            animateTranslateView(this.raiseMarkLabelView, open, false, null);
        }
        if (open) {
            alphaFrom = 0.0f;
        } else {
            alphaFrom = 1.0f;
        }
        if (!open) {
            alphaTo = 0.0f;
        }
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(new float[]{alphaFrom, alphaTo});
        alphaAnimator.setDuration((long) this.animationDuration);
        alphaAnimator.addUpdateListener(new C07187());
        alphaAnimator.addListener(new C07198(open));
        alphaAnimator.start();
    }

    protected final void setUserMarkViewsVisibility(boolean visible) {
        int visibility = visible ? 0 : 4;
        if (this.userMark != 6) {
            this.maxMarkView.setVisibility(visibility);
            this.raiseMarkLabelView.setVisibility(visibility);
        }
        this.yourMarkLabelView.setVisibility(visibility);
        this.userMarkView.setVisibility(visibility);
    }

    protected final void animateTranslateView(View view, boolean toPosition, boolean fillAfter, AnimationListener listener) {
        TranslateAnimation translate;
        int edgeXPosition = view.getLeft() - this.mControlView.getRight();
        if (toPosition) {
            translate = new TranslateAnimation(0, (float) (-edgeXPosition), 1, 0.0f, 0, 0.0f, 0, 0.0f);
        } else {
            translate = new TranslateAnimation(1, 0.0f, 0, (float) (-edgeXPosition), 0, 0.0f, 0, 0.0f);
        }
        translate.setInterpolator(new DecelerateInterpolator());
        translate.setFillAfter(fillAfter);
        translate.setDuration((long) this.animationDuration);
        translate.setAnimationListener(listener);
        view.startAnimation(translate);
    }

    protected final void animateFadeView(View view, boolean fillAfter, AnimationListener listener) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration((long) this.animationDuration);
        alphaAnimation.setFillAfter(fillAfter);
        alphaAnimation.setAnimationListener(listener);
        view.startAnimation(alphaAnimation);
    }

    protected final void updateControlButton() {
        if (this.drawerState != 0) {
            this.mControlView.setImageResource(2130838540);
        } else if (this.userMark == 0) {
            this.mControlView.setImageResource(2130838162);
        } else {
            this.mControlView.setImageResource(getMarkImageRes(this.userMark));
        }
        this.mControlView.clearAnimation();
        this.mControlView.setVisibility(0);
        if (this.userMark != 0) {
            this.mMarksControlSpace.getLayoutParams().width = this.mControlView.getMeasuredWidth();
            this.mMarksControlSpace.setVisibility(0);
            return;
        }
        this.mMarksControlSpace.setVisibility(8);
    }

    protected static final int getMarkImageRes(int mark) {
        switch (mark) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return 2130838070;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return 2130838071;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return 2130838072;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return 2130838073;
            case Message.UUID_FIELD_NUMBER /*5*/:
                return 2130838074;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return 2130838075;
            default:
                return 2130838162;
        }
    }

    public final void setUserMark(int mark) {
        this.userMark = mark;
        if (this.drawerState == 1) {
            animateMarksDrawer(false);
        } else if (this.drawerState == 2) {
            animateUserMarkDrawer(false);
        }
        this.userMarkView.setImageResource(getMarkImageRes(this.userMark));
        updateControlButton();
    }

    public final void updateDrawerState(int newState) {
        this.drawerState = newState;
        if (this.onDrawerStateChangeListener != null) {
            this.onDrawerStateChangeListener.onDrawerStateChange(this.drawerState);
        }
    }

    public void setOnDrawerStateChangeListener(OnDrawerStateChangeListener onDrawerStateChangeListener) {
        this.onDrawerStateChangeListener = onDrawerStateChangeListener;
    }

    public void setOnMarkSelectedListener(OnMarkSelectedListener onMarkSelectedListener) {
        this.onMarkSelectedListener = onMarkSelectedListener;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mMarksControlSpace.setMinimumWidth(this.mControlView.getMeasuredWidth());
    }
}
