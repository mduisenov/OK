package ru.ok.android.fragments.web.hooks.users;

import android.net.Uri;
import java.util.List;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;
import ru.ok.android.utils.Logger;

public class ShortLinkProfileUserProcessor extends ShortLinkBaseProcessor {
    private final ProfileUserListener listener;

    public interface ProfileUserListener {
        void onShowUserProfile(String str);
    }

    public ShortLinkProfileUserProcessor(ProfileUserListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/profile/";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            List<String> segs = uri.getPathSegments();
            String userIdString = (segs == null || segs.size() <= 1) ? null : (String) segs.get(1);
            try {
                long userId = Long.valueOf((String) segs.get(1)).longValue();
                if (userId != 265224201205L) {
                    userId ^= 265224201205L;
                }
                userIdString = String.valueOf(userId);
            } catch (NumberFormatException e) {
                Logger.m180e(e, "Failed to XOR uri: %s", uri);
            }
            this.listener.onShowUserProfile(userIdString);
        }
    }

    protected boolean isUriMatches(Uri uri) {
        if (uri.getPath().startsWith(getHookName()) && uri.getPathSegments().size() == 2) {
            return true;
        }
        return false;
    }
}
