package ru.ok.android.ui.activity;

import android.os.Bundle;
import ru.ok.android.ui.activity.main.ActivityExecutor;
import ru.ok.android.ui.activity.main.OdklSubActivity;
import ru.ok.android.ui.fragments.PlayerFragment;
import ru.ok.android.ui.utils.HomeButtonUtils;
import ru.ok.android.utils.animation.PlayerAnimationHelper;

public class PlayerActivity extends OdklSubActivity {
    private boolean playerButtonNotified;

    protected boolean isSupportToolbarOverlay() {
        return true;
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        ActivityExecutor activityExecutor = new ActivityExecutor(this, PlayerFragment.class);
        activityExecutor.setAddToBackStack(false);
        activityExecutor.setNeedToolbar(false);
        activityExecutor.setTag("player_fragment");
        if (getIntent() != null && getIntent().getBooleanExtra("extra_animate", false)) {
            Bundle args = new Bundle();
            args.putBoolean("argument_animate", true);
            activityExecutor.setArguments(args);
        }
        HomeButtonUtils.showHomeButton(this);
        showFragment(activityExecutor);
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (!this.playerButtonNotified) {
            PlayerAnimationHelper.sendPlayerCollapsed();
            this.playerButtonNotified = true;
        }
    }

    protected void onStop() {
        super.onStop();
        if (!this.playerButtonNotified) {
            PlayerAnimationHelper.sendPlayerCollapsed();
            this.playerButtonNotified = true;
        }
    }
}
