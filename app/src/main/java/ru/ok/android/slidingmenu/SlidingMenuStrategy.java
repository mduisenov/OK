package ru.ok.android.slidingmenu;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.tabbar.OdklTabbar;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;

public abstract class SlidingMenuStrategy {
    private static final Handler handler;
    private static StrategyType strategyType;
    protected final OdklSlidingMenuFragmentActivity activity;

    /* renamed from: ru.ok.android.slidingmenu.SlidingMenuStrategy.1 */
    static /* synthetic */ class C05241 {
        static final /* synthetic */ int[] f87xc4aa0b04;

        static {
            f87xc4aa0b04 = new int[StrategyType.values().length];
            try {
                f87xc4aa0b04[StrategyType.Google.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f87xc4aa0b04[StrategyType.Custom.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public enum StrategyType {
        Google,
        Custom
    }

    public abstract void buildTabbarActions(OdklTabbar odklTabbar);

    protected abstract void closeMenu();

    public abstract boolean isCollapsible();

    protected abstract boolean isMenuOpen();

    protected abstract void onCreateLocalized(Bundle bundle);

    public abstract boolean onKeyUp(int i, KeyEvent keyEvent);

    protected abstract void onPostCreate(Bundle bundle);

    protected abstract void openMenu();

    protected abstract void setContentView(int i);

    protected abstract void setContentView(View view);

    protected abstract void setContentView(View view, LayoutParams layoutParams);

    static {
        handler = new Handler(Looper.getMainLooper());
        strategyType = null;
    }

    public static StrategyType getStrategyType() {
        if (strategyType == null) {
            Context context = OdnoklassnikiApplication.getContext();
            if (DeviceUtils.getType(context) == DeviceLayoutType.SMALL || DeviceUtils.needSlidingMenuFixForWebView(context)) {
                strategyType = StrategyType.Google;
            } else {
                strategyType = StrategyType.Custom;
            }
        }
        return strategyType;
    }

    public static StrategyType getStrategyType(int screenOrientation) {
        return getStrategyType();
    }

    public static boolean isFromLeftMenu(Activity activity) {
        return (activity == null || activity.getIntent() == null || !activity.getIntent().getBooleanExtra("key_activity_from_menu", false)) ? false : true;
    }

    public static final SlidingMenuStrategy getCurrentStrategy(OdklSlidingMenuFragmentActivity activity) {
        switch (C05241.f87xc4aa0b04[getStrategyType().ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return new SlidingMenuStrategyGoogle(activity);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return new SlidingMenuStrategyCustom(activity);
            default:
                return null;
        }
    }

    public static int getContentWidth(Resources res, int screenOrientation, StrategyType type, int containerWidth) {
        switch (C05241.f87xc4aa0b04[type.ordinal()]) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return containerWidth - getClosedVisibleWidth(res, screenOrientation, type);
            default:
                return containerWidth;
        }
    }

    public static int getClosedVisibleWidth(Resources res, int screenOrientation, StrategyType type) {
        switch (C05241.f87xc4aa0b04[type.ordinal()]) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return !DeviceUtils.isShowTabbar() ? res.getDimensionPixelSize(2131230746) : 0;
            default:
                return 0;
        }
    }

    public static final boolean isNeedShowTabbar() {
        switch (C05241.f87xc4aa0b04[getStrategyType().ordinal()]) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (DeviceUtils.getType(OdnoklassnikiApplication.getContext()) != DeviceLayoutType.SMALL) {
                    return false;
                }
                return true;
            default:
                return true;
        }
    }

    protected void onPause() {
    }

    protected void onResume() {
    }

    protected SlidingMenuStrategy(OdklSlidingMenuFragmentActivity activity) {
        this.activity = activity;
    }

    protected void onSaveInstanceState(Bundle outState) {
    }

    public View findViewById(int id) {
        return null;
    }

    public boolean isMenuIndicatorEnable() {
        return false;
    }

    public void setMenuIndicatorEnable(boolean enable) {
    }

    public void processRunnableClick(Runnable runnable) {
        handler.postDelayed(runnable, 180);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }
}
