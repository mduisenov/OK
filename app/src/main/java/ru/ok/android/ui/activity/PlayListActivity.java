package ru.ok.android.ui.activity;

import android.os.Bundle;
import ru.ok.android.fragments.music.MusicPlayListFragment;
import ru.ok.android.ui.activity.main.ActivityExecutor;
import ru.ok.android.ui.activity.main.OdklSubActivity;
import ru.ok.android.ui.utils.HomeButtonUtils;

public class PlayListActivity extends OdklSubActivity {
    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        ActivityExecutor activityExecutor = new ActivityExecutor(this, MusicPlayListFragment.class);
        activityExecutor.setAddToBackStack(false);
        activityExecutor.setNeedToolbar(true);
        HomeButtonUtils.showHomeButton(this);
        showFragment(activityExecutor);
    }
}
