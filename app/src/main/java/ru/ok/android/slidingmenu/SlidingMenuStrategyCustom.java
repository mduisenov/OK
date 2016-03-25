package ru.ok.android.slidingmenu;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnChangePosition;
import com.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.slidingmenu.lib.app.SlidingActivityHelper;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.slidingmenu.SlidingMenuStrategy.StrategyType;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.tabbar.OdklTabbar;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.android.widget.MenuView;

public class SlidingMenuStrategyCustom extends SlidingMenuStrategy implements OnChangePosition, OnClosedListener, OnOpenedListener {
    private int closedVisibleWidth;
    private boolean fromUser;
    private int fullWidth;
    private Boolean isStarted;
    private SlidingActivityHelper mHelper;

    protected SlidingMenuStrategyCustom(OdklSlidingMenuFragmentActivity activity) {
        super(activity);
        this.isStarted = null;
        this.fromUser = true;
        this.mHelper = new SlidingActivityHelper(activity);
    }

    protected void setContentView(int layoutResID) {
        setContentView(this.activity.getLayoutInflater().inflate(layoutResID, null));
    }

    protected void setContentView(View view) {
        setContentViewSpec(view, new LayoutParams(-1, -1));
    }

    protected void setContentView(View view, LayoutParams params) {
        this.activity.setContentView(view, params);
    }

    protected void closeMenu() {
        this.mHelper.showContent();
    }

    protected void openMenu() {
        this.fromUser = false;
        this.mHelper.showMenu();
    }

    protected boolean isMenuOpen() {
        return this.mHelper.getSlidingMenu().isMenuShowing();
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        this.mHelper.onCreate(savedInstanceState);
        MenuView menuView = new MenuView(this.activity);
        menuView.setId(2131624304);
        menuView.setFitsSystemWindows(true);
        menuView.setDividerHeight(0);
        this.activity.setMenuAdapter(this.activity.createMenuAdapterAndInitItems());
        this.activity.notifyCreateAdapter();
        setBehindContentView(menuView);
        this.activity.setMenuView(menuView);
        Resources r = this.activity.getResources();
        int shadowWidth = r.getDimensionPixelSize(2131231163);
        SlidingMenu sm = this.mHelper.getSlidingMenu();
        sm.setShadowWidth(shadowWidth);
        sm.setBehindOffsetRes(2131230736);
        sm.setRecommentedWidthRes(2131231182);
        sm.setFadeDegree(0.0f);
        this.closedVisibleWidth = SlidingMenuStrategy.getClosedVisibleWidth(r, r.getConfiguration().orientation, StrategyType.Custom);
        this.fullWidth = this.activity.getResources().getDimensionPixelSize(2131231182);
        sm.setLeftPadding(this.closedVisibleWidth);
        sm.setBehindScrollScale(0.0f);
        sm.setShadowDrawable(2130838657);
        sm.setTouchModeAbove(0);
        sm.setSlidingEnabled(true);
        sm.setOnClosedListener(this);
        sm.setOnOpenedListener(this);
        sm.setOnChangePosition(this);
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        this.mHelper.onPostCreate(savedInstanceState);
        if (this.activity.getMenuView() != null) {
            this.activity.getMenuView().setAdapter(this.activity.getMenuAdapter());
            EventsManager.getInstance().sendActualValue();
            GlobalBus.send(2131624108, new BusEvent());
        }
    }

    protected void onPause() {
        this.mHelper.onPause();
    }

    protected void onResume() {
        this.mHelper.onResume();
    }

    public void setContentViewSpec(View v, LayoutParams params) {
        this.activity.odklSuperSetContentView(v, params);
        this.mHelper.registerAboveContentView(v, params);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.mHelper.onSaveInstanceState(outState);
    }

    private void setBehindContentView(View v) {
        setBehindContentView(v, new LayoutParams(-1, -1));
    }

    private void setBehindContentView(View v, LayoutParams params) {
        this.mHelper.setBehindContentView(v, params);
    }

    public View findViewById(int id) {
        View v = super.findViewById(id);
        return v != null ? v : this.mHelper.findViewById(id);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return this.mHelper.onKeyUp(keyCode, event);
    }

    public void buildTabbarActions(OdklTabbar tabbar) {
        tabbar.addAction(tabbar.getMenuTabbarAction());
        tabbar.addAction(tabbar.getFeedAction());
        tabbar.addAction(tabbar.getDiscussionsAction());
        tabbar.addAction(tabbar.getConversationAction());
        tabbar.addAction(tabbar.getMusicAction());
    }

    public boolean isCollapsible() {
        return DeviceUtils.getType(this.activity) != DeviceLayoutType.SMALL;
    }

    public void onClosed() {
        this.activity.scrollMenuToTop();
    }

    public void onOpened() {
        KeyBoardUtils.hideKeyBoard(this.activity, this.activity.getWindow().getDecorView().getApplicationWindowToken());
        if (this.fromUser) {
            StatisticManager.getInstance().addStatisticEvent("left_menu-open_swipe", new Pair[0]);
        }
        this.fromUser = true;
    }

    public void onChangePosition(int dx) {
        this.activity.onSlidingMenuChangedOpeningRatio(((float) dx) / ((float) (this.closedVisibleWidth - this.fullWidth)));
        if (dx < -5 && this.isStarted != Boolean.TRUE) {
            this.isStarted = Boolean.valueOf(true);
            if (this.activity.getMenuAdapter() != null) {
                this.activity.getMenuAdapter().setCollapsed(false);
            }
        } else if (dx > -5 && this.isStarted != Boolean.FALSE) {
            this.isStarted = Boolean.valueOf(false);
            if (this.activity.getMenuAdapter() != null) {
                this.activity.getMenuAdapter().setCollapsed(true);
            }
        }
    }

    public void processRunnableClick(Runnable runnable) {
        if (isMenuOpen()) {
            super.processRunnableClick(runnable);
        } else {
            runnable.run();
        }
    }
}
