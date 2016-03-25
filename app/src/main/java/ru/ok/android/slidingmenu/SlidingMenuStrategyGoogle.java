package ru.ok.android.slidingmenu;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.activity.main.OdklActivity;
import ru.ok.android.ui.search.activity.SearchActivity;
import ru.ok.android.ui.settings.SettingsProfileActivity;
import ru.ok.android.ui.tabbar.OdklTabbar;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.widget.MenuView;

public class SlidingMenuStrategyGoogle extends SlidingMenuStrategy {
    protected DrawerLayout drawerLayout;
    private View mContentView;
    private ActionBarDrawerToggle mDrawerToggle;
    private ViewGroup mRootLayout;

    /* renamed from: ru.ok.android.slidingmenu.SlidingMenuStrategyGoogle.1 */
    class C05251 implements Runnable {
        C05251() {
        }

        public void run() {
            SlidingMenuStrategyGoogle.this.activity.setMenuAdapter(SlidingMenuStrategyGoogle.this.activity.createMenuAdapterAndInitItems());
            SlidingMenuStrategyGoogle.this.activity.getMenuView().setAdapter(SlidingMenuStrategyGoogle.this.activity.getMenuAdapter());
            SlidingMenuStrategyGoogle.this.activity.notifyCreateAdapter();
            EventsManager.getInstance().sendActualValue();
            GlobalBus.send(2131624108, new BusEvent());
        }
    }

    /* renamed from: ru.ok.android.slidingmenu.SlidingMenuStrategyGoogle.2 */
    class C05262 extends ActionBarDrawerToggle {
        C05262(Activity x0, DrawerLayout x1, int x2, int x3) {
            super(x0, x1, x2, x3);
        }

        public void onDrawerClosed(View view) {
            SlidingMenuStrategyGoogle.this.activity.invalidateOptionsMenu();
            SlidingMenuStrategyGoogle.this.mDrawerToggle.syncState();
        }

        public void onDrawerOpened(View drawerView) {
            KeyBoardUtils.hideKeyBoard(SlidingMenuStrategyGoogle.this.activity);
            SlidingMenuStrategyGoogle.this.activity.invalidateOptionsMenu();
            SlidingMenuStrategyGoogle.this.mDrawerToggle.syncState();
        }

        public void onDrawerStateChanged(int newState) {
            super.onDrawerStateChanged(newState);
            if (1 == newState && !SlidingMenuStrategyGoogle.this.isMenuOpen()) {
                StatisticManager.getInstance().addStatisticEvent("left_menu-open_swipe", new Pair[0]);
            }
        }
    }

    protected SlidingMenuStrategyGoogle(OdklSlidingMenuFragmentActivity activity) {
        super(activity);
        this.drawerLayout = null;
    }

    protected void setContentView(int layoutResID) {
        View mContentView = LocalizationManager.inflate(this.activity, layoutResID, null, false);
        LocalizationManager.from(this.activity).registerView(mContentView, layoutResID);
        if (this.mRootLayout != null) {
            this.mRootLayout.addView(mContentView);
        }
    }

    protected void setContentView(View view) {
        this.mContentView = view;
        if (this.mRootLayout != null) {
            this.mRootLayout.addView(this.mContentView);
        }
    }

    protected void setContentView(View view, LayoutParams params) {
        this.mContentView = view;
        this.mRootLayout.addView(this.mContentView, params);
    }

    protected void closeMenu() {
        if (this.drawerLayout != null) {
            this.drawerLayout.closeDrawers();
        }
    }

    protected void openMenu() {
        if (this.drawerLayout != null) {
            this.drawerLayout.openDrawer(3);
        }
    }

    protected boolean isMenuOpen() {
        if (this.drawerLayout != null) {
            return this.drawerLayout.isDrawerOpen(3);
        }
        return false;
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        this.activity.odklSuperSetContentView(2130903225);
        this.activity.setMenuView((MenuView) this.activity.findViewById(2131624643));
        this.mRootLayout = (ViewGroup) this.activity.findViewById(2131624888);
    }

    public void setMenuIndicatorEnable(boolean enable) {
        ActionBar actionBar = this.activity == null ? null : this.activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        if (this.mDrawerToggle != null) {
            this.mDrawerToggle.setDrawerIndicatorEnabled(enable);
            this.mDrawerToggle.syncState();
        }
    }

    public boolean isMenuIndicatorEnable() {
        return this.mDrawerToggle != null && this.mDrawerToggle.isDrawerIndicatorEnabled();
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        this.drawerLayout = (DrawerLayout) this.activity.findViewById(2131624638);
        if (this.drawerLayout != null) {
            LocalizationManager.from(this.activity).registerView(this.drawerLayout, 2130903225);
            this.drawerLayout.postDelayed(new C05251(), 100);
            this.mDrawerToggle = new C05262(this.activity, this.drawerLayout, 2131165722, 2131165721);
            this.drawerLayout.setDrawerListener(this.mDrawerToggle);
            if (isMenuButtonEnable(this.activity)) {
                ActionBar actionBar = this.activity.getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                this.mDrawerToggle.setDrawerIndicatorEnabled(true);
                this.mDrawerToggle.syncState();
                return;
            }
            this.mDrawerToggle.setDrawerIndicatorEnabled(false);
            this.mDrawerToggle.syncState();
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode != 4 || !isMenuOpen()) {
            return false;
        }
        closeMenu();
        return true;
    }

    public void buildTabbarActions(OdklTabbar tabbar) {
        tabbar.addAction(tabbar.getFeedAction());
        tabbar.addAction(tabbar.getDiscussionsAction());
        tabbar.addAction(tabbar.getConversationAction());
        tabbar.addAction(tabbar.getMusicAction());
        tabbar.addAction(tabbar.getMenuTabbarAction());
    }

    public boolean isCollapsible() {
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == null || item.getItemId() != 16908332 || (!isMenuButtonEnable(this.activity) && (this.mDrawerToggle == null || !this.mDrawerToggle.isDrawerIndicatorEnabled()))) {
            return super.onOptionsItemSelected(item);
        }
        if (this.drawerLayout.isDrawerVisible((int) GravityCompat.START)) {
            this.drawerLayout.closeDrawer((int) GravityCompat.START);
        } else {
            this.drawerLayout.openDrawer((int) GravityCompat.START);
            StatisticManager.getInstance().addStatisticEvent("left_menu-open_but_action_bar", new Pair[0]);
        }
        return true;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isMenuButtonEnable(this.activity) && this.mDrawerToggle != null) {
            this.mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    private boolean isMenuButtonEnable(Activity activity) {
        return SlidingMenuStrategy.isFromLeftMenu(activity) || (activity instanceof OdklActivity) || (activity instanceof SettingsProfileActivity) || (activity instanceof SearchActivity);
    }
}
