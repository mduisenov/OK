package ru.ok.android.ui.image.view;

import ru.ok.android.ui.activity.BaseActivity;

public class TransparentToolbarBaseActivity extends BaseActivity {
    protected boolean isSupportToolbarVisible() {
        return true;
    }

    public boolean isSupportToolbarOverlay() {
        return true;
    }

    protected boolean isToolbarTitleEnabled() {
        return false;
    }
}
