package ru.ok.android.fresco;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.DraweeHolder;

public class DraweeHolderView extends View {
    private DraweeHolder<GenericDraweeHierarchy> holder;

    public DraweeHolderView(Context context) {
        super(context);
        initHolder();
    }

    protected void initHolder() {
    }

    public DraweeHolderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHolder();
    }

    public DraweeHolderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHolder();
    }

    public void setHolder(DraweeHolder<GenericDraweeHierarchy> holder) {
        if (this.holder != null) {
            this.holder.onDetach();
        }
        this.holder = holder;
    }

    public Drawable getDrawable() {
        if (this.holder != null) {
            return this.holder.getTopLevelDrawable();
        }
        return null;
    }

    public DraweeHolder<GenericDraweeHierarchy> getHolder() {
        return this.holder;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.holder != null) {
            this.holder.onDetach();
        }
    }

    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        if (this.holder != null) {
            this.holder.onDetach();
        }
    }

    protected boolean verifyDrawable(Drawable who) {
        return (this.holder != null && this.holder.getTopLevelDrawable() == who) || super.verifyDrawable(who);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.holder != null) {
            this.holder.onAttach();
        }
    }

    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        if (this.holder != null) {
            this.holder.onAttach();
        }
    }
}
