package ru.ok.android.ui.stream;

import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.ui.users.fragments.profiles.CurrentProfileUserFragment;
import ru.ok.android.ui.users.fragments.profiles.ProfileUserFragment;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.localization.LocalizationManager;

public final class StreamCurrentUserFragment extends BaseStreamUserFragment {
    protected String getTitle() {
        if (this.profileFragment != null) {
            String userName = ((ProfileUserFragment) this.profileFragment).getUserName();
            if (userName != null) {
                return userName;
            }
        }
        if (DeviceUtils.isSmall(getContext())) {
            return "";
        }
        return LocalizationManager.getString(OdnoklassnikiApplication.getContext(), 2131166247);
    }

    public String getUserId() {
        return OdnoklassnikiApplication.getCurrentUser().uid;
    }

    public ProfileUserFragment createProfileFragment() {
        return new CurrentProfileUserFragment();
    }

    protected boolean isMediaPostPanelRequired() {
        return true;
    }
}
