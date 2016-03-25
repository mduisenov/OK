package ru.ok.android.fragments.web.hooks.search;

import android.net.Uri;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;

public class HookSearchHappeningsObserver extends HookBaseProcessor {
    private OnSearchHappeningsListener onSearchHappeningsListener;

    public interface OnSearchHappeningsListener {
        void onSearchHappenings(String str);
    }

    public HookSearchHappeningsObserver(OnSearchHappeningsListener onSearchHappeningsListener) {
        this.onSearchHappeningsListener = onSearchHappeningsListener;
    }

    protected void onHookExecute(Uri uri) {
        String query = uri.getQueryParameter(DiscoverInfo.ELEMENT);
        if (this.onSearchHappeningsListener != null) {
            this.onSearchHappeningsListener.onSearchHappenings(query);
        }
    }

    protected String getHookName() {
        return "/apphook/searchHappenings";
    }
}
