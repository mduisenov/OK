package ru.ok.android.ui.users.activity;

import android.os.Bundle;
import ru.ok.android.ui.activity.main.OdklSubActivity;
import ru.ok.android.ui.utils.HomeButtonUtils;

public final class UsersLikesActivity extends OdklSubActivity {
    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        HomeButtonUtils.hideHomeButton(this);
    }
}
