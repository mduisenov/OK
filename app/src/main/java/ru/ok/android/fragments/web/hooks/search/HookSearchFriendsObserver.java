package ru.ok.android.fragments.web.hooks.search;

import android.net.Uri;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;

public class HookSearchFriendsObserver extends HookBaseProcessor {
    private OnSearchFriendsListener onSearchFriendsListener;

    public interface OnSearchFriendsListener {
        void onSearchFriends(String str);
    }

    public HookSearchFriendsObserver(OnSearchFriendsListener onSearchFriendsListener) {
        this.onSearchFriendsListener = onSearchFriendsListener;
    }

    protected void onHookExecute(Uri uri) {
        String query = uri.getQueryParameter(DiscoverInfo.ELEMENT);
        if (this.onSearchFriendsListener != null) {
            this.onSearchFriendsListener.onSearchFriends(query);
        }
    }

    protected String getHookName() {
        return "/apphook/searchFriends";
    }
}
