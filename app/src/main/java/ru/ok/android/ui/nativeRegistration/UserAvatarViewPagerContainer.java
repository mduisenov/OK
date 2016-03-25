package ru.ok.android.ui.nativeRegistration;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import ru.ok.android.app.OdnoklassnikiApplication;

public class UserAvatarViewPagerContainer extends FrameLayout {
    private int centerX;
    private ViewPager mPager;
    private float offsetY;

    public UserAvatarViewPagerContainer(Context context) {
        super(context);
        init();
    }

    public UserAvatarViewPagerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UserAvatarViewPagerContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setClipChildren(false);
        setLayerType(1, null);
    }

    protected void onFinishInflate() {
        try {
            this.mPager = (ViewPager) getChildAt(0);
            setCenterX(getViewPager().getWidth() / 2);
        } catch (Exception e) {
            throw new IllegalStateException("The root child of PagerContainer must be a ViewPager");
        }
    }

    public ViewPager getViewPager() {
        return this.mPager;
    }

    public void setCenterX(int width) {
        this.centerX = width;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        ev.offsetLocation((float) (-this.centerX), this.offsetY);
        return this.mPager.dispatchTouchEvent(ev);
    }

    public void setPagerWidth() {
        int newWidth;
        float k;
        int screenSize = getResources().getConfiguration().screenLayout & 15;
        boolean isScreenLarge = screenSize == 3 || screenSize == 4;
        int orientation = getResources().getConfiguration().orientation;
        int width = OdnoklassnikiApplication.getContext().getResources().getDisplayMetrics().widthPixels;
        if (isScreenLarge && orientation == 2) {
            newWidth = width / 4;
            k = 0.6666667f;
        } else {
            newWidth = width / 2;
            k = 2.0f;
        }
        getViewPager().getLayoutParams().width = newWidth;
        setCenterX((int) (((float) newWidth) / k));
        this.mPager.requestLayout();
        this.offsetY = this.mPager.getY();
    }
}
