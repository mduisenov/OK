package ru.ok.android.fragments;

import android.content.Context;
import android.os.Bundle;
import ru.ok.android.fragments.web.WebBaseFragment.DefaultWebViewClient;
import ru.ok.android.fragments.web.WebFragment;
import ru.ok.android.fragments.web.client.interceptor.hooks.AppHooksInterceptor;
import ru.ok.android.fragments.web.hooks.HookPaymentCancelled;
import ru.ok.android.fragments.web.hooks.HookPaymentCancelled.HookPaymentCancelledListener;
import ru.ok.android.fragments.web.hooks.HookPaymentDone;
import ru.ok.android.fragments.web.hooks.HookPaymentDone.HookPaymentDoneListener;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.WebUrlCreator;

public class PaymentWebFragment extends WebFragment implements HookPaymentCancelledListener, HookPaymentDoneListener {

    class PaymentWebClient extends DefaultWebViewClient {
        public PaymentWebClient(Context context) {
            super(context);
            addInterceptor(new AppHooksInterceptor().addHookProcessor(new HookPaymentDone(r6)).addHookProcessor(new HookPaymentCancelled(r6)));
        }

        public boolean isExternalUrl(String url) {
            return false;
        }
    }

    public String getStartUrl() {
        return WebUrlCreator.getPaymentUrl(JsonSessionTransportProvider.getInstance().getStateHolder().getUserId());
    }

    public DefaultWebViewClient createWebViewClient() {
        return new PaymentWebClient(getContext());
    }

    public void reloadUrl() {
        loadUrl(getStartUrl());
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getWebView().saveState(outState);
    }

    public void onPaymentDone(int serviceId) {
        Logger.m172d("");
        NavigationHelper.finishActivity(getActivity());
    }

    public void onPaymentCancelled(int serviceId) {
        Logger.m172d("");
        NavigationHelper.finishActivity(getActivity());
    }

    protected int getTitleResId() {
        return 2131166601;
    }
}
