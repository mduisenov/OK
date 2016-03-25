package ru.ok.android.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import ru.ok.android.C0206R;

public class FadeButton extends FrameLayout {
    private static final int[] ATTRS_ARRAY;
    private final AlphaAnimation FADE_OUT;
    private AlphaAnimation FROM_PRESS_TO_FOCUS;
    protected Drawable mBackgroundDrawable;
    protected ImageView mBackgroundView;
    protected boolean mFocused;
    protected boolean mPressed;

    /* renamed from: ru.ok.android.ui.custom.FadeButton.1 */
    class C06131 extends AlphaAnimation {

        /* renamed from: ru.ok.android.ui.custom.FadeButton.1.1 */
        class C06121 implements AnimationListener {
            C06121() {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                FadeButton.this.setBackgroundDrawableAlpha(0);
            }
        }

        C06131(float x0, float x1) {
            super(x0, x1);
            setDuration(200);
            setAnimationListener(new C06121());
        }
    }

    /* renamed from: ru.ok.android.ui.custom.FadeButton.2 */
    class C06152 extends AlphaAnimation {

        /* renamed from: ru.ok.android.ui.custom.FadeButton.2.1 */
        class C06141 implements AnimationListener {
            C06141() {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                FadeButton.this.setBackgroundDrawableAlpha(C0206R.styleable.Theme_checkedTextViewStyle);
            }
        }

        C06152(float x0, float x1) {
            super(x0, x1);
            setDuration(200);
            setAnimationListener(new C06141());
        }
    }

    private final class OnClickListenerWrapper implements OnClickListener {
        private OnClickListener onClickListener;

        /* renamed from: ru.ok.android.ui.custom.FadeButton.OnClickListenerWrapper.1 */
        class C06161 implements Runnable {
            final /* synthetic */ View val$view;

            C06161(View view) {
                this.val$view = view;
            }

            public void run() {
                if (OnClickListenerWrapper.this.onClickListener != null) {
                    OnClickListenerWrapper.this.onClickListener.onClick(this.val$view);
                }
            }
        }

        public OnClickListenerWrapper(OnClickListener toWrap) {
            this.onClickListener = toWrap;
        }

        public void onClick(View view) {
            view.postDelayed(new C06161(view), 200);
        }
    }

    static {
        ATTRS_ARRAY = new int[]{16842964};
    }

    public FadeButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.FADE_OUT = new C06131(1.0f, 0.0f);
        this.FROM_PRESS_TO_FOCUS = new C06152(0.8f, 0.4f);
        build(context, attrs, defStyle);
    }

    public FadeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.FADE_OUT = new C06131(1.0f, 0.0f);
        this.FROM_PRESS_TO_FOCUS = new C06152(0.8f, 0.4f);
        build(context, attrs, 0);
    }

    public FadeButton(Context context) {
        super(context);
        this.FADE_OUT = new C06131(1.0f, 0.0f);
        this.FROM_PRESS_TO_FOCUS = new C06152(0.8f, 0.4f);
        build(context, null, 0);
    }

    protected void build(Context context, AttributeSet attrs, int defStyle) {
        setFocusable(true);
        setOnClickListener(new OnClickListenerWrapper());
        this.mBackgroundView = new ImageView(context);
        this.mBackgroundView.setEnabled(false);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, ATTRS_ARRAY);
            this.mBackgroundDrawable = typedArray.getDrawable(0);
            typedArray.recycle();
        }
        setBackgroundDrawable(this.mBackgroundDrawable);
        setBackgroundDrawableAlpha(0);
        addView(this.mBackgroundView, new LayoutParams(-2, -2));
        setClipToPadding(false);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.mBackgroundView.layout(-getPaddingLeft(), -getPaddingTop(), (getWidth() + getPaddingLeft()) + getPaddingRight(), (getHeight() + getPaddingTop()) + getPaddingBottom());
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (isEnabled()) {
            if (event.getAction() == 0) {
                setBackgroundDrawableAlpha(MotionEventCompat.ACTION_MASK);
                this.mPressed = true;
                this.mFocused = false;
            } else if (1 == event.getAction()) {
                if (this.mPressed) {
                    this.FADE_OUT.reset();
                    this.mBackgroundView.startAnimation(this.FADE_OUT);
                    this.mPressed = false;
                }
            } else if (2 == event.getAction()) {
                if (this.mPressed) {
                    boolean outside = false;
                    if (event.getY() < 0.0f || event.getX() < 0.0f) {
                        outside = true;
                    } else if (event.getY() > ((float) getHeight())) {
                        outside = true;
                    } else if (event.getX() > ((float) getWidth())) {
                        outside = true;
                    }
                    if (outside) {
                        this.FADE_OUT.reset();
                        this.mBackgroundView.startAnimation(this.FADE_OUT);
                        this.mPressed = false;
                    }
                }
            } else if (3 == event.getAction() && this.mPressed) {
                this.FADE_OUT.reset();
                this.mBackgroundView.startAnimation(this.FADE_OUT);
                this.mPressed = false;
            }
        }
        return true;
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (gainFocus) {
            setBackgroundDrawableAlpha(C0206R.styleable.Theme_checkedTextViewStyle);
            this.mFocused = true;
        } else if (this.mFocused) {
            this.FADE_OUT.reset();
            this.mBackgroundView.startAnimation(this.FADE_OUT);
        }
    }

    public void showPressed(boolean pressed) {
        onFocusChanged(pressed, 0, null);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!this.mPressed && (keyCode == 66 || keyCode == 23)) {
            setBackgroundDrawableAlpha(MotionEventCompat.ACTION_MASK);
            this.mPressed = true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (this.mPressed && (keyCode == 66 || keyCode == 23)) {
            if (this.mFocused) {
                setBackgroundDrawableAlpha(MotionEventCompat.ACTION_MASK);
                this.FROM_PRESS_TO_FOCUS.reset();
                this.mBackgroundView.startAnimation(this.FROM_PRESS_TO_FOCUS);
            } else {
                this.FADE_OUT.reset();
                this.mBackgroundView.startAnimation(this.FADE_OUT);
            }
            this.mPressed = false;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void setBackgroundColor(int color) {
        setBackgroundDrawable(new ColorDrawable(color));
    }

    public void setBackgroundResource(int resid) {
        if (this.mBackgroundView != null) {
            this.mBackgroundView.setBackgroundResource(resid);
            setBackgroundDrawable(this.mBackgroundView.getBackground());
        }
    }

    public void setBackgroundDrawable(Drawable d) {
        if (this.mBackgroundView != null) {
            this.mBackgroundDrawable = d.mutate();
            this.mBackgroundView.setBackgroundDrawable(this.mBackgroundDrawable);
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.mBackgroundView.setEnabled(enabled);
    }

    protected final void setBackgroundDrawableAlpha(int alpha) {
        if (this.mBackgroundDrawable != null) {
            this.mBackgroundDrawable.setAlpha(alpha);
            this.mBackgroundDrawable.invalidateSelf();
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        super.setOnClickListener(new OnClickListenerWrapper(onClickListener));
    }
}
