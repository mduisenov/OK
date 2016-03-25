package ru.ok.android.ui.custom.parallax;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import ru.ok.android.C0206R;

public class ParallaxListView extends ListView implements OnScrollListener {
    private boolean isCircular;
    private OnScrollListener listener;
    private float parallaxFactor;
    private ParallaxedView parallaxedView;

    public class ParallaxedListView extends ParallaxedView {
        public ParallaxedListView(View view) {
            super(view);
        }

        protected void translatePreICS(View view, float offset) {
            TranslateAnimation ta = new TranslateAnimation(0.0f, 0.0f, offset, offset);
            ta.setDuration(0);
            ta.setFillAfter(true);
            view.setAnimation(ta);
            ta.start();
        }
    }

    public ParallaxListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.parallaxFactor = 1.9f;
        this.listener = null;
        init(context, attrs);
    }

    public ParallaxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.parallaxFactor = 1.9f;
        this.listener = null;
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.obtainStyledAttributes(attrs, C0206R.styleable.ParallaxScroll);
        this.parallaxFactor = typeArray.getFloat(0, 1.9f);
        this.isCircular = typeArray.getBoolean(3, false);
        typeArray.recycle();
        super.setOnScrollListener(this);
    }

    public void setOnScrollListener(OnScrollListener l) {
        this.listener = l;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    protected void parallaxScroll() {
        if (this.isCircular) {
            circularParallax();
        } else {
            headerParallax();
        }
    }

    private void circularParallax() {
        if (getChildCount() > 0) {
            int top = -getChildAt(0).getTop();
            float factor = this.parallaxFactor;
            fillParallaxedViews();
            this.parallaxedView.setOffset(((float) top) / factor);
        }
    }

    private void headerParallax() {
        if (this.parallaxedView != null && getChildCount() > 0) {
            int top = -getChildAt(0).getTop();
            this.parallaxedView.setOffset(((float) top) / this.parallaxFactor);
        }
    }

    private void fillParallaxedViews() {
        if (this.parallaxedView != null && this.parallaxedView.is(getChildAt(0))) {
            return;
        }
        if (this.parallaxedView != null) {
            this.parallaxedView.setOffset(0.0f);
            this.parallaxedView.setView(getChildAt(0));
            return;
        }
        this.parallaxedView = new ParallaxedListView(getChildAt(0));
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        parallaxScroll();
        if (this.listener != null) {
            this.listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (this.listener != null) {
            this.listener.onScrollStateChanged(view, scrollState);
        }
    }
}
