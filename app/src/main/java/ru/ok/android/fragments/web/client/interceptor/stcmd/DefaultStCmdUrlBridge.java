package ru.ok.android.fragments.web.client.interceptor.stcmd;

import android.app.Activity;
import ru.ok.android.fragments.web.client.interceptor.stcmd.StCmdUrlInterceptor.StCmdUrlInterceptorCallBack;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NavigationHelper.Source;

public class DefaultStCmdUrlBridge implements StCmdUrlInterceptorCallBack {
    private Activity activity;

    public DefaultStCmdUrlBridge(Activity activity) {
        this.activity = activity;
    }

    public boolean onGoHomePage(String url) {
        NavigationHelper.showFeedPage(this.activity, Source.st_cmd);
        return true;
    }

    public boolean onGoGuestsPage() {
        NavigationHelper.showGuestPage(this.activity);
        return true;
    }

    public boolean onGoMarksPage() {
        NavigationHelper.showMarksPage(this.activity);
        return true;
    }

    public boolean onGoDscPage() {
        NavigationHelper.showDiscussionPage(this.activity);
        return true;
    }

    public boolean onGoProfilePage() {
        NavigationHelper.showCurrentUser(this.activity, false);
        return false;
    }

    public boolean onGoUserStream(String uid) {
        NavigationHelper.showUserStreamPage(this.activity, uid);
        return true;
    }

    public boolean onGoGroupStream(String gid) {
        NavigationHelper.showGroupStreamPage(this.activity, gid);
        return true;
    }
}
