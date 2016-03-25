package ru.ok.android.ui.settings;

import android.os.Bundle;
import ru.ok.android.fragments.settings.SettingsWebFragment;
import ru.ok.android.ui.activity.ShowFragmentActivity;
import ru.ok.android.ui.activity.main.ActivityExecutor;
import ru.ok.android.ui.utils.HomeButtonUtils;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;

public class SettingsProfileActivity extends ShowFragmentActivity {
    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        setContentView(2130903355);
        ActivityExecutor activityExecutor = new ActivityExecutor(this, SettingsWebFragment.class);
        activityExecutor.setAddToBackStack(false);
        activityExecutor.setNeedToolbar(true);
        HomeButtonUtils.showHomeButton(this);
        showFragment(activityExecutor);
    }

    protected Type getSlidingMenuSelectedItem() {
        return Type.profile_settings;
    }

    public boolean isUseTabbar() {
        return true;
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}
