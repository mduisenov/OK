package ru.ok.android.ui.activity.compat;

import android.content.Context;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.LayoutParams;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import ru.ok.android.ui.coordinator.behaviors.AppBarLayoutBehavior;
import ru.ok.android.ui.tabbar.OdklTabbar;
import ru.ok.android.ui.tabbar.manager.BaseTabbarManager;
import ru.ok.android.ui.tabbar.manager.FullTabbarManager;
import ru.ok.android.ui.tabbar.manager.MockTabbarManager;
import ru.ok.android.ui.tabbar.manager.TabbarManager;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DimenUtils;
import ru.ok.android.utils.controls.events.EventsManager.OnEvents;
import ru.ok.model.events.OdnkEvent;

public class BaseCompatToolbarActivity extends AppCompatActivity implements BaseTabbarManager, OnEvents {
    protected AppBarLayout appBarLayout;
    protected FrameLayout contentWrapper;
    protected CoordinatorLayout coordinator;
    private CoordinatorManager coordinatorManager;
    private AlphaForegroundColorSpan foregroundColorSpan;
    protected View shadow;
    private SpannableString spannableString;
    private FullTabbarManager tabbarManager;
    protected Toolbar toolbar;

    private class AlphaForegroundColorSpan extends ForegroundColorSpan {
        private int mAlpha;

        public AlphaForegroundColorSpan(int color) {
            super(color);
            this.mAlpha = MotionEventCompat.ACTION_MASK;
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.mAlpha);
        }

        public void updateDrawState(TextPaint ds) {
            ds.setColor(getAlphaColor());
        }

        public void setAlpha(int alpha) {
            this.mAlpha = alpha;
        }

        private int getAlphaColor() {
            int foregroundColor = getForegroundColor();
            return Color.argb(this.mAlpha, Color.red(foregroundColor), Color.green(foregroundColor), Color.blue(foregroundColor));
        }
    }

    public BaseCompatToolbarActivity() {
        this.foregroundColorSpan = new AlphaForegroundColorSpan(-1);
        this.tabbarManager = new MockTabbarManager();
    }

    private void prepareSetContentView() {
        if (VERSION.SDK_INT >= 21) {
            getWindow().addFlags(LinearLayoutManager.INVALID_OFFSET);
        }
        super.setContentView(getBaseCompatLayoutId());
        onBaseBindViews();
        if (isUseTabbar()) {
            this.tabbarManager = new TabbarManager(this, this.coordinator, this.contentWrapper);
        }
        this.coordinatorManager = new CoordinatorManager(this.coordinator);
        if (this.toolbar != null) {
            setSupportActionBar(this.toolbar);
            if (!isToolbarTitleEnabled()) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
        postProcessView();
    }

    public boolean isShadowVisible() {
        return !isSupportToolbarOverlay() && isSupportToolbarVisible();
    }

    protected void postProcessView() {
        int i = 0;
        if (this.appBarLayout != null) {
            this.appBarLayout.setVisibility(isSupportToolbarVisible() ? 0 : 8);
            ((LayoutParams) this.toolbar.getLayoutParams()).setScrollFlags(isToolbarLocked() ? 0 : 5);
            if (isToolbarLocked()) {
                ((AppBarLayoutBehavior) ((CoordinatorLayout.LayoutParams) this.appBarLayout.getLayoutParams()).getBehavior()).setToolbarLocked(true);
            }
        }
        if (this.contentWrapper != null) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) this.contentWrapper.getLayoutParams();
            if (isSupportToolbarOverlay()) {
                lp.gravity = 85;
                if (isSupportToolbarVisible()) {
                    lp.setAnchorId(2131624640);
                    lp.anchorGravity = 48;
                    lp.setBehavior(null);
                }
            } else if (isToolbarLocked()) {
                lp.setBehavior(null);
                this.contentWrapper.setPadding(0, isSupportToolbarVisible() ? DimenUtils.getToolbarHeight(this) : 0, 0, 0);
            }
        }
        if (this.shadow != null) {
            View view = this.shadow;
            if (!isShadowVisible()) {
                i = 8;
            }
            view.setVisibility(i);
            this.shadow.bringToFront();
        }
    }

    protected void onBaseBindViews() {
        this.contentWrapper = (FrameLayout) findViewById(2131624639);
        this.shadow = findViewById(2131624642);
        this.toolbar = (Toolbar) findViewById(2131624641);
        this.coordinator = (CoordinatorLayout) findViewById(2131624637);
        this.appBarLayout = (AppBarLayout) findViewById(2131624640);
    }

    protected int getBaseCompatLayoutId() {
        return 2130903113;
    }

    protected boolean isSupportToolbarVisible() {
        return true;
    }

    public Toolbar getSupportToolbar() {
        return this.toolbar;
    }

    public void setContentView() {
        prepareSetContentView();
    }

    public void setContentView(int layoutResID) {
        prepareSetContentView();
        LayoutInflater.from(this).inflate(layoutResID, this.contentWrapper, true);
    }

    public void setContentView(View view) {
        prepareSetContentView();
        this.contentWrapper.addView(view, -1, -1);
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        prepareSetContentView();
        this.contentWrapper.addView(view, params);
    }

    protected boolean isSupportToolbarOverlay() {
        return false;
    }

    protected boolean isToolbarTitleEnabled() {
        return true;
    }

    public void setToolbarTitle(CharSequence title) {
        this.spannableString = new SpannableString(title);
        updateHeader();
    }

    public void setToolbarTitleTextAlpha(int newAlpha) {
        this.foregroundColorSpan.setAlpha(newAlpha);
        updateHeader();
    }

    public void setShadowAlpha(int newAlpha) {
        this.shadow.setAlpha(((float) newAlpha) / 255.0f);
    }

    private void updateHeader() {
        if (this.spannableString != null) {
            this.spannableString.setSpan(this.foregroundColorSpan, 0, this.spannableString.length(), 33);
            this.toolbar.setTitle(this.spannableString);
        }
    }

    protected void onResume() {
        super.onResume();
        this.tabbarManager.onResume();
        appBarExpandAnimated();
    }

    protected void onPause() {
        super.onPause();
        this.tabbarManager.onPause();
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode != 82 || !isMenuWorkaroundRequired()) {
            return super.onKeyUp(keyCode, event);
        }
        openOptionsMenu();
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 82 && isMenuWorkaroundRequired()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.tabbarManager.onRestoreInstanceState(savedInstanceState);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.tabbarManager.onSaveInstanceState(outState);
    }

    public OdklTabbar getTabbarView() {
        return this.tabbarManager.getTabbarView();
    }

    public void showAboveTabbar() {
        this.tabbarManager.showAboveTabbar();
    }

    public int getScrollTabbar() {
        return this.tabbarManager.getScrollTabbar();
    }

    public void setScrollTabbar(float scroll) {
        this.tabbarManager.setScrollTabbar(scroll);
    }

    public void showTabbar(boolean isAnimate) {
        this.tabbarManager.showTabbar(isAnimate);
    }

    public void setNeedShowTabbar(boolean needShowTabbar) {
        this.tabbarManager.setNeedShowTabbar(needShowTabbar);
    }

    public void onGetNewEvents(ArrayList<OdnkEvent> returnList) {
        this.tabbarManager.onGetNewEvents(returnList);
    }

    public boolean isUseTabbar() {
        return false;
    }

    public void restoreToolbarBehavior() {
    }

    public static boolean isUseTabbar(Context context) {
        return (context instanceof BaseCompatToolbarActivity) && ((BaseCompatToolbarActivity) context).isUseTabbar();
    }

    private static boolean isMenuWorkaroundRequired() {
        return VERSION.SDK_INT < 19 && (DeviceUtils.isLG() || DeviceUtils.isE6710());
    }

    public ViewGroup getFullContainer() {
        return this.coordinator;
    }

    public CoordinatorManager getCoordinatorManager() {
        return this.coordinatorManager;
    }

    public AppBarLayout getAppBarLayout() {
        return this.appBarLayout;
    }

    public void appBarExpandAnimated() {
        if (this.appBarLayout != null && this.appBarLayout.getTop() != 0) {
            this.appBarLayout.setExpanded(true, true);
        }
    }

    public boolean isAppBarLocked() {
        if (this.coordinator == null) {
            return true;
        }
        boolean z = this.toolbar != null && ((LayoutParams) this.toolbar.getLayoutParams()).getScrollFlags() == 0;
        return z;
    }

    public void toolbarLockScroll() {
        ((LayoutParams) this.toolbar.getLayoutParams()).setScrollFlags(0);
    }

    protected boolean isToolbarLocked() {
        return false;
    }
}
