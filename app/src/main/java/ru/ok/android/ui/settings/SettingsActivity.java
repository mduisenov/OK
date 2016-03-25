package ru.ok.android.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.settings.Settings;

public class SettingsActivity extends BaseActivity {
    protected void onCreateLocalized(Bundle savedInstanceState) {
        setContentView();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getFragmentManager().beginTransaction().replace(2131624639, new SettingsFragment()).commit();
    }

    protected void onPostResume() {
        super.onPostResume();
        Intent intent = getIntent();
        if (intent == null || !intent.getBooleanExtra("allow_non_login_state", false)) {
            startLoginIfNeeded();
        }
    }

    protected boolean startLoginIfNeeded() {
        if (Settings.hasLoginData(this)) {
            return false;
        }
        NavigationHelper.login(this);
        return true;
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}
