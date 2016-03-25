package ru.ok.android.fresco;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;

public class FrescoBackgroundRelativeLayout extends RelativeLayout {
    private GenericDraweeHierarchy hierarchy;
    private DraweeHolder holder;

    public FrescoBackgroundRelativeLayout(Context context) {
        super(context);
        init();
    }

    public FrescoBackgroundRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FrescoBackgroundRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.hierarchy = GenericDraweeHierarchyBuilder.newInstance(getResources()).build();
        setWillNotDraw(false);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.holder != null) {
            Drawable drawable = this.holder.getTopLevelDrawable();
            if (drawable != null) {
                drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                drawable.draw(canvas);
            }
        }
    }

    public void setBackgroundUri(Uri uri) {
        if (this.holder == null) {
            this.holder = DraweeHolder.create(this.hierarchy, getContext());
        }
        this.holder.setController(((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setOldController(this.holder.getController())).setUri(uri).build());
        this.holder.getTopLevelDrawable().setCallback(this);
    }

    public void setBackgroundController(DraweeController controller) {
        if (this.holder == null) {
            this.holder = DraweeHolder.create(this.hierarchy, getContext());
        }
        this.holder.setController(controller);
        this.holder.getTopLevelDrawable().setCallback(this);
    }

    public DraweeController getBackgroundController() {
        if (this.holder == null) {
            return null;
        }
        return this.holder.getController();
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
