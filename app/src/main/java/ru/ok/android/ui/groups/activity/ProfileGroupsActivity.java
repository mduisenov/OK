package ru.ok.android.ui.groups.activity;

import android.os.Bundle;
import ru.ok.android.ui.activity.main.ActivityExecutor;
import ru.ok.android.ui.activity.main.OdklSubActivity;
import ru.ok.android.ui.stream.StreamGroupFragment;
import ru.ok.android.ui.utils.HomeButtonUtils;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;

public final class ProfileGroupsActivity extends OdklSubActivity {
    public void onCreate(Bundle icicle) {
        if (DeviceUtils.getType(this) == DeviceLayoutType.SMALL) {
            setTheme(2131296786);
        } else {
            setTheme(2131296785);
        }
        super.onCreate(icicle);
    }

    protected void postProcessView() {
        super.postProcessView();
        if (DeviceUtils.getType(this) == DeviceLayoutType.SMALL) {
            this.appBarLayout.setBackgroundDrawable(getResources().getDrawable(2130837608));
        }
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        ActivityExecutor activityExecutor = new ActivityExecutor(this, StreamGroupFragment.class);
        activityExecutor.setAddToBackStack(false);
        activityExecutor.setNeedToolbar(true);
        activityExecutor.setSlidingMenuEnable(true);
        activityExecutor.setArguments(StreamGroupFragment.newArguments(getGroupId()));
        activityExecutor.setHideHomeButton(false);
        HomeButtonUtils.hideHomeButton(this);
        showFragment(activityExecutor);
    }

    public String getGroupId() {
        return getIntent().getStringExtra("group_input");
    }

    public boolean isNeedShowLeftMenu() {
        return true;
    }

    public boolean isSupportToolbarOverlay() {
        return DeviceUtils.isSmall(this);
    }
}
