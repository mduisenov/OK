package ru.ok.android.fragments.web.hooks;

import android.net.Uri;

public class HookFriends extends HookBaseProcessor {
    private final HookFriendsListener friendsListener;

    public interface HookFriendsListener {
        void onShowFriends(String str, String str2);
    }

    public HookFriends(HookFriendsListener friendsListener) {
        this.friendsListener = friendsListener;
    }

    protected String getHookName() {
        return "/apphook/friends";
    }

    protected void onHookExecute(Uri uri) {
        this.friendsListener.onShowFriends(uri.getQueryParameter("uid"), uri.getQueryParameter("rel"));
    }
}
