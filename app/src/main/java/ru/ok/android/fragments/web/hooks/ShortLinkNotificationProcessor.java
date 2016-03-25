package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public class ShortLinkNotificationProcessor extends ShortLinkBaseProcessor {
    private final NotificationsListener listener;

    public interface NotificationsListener {
        void onShowNotifications();
    }

    public ShortLinkNotificationProcessor(NotificationsListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/notifications";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onShowNotifications();
        }
    }
}
