package ru.ok.android.fragments.groups;

import android.content.Context;
import android.os.Bundle;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fragments.web.WebBaseFragment.DefaultWebViewClient;
import ru.ok.android.fragments.web.WebFragment;
import ru.ok.android.utils.WebUrlCreator;

public final class GroupWebFragment extends WebFragment {

    class GroupWebViewClient extends DefaultWebViewClient {
        public GroupWebViewClient(Context context) {
            super(context);
        }

        protected boolean isExternalUrl(String url) {
            return false;
        }
    }

    public String getStartUrl() {
        return WebUrlCreator.getGroupPageUrl(getGroupId());
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getWebView().saveState(outState);
    }

    public static Bundle newArguments(String groupId) {
        Bundle args = new Bundle();
        args.putString("GID", groupId);
        return args;
    }

    protected String getGroupId() {
        return getArguments().getString("GID");
    }

    @Subscribe(on = 2131623946, to = 2131624226)
    public void onGroupTopicLoad(BusEvent event) {
        if (isVisible()) {
            reloadUrl();
        }
    }

    public DefaultWebViewClient createWebViewClient() {
        return new GroupWebViewClient(getContext());
    }
}
