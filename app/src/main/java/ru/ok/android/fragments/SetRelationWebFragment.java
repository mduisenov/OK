package ru.ok.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import ru.ok.android.fragments.web.WebBaseFragment.DefaultWebViewClient;
import ru.ok.android.fragments.web.WebFragment;
import ru.ok.android.fragments.web.client.interceptor.UrlInterceptor;
import ru.ok.android.fragments.web.client.interceptor.hooks.AppHooksInterceptor;
import ru.ok.android.fragments.web.hooks.profiles.HookUserProfileProcessor;
import ru.ok.android.fragments.web.hooks.profiles.HookUserProfileProcessor.HookUserProfileListener;
import ru.ok.android.utils.WebUrlCreator;

public class SetRelationWebFragment extends WebFragment implements HookUserProfileListener {

    /* renamed from: ru.ok.android.fragments.SetRelationWebFragment.1 */
    class C02871 implements UrlInterceptor {
        final AppHooksInterceptor appHooksInterceptor;

        /* renamed from: ru.ok.android.fragments.SetRelationWebFragment.1.1 */
        class C02861 extends HookUserProfileProcessor {
            C02861(HookUserProfileListener x0) {
                super(x0);
            }

            protected boolean isUriMatches(Uri uri) {
                return super.isUriMatches(uri) && TextUtils.equals(getUidFromQueryParam(uri), SetRelationWebFragment.this.getSrcProfileUid());
            }
        }

        C02871() {
            this.appHooksInterceptor = new AppHooksInterceptor();
            this.appHooksInterceptor.addHookProcessor(new C02861(SetRelationWebFragment.this));
        }

        public boolean handleUrl(String url) {
            return this.appHooksInterceptor.handleUrl(url);
        }
    }

    /* renamed from: ru.ok.android.fragments.SetRelationWebFragment.2 */
    class C02882 extends DefaultWebViewClient {
        C02882(Context x0) {
            super(x0);
        }

        protected boolean isExternalUrl(String url) {
            return false;
        }
    }

    public static final Bundle newArguments(String targetUid, String srcProfileUid) {
        Bundle args = new Bundle();
        args.putString("target_uid", targetUid);
        args.putString("src_profile_uid", srcProfileUid);
        return args;
    }

    public String getTargetUid() {
        Bundle args = getArguments();
        return args == null ? null : args.getString("target_uid");
    }

    public String getSrcProfileUid() {
        Bundle args = getArguments();
        return args == null ? null : args.getString("src_profile_uid");
    }

    public String getStartUrl() {
        return WebUrlCreator.getFriendsShipPageUrl(getTargetUid());
    }

    protected void initWebViewClient(DefaultWebViewClient client) {
        client.addInterceptor(new C02871());
        super.initWebViewClient(client);
    }

    public DefaultWebViewClient createWebViewClient() {
        return new C02882(getContext());
    }

    public void onUserProfileSelected(String userId) {
        Activity activity = getActivity();
        if (TextUtils.equals(userId, getSrcProfileUid())) {
            activity.finish();
        }
    }
}
