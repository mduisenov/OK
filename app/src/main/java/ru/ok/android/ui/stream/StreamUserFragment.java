package ru.ok.android.ui.stream;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import ru.ok.android.ui.users.fragments.profiles.ExternalProfileUserFragment;
import ru.ok.android.ui.users.fragments.profiles.ProfileUserFragment;

public final class StreamUserFragment extends BaseStreamUserFragment {
    public static Bundle newArguments(String userId) {
        Bundle args = new Bundle();
        String str = "UID";
        if (TextUtils.isEmpty(userId)) {
            userId = null;
        }
        args.putString(str, userId);
        return args;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem itemMediaTopic = menu.findItem(2131625475);
        if (itemMediaTopic != null) {
            itemMediaTopic.setVisible(false);
        }
    }

    public String getUserId() {
        return getArguments().getString("UID");
    }

    public ProfileUserFragment createProfileFragment() {
        return ExternalProfileUserFragment.newInstance(getUserId());
    }

    protected boolean isMediaPostPanelRequired() {
        return false;
    }
}
