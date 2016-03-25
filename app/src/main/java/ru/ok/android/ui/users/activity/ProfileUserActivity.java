package ru.ok.android.ui.users.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import ru.ok.android.app.helper.AccountsHelper;
import ru.ok.android.onelog.AppLaunchLog;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.activity.main.ActivityExecutor;
import ru.ok.android.ui.activity.main.OdklSubActivity;
import ru.ok.android.ui.stream.StreamCurrentUserFragment;
import ru.ok.android.ui.stream.StreamUserFragment;
import ru.ok.android.ui.utils.HomeButtonUtils;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NavigationHelper.Source;

public final class ProfileUserActivity extends OdklSubActivity {
    public void onCreate(Bundle icicle) {
        if (isSupportToolbarOverlay() && DeviceUtils.getType(this) == DeviceLayoutType.SMALL) {
            setTheme(2131296786);
        } else {
            setTheme(2131296785);
        }
        super.onCreate(icicle);
    }

    protected void postProcessView() {
        super.postProcessView();
        if (isSupportToolbarOverlay() && DeviceUtils.isSmall(this)) {
            this.appBarLayout.setBackgroundDrawable(getResources().getDrawable(2130837608));
        }
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        ActivityExecutor activityExecutor;
        super.onCreateLocalized(savedInstanceState);
        String userId = getUserId(savedInstanceState);
        if (userId != null) {
            activityExecutor = new ActivityExecutor(this, StreamUserFragment.class);
            activityExecutor.setArguments(StreamUserFragment.newArguments(userId));
        } else {
            activityExecutor = new ActivityExecutor(this, StreamCurrentUserFragment.class);
        }
        activityExecutor.setAddToBackStack(false);
        activityExecutor.setNeedToolbar(true);
        activityExecutor.setSlidingMenuEnable(true);
        activityExecutor.setHideHomeButton(false);
        HomeButtonUtils.hideHomeButton(this);
        showFragment(activityExecutor);
    }

    protected void onResume() {
        super.onResume();
        startLoginIfNeeded();
    }

    public boolean isSupportToolbarOverlay() {
        return DeviceUtils.isSmall(this);
    }

    private String getUserId(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String type = intent.getType();
        if (DeviceUtils.isSonyDevice() || TextUtils.equals(type, getString(2131166208))) {
            String userId = AccountsHelper.extractUserIdFromContactUri(this, intent);
            if (userId != null) {
                AppLaunchLog.contacts();
                if (savedInstanceState != null) {
                    return userId;
                }
                StatisticManager.getInstance().addStatisticEvent("app-launched-from-contacts", new Pair("source", "profile"));
                return userId;
            }
        }
        return intent.getStringExtra("user_input");
    }

    public boolean isBackGoToFeed() {
        return getIntent().getBooleanExtra("is_back_to_feed", false);
    }

    public boolean isNeedShowLeftMenu() {
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (isBackGoToFeed()) {
            NavigationHelper.showFeedPage(this, Source.back);
        }
    }
}
