package ru.ok.android.drawable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.util.AttributeSet;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class VariableInsetDrawable extends Drawable implements Callback {
    private InsetState mInsetState;
    private boolean mMutated;
    private final Rect mTmpRect;

    static final class InsetState extends ConstantState {
        boolean mCanConstantState;
        int mChangingConfigurations;
        boolean mCheckedConstantState;
        Drawable mDrawable;
        int mInsetBottom;
        int mInsetLeft;
        int mInsetRight;
        int mInsetTop;

        InsetState(InsetState orig, VariableInsetDrawable owner, Resources res) {
            if (orig != null) {
                if (res != null) {
                    this.mDrawable = orig.mDrawable.getConstantState().newDrawable(res);
                } else {
                    this.mDrawable = orig.mDrawable.getConstantState().newDrawable();
                }
                this.mDrawable.setCallback(owner);
                this.mInsetLeft = orig.mInsetLeft;
                this.mInsetTop = orig.mInsetTop;
                this.mInsetRight = orig.mInsetRight;
                this.mInsetBottom = orig.mInsetBottom;
                this.mCanConstantState = true;
                this.mCheckedConstantState = true;
            }
        }

        public Drawable newDrawable() {
            return new VariableInsetDrawable(null, null);
        }

        public Drawable newDrawable(Resources res) {
            return new VariableInsetDrawable(res, null);
        }

        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        boolean canConstantState() {
            if (!this.mCheckedConstantState) {
                this.mCanConstantState = this.mDrawable.getConstantState() != null;
                this.mCheckedConstantState = true;
            }
            return this.mCanConstantState;
        }
    }

    VariableInsetDrawable() {
        this(null, null);
    }

    public VariableInsetDrawable(Drawable drawable) {
        this(null, null);
        this.mInsetState.mDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    public void setInsets(int insetLeft, int insetTop, int insetRight, int insetBottom) {
        this.mInsetState.mInsetLeft = insetLeft;
        this.mInsetState.mInsetTop = insetTop;
        this.mInsetState.mInsetRight = insetRight;
        this.mInsetState.mInsetBottom = insetBottom;
        invalidateDrawable(this);
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        throw new UnsupportedOperationException("This drawable should not (and can not) be used in XMLs");
    }

    public void invalidateDrawable(Drawable who) {
        Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, what, when);
        }
    }

    public void unscheduleDrawable(Drawable who, Runnable what) {
        Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, what);
        }
    }

    public void draw(Canvas canvas) {
        this.mInsetState.mDrawable.draw(canvas);
    }

    public int getChangingConfigurations() {
        return (super.getChangingConfigurations() | this.mInsetState.mChangingConfigurations) | this.mInsetState.mDrawable.getChangingConfigurations();
    }

    public boolean getPadding(Rect padding) {
        boolean pad = this.mInsetState.mDrawable.getPadding(padding);
        padding.left += this.mInsetState.mInsetLeft;
        padding.right += this.mInsetState.mInsetRight;
        padding.top += this.mInsetState.mInsetTop;
        padding.bottom += this.mInsetState.mInsetBottom;
        if (pad || (((this.mInsetState.mInsetLeft | this.mInsetState.mInsetRight) | this.mInsetState.mInsetTop) | this.mInsetState.mInsetBottom) != 0) {
            return true;
        }
        return false;
    }

    public boolean setVisible(boolean visible, boolean restart) {
        this.mInsetState.mDrawable.setVisible(visible, restart);
        return super.setVisible(visible, restart);
    }

    public void setAlpha(int alpha) {
        this.mInsetState.mDrawable.setAlpha(alpha);
    }

    public int getAlpha() {
        return this.mInsetState.mDrawable.getAlpha();
    }

    public void setColorFilter(ColorFilter cf) {
        this.mInsetState.mDrawable.setColorFilter(cf);
    }

    public int getOpacity() {
        return this.mInsetState.mDrawable.getOpacity();
    }

    public boolean isStateful() {
        return this.mInsetState.mDrawable.isStateful();
    }

    protected boolean onStateChange(int[] state) {
        boolean changed = this.mInsetState.mDrawable.setState(state);
        onBoundsChange(getBounds());
        return changed;
    }

    protected void onBoundsChange(Rect bounds) {
        Rect r = this.mTmpRect;
        r.set(bounds);
        r.left += this.mInsetState.mInsetLeft;
        r.top += this.mInsetState.mInsetTop;
        r.right -= this.mInsetState.mInsetRight;
        r.bottom -= this.mInsetState.mInsetBottom;
        this.mInsetState.mDrawable.setBounds(r.left, r.top, r.right, r.bottom);
    }

    public int getIntrinsicWidth() {
        return this.mInsetState.mDrawable.getIntrinsicWidth();
    }

    public int getIntrinsicHeight() {
        return this.mInsetState.mDrawable.getIntrinsicHeight();
    }

    public ConstantState getConstantState() {
        if (!this.mInsetState.canConstantState()) {
            return null;
        }
        this.mInsetState.mChangingConfigurations = getChangingConfigurations();
        return this.mInsetState;
    }

    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mInsetState.mDrawable.mutate();
            this.mMutated = true;
        }
        return this;
    }

    public Drawable getDrawable() {
        return this.mInsetState.mDrawable;
    }

    private VariableInsetDrawable(InsetState state, Resources res) {
        this.mTmpRect = new Rect();
        this.mInsetState = new InsetState(state, this, res);
    }
}
