package ru.ok.android.ui.activity.compat;

import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.v4.widget.DrawerLayout;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity;
import ru.ok.android.slidingmenu.SlidingMenuStrategy;
import ru.ok.android.slidingmenu.SlidingMenuStrategy.StrategyType;

public class BaseCompatDrawerToolbarActivity extends BaseCompatToolbarActivity {
    private DrawerLayout drawerLayout;

    protected int getBaseCompatLayoutId() {
        if (SlidingMenuStrategy.getStrategyType() != StrategyType.Custom && (this instanceof OdklSlidingMenuFragmentActivity) && isNeedShowLeftMenu()) {
            return 2130903112;
        }
        return super.getBaseCompatLayoutId();
    }

    protected void onBaseBindViews() {
        super.onBaseBindViews();
        this.drawerLayout = (DrawerLayout) findViewById(2131624638);
    }

    protected void postProcessView() {
        super.postProcessView();
        if (VERSION.SDK_INT >= 21) {
            TypedArray a = getTheme().obtainStyledAttributes(new int[]{16843828});
            int colorPrimaryDarkResId = a.getResourceId(0, 0);
            a.recycle();
            if (this.drawerLayout != null) {
                this.drawerLayout.setStatusBarBackground(colorPrimaryDarkResId);
            }
            if (!(this instanceof OdklSlidingMenuFragmentActivity) || !isNeedShowLeftMenu()) {
                getWindow().setStatusBarColor(getResources().getColor(colorPrimaryDarkResId));
            }
        }
    }

    public DrawerLayout getDrawerLayout() {
        return this.drawerLayout;
    }

    public boolean isNeedShowLeftMenu() {
        return true;
    }
}
