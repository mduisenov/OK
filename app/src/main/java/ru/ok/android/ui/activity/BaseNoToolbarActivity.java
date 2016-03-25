package ru.ok.android.ui.activity;

import android.widget.FrameLayout;

public class BaseNoToolbarActivity extends BaseActivity {
    protected void onBaseBindViews() {
        this.contentWrapper = (FrameLayout) findViewById(2131624637);
    }

    protected int getBaseCompatLayoutId() {
        return 2130903111;
    }

    protected boolean isSupportToolbarVisible() {
        return false;
    }

    public boolean isUseTabbar() {
        return false;
    }

    protected void postProcessView() {
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}
