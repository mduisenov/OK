package ru.ok.android.ui.stream.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.TransitionDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import ru.ok.android.C0206R;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.animation.SimpleAnimationListener;
import ru.ok.android.utils.animation.SimpleAnimatorListener;
import ru.ok.android.utils.localization.LocalizationManager;

public final class StreamScrollTopView extends FrameLayout {
    private Animator currentExpandCollapseAnimator;
    private boolean hasPendingExpand;
    private final ImageView imageView;
    private boolean isExpanded;
    private boolean isExpandedShownOnScroll;
    private boolean isShownOnScroll;
    private int measuredCollapsedWidth;
    private int measuredExpandedWidth;
    private int newEventsCount;
    private final boolean showCounter;
    private final TextView textView;
    private final TransitionDrawable transitionDrawable;
    private boolean transitionDrawableExpanded;

    /* renamed from: ru.ok.android.ui.stream.view.StreamScrollTopView.1 */
    class C12651 extends SimpleAnimationListener {
        C12651() {
        }

        public void onAnimationEnd(Animation animation) {
            StreamScrollTopView.this.startExpandAnimation();
        }
    }

    /* renamed from: ru.ok.android.ui.stream.view.StreamScrollTopView.2 */
    class C12662 extends SimpleAnimatorListener {
        boolean isCanceled;

        C12662() {
            this.isCanceled = false;
        }

        public void onAnimationCancel(Animator animation) {
            this.isCanceled = true;
        }

        public void onAnimationEnd(Animator animation) {
            int transitionDuration;
            StreamScrollTopView.this.currentExpandCollapseAnimator = null;
            StreamScrollTopView.this.resetViewState();
            if (this.isCanceled) {
                transitionDuration = 1;
            } else {
                Animation showTextAnimation = StreamScrollTopView.this.getAnimationIn();
                showTextAnimation.setFillAfter(true);
                StreamScrollTopView.this.textView.startAnimation(showTextAnimation);
                transitionDuration = 100;
            }
            if (!StreamScrollTopView.this.transitionDrawableExpanded) {
                StreamScrollTopView.this.transitionDrawable.reverseTransition(transitionDuration);
                StreamScrollTopView.this.transitionDrawableExpanded = true;
            }
        }
    }

    /* renamed from: ru.ok.android.ui.stream.view.StreamScrollTopView.3 */
    class C12673 implements AnimatorUpdateListener {
        C12673() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            LayoutParams lp = StreamScrollTopView.this.getLayoutParams();
            if (lp != null) {
                lp.width = ((Integer) animation.getAnimatedValue()).intValue();
                StreamScrollTopView.this.setLayoutParams(lp);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.stream.view.StreamScrollTopView.4 */
    class C12694 extends SimpleAnimationListener {

        /* renamed from: ru.ok.android.ui.stream.view.StreamScrollTopView.4.1 */
        class C12681 extends SimpleAnimatorListener {
            boolean isCanceled;

            C12681() {
                this.isCanceled = false;
            }

            public void onAnimationCancel(Animator animation) {
                this.isCanceled = true;
            }

            public void onAnimationEnd(Animator animation) {
                StreamScrollTopView.this.currentExpandCollapseAnimator = null;
                StreamScrollTopView.this.resetViewState();
                if (!StreamScrollTopView.this.isShownOnScroll) {
                    StreamScrollTopView.this.setVisibility(8);
                    if (!this.isCanceled) {
                        StreamScrollTopView.this.startAnimation(StreamScrollTopView.this.getAnimationOut());
                    }
                }
            }
        }

        C12694() {
        }

        public void onAnimationEnd(Animation animation) {
            if (StreamScrollTopView.this.measuredExpandedWidth < 0 || StreamScrollTopView.this.measuredCollapsedWidth < 0) {
                StreamScrollTopView.this.measureExpandedCollapsed();
            }
            ValueAnimator collapseButtonAnimator = StreamScrollTopView.this.createLayoutParamWidthAnimator(StreamScrollTopView.this.measuredExpandedWidth, StreamScrollTopView.this.measuredCollapsedWidth);
            collapseButtonAnimator.addListener(new C12681());
            collapseButtonAnimator.setDuration(600);
            collapseButtonAnimator.start();
            StreamScrollTopView.this.currentExpandCollapseAnimator = collapseButtonAnimator;
        }
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR;
        final boolean isExpanded;
        final boolean isExpandedShownOnScroll;
        final boolean isShownOnScroll;
        final int newEventsCount;

        /* renamed from: ru.ok.android.ui.stream.view.StreamScrollTopView.SavedState.1 */
        static class C12701 implements Creator<SavedState> {
            C12701() {
            }

            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        public SavedState(Parcel source) {
            boolean z;
            boolean z2 = true;
            super(source);
            if (source.readByte() != null) {
                z = true;
            } else {
                z = false;
            }
            this.isExpanded = z;
            if (source.readByte() != null) {
                z = true;
            } else {
                z = false;
            }
            this.isShownOnScroll = z;
            if (source.readByte() == null) {
                z2 = false;
            }
            this.isExpandedShownOnScroll = z2;
            this.newEventsCount = source.readInt();
        }

        public SavedState(Parcelable superState, boolean isExpanded, boolean isShownOnScroll, boolean isExpandedShownOnScroll, int newEventsCount) {
            super(superState);
            this.isExpanded = isExpanded;
            this.isShownOnScroll = isShownOnScroll;
            this.isExpandedShownOnScroll = isExpandedShownOnScroll;
            this.newEventsCount = newEventsCount;
        }

        public void writeToParcel(Parcel dest, int flags) {
            byte b;
            byte b2 = (byte) 1;
            super.writeToParcel(dest, flags);
            if (this.isExpanded) {
                b = (byte) 1;
            } else {
                b = (byte) 0;
            }
            dest.writeByte(b);
            if (this.isShownOnScroll) {
                b = (byte) 1;
            } else {
                b = (byte) 0;
            }
            dest.writeByte(b);
            if (!this.isExpandedShownOnScroll) {
                b2 = (byte) 0;
            }
            dest.writeByte(b2);
            dest.writeInt(this.newEventsCount);
        }

        public String toString() {
            return "SavedState[isExpanded=" + this.isExpanded + " isShownOnScroll=" + this.isShownOnScroll + " isExpandedShownOnScroll=" + this.isExpandedShownOnScroll + " newEventsCount=" + this.newEventsCount + "]";
        }

        static {
            CREATOR = new C12701();
        }
    }

    public StreamScrollTopView(Context context) {
        this(context, null);
    }

    public StreamScrollTopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 2131296611);
    }

    public StreamScrollTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, 0, defStyleAttr);
    }

    public StreamScrollTopView(Context context, AttributeSet attrs, int defThemeAttr, int defStyle) {
        super(context, attrs);
        this.transitionDrawableExpanded = false;
        this.measuredExpandedWidth = -1;
        this.measuredCollapsedWidth = -1;
        this.isExpanded = false;
        this.isShownOnScroll = false;
        this.isExpandedShownOnScroll = true;
        this.hasPendingExpand = false;
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.StreamScrollTopView, defThemeAttr, defStyle);
        int layoutResId = a.getResourceId(0, 2130903521);
        this.showCounter = a.getBoolean(1, false);
        a.recycle();
        LocalizationManager.inflate(context, layoutResId, (ViewGroup) this, true);
        this.textView = (TextView) findViewById(C0263R.id.text);
        this.imageView = (ImageView) findViewById(C0263R.id.image);
        this.transitionDrawable = (TransitionDrawable) getResources().getDrawable(2130838688);
        this.transitionDrawable.setCrossFadeEnabled(true);
        this.imageView.setImageDrawable(this.transitionDrawable);
    }

    public void setNewEventCount(int newEventsCount) {
        this.newEventsCount = newEventsCount;
        if (this.showCounter) {
            Context context = getContext();
            this.textView.setText(LocalizationManager.getString(context, 2131166641, Integer.valueOf(this.newEventsCount)));
        }
        if (this.newEventsCount > 0) {
            expand();
        } else {
            collapse();
        }
    }

    public int getNewEventsCount() {
        return this.newEventsCount;
    }

    private void show() {
        if (getVisibility() != 0) {
            clearAnimation();
            resetViewState();
            startAnimation(getAnimationIn());
        }
    }

    private void hide() {
        if (getVisibility() == 0) {
            clearAnimation();
            resetViewState();
            setVisibility(8);
            startAnimation(getAnimationOut());
        }
    }

    private void expand() {
        if (getMeasuredWidth() == 0) {
            setVisibility(4);
            this.hasPendingExpand = true;
        } else if (!this.isExpanded) {
            doExpand();
        }
        this.isExpanded = true;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.hasPendingExpand) {
            this.hasPendingExpand = false;
            if (this.isExpanded) {
                doExpand();
            }
        }
    }

    private void doExpand() {
        clearAnimation();
        if (getVisibility() != 0) {
            setVisibility(0);
            this.textView.setVisibility(8);
            Animation inAnimation = getAnimationIn();
            inAnimation.setAnimationListener(new C12651());
            startAnimation(inAnimation);
        } else if (this.textView.getVisibility() == 8) {
            startExpandAnimation();
        } else {
            resetViewState();
        }
    }

    private void startExpandAnimation() {
        if (this.measuredExpandedWidth < 0 || this.measuredCollapsedWidth < 0) {
            measureExpandedCollapsed();
        }
        ValueAnimator expandButtonAnimator = createLayoutParamWidthAnimator(this.measuredCollapsedWidth, this.measuredExpandedWidth);
        expandButtonAnimator.addListener(new C12662());
        expandButtonAnimator.setDuration(600);
        expandButtonAnimator.start();
        this.currentExpandCollapseAnimator = expandButtonAnimator;
    }

    public void clearAnimation() {
        super.clearAnimation();
        this.textView.clearAnimation();
        if (this.currentExpandCollapseAnimator != null) {
            this.currentExpandCollapseAnimator.cancel();
            this.currentExpandCollapseAnimator = null;
        }
        if (this.transitionDrawableExpanded != this.isExpanded) {
            this.transitionDrawable.reverseTransition(1);
            this.transitionDrawableExpanded = this.isExpanded;
        }
    }

    private void resetViewState() {
        int i = 0;
        this.textView.setVisibility(this.isExpanded ? 0 : 8);
        restoreLayoutParamWidth(this.isExpanded);
        if (!((this.isExpanded && this.isExpandedShownOnScroll) || this.isShownOnScroll)) {
            i = 4;
        }
        setVisibility(i);
    }

    private void restoreLayoutParamWidth(boolean expanded) {
        if (this.measuredExpandedWidth < 0 || this.measuredCollapsedWidth < 0) {
            measureExpandedCollapsed();
        }
        LayoutParams lp = getLayoutParams();
        if (lp != null) {
            lp.width = expanded ? this.measuredExpandedWidth : -2;
            setLayoutParams(lp);
        }
    }

    private ValueAnimator createLayoutParamWidthAnimator(int startWidth, int endWidth) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(new int[]{startWidth, endWidth});
        valueAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        valueAnimator.addUpdateListener(new C12673());
        return valueAnimator;
    }

    private void measureExpandedCollapsed() {
        int unspecifiedSpec = MeasureSpec.makeMeasureSpec(0, 0);
        measureChildWithMargins(this.textView, unspecifiedSpec, 0, unspecifiedSpec, 0);
        measureChildWithMargins(this.imageView, unspecifiedSpec, 0, unspecifiedSpec, 0);
        this.measuredCollapsedWidth = (getPaddingLeft() + this.imageView.getMeasuredWidth()) + getPaddingRight();
        this.measuredExpandedWidth = (this.measuredCollapsedWidth + this.textView.getMeasuredWidth()) - ((int) Utils.dipToPixels(4.0f));
    }

    private void collapse() {
        if (this.isExpanded) {
            this.isExpanded = false;
            clearAnimation();
            if (getVisibility() == 0 && this.textView.getVisibility() == 0) {
                startCollapseAnimation();
            } else {
                resetViewState();
            }
        }
    }

    private void startCollapseAnimation() {
        Animation textOutAnimation = getAnimationOut();
        this.textView.setVisibility(8);
        this.textView.startAnimation(textOutAnimation);
        if (this.transitionDrawableExpanded) {
            this.transitionDrawable.reverseTransition(600);
            this.transitionDrawableExpanded = false;
        }
        textOutAnimation.setAnimationListener(new C12694());
    }

    public void onScroll(boolean showOnScroll, boolean hideOnScroll, boolean showExpandedOnScroll, boolean hideExpandedOnScroll) {
        int i = 1;
        if (isEnabled()) {
            this.isShownOnScroll |= showOnScroll;
            this.isShownOnScroll = (!hideOnScroll ? 1 : 0) & this.isShownOnScroll;
            this.isExpandedShownOnScroll |= showExpandedOnScroll;
            boolean z = this.isExpandedShownOnScroll;
            if (hideExpandedOnScroll) {
                i = 0;
            }
            this.isExpandedShownOnScroll = z & i;
            if (showOnScroll || (this.isExpanded && showExpandedOnScroll)) {
                show();
            } else if (hideOnScroll || (this.isExpanded && hideExpandedOnScroll)) {
                hide();
            }
        }
    }

    private Animation getAnimationOut() {
        Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setInterpolator(new DecelerateInterpolator());
        fadeOut.setDuration(100);
        return fadeOut;
    }

    private Animation getAnimationIn() {
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(100);
        return fadeIn;
    }

    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), this.isExpanded, this.isShownOnScroll, this.isExpandedShownOnScroll, this.newEventsCount);
    }

    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.isExpanded = savedState.isExpanded;
        this.isShownOnScroll = savedState.isShownOnScroll;
        this.isExpandedShownOnScroll = savedState.isExpandedShownOnScroll;
        this.newEventsCount = savedState.newEventsCount;
        resetViewState();
    }
}
