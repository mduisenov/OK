package ru.ok.android.fragments.web.hooks.users;

import android.net.Uri;
import android.text.TextUtils;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public class ShortLinkProfileCurrentUserProcessor extends ShortLinkBaseProcessor {
    private final ProfileCurrentUserListener listener;

    public interface ProfileCurrentUserListener {
        void onShowMyProfile();
    }

    public ShortLinkProfileCurrentUserProcessor(ProfileCurrentUserListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        String userIdString = OdnoklassnikiApplication.getCurrentUser().getId();
        if (TextUtils.isEmpty(userIdString)) {
            return "/profile/has_no_current_user";
        }
        long userId = Long.parseLong(userIdString);
        if (userId != 265224201205L) {
            userId ^= 265224201205L;
        }
        return "/profile/" + userId;
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onShowMyProfile();
        }
    }

    protected boolean isUriMatches(Uri uri) {
        return uri.getPath().equals(getHookName());
    }
}
