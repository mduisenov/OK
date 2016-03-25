package ru.ok.android.ui.activity;

import android.support.v4.app.Fragment;
import java.util.List;
import ru.ok.android.fragments.ExternalUrlWebFragment;
import ru.ok.android.ui.activity.main.OdklSubActivity;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;

public final class ExternalUrlActivity extends OdklSubActivity {
    protected Type getSlidingMenuSelectedItem() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(getIntent().getStringExtra("key_fragment_tag"));
        if (fragment == null) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (!(fragments == null || fragments.isEmpty())) {
                fragment = (Fragment) fragments.get(0);
            }
        }
        if (fragment instanceof ExternalUrlWebFragment) {
            return ((ExternalUrlWebFragment) fragment).getType();
        }
        return null;
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}
