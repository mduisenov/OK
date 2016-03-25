package ru.ok.android.ui.users.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import ru.ok.android.fragments.RecommendedUsersFragment;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.ui.utils.HomeButtonUtils;

public class RecommendedUsersActivity extends BaseActivity {
    private RecommendedUsersFragment fragment;

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        setContentView(getLayoutId());
        showFragment();
    }

    public void onBackPressed() {
        this.fragment.onBackPressed();
        super.onBackPressed();
    }

    protected int getLayoutId() {
        return 2130903355;
    }

    protected void showFragment() {
        HomeButtonUtils.hideHomeButton(this);
        Intent intent = getIntent();
        if (intent != null) {
            String tag = intent.getStringExtra("key_fragment_tag");
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment == null) {
                Bundle bundle = intent.getBundleExtra("key_argument_name");
                if (bundle == null) {
                    bundle = new Bundle();
                }
                fragment = RecommendedUsersFragment.newInstance(bundle);
                fragment.setArguments(bundle);
            }
            this.fragment = (RecommendedUsersFragment) fragment;
            getSupportFragmentManager().beginTransaction().replace(2131624639, fragment, tag).commit();
        }
    }
}
