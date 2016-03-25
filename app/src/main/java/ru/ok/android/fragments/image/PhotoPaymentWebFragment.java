package ru.ok.android.fragments.image;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.fragments.web.WebBaseFragment.DefaultWebViewClient;
import ru.ok.android.fragments.web.WebFragment;
import ru.ok.android.fragments.web.client.interceptor.hooks.AppHooksInterceptor;
import ru.ok.android.fragments.web.hooks.HookPaymentCancelled;
import ru.ok.android.fragments.web.hooks.HookPaymentCancelled.HookPaymentCancelledListener;
import ru.ok.android.fragments.web.hooks.HookPaymentDone;
import ru.ok.android.fragments.web.hooks.HookPaymentDone.HookPaymentDoneListener;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.WebUrlCreator;

public final class PhotoPaymentWebFragment extends WebFragment implements HookPaymentCancelledListener, HookPaymentDoneListener {

    /* renamed from: ru.ok.android.fragments.image.PhotoPaymentWebFragment.1 */
    class C03121 extends DefaultWebViewClient {
        C03121(Context x0) {
            super(x0);
            addInterceptor(new AppHooksInterceptor().addHookProcessor(new HookPaymentDone(PhotoPaymentWebFragment.this)).addHookProcessor(new HookPaymentCancelled(PhotoPaymentWebFragment.this)));
        }

        protected boolean isExternalUrl(String url) {
            return false;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setNoRefreshingMode();
        return view;
    }

    public String getStartUrl() {
        return WebUrlCreator.getPhotoPaymentUrl(getAid(), getPid(), getFid());
    }

    public static Bundle newArguments(String aid, String pid, String fid) {
        Bundle args = new Bundle();
        args.putString("aid", aid);
        args.putString("pid", pid);
        args.putString("fid", fid);
        return args;
    }

    public DefaultWebViewClient createWebViewClient() {
        return new C03121(getContext());
    }

    private String getAid() {
        return getArguments().getString("aid");
    }

    private String getPid() {
        return getArguments().getString("pid");
    }

    private String getFid() {
        return getArguments().getString("fid");
    }

    protected int getTitleResId() {
        return 2131166069;
    }

    public void onPaymentDone(int serviceId) {
        Logger.m172d("");
        NavigationHelper.finishActivity(getActivity());
    }

    public void onPaymentCancelled(int serviceId) {
        Logger.m172d("");
        NavigationHelper.finishActivity(getActivity());
    }
}
